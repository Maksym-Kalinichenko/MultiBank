package com.realtime.presentation.viewModel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realtime.data.source.WebSocketDataSource
import com.realtime.domain.model.Stock
import com.realtime.presentation.model.MarketItemPresentable
import com.realtime.presentation.sampleData.stockList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val webSocketDataSource: WebSocketDataSource
) : ViewModel(), IHomeViewModel {

    private val _viewState = MutableStateFlow(HomeScreenContract.HomeViewUIState())
    override val viewState: StateFlow<HomeScreenContract.HomeViewUIState> get() = _viewState

    private var _marketListViewState =
        MutableStateFlow<HomeScreenContract.MarketViewState>(HomeScreenContract.MarketViewState.Loading)
    override val marketListViewState: StateFlow<HomeScreenContract.MarketViewState> =
        _marketListViewState
    private var periodicSendJob: Job? = null
    private var webSocketJob: Job = Job()

    override fun setEvent(event: HomeScreenContract.HomeViewEvent) {
        when (event) {
            HomeScreenContract.HomeViewEvent.Init -> onInit()
            HomeScreenContract.HomeViewEvent.SwitchChanged -> onSwitchChanged()
        }
    }

    private fun onInit() {
        val stocks = stockList
        val items = stocks.map { stock ->

            val lastPrice = stock.price
            val previousPrice = (lastPrice * (0.98 + Math.random() * 0.04))

            MarketItemPresentable(
                name = stock.name,
                lastPrice = lastPrice,
                previousPrice = previousPrice
            )
        }.sortedByDescending { it.lastPrice }

        _marketListViewState.value = HomeScreenContract.MarketViewState.Success(items)
        listenWebSocket()
    }

    private fun listenWebSocket() {
        webSocketJob.cancel()
        webSocketJob = viewModelScope.launch(Dispatchers.IO) {
            webSocketDataSource.marketValue.collect { stockList ->

                val currentMarketItems = when (val currentState = _marketListViewState.value) {
                    is HomeScreenContract.MarketViewState.Success -> currentState.items
                    else -> emptyList()
                }

                val marketItems = stockList.map { stock ->
                    val previousPrice =
                        currentMarketItems.find { it.name == stock.name }?.lastPrice ?: stock.price

                    MarketItemPresentable(
                        name = stock.name,
                        lastPrice = stock.price,
                        previousPrice = previousPrice
                    )
                }.sortedByDescending { it.lastPrice }

                _marketListViewState.value = HomeScreenContract.MarketViewState.Success(marketItems)

            }
        }
    }


    private fun onSwitchChanged() {
        val newState = !viewState.value.switchState
        _viewState.value = viewState.value.copy(switchState = newState)

        if (newState) {
            startPeriodicWebsocketSending()
        } else {
            stopPeriodicWebsocketSending()
        }
    }

    private fun startPeriodicWebsocketSending() {
        periodicSendJob?.cancel()

        periodicSendJob = viewModelScope.launch(Dispatchers.IO) {
            val random = java.util.Random()

            while (isActive) {
                val currentMarketItems = when (val currentState = _marketListViewState.value) {
                    is HomeScreenContract.MarketViewState.Success -> currentState.items
                    else -> emptyList()
                }

                val stocks = currentMarketItems.map { item ->
                    val changePercent = (random.nextDouble() - 0.5) * 0.3
                    val newPrice = item.lastPrice * (1 + changePercent)

                    Stock(
                        name = item.name,
                        price = newPrice
                    )
                }
                val json = buildJson(stocks)
                webSocketDataSource.send(json)

                delay(2000)
            }
        }
    }

    private fun stopPeriodicWebsocketSending() {
        periodicSendJob?.cancel()
        periodicSendJob = null
    }

    private fun buildJson(stocks: List<Stock>): String {
        return stocks.joinToString(
            prefix = "[", postfix = "]", separator = ","
        ) {
            """{"name":"${it.name}","price":${it.price}}"""
        }
    }
}