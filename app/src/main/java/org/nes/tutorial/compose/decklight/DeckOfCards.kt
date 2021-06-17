package org.nes.tutorial.compose.decklight

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import org.nes.tutorial.compose.common.LOREM_IPSUM_BACK
import org.nes.tutorial.compose.common.LOREM_IPSUM_FRONT
import org.nes.tutorial.compose.common.cardHeight
import org.nes.tutorial.compose.common.cardWidth
import org.nes.tutorial.compose.common.paddingOffset
import org.nes.tutorial.compose.common.primaryColor
import org.nes.tutorial.compose.common.secondaryColor
import org.nes.tutorial.compose.common.tertiaryColor
import org.nes.tutorial.compose.deck.CardFlipState
import org.nes.tutorial.compose.deck.StudyCard

data class StudyCard(
    val index: Int,
    val frontVal: String,
    val backVal: String,
    val frontLang: String = "English",
    val backLang: String = "English"
)

val data = arrayListOf(
    StudyCard(0, "1$LOREM_IPSUM_FRONT", "1$LOREM_IPSUM_BACK"),
    StudyCard(1, "2$LOREM_IPSUM_FRONT", "2$LOREM_IPSUM_BACK"),
    StudyCard(2, "3$LOREM_IPSUM_FRONT", "3$LOREM_IPSUM_BACK"),
    StudyCard(3, "4$LOREM_IPSUM_FRONT", "4$LOREM_IPSUM_BACK"),
    StudyCard(4, "5$LOREM_IPSUM_FRONT", "5$LOREM_IPSUM_BACK"),
    StudyCard(5, "6$LOREM_IPSUM_FRONT", "6$LOREM_IPSUM_BACK")
)

val colors = arrayOf(primaryColor, secondaryColor, tertiaryColor)
private val initScale: MutableList<Float> = mutableListOf()
private val initOffset: MutableList<Float> = mutableListOf()

@Preview
@Composable
fun TestStudyCardDeck() {
    StudyCardDeck(0, 3, data)
}

@Composable
fun StudyCardDeck(
    current: Int,
    visible: Int,
    dataSource: List<StudyCard>
) {
    val topCardIdx = 0
    val count = dataSource.size
    val visibleCards: Int = StrictMath.min(visible, dataSource.size - current)

    initScale.clear()
    repeat(visibleCards) {
        initScale.add(1f - it * (1f / 10f))
        initOffset.add(paddingOffset * (it + 1))
    }

    Box(Modifier.fillMaxSize()) {
        repeat(visibleCards) { idx ->
            // index in data source
            val index = current + idx
            val card = dataSource[index]
            val colorIndex = card.index % colors.size
            val cardColor = colors[colorIndex]
            val data = card.frontVal
            val zIndex = 100f - idx
            val scaleX = calculateScale(idx)
            val offsetY = calculateOffset(idx)
            val cardModifier = Modifier
                .scale(scaleX, 1f)
                .offset { IntOffset(0, offsetY) }
                .align(Alignment.TopCenter)
                .zIndex(zIndex)
                .size(cardWidth, cardHeight)

            StudyCardView(
                backgroundColor = cardColor,
                side = CardFlipState.FRONT_FACE,
                modifier = cardModifier,
                content = { frontSideColor ->
                    StudyCardsContent(
                        data, frontSideColor
                    )
                },
                bottomBar = { frontSideColor ->
                    if (idx == topCardIdx) {
                        StudyCardsBottomBar(
                            card.index, count, CardFlipState.FRONT_FACE, frontSideColor,
                            leftActionHandler = { buttonOnSide ->
                            },
                            rightActionHandler = { }
                        )
                    }
                }
            )
        }
    }
}

private fun calculateScale(idx: Int): Float {
    return 1f - idx * (1f / 10f)
}

private fun calculateOffset(idx: Int): Int {
    return (paddingOffset * (idx + 1)).toInt()
}
