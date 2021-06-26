package org.nes.tutorial.compose.deckpart3.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import org.nes.tutorial.compose.common.*
import org.nes.tutorial.compose.deckpart3.StudyCard

data class StudyCardDeckModel(
    var current: Int,
    val dataSource: List<StudyCard>,
    val visible: Int = 3,
    val screenWidth: Int,
    val screenHeight: Int,
    val preview: Boolean = false
) {
    private val colors = arrayOf(primaryColor, secondaryColor, tertiaryColor)
    val count = dataSource.size
    val visibleCards: Int = StrictMath.min(visible, dataSource.size - current)

    fun card(visibleIndex: Int) = dataSource[dataSourceIndex(visibleIndex)]

    private fun dataSourceIndex(visibleIndex: Int): Int {
        return current + visibleIndex
    }

    fun colorForIndex(visibleIndex: Int): Color {
        val index = dataSourceIndex(visibleIndex)
        val colorIndex = index % colors.size
        return colors[colorIndex]
    }

    @Composable
    fun cardWidthPx(): Float {
        return with(LocalDensity.current) { cardWidth.toPx() }
    }

    @Composable
    fun cardHeightPx(): Float {
        return with(LocalDensity.current) { cardHeight.toPx() }
    }
}