package org.nes.tutorial.compose.deckpart3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import org.nes.tutorial.compose.common.*
import org.nes.tutorial.compose.common.NiceButton
import org.nes.tutorial.compose.deckpart3.models.StudyCardDeckEvents
import org.nes.tutorial.compose.deckpart3.models.StudyCardDeckModel

data class StudyCard(
    val index: Int,
    val frontVal: String,
    val backVal: String,
    val frontLang: String = "English",
    val backLang: String = "English"
)

@Preview
@Composable
fun TestStudyCardView() {
    val data = arrayListOf(
        StudyCard(0, "1$LOREM_IPSUM_FRONT", "1$LOREM_IPSUM_BACK"),
        StudyCard(1, "2$LOREM_IPSUM_FRONT", "2$LOREM_IPSUM_BACK"),
        StudyCard(2, "3$LOREM_IPSUM_FRONT", "3$LOREM_IPSUM_BACK"),
        StudyCard(3, "4$LOREM_IPSUM_FRONT", "4$LOREM_IPSUM_BACK"),
        StudyCard(4, "5$LOREM_IPSUM_FRONT", "5$LOREM_IPSUM_BACK"),
        StudyCard(5, "6$LOREM_IPSUM_FRONT", "6$LOREM_IPSUM_BACK")
    )

    var topCardIndex by remember { mutableStateOf(0) }
    val model = StudyCardDeckModel(
        current = topCardIndex,
        dataSource = data,
        visible = 3,
        screenWidth = 1200,
        screenHeight = 1600
    )
    val events = StudyCardDeckEvents(
        cardWidth = model.cardWidthPx(),
        cardHeight = model.cardHeightPx(),
        model = model,
        peepHandler = {},
        playHandler = { _, _ ->
        },
        nextHandler = {
            if (topCardIndex < data.lastIndex) {
                topCardIndex += 1
            } else {
                topCardIndex = 0
            }
        }
    )
    val coroutineScope = rememberCoroutineScope()

    Column {
        NiceButton(title = "Test Swipe") {
            events.cardSwipe.animateToTarget(
                coroutineScope,
                CardSwipeState.SWIPED
            ) {
                if (topCardIndex < data.lastIndex) {
                    topCardIndex += 1
                } else {
                    topCardIndex = 0
                }
            }
        }
        StudyCardDeck(coroutineScope, model, events)
    }
}

private const val TOP_CARD_INDEX = 0
private const val TOP_Z_INDEX = 100f

@Composable
fun StudyCardDeck(
    coroutineScope: CoroutineScope,
    model: StudyCardDeckModel,
    events: StudyCardDeckEvents
) {

    events.apply {
        flipCard.Init()
        cardsInDeck.Init()
        cardSwipe.Init()
    }
    Box(Modifier.fillMaxSize()) {
        repeat(model.visibleCards) { visibleIndex ->
            val isFront = events.flipCard.isFrontSide()
            val card = model.cardVisible(visibleIndex)
            val cardColor = model.colorForIndex(visibleIndex)
            val cardData = if (isFront) card.frontVal else card.backVal
            val cardLanguage = if (isFront) card.frontLang else card.backLang
            val cardSide = if (visibleIndex > TOP_CARD_INDEX) {
                CardFlipState.FRONT_FACE
            } else {
                events.flipCard.cardSide()
            }
            val cardZIndex = TOP_Z_INDEX - visibleIndex
            val cardModifier = events.makeCardModifier(
                coroutineScope,
                TOP_CARD_INDEX,
                visibleIndex)
                .align(Alignment.TopCenter)
                .zIndex(cardZIndex)
                .size(cardWidth, cardHeight)

            StudyCardView(
                backgroundColor = cardColor,
                side = cardSide,
                modifier = cardModifier,
                content = { frontSideColor ->
                    StudyCardsContent(
                        cardData, frontSideColor
                    )
                },
                bottomBar = { frontSideColor ->
                    if (visibleIndex == TOP_CARD_INDEX) {
                        StudyCardsBottomBar(
                           index = card.index,
                            count = model.count,
                            side = cardSide,
                            frontSideColor = frontSideColor,
                            leftActionHandler = { buttonOnSide ->
                                if (buttonOnSide == CardFlipState.FRONT_FACE) {
                                    events.flipCard.flipToBackSide()
                                } else {
                                    events.flipCard.flipToFrontSide()
                                }
                            },
                            rightActionHandler = {
                                events.playHandler.invoke(cardData, cardLanguage)
                            }
                        )
                    }
                }
            )
            events.cardSwipe.backToInitialState(coroutineScope)
        }
    }
}