package com.example.healthapp.screens.content.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun WorkoutsContent() {
    var videos by remember { mutableStateOf(emptyList<Video>()) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                if (!isLoading) {
                    isLoading = true
                    fetchVideos { newVideos ->
                        videos = newVideos
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Loading..." else "Load Videos")
        }
        Spacer(modifier = Modifier.height(16.dp))
        VideoListContent(videos)
    }
}

@Composable
fun VideoListContent(videos: List<Video>) {
    Column {
        for (video in videos) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = video.title)
        }
    }
}

data class Video(val title: String)

private fun fetchVideos(onSuccess: (List<Video>) -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        val query = "dogs"
        val maxResults = 10
        val apiKey = "AIzaSyBkgRvDZDq5xXj4KawIv5K5FvZs0q63xtw"

        val url =
            URL("https://www.googleapis.com/youtube/v3/search?key=$apiKey&q=$query&maxResults=$maxResults")
        val connection = url.openConnection() as HttpURLConnection
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

            val videos = mutableListOf<Video>()
            val jsonObject = JSONObject(response.toString())
            val items = jsonObject.getJSONArray("items")
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val id = item.getJSONObject("id")
                val videoId = id.getString("videoId")
                videos.add(Video(videoId))
            }

            onSuccess(videos)
        } else {
            Log.e("HERE", "Failed to fetch data from YouTube API. Response code: $responseCode")
        }

        connection.disconnect()
    }
}
