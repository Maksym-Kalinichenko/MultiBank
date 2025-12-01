package com.realtime.presentation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.realtime.presentation.model.MarketItemPresentable
import com.realtime.presentation.view.home.HomeScreen
import com.realtime.presentation.viewModel.home.HomeScreenContract
import com.realtime.presentation.viewModel.home.IHomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test

class FakeHomeViewModel : IHomeViewModel {

    private val _marketListViewState = MutableStateFlow<HomeScreenContract.MarketViewState>(
        HomeScreenContract.MarketViewState.Success(
            listOf(
                MarketItemPresentable("AAPL", 123.45),
                MarketItemPresentable("GOOG", 2345.67)
            )
        )
    )
    override val marketListViewState: StateFlow<HomeScreenContract.MarketViewState> =
        _marketListViewState

    private val _viewStateFlow =
        MutableStateFlow(HomeScreenContract.HomeViewUIState(switchState = false))
    override val viewState: StateFlow<HomeScreenContract.HomeViewUIState> = _viewStateFlow

    override fun setEvent(event: HomeScreenContract.HomeViewEvent) {
        when (event) {
            HomeScreenContract.HomeViewEvent.SwitchChanged -> {
                val current = _viewStateFlow.value
                _viewStateFlow.value = current.copy(switchState = !current.switchState)
            }

            else -> {}
        }
    }

    fun setMarketState(state: HomeScreenContract.MarketViewState) {
        _marketListViewState.value = state
    }
}

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun switch_click_changes_state() {
        val fakeViewModel = FakeHomeViewModel()

        composeTestRule.setContent {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                homeViewModel = fakeViewModel
            )
        }

        composeTestRule.onNode(isToggleable()).assertIsOff()

        composeTestRule.onNode(isToggleable()).performClick()

        composeTestRule.onNode(isToggleable()).assertIsOn()
    }

    @Test
    fun success_state_shows_market_items() {
        val fakeViewModel = FakeHomeViewModel()

        composeTestRule.setContent {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                homeViewModel = fakeViewModel
            )
        }

        composeTestRule.onNodeWithText("AAPL").assertIsDisplayed()
        composeTestRule.onNodeWithText("GOOG").assertIsDisplayed()
    }

    @Test
    fun error_state_shows_error_message() {
        val fakeViewModel = FakeHomeViewModel()
        fakeViewModel.setMarketState(HomeScreenContract.MarketViewState.Error(exception = null))
        composeTestRule.setContent {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                homeViewModel = fakeViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Something went wrong, please try again later…")
            .assertIsDisplayed()
    }
}