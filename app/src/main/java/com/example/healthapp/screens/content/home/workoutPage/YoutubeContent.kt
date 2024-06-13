package com.example.healthapp.screens.content.home.workoutPage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.healthapp.R
import com.example.healthapp.database.bpm.daily.BpmDaily
import com.example.healthapp.database.schedule.WorkoutScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class Video(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val creator: String
)

fun addVideo(dayIndex: Int, videoId: String, workoutScheduleRepository: WorkoutScheduleRepository, coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        workoutScheduleRepository.addVideoToDay(dayIndex, videoId)
    }
}

@Composable
fun YoutubeContent(dayIndex: Int, workoutScheduleRepository: WorkoutScheduleRepository) {
    var videos by remember { mutableStateOf(emptyList<Video>()) }
    var isLoading by remember { mutableStateOf(false) }
    var nextPageToken by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var dayVideos by remember { mutableStateOf<Set<String>>(emptySet()) }
    var videoInWorkoutStatus by remember { mutableStateOf(List(videos.size) { false }) }

    LaunchedEffect(Unit) {
        dayVideos = withContext(Dispatchers.IO) {
            workoutScheduleRepository.getListForDay(dayIndex)?.workouts ?: emptySet()
        }
    }

    // Initialize videoInWorkoutStatus based on dayVideos
    LaunchedEffect(dayVideos) {
        videoInWorkoutStatus = videos.map { video ->
            dayVideos.contains(video.videoId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar with search button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                label = { Text("Search YouTube") },
                singleLine = true,
                maxLines = 1,
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        IconButton(onClick = {
                            if (!isLoading) {
                                isLoading = true
                                fetchVideos(null, searchQuery) { newVideos, newNextPageToken ->
                                    videos = newVideos
                                    nextPageToken = newNextPageToken
                                    isLoading = false
                                }
                            }
                        }) {
                            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search")
                        }
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (videos.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (!isLoading) {
                            isLoading = true
                            fetchVideos(nextPageToken, searchQuery) { newVideos, newNextPageToken ->
                                videos += newVideos
                                isLoading = false


                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Loading..." else "Load more videos")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            VideoListContent(videos, dayIndex, dayVideos, workoutScheduleRepository)
        }
    }
}

private fun fetchVideos(pageToken: String?, query: String, onSuccess: (List<Video>, String?) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        val maxResults = 10
        val apiKey = "AIzaSyB68zQeAKVL--6wMjwZ28YB2CSZJVXYqBU"

        val searchUrl = URL("https://www.googleapis.com/youtube/v3/search?key=$apiKey&q=$query&type=video&channelId=UChVRfsT_ASBZk10o0An7Ucg&maxResults=$maxResults&pageToken=${pageToken ?: ""}")
        val connection = searchUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuilder()

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()
            inputStream.close()

            val jsonObject = JSONObject(response.toString())
            val items = jsonObject.getJSONArray("items")
            val nextPageToken = jsonObject.optString("nextPageToken", null)

            val videos = mutableListOf<Video>()
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val id = item.getJSONObject("id")
                val videoId = id.getString("videoId")

                // Fetch additional details for each video
                val videoDetailsUrl = URL("https://www.googleapis.com/youtube/v3/videos?key=$apiKey&id=$videoId&part=snippet")
                val videoDetailsConnection = videoDetailsUrl.openConnection() as HttpURLConnection
                videoDetailsConnection.requestMethod = "GET"

                val videoDetailsResponseCode = videoDetailsConnection.responseCode
                if (videoDetailsResponseCode == HttpURLConnection.HTTP_OK) {
                    val videoDetailsInputStream = videoDetailsConnection.inputStream
                    val videoDetailsReader = BufferedReader(InputStreamReader(videoDetailsInputStream))
                    val videoDetailsResponse = StringBuilder()

                    var videoDetailsLine: String?
                    while (videoDetailsReader.readLine().also { videoDetailsLine = it } != null) {
                        videoDetailsResponse.append(videoDetailsLine)
                    }

                    videoDetailsReader.close()
                    videoDetailsInputStream.close()

                    val videoDetailsJson = JSONObject(videoDetailsResponse.toString())
                    val videoDetailsItems = videoDetailsJson.getJSONArray("items")
                    if (videoDetailsItems.length() > 0) {
                        val videoDetailsItem = videoDetailsItems.getJSONObject(0)
                        val snippet = videoDetailsItem.getJSONObject("snippet")
                        val title = snippet.getString("title")
                        val thumbnailUrl = snippet.getJSONObject("thumbnails").getJSONObject("high").getString("url")
                        val creator = snippet.getString("channelTitle")

                        videos.add(Video(videoId, title, thumbnailUrl, creator))
                    }
                } else {
                    Log.e("YoutubeContent", "Failed to fetch video details from YouTube API. Response code: $videoDetailsResponseCode")
                }

                videoDetailsConnection.disconnect()
            }

            onSuccess(videos, nextPageToken)
        } else {
            Log.e("YoutubeContent", "Failed to fetch data from YouTube API. Response code: $responseCode")
        }

        connection.disconnect()
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun VideoListContent(
    videos: List<Video>,
    dayIndex: Int,
    dayVideos: Set<String>,
    workoutScheduleRepository: WorkoutScheduleRepository
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var videoToDelete by remember { mutableStateOf<Video?>(null) }
    var videoInWorkoutStatus by remember { mutableStateOf<List<Boolean>>(emptyList()) }
    var dayVideosLocal by remember { mutableStateOf<Set<String>>(emptySet()) }

    coroutineScope.launch {
        dayVideosLocal = withContext(Dispatchers.IO) {
            workoutScheduleRepository.getListForDay(dayIndex)?.workouts ?: emptySet()
        }
        videoInWorkoutStatus = videos.map { video ->
            dayVideosLocal.contains(video.videoId)
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this video from the workout list?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            workoutScheduleRepository.deleteVideoFromDay(dayIndex, videoToDelete!!.videoId)
                            showDialog = false
                            videoInWorkoutStatus = videoInWorkoutStatus.toMutableList().apply {
                                set(videos.indexOf(videoToDelete!!), false)
                            }
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxHeight(0.92f)) {
        itemsIndexed(videos) { index, video ->
            val isVideoInWorkout = if (videoInWorkoutStatus.size >index) videoInWorkoutStatus[index] else false// Get current status from list
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/watch?v=${video.videoId}")
                        )
                        context.startActivity(intent)
                    }
            ) {
                ThumbnailImage(videoId = video.videoId)
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .weight(1f)
                ) {
                    Text(text = video.title, modifier = Modifier.padding(bottom = 4.dp), maxLines = 2)
                    Text(text = video.creator, maxLines = 1)
                }
                Button(
                    onClick = {
                        if (isVideoInWorkout) {
                            videoToDelete = video
                            showDialog = true
                        } else {
                            addVideo(dayIndex, video.videoId, workoutScheduleRepository, coroutineScope)
                            videoInWorkoutStatus = videoInWorkoutStatus.toMutableList().apply {
                                set(index, true)
                            }
                        }
                    }
                ) {
                    Text(if (isVideoInWorkout) "✔" else "+")
                }
            }
        }
    }
}

@Composable
fun ThumbnailImage(videoId: String) {
    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/mqdefault.jpg"
    val context = LocalContext.current

    Image(
        painter = rememberImagePainter(
            data = thumbnailUrl
        ),
        contentDescription = "Thumbnail",
        modifier = Modifier
            .height(60.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=$videoId")
                )
                context.startActivity(intent)
            }
    )
}

