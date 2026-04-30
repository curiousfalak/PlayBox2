package com.example.playbox2


import GetVideoListUseCase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playbox2.data.local.NetworkMonitor
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
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = android.graphics.Color.parseColor("#F63B51")

        // 🔹 Retrofit (ONLINE)
        val api = Retrofit.Builder()
            .baseUrl("http://10.87.124.69:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoApi::class.java)
        val db = PlayBoxDatabase.getInstance(applicationContext)


        // 🔹 Repository (ONLINE + OFFLINE)
        val repository = VideoRepositoryImpl(
            api = api,
            dao = db.videoDao(),
            context = applicationContext
        )


        // 🔹 UseCase
        val getVideoListUseCase = GetVideoListUseCase(repository)

        setContent {
            PlayBox2Theme {
                val viewModel: VideoListViewModel = viewModel(
                    factory = VideoListViewModelFactory(
                        repository = repository,
                        networkMonitor = NetworkMonitor(applicationContext)
                    )
                )



                AppNavGraph(
                    videoListViewModel = viewModel
                )
            }
        }
    }
}
