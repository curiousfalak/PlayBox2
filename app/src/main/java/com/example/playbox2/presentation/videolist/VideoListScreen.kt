package com.example.playbox2.presentation.videolist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.playbox2.R
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

val AppPrimary = Color(0xFFF63B51)
val AppBackground = Color.White
val SearchBarBg = Color.White
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    viewModel: VideoListViewModel,
    navController: NavController
) {
    val videos by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }



    val filteredVideos = videos.filter { video ->
        val matchesSearch =
            searchQuery.isBlank() ||
                    video.title.contains(searchQuery, ignoreCase = true)

        val matchesCategory =
            selectedCategory == "All" ||
                    video.category.equals(selectedCategory, ignoreCase = true)

        matchesSearch && matchesCategory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {

        /* ---------- SEARCH ---------- */
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search videos...") },
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SearchBarBg,
                    unfocusedContainerColor = SearchBarBg,
                    focusedBorderColor = AppPrimary,
                    cursorColor = AppPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
        }

        /* ---------- CATEGORY ---------- */
        item {
            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        /* ---------- BANNER ---------- */
           item {
               Caros()
           }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        /* ---------- TITLE ---------- */
        item {
            Text(
                text = "All Videos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        /* ---------- VIDEO LIST ---------- */
        items(filteredVideos) { video ->
            val encodedUrl = remember(video.streamUrl) {
                URLEncoder.encode(
                    video.streamUrl,
                    StandardCharsets.UTF_8.toString()
                )
            }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clickable {
                            navController.navigate("videoplayer/$encodedUrl")
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = getPosterForVideo(video.title)),
                        contentDescription = "Video thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                1.dp,
                                Color(0xFFEDEDED),
                                RoundedCornerShape(14.dp)
                            )
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = video.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(50),
                            tonalElevation = 2.dp,
                            color = Color(0xFFF5F5F5),
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                viewModel.downloadVideo(video)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Download,
                                    contentDescription = "Download",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Download",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }


        }


        }
            }





@Composable
fun SlidingImageCarousel(imageResIds: List<Int>) {
    var currentImageIndex by remember { mutableStateOf(0) }
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            isVisible = false
            delay(500)
            currentImageIndex = (currentImageIndex + 1) % imageResIds.size
            isVisible = true
            delay(6000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000))
        ) {
            Image(
                painter = painterResource(id = imageResIds[currentImageIndex]),
                contentDescription = "Sliding Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.height(224.dp).width(624.dp)
            )
        }
    }


}

@Composable
fun Caros() {
    SlidingImageCarousel(
        imageResIds = listOf(
            R.drawable.captain_america,
            R.drawable.uri_movie,
            R.drawable.football,
            R.drawable.phir_hera_pheri,
            R.drawable.superasiacup23,
            R.drawable.img_1,
            R.drawable.rajshamani,
            R.drawable.football
        )
    )
}

@Composable
fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("All", "Movies", "TV Shows", "Sports", "Live TV")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { cat ->
            val isSelected = cat == selectedCategory

            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = if (isSelected) 6.dp else 0.dp,
                shadowElevation = if (isSelected) 6.dp else 0.dp,
                color = if (isSelected) AppPrimary else Color(0xFFF7F7F7),
                border = if (isSelected) null else BorderStroke(
                    1.dp,
                    AppPrimary.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .clickable { onCategorySelected(cat) }
            ) {
                Text(
                    text = cat,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Color.White else AppPrimary,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    )
                )
            }
        }
    }
}





// 🎬 Poster Mapper (MVP-friendly)
fun getPosterForVideo(name: String): Int {
    return when {
        name.contains("uri", true) -> R.drawable.uri_movie
        name.contains("hera", true) -> R.drawable.phir_hera_pheri
        name.contains("golmaal", true) -> R.drawable.golmaal
        name.contains("captain", true) -> R.drawable.captain_america
        name.contains("taarak", true) -> R.drawable.img_1
        name.contains("mrbean",true)->R.drawable.mrbean
        name.contains("superasiacup23",true)->R.drawable.superasiacup23
        name.contains("football",true)->R.drawable.football
        name.contains("jayshetty",true)->R.drawable.jayshetty
        name.contains("rajshamani",true)->R.drawable.rajshamani

        else -> R.drawable.img

    }
}
