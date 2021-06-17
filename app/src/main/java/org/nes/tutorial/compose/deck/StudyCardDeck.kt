package org.nes.tutorial.compose.deck

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import org.nes.tutorial.compose.common.*
import org.nes.tutorial.compose.common.NiceButton

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
    val coroutineScope = rememberCoroutineScope()
    var topCardIndex by remember { mutableStateOf(0) }
    val model = StudyCardDeckModel(
        current = topCardIndex,
        dataSource = data,
        visible = 3,
        screenWidth = 1200,
        screenHeight = 1600,
        preview = true
    )
    val actions = StudyCardDeckActions(
        cardWidth = model.cardWidthPx(),
        cardHeight = model.cardHeightPx(),
        coroutineScope = coroutineScope,
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

    Column {
        NiceButton(title = "Test Swipe") {
            actions.cardSwipe.animateToTarget(CardSwipeState.SWIPED) {
                if (topCardIndex < data.lastIndex) {
                    topCardIndex += 1
                } else {
                    topCardIndex = 0
                }
            }
        }
        StudyCardDeck(model, actions)
    }
}

@Composable
fun StudyCardDeck(
    model: StudyCardDeckModel,
    actions: StudyCardDeckActions
) {
    val topCardIdx = 0
    actions.apply {
        flipCard.Init()
        cardsInDeck.Init()
        cardSwipe.InitCardPosition()
    }
    Box(Modifier.fillMaxSize()) {
        repeat(model.visibleCards) { idx ->
            // index in data source
            val index = model.current + idx
            val card = model.dataSource[index]
            val colorIndex = card.index % model.colors.size
            val cardColor = model.colors[colorIndex]
            val isFront = actions.flipCard.isFrontSide()
            val data = if (isFront) card.frontVal else card.backVal
            val locale = if (isFront) card.frontLang else card.backLang
            val side = if (idx > topCardIdx) CardFlipState.FRONT_FACE
            else actions.flipCard.cardSide()
            val zIndex = 100f - idx
            val cardModifier = makeCardModifier(topCardIdx, idx, actions)
                .align(Alignment.TopCenter)
                .zIndex(zIndex)
                .size(cardWidth, cardHeight)

            StudyCardView(
                backgroundColor = cardColor, side = side,
                modifier = cardModifier,
                content = { frontSideColor ->
                    StudyCardsContent(
                        data, frontSideColor
                    )
                },
                bottomBar = { frontSideColor ->
                    if (idx == topCardIdx) {
                        StudyCardsBottomBar(
                            card.index, model.count, side, frontSideColor,
                            leftActionHandler = { buttonOnSide ->
                                if (buttonOnSide == CardFlipState.FRONT_FACE) {
                                    actions.flipCard.flipToBackSide()
                                } else {
                                    actions.flipCard.flipToFrontSide()
                                }
                            },
                            rightActionHandler = { actions.playHandler.invoke(data, locale) }
                        )
                    }
                }
            )
            actions.cardSwipe.backToInitialState()
        }
    }
}

fun makeCardModifier(
    topCardIdx: Int,
    idx: Int,
    actions: StudyCardDeckActions
): Modifier {
    return if (idx > topCardIdx) {
        Modifier
            .scale(actions.cardsInDeck.scaleX(idx), 1f)
            .offset { actions.cardsInDeck.offset(idx) }
    } else {
        Modifier
            .scale(actions.flipCard.scaleX(), actions.flipCard.scaleY())
            .offset { actions.cardSwipe.toIntOffset() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        actions.run {
                            cardSwipe.animateToTarget(CardSwipeState.DRAGGING) {
                                if (it) {
                                    nextHandler()
                                    flipCard.backToInitState()
                                }
                                cardsInDeck.backToInitState()
                            }
                        }
                    },
                    onDrag = { change, _ ->
                        actions.run {
                            cardSwipe.druggingCard(change) {
                                cardsInDeck.pushBackToTheFront()
                            }
                        }
                    }
                )
            }
    }
}
