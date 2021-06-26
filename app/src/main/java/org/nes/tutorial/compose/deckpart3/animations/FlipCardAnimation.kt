package org.nes.tutorial.compose.deckpart3.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import org.nes.tutorial.compose.common.animationTime
import org.nes.tutorial.compose.deckpart3.CardFlipState

data class FlipCardAnimation(
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
                if (flipState.value == CardFlipState.FLIP_BACK) {
                    flipState.value = CardFlipState.BACK_FACE
                } else if (flipState.value == CardFlipState.FLIP_FRONT) {
                    flipState.value = CardFlipState.FRONT_FACE
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