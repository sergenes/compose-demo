package org.nes.tutorial.compose.deck

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.nes.tutorial.compose.common.paddingOffset

const val animationTime = 350

data class CardsInDeckAnimation(
    val paddingOffset: Float
) {
    private lateinit var scaleCards: List<MutableState<Float>>
    private lateinit var offsetCards: List<MutableState<Float>>
    private val initScale = arrayListOf(1.0f, 0.9f, 0.8f)
    private val initOffset = arrayListOf(
        paddingOffset,
        2 * paddingOffset,
        3 * paddingOffset
    )

    @Composable
    fun Init() {
        scaleCards = remember {
            listOf(
                mutableStateOf(initScale[0]),
                mutableStateOf(initScale[1]),
                mutableStateOf(initScale[2])
            )
        }
        offsetCards = remember {
            listOf(
                mutableStateOf(initOffset[0]),
                mutableStateOf(initOffset[1]),
                mutableStateOf(initOffset[2]),

            )
        }
    }

    fun scaleX(index: Int): Float {
        return scaleCards[index].value
    }

    fun offset(index: Int): IntOffset {
        return IntOffset(0, offsetCards[index].value.toInt())
    }

    fun backToInitState() {
        repeat(initScale.size) {
            scaleCards[it].value = initScale[it]
            offsetCards[it].value = initOffset[it]
        }
    }

    fun pushBackToTheFront() {
        scaleCards[0].value = 1f
        scaleCards[1].value = 1.0f
        scaleCards[2].value = 0.9f
        offsetCards[0].value = paddingOffset
        offsetCards[1].value = paddingOffset
        offsetCards[2].value = 2 * paddingOffset
    }
}

data class FlipCardAnimation(
    val coroutineScope: CoroutineScope,
    val peepHandler: () -> Unit
) {
    private lateinit var flipState: MutableState<CardFlipState>
    private lateinit var scaleXAnimation: State<Float>
    private lateinit var scaleYAnimation: State<Float>

    @Composable
    fun Init() {
        flipState = remember { mutableStateOf(CardFlipState.FRONT_FACE) }

        scaleXAnimation = animateFloatAsState(
            if (flipState.value == CardFlipState.FRONT_FACE ||
                flipState.value == CardFlipState.BACK_FACE
            ) 1f else 0.8f,
            animationSpec = animationSpec,
            finishedListener = {
            }
        )
        scaleYAnimation = animateFloatAsState(
            if (flipState.value == CardFlipState.FRONT_FACE ||
                flipState.value == CardFlipState.BACK_FACE
            ) 1f else 0.1f,
            animationSpec = animationSpec,
            finishedListener = {
                coroutineScope.launch {
                    if (flipState.value == CardFlipState.FLIP_BACK) {
                        flipState.value = CardFlipState.BACK_FACE
                    } else if (flipState.value == CardFlipState.FLIP_FRONT) {
                        flipState.value = CardFlipState.FRONT_FACE
                    }
                }
            }
        )
    }

    private val animationSpec: TweenSpec<Float> = tween(
        durationMillis = animationTime,
        easing = LinearEasing
    )

    fun flipToBackSide() {
        flipState.value = CardFlipState.FLIP_BACK
        peepHandler.invoke()
    }

    fun flipToFrontSide() {
        flipState.value = CardFlipState.FLIP_FRONT
    }

    fun backToInitState() {
        flipState.value = CardFlipState.FRONT_FACE
    }

    fun isFrontSide(): Boolean {
        return flipState.value == CardFlipState.FRONT_FACE
    }

    fun scaleX(): Float {
        return scaleXAnimation.value
    }

    fun scaleY(): Float {
        return scaleYAnimation.value
    }

    fun cardSide(): CardFlipState {
        return flipState.value
    }
}

data class CardSwipeAnimation(
    val actions: StudyCardDeckActions,
    val model: StudyCardDeckModel,
    val cardWidth: Float,
    val cardHeight: Float
) {
    private lateinit var cardDragOffset: Animatable<Offset, AnimationVector2D>

    @Composable
    fun InitCardPosition() {
        cardDragOffset = remember {
            Animatable(
                targetValueByState(CardSwipeState.INITIAL),
                Offset.VectorConverter,
            )
        }
    }

    private fun targetValueByState(state: CardSwipeState): Offset {
        return when (state) {
            CardSwipeState.INITIAL -> {
                Offset(0F, paddingOffset)
            }
            CardSwipeState.SWIPED -> {
                Offset(model.screenWidth.toFloat() + cardWidth, paddingOffset)
            }
            else -> {
                swipeDirection()
            }
        }
    }

    private val animationSpec: FiniteAnimationSpec<Offset> = tween(
        durationMillis = animationTime,
        easing = FastOutLinearInEasing
    )

    private fun swipeDirection(): Offset {
        val halfW = model.screenWidth / 2f
        val halfH = model.screenHeight / 2f
        val x = when {
            cardDragOffset.value.x > halfW -> model.screenWidth.toFloat()
            cardDragOffset.value.x + cardWidth < halfW -> -cardWidth
            else -> 0f
        }
        val y = when {
            cardDragOffset.value.y > halfH -> model.screenHeight.toFloat()
            cardDragOffset.value.y + cardHeight < halfH -> -cardHeight
            else -> 0f
        }
        return Offset(x, y)
    }

    fun animateToTarget(state: CardSwipeState, finishedListener: (Boolean) -> Unit) {
        actions.coroutineScope.launch {
            val target = targetValueByState(state)
            cardDragOffset.animateTo(
                targetValue = target,
                animationSpec = animationSpec,
                block = {
                    if (value.x == targetValue.x &&
                        value.y == targetValue.y
                    ) {
                        val next = !(targetValue.x == 0f && targetValue.y == 0f)
                        finishedListener(next)
                    }
                }
            )
        }
    }

    fun toIntOffset(): IntOffset {
        return IntOffset(
            cardDragOffset.value.x.toInt(),
            cardDragOffset.value.y.toInt()
        )
    }

    fun backToInitialState() {
        actions.coroutineScope.launch {
            cardDragOffset.snapTo(targetValueByState(CardSwipeState.INITIAL))
        }
    }

    private fun snapTo(target: Offset) {
        actions.coroutineScope.launch {
            actions.cardSwipe.cardDragOffset.snapTo(target)
        }
    }

    fun druggingCard(change: PointerInputChange, callBack: () -> Unit) {
        if (change.pressed) {
            val original =
                Offset(
                    actions.cardSwipe.cardDragOffset.value.x,
                    actions.cardSwipe.cardDragOffset.value.y
                )
            val summed = original + change.positionChange()
            val newValue = Offset(
                x = summed.x,
                y = summed.y
            )
            change.consumePositionChange()
            actions.cardSwipe.snapTo(Offset(newValue.x, newValue.y))
            callBack()
        }
    }
}
