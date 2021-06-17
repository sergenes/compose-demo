package org.nes.tutorial.compose.deck

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.CoroutineScope
import org.nes.tutorial.compose.common.*
import java.lang.StrictMath.min

data class StudyCardDeckModel(
    var current: Int,
    val dataSource: List<StudyCard>,
    val visible: Int = 3,
    val screenWidth: Int,
    val screenHeight: Int,
    val preview: Boolean = false
) {
    val count = dataSource.size
    val visibleCards: Int = min(visible, dataSource.size - current)
    val colors = arrayOf(primaryColor, secondaryColor, tertiaryColor)

    @Composable
    fun cardWidthPx(): Float {
        return with(LocalDensity.current) { cardWidth.toPx() }
    }

    @Composable
    fun cardHeightPx(): Float {
        return with(LocalDensity.current) { cardHeight.toPx() }
    }
}

data class StudyCardDeckActions(
    val cardWidth: Float,
    val cardHeight: Float,
    val coroutineScope: CoroutineScope,
    val model: StudyCardDeckModel,
    val peepHandler: () -> Unit,
    val playHandler: (String, String) -> Unit,
    val nextHandler: () -> Unit,
    val actionCallback: (String) -> Unit = {}
) {
    val cardSwipe: CardSwipeAnimation = CardSwipeAnimation(
        this,
        model,
        cardWidth,
        cardHeight
    )
    val flipCard = FlipCardAnimation(coroutineScope, peepHandler)
    val cardsInDeck = CardsInDeckAnimation(paddingOffset)
}
