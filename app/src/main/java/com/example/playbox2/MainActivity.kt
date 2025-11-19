package com.example.playbox2

import GetVideoListUseCase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.playbox2.data.remote.VideoApi
import com.example.playbox2.data.remote.VideoRepositoryImpl
import com.example.playbox2.presentation.navigation.AppNavGraph
import com.example.playbox2.presentation.videolist.VideoListScreen
import com.example.playbox2.presentation.videolist.VideoListViewModel
import com.example.playbox2.presentation.videoplayer.VideoPlayerScreen
import com.example.playbox2.ui.theme.PlayBox2Theme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = Retrofit.Builder()
            .baseUrl("http://10.50.157.69:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoApi::class.java)

        val repo = VideoRepositoryImpl(api)
        val useCase = GetVideoListUseCase(repo)
        val viewModel = VideoListViewModel(useCase)

        setContent {
            PlayBox2Theme {
                AppNavGraph(viewModel = viewModel)

            }
        }
    }
}
