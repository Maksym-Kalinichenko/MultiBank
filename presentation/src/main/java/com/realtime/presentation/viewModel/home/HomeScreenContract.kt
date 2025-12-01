package com.realtime.presentation.viewModel.home

import com.realtime.presentation.model.MarketItemPresentable

class HomeScreenContract {

    sealed class HomeViewEvent {
        data object Init : HomeViewEvent()
        data object SwitchChanged : HomeViewEvent()
    }

    data class HomeViewUIState(
        val switchState: Boolean = false,
    )

    sealed class MarketViewState {
        data object Loading : MarketViewState()
        data object Empty : MarketViewState()
        data class Error(val exception: Throwable?) : MarketViewState()
        data class Success(val items: List<MarketItemPresentable>) : MarketViewState()

        fun asSuccess() = this as? Success
    }
}