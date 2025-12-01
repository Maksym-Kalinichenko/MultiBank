package com.realtime.network.source

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.realtime.core.qualifier.IoDispatcher
import com.realtime.data.model.StockDataModel
import com.realtime.data.source.WebSocketDataSource
import com.realtime.network.mapper.StockDTOMapper
import com.realtime.network.model.StockDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DefaultWebSocketDataSource @Inject constructor(
    private val stockDTOMapper: StockDTOMapper,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WebSocketDataSource, WebSocketListener() {

    companion object {
        const val TAG = "DefaultWebSocketDataSource"
        const val NORMAL_CLOSURE_STATUS = 1000
        const val WS_URL = "wss://ws.postman-echo.com/raw"
    }

    private val _marketValue = MutableSharedFlow<List<StockDataModel>>()
    override var marketValue: SharedFlow<List<StockDataModel>> = _marketValue
    private var _webSocket: WebSocket? = null
    private var _scope = CoroutineScope(dispatcher)
    private var _json = Json { ignoreUnknownKeys = true }
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private var reconnect: Boolean = false
    private var isConnected: Boolean = false
    private val _pendingMessages = mutableListOf<String>()

    override fun observeLifecycle(lifecycleOwner: LifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "App paused, stopping WebSocket")
                    reconnect = true
                    stopWebSocket()
                }

                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG, "App resumed, reconnect=$reconnect")
                    if (reconnect) {
                        reconnectWebSocket()
                    }
                }

                Lifecycle.Event.ON_DESTROY -> {
                    Log.d(TAG, "App destroyed")
                    _pendingMessages.clear()
                    stopWebSocket()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    private fun getWebClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .pingInterval(20, TimeUnit.SECONDS)
        .build()

    override fun startWebSocket() {
        isConnected = false
        val request = Request.Builder().url(WS_URL).build()
        Log.d(TAG, "Connecting to WebSocket: $WS_URL")
        _webSocket = getWebClient().newWebSocket(request, this)
    }

    override fun stopWebSocket() {
        _webSocket?.apply {
            try {
                close(NORMAL_CLOSURE_STATUS, "Closing WebSocket")
            } catch (ex: Exception) {
                Log.e(TAG, "Error stopping WebSocket", ex)
            }
        }
        _webSocket = null
        isConnected = false
    }

    override fun send(data: String) {
        _scope.launch {
            if (isConnected && _webSocket != null) {
                _webSocket?.send(data)
            } else {
                Log.d(TAG, "Socket not connected, adding to pending messages")
                _pendingMessages.add(data)
            }
        }
    }

    override fun reconnectWebSocket() {
        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++
            _scope.launch {
                stopWebSocket()
                delay(3000L * reconnectAttempts)
                startWebSocket()
            }
        } else {
            Log.e(TAG, "Max reconnect attempts reached")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        isConnected = true
        reconnectAttempts = 0
        Log.d(TAG, "WebSocket opened!")

        _pendingMessages.forEach {
            webSocket.send(it)
        }
        _pendingMessages.clear()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e(TAG, "WebSocket failure: ${t.message}")
        isConnected = false
        reconnect = true
        reconnectWebSocket()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        isConnected = false
        Log.d(TAG, "WebSocket closed: $code / $reason")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        _scope.launch {
            try {
                val stockDTO = _json.decodeFromString<List<StockDTO>>(text)
                val result = stockDTOMapper.fromList(stockDTO)
                _marketValue.emit(result)
            } catch (e: Throwable) {
                Log.e(
                    TAG, "[onMessage] socketErrorEvent parsing error: ${e.message} $text"
                )
            }
        }
    }
}
