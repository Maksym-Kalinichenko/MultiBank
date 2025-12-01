package com.realtime.presentation.viewModel.mainScreen

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModel
import com.realtime.data.source.WebSocketDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val webSocketDataSource: WebSocketDataSource,
) : ViewModel() {


    init {
        webSocketDataSource.observeLifecycle(ProcessLifecycleOwner.get())
        webSocketDataSource.startWebSocket()
    }
}