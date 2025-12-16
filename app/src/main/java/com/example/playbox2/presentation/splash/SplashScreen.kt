package com.example.playbox2.presentation.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playbox2.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(4000)
            _isLoading.value = false
        }
    }
}

@Composable
fun SplashScreen(navController: NavController, mainViewModel: MainViewModel = viewModel()) {
    val isLoading by mainViewModel.isLoading.collectAsState()

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            navController.navigate("videolist") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    val backgroundColor = Color(0xFFFCFAFA)
    val splashIcon = ImageBitmap.imageResource(id = R.drawable.splashicon)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 280f
            val iconSize = 80f
            val rotation = 35f

            val cols = (size.width / spacing).toInt() + 2
            val rows = (size.height / spacing).toInt() + 2

            for (x in 0 until cols) {
                for (y in 0 until rows) {
                    val offsetX = x * spacing + if (y % 2 == 0) spacing / 2 else 0f
                    val offsetY = y * spacing - spacing * 2

                    withTransform({
                        translate(left = offsetX, top = offsetY)
                        rotate(rotation)
                        scale(0.4f)
                    }) {
                        drawImage(
                            image = splashIcon,
                            alpha = 0.15f,
                            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splashicon),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(45.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(36.dp))

            Text(
                text = "PlayBox", fontSize = 42.sp, modifier = Modifier.padding(top = 20.dp)
                )


        }
    }
}
