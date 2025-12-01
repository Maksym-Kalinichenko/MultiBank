package com.realtime.presentation.viewModel.home


import kotlinx.coroutines.flow.StateFlow

interface IHomeViewModel {
    val marketListViewState: StateFlow<HomeScreenContract.MarketViewState>
    val viewState: StateFlow<HomeScreenContract.HomeViewUIState>
    fun setEvent(event: HomeScreenContract.HomeViewEvent)
}