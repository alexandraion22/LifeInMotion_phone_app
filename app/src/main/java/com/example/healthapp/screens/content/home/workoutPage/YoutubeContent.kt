package com.example.healthapp.screens.content.home.workoutPage

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

@Composable
fun YoutubeContent() {
    var videos by remember { mutableStateOf(emptyList<Video>()) }
    var isLoading by remember { mutableStateOf(false) }
    var nextPageToken by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") } // State for the search query

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter search query") },
            singleLine = true,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isLoading) {
                    isLoading = true
                    fetchVideos(null,searchQuery) { newVideos, newNextPageToken ->
                        videos = newVideos
                        nextPageToken = newNextPageToken
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Loading..." else "Search")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isLoading) {
                    isLoading = true
                    fetchVideos(nextPageToken,searchQuery) { newVideos, newNextPageToken ->
                        videos+= newVideos
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Loading..." else "Next")
        }
        if (videos.isNotEmpty()) {
            VideoListContent(videos)
        }
    }
}

private fun fetchVideos(pageToken: String?, query: String, onSuccess: (List<Video>, String?) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        val maxResults = 10
        val apiKey = "AIzaSyB68zQeAKVL--6wMjwZ28YB2CSZJVXYqBU"

        val searchUrl = URL("https://www.googleapis.com/youtube/v3/search?key=$apiKey&q=$query&type=video&channelId=UChVRfsT_ASBZk10o0An7Ucg&maxResults=$maxResults&pageToken=${pageToken ?: ""}")
        Log.e("HERE",searchUrl.toString())
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
                    Log.e("HERE", "Failed to fetch video details from YouTube API. Response code: $videoDetailsResponseCode")
                }

                videoDetailsConnection.disconnect()
            }

            onSuccess(videos, nextPageToken)
        } else {
            Log.e("HERE", "Failed to fetch data from YouTube API. Response code: $responseCode")
        }

        connection.disconnect()
    }
}

@Composable
fun VideoListContent(videos: List<Video>) {
    val context = LocalContext.current

    LazyColumn (modifier = Modifier.fillMaxHeight(0.92f)){
        items(videos) { video ->
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video.videoId}"))
                        context.startActivity(intent)
                    }
            ) {
                Image(
                    painter = rememberImagePainter(video.thumbnailUrl),
                    contentDescription = "Thumbnail",
                    modifier = Modifier
                        .width(120.dp)
                        .height(90.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxHeight()
                ) {
                    Text(text = video.title, modifier = Modifier.padding(bottom = 4.dp), maxLines = 2)
                    Text(text = "Creator: ${video.creator}", maxLines = 1)
                }
            }
        }
    }
}
