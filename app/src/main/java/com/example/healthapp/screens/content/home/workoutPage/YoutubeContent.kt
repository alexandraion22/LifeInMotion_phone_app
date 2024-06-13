package com.example.healthapp.screens.content.home.workoutPage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.healthapp.R
import com.example.healthapp.database.bpm.daily.BpmDaily
import com.example.healthapp.database.schedule.WorkoutScheduleRepository
import com.example.healthapp.ui.theme.CoolGray
import com.example.healthapp.ui.theme.KindaLightGray
import com.example.healthapp.ui.theme.PsychedelicPurple
import com.example.healthapp.ui.theme.customTextFieldColors
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
    var nextPageTokens by remember { mutableStateOf(listOf<String?>(null, null, null, null, null)) }
    var searchQuery by remember { mutableStateOf("") }
    var dayVideos by remember { mutableStateOf<Set<String>>(emptySet()) }
    var videoInWorkoutStatus by remember { mutableStateOf(List(videos.size) { false }) }
    val textFieldColors = customTextFieldColors()

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
            .fillMaxWidth()
            .fillMaxHeight(0.98f)
            .background(color = KindaLightGray)
    ) {
        // Search bar with search button
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search workouts") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KindaLightGray, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                maxLines = 1,
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.LightGray)
                    } else {
                        IconButton(onClick = {
                            if (!isLoading) {
                                isLoading = true
                                fetchVideos(nextPageTokens, searchQuery) { newVideos, newNextPageTokens ->
                                    videos = newVideos
                                    nextPageTokens = newNextPageTokens
                                    isLoading = false
                                }
                            }
                        }) {
                            Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "Search")
                        }
                    }
                },
                colors = textFieldColors
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        if (videos.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = PsychedelicPurple),
                    onClick = {
                        if (!isLoading) {
                            isLoading = true
                            fetchVideos(nextPageTokens, searchQuery) { newVideos, newNextPageTokens ->
                                videos += newVideos
                                nextPageTokens = newNextPageTokens
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text("Load more videos", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            VideoListContent(videos, dayIndex, dayVideos, workoutScheduleRepository)
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

private fun fetchVideos(
    pageTokens: List<String?>,
    query: String,
    onSuccess: (List<Video>, List<String?>) -> Unit
) {
    val channelIds = listOf(
        "UCpis3RcTw6t47XO0R_KY4WQ",
        "UCVQJZE_on7It_pEv6tn-jdA",
        "UCsLF0qPTpkYKq81HsjgzhwQ",
        "UCwrXi5ZknKThspJc-Gai04g",
        "UCqjwF8rxRsotnojGl4gM0Zw"
    )

    GlobalScope.launch(Dispatchers.IO) {
        val maxResults = 2
        val apiKey = "AIzaSyBfgupEF5Xh1WLTPuM27gawhOU-qj3XeRc"
        val combinedVideos = mutableListOf<Video>()
        val newPageTokens = mutableListOf<String?>()

        channelIds.forEachIndexed { index, channelId ->
            val pageToken = pageTokens.getOrNull(index)
            val searchUrl = URL("https://www.googleapis.com/youtube/v3/search?key=$apiKey&q=$query&type=video&channelId=$channelId&maxResults=$maxResults&pageToken=${pageToken ?: ""}")
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

                combinedVideos.addAll(videos)
                newPageTokens.add(nextPageToken)
            } else {
                Log.e("YoutubeContent", "Failed to fetch data from YouTube API. Response code: $responseCode")
                newPageTokens.add(null)
            }
            connection.disconnect()
        }

        combinedVideos.shuffle() // Shuffle the combined video list
        onSuccess(combinedVideos, newPageTokens)
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

    LazyColumn(modifier = Modifier.fillMaxHeight(0.92f).padding(horizontal = 16.dp)) {
        itemsIndexed(videos) { index, video ->
            val isVideoInWorkout = if (videoInWorkoutStatus.size >index) videoInWorkoutStatus[index] else false// Get current status from list
            Box(modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, KindaLightGray, RoundedCornerShape(24.dp))
                .background(color = Color.White))
            {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=${video.videoId}")
                            )
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThumbnailImage(videoId = video.videoId)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f)
                    ) {
                        Text(text = video.title, maxLines = 2, fontSize = 14.sp)
                        Text(text = video.creator, maxLines = 1, fontSize = 12.sp, color = CoolGray)
                    }
                    Button(
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(24.dp)).border(1.dp, KindaLightGray, RoundedCornerShape(24.dp)).padding(0.dp),
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
                        Icon(
                            modifier = Modifier.size(60.dp),
                            painter = painterResource(if (isVideoInWorkout) R.drawable.ic_check else R.drawable.ic_add),
                            contentDescription = "Video Status",
                            tint = if (isVideoInWorkout) Color(0XFF20A072) else CoolGray
                        )
                    }
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
            .clip(shape = RoundedCornerShape(20.dp))
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=$videoId")
                )
                context.startActivity(intent)
            }
    )
}

