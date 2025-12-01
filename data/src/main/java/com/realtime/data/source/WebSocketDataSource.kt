package com.realtime.data.source

import androidx.lifecycle.LifecycleOwner
import com.realtime.data.model.StockDataModel
import kotlinx.coroutines.flow.SharedFlow


interface WebSocketDataSource {

    var marketValue: SharedFlow<List<StockDataModel>>

    fun startWebSocket()
    fun stopWebSocket()
    fun send(data: String)
    fun reconnectWebSocket()

    fun observeLifecycle(lifecycleOwner: LifecycleOwner)
}