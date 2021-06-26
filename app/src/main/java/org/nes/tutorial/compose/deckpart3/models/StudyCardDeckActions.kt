package org.nes.tutorial.compose.deckpart3.models

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import org.nes.tutorial.compose.common.paddingOffset
import org.nes.tutorial.compose.deckpart3.CardSwipeState
import org.nes.tutorial.compose.deckpart3.animations.CardSwipeAnimation
import org.nes.tutorial.compose.deckpart3.animations.CardsInDeckAnimation
import org.nes.tutorial.compose.deckpart3.animations.FlipCardAnimation

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
        actions = this,
        model = model,
        cardWidth = cardWidth,
        cardHeight = cardHeight
    )
    val flipCard = FlipCardAnimation(peepHandler)
    val cardsInDeck = CardsInDeckAnimation(paddingOffset, model.count)

    @SuppressLint("ModifierFactoryExtensionFunction")
    fun makeCardModifier(
        topCardIndex: Int,
        idx: Int
    ): Modifier {
        return if (idx > topCardIndex) {
            Modifier
                .scale(cardsInDeck.scaleX(idx), 1f)
                .offset { cardsInDeck.offset(idx) }
        } else {
            Modifier
                .scale(flipCard.scaleX(), flipCard.scaleY())
                .offset { cardSwipe.toIntOffset() }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            cardSwipe.animateToTarget(CardSwipeState.DRAGGING) {
                                if (it) {
                                    nextHandler()
                                    flipCard.backToInitState()
                                }
                                cardsInDeck.backToInitState()
                            }
                        },
                        onDrag = { change, _ ->
                            cardSwipe.druggingCard(change) {
                                cardsInDeck.pushBackToTheFront()
                            }
                        }
                    )
                }
        }
    }
}
