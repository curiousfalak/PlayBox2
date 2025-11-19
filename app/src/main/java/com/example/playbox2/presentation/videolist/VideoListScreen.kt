package com.example.playbox2.presentation.videolist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    viewModel: VideoListViewModel,
    navController: NavController
) {
    val videos = viewModel.state.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search videos...", color = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(


              focusedTextColor = Color.Black,

                focusedContainerColor = Color(0xFFF3EDED),
                unfocusedContainerColor = Color(0xFFF1DBDB)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )



        // -----------------------------------------------------

        Text(
            text = "Top Searches",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ------------------ VIDEO CARD LIST ------------------
        LazyColumn {
            items(videos.filter { it.title.contains(searchQuery, ignoreCase = true) }) { video ->

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            val encodedUrl = URLEncoder.encode(
                                video.streamUrl,
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate("videoplayer/$encodedUrl")
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {

                        // ---------- Thumbnail (you can enable later) ----------

//                        Image(
//                            painter = rememberAsyncImagePainter(video.thumbnailUrl ?: ""),
//                            contentDescription = video.title,
//                            modifier = Modifier
//                                .size(80.dp)
//                                .clip(RoundedCornerShape(12.dp))
//                        )


                        // If keeping thumbnail disabled, keep spacing consistent
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFD6D6D6))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // ---------- Title ----------
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1f)
                                .alignByBaseline()
                        )
                    }
                }
            }
        }
        // -----------------------------------------------------
    }
}


