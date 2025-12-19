package com.example.playbox2

import GetVideoListUseCase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.playbox2.data.local.database.PlayBoxDatabase
import com.example.playbox2.data.remote.VideoApi
import com.example.playbox2.data.remote.VideoRepositoryImpl


import com.example.playbox2.presentation.navigation.AppNavGraph
import com.example.playbox2.presentation.videolist.VideoListViewModel
import com.example.playbox2.presentation.videolist.VideoListViewModelFactory

import com.example.playbox2.ui.theme.PlayBox2Theme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Retrofit (ONLINE)
        val api = Retrofit.Builder()
            .baseUrl("http://10.191.62.69:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoApi::class.java)

        // 🔹 Room (OFFLINE)
        val db = Room.databaseBuilder(
            applicationContext,
            PlayBoxDatabase::class.java,
            "playbox_db"
        ).build()

        // 🔹 Repository (ONLINE + OFFLINE)
        val repository = VideoRepositoryImpl(
            api = api,
            dao = db.videoDao()
        )

        // 🔹 UseCase
        val getVideoListUseCase = GetVideoListUseCase(repository)

        setContent {
            PlayBox2Theme {

                val viewModel: VideoListViewModel = viewModel(
                    factory = VideoListViewModelFactory(
                        getVideos = getVideoListUseCase,
                        repository = repository
                    )
                )


                AppNavGraph(viewModel = viewModel)
            }
        }
    }
}
