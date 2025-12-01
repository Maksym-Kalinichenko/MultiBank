package com.realtime.presentation.view.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realtime.presentation.model.MarketItemPresentable
import com.realtime.presentation.ui.theme.buyColor
import com.realtime.presentation.ui.theme.sellColor
import com.realtime.presentation.viewModel.home.HomeScreenContract
import com.realtime.presentation.viewModel.home.HomeViewModel
import com.realtime.presentation.viewModel.home.IHomeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    homeViewModel: IHomeViewModel = hiltViewModel<HomeViewModel>()
) {
    val marketViewState by homeViewModel.marketListViewState.collectAsStateWithLifecycle()
    val viewState by homeViewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.setEvent(event = HomeScreenContract.HomeViewEvent.Init)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(top = 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    Switch(
                        modifier = Modifier
                            .padding(end = 12.dp),
                        checked = viewState.switchState,
                        onCheckedChange = {
                            homeViewModel.setEvent(HomeScreenContract.HomeViewEvent.SwitchChanged)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                            checkedTrackColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.tertiary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (viewState.switchState) buyColor else sellColor)
                    )
                },
                title = {
                }
            )
        }
    ) { paddingView ->
        Column(
            modifier = modifier
                .padding(
                    PaddingValues(
                        top = paddingView.calculateTopPadding(),
                        start = paddingView.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingView.calculateEndPadding(LayoutDirection.Ltr),
                    )
                )
                .background(MaterialTheme.colorScheme.background)
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (marketViewState) {
                    is HomeScreenContract.MarketViewState.Empty -> ErrorColumn(
                        modifier,
                        "There are no symbols in this list"
                    )

                    is HomeScreenContract.MarketViewState.Error -> ErrorColumn(
                        modifier,
                        "Something went wrong, please try again later…"
                    )

                    is HomeScreenContract.MarketViewState.Loading -> Box(
                        modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    is HomeScreenContract.MarketViewState.Success -> MarketItems((marketViewState as HomeScreenContract.MarketViewState.Success).items)
                }

            }
        }
    }
}

@Composable
fun ErrorColumn(modifier: Modifier = Modifier, stringResource: String) {
    Column(
        modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource,
            style = TextStyle(
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 14.sp,
                fontWeight = FontWeight.W800,
            )
        )
    }
}

@Composable
fun MarketItems(items: List<MarketItemPresentable>) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        itemsIndexed(items = items, key = { index, _ -> index }) { _, item ->
            MarketWatchItem(
                item = item
            )
        }
    }
}

@Composable
fun MarketWatchItem(
    item: MarketItemPresentable,
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                item.name, style = TextStyle(
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W800,
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val color = if (item.changeStatus) buyColor else sellColor
                FormattedPriceText(item, color)
                Icon(
                    modifier = Modifier
                        .rotate(if (item.changeStatus) (-90f) else 90f),
                    tint = color,
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Localized description"
                )
            }
        }
    }
}

@Composable
fun FormattedPriceText(
    item: MarketItemPresentable,
    highlightColor: Color
) {
    val defaultColor = MaterialTheme.colorScheme.tertiary
    val price = item.priceFormatted
    val digits = 5

    var isHighlighted by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        targetValue = if (isHighlighted) highlightColor else defaultColor,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(item.lastPrice, item.previousPrice) {
        if (item.lastPrice != item.previousPrice) {
            isHighlighted = true
            delay(1000)
            isHighlighted = false
        }
    }

    val baseStyle = TextStyle(
        color = animatedColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.W800,
    )
    val bigFontSize = baseStyle.fontSize * 1.5f
    val smallFontSize = baseStyle.fontSize * 0.6f

    val decimalIndex = price.indexOf(",").takeIf { it != -1 } ?: price.indexOf(".")
    if (decimalIndex == -1 || digits == 0) {
        Text(text = price, style = baseStyle)
        return
    }

    val beforeDecimal = price.substring(0, decimalIndex + 1)
    val afterDecimal = price.substring(decimalIndex + 1)

    val formatted = buildAnnotatedString {
        append(beforeDecimal)

        when {
            digits < 2 -> append(afterDecimal)

            digits == 2 -> {
                val normal = afterDecimal.dropLast(2)
                val big = afterDecimal.takeLast(2)
                append(normal)
                withStyle(SpanStyle(fontSize = bigFontSize)) {
                    append(big)
                }
            }

            else -> {
                val len = afterDecimal.length
                val normal = afterDecimal.dropLast(3)
                val big = afterDecimal.substring(len - 3, len - 1)
                val superDigit = afterDecimal.last().toString()

                append(normal)
                withStyle(SpanStyle(fontSize = bigFontSize)) {
                    append(big)
                }
                withStyle(
                    SpanStyle(
                        fontSize = smallFontSize,
                        baselineShift = BaselineShift(1.2f)
                    )
                ) {
                    append(superDigit)
                }
            }
        }
    }

    Text(
        text = formatted,
        style = baseStyle,
        maxLines = 1
    )
}