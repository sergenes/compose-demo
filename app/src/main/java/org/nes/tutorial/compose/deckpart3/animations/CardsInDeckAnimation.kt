package org.nes.tutorial.compose.deckpart3.animations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset

data class CardsInDeckAnimation(
    val paddingOffset: Float,
    val count: Int = 3
) {
    private lateinit var scaleCards: List<MutableState<Float>>
    private lateinit var offsetCards: List<MutableState<Float>>

    private fun calculateScale(idx: Int): Float {
        return 1f - idx * (1f / 10f)
    }

    private fun calculateOffset(idx: Int): Int {
        return (paddingOffset * (idx + 1)).toInt()
    }

    @Composable
    fun Init() {
        scaleCards = remember {
            List(count) { mutableStateOf(calculateScale(it)) }
        }
        offsetCards = remember {
            List(count) { mutableStateOf(calculateOffset(it).toFloat()) }
        }
    }

    fun scaleX(index: Int): Float {
        return scaleCards[index].value
    }

    fun offset(index: Int): IntOffset {
        return IntOffset(0, offsetCards[index].value.toInt())
    }

    fun backToInitState() {
        repeat(count) {
            scaleCards[it].value = calculateScale(it)
            offsetCards[it].value = calculateOffset(it).toFloat()
        }
    }

    fun pushBackToTheFront() {
        repeat(count) {
            if (it == 0 || it == 1) {
                scaleCards[it].value = calculateScale(0)
                offsetCards[it].value = calculateOffset(0).toFloat()
            } else {
                scaleCards[it].value = calculateScale(it - 1)
                offsetCards[it].value = calculateOffset(it - 1).toFloat()
            }
        }
    }
}
