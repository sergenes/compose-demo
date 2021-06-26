package org.nes.tutorial.compose.deckpart3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import org.nes.tutorial.compose.common.*
import org.nes.tutorial.compose.common.NiceButton

enum class CardFlipState {
    FRONT_FACE, FLIP_BACK, BACK_FACE, FLIP_FRONT
}

enum class CardSwipeState {
    INITIAL, SWIPED, DRAGGING
}

@Composable
fun StudyCardView(
    modifier: Modifier = Modifier,
    side: CardFlipState = CardFlipState.FRONT_FACE,
    backgroundColor: Color = backSideColor,
    content: @Composable (Color) -> Unit,
    bottomBar: @Composable (Color) -> Unit
) {
    val color = if (side == CardFlipState.FRONT_FACE ||
        side == CardFlipState.FLIP_BACK
    ) backgroundColor
    else backSideColor
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadiusBig),
        color = color,
        elevation = normalElevation,
        content = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {},
                bottomBar = { bottomBar(backgroundColor) },
                content = { content(color) }
            )
        }
    )
}

@Composable
fun StudyCardsContent(data: String, backgroundColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(backgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = data,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(normalSpace),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StudyCardsBottomBar(
    index: Int,
    count: Int,
    side: CardFlipState = CardFlipState.FRONT_FACE,
    frontSideColor: Color,
    leftActionHandler: (CardFlipState) -> Unit = {},
    rightActionHandler: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(smallSpace)
    ) {
        val buttonColor = if (side == CardFlipState.FRONT_FACE) backSideColor else frontSideColor
        val leftTitle = if (side == CardFlipState.FRONT_FACE) "Peep" else "Back"
        val rightTitle = "Say"
        NiceButton(
            title = leftTitle,
            backgroundColor = buttonColor,
            onClick = { leftActionHandler.invoke(side) }
        )
        Spacer(Modifier.weight(1f))
        Text("${index + 1} of $count")
        Spacer(Modifier.weight(1f))
        NiceButton(
            title = rightTitle,
            backgroundColor = buttonColor,
            onClick = { rightActionHandler.invoke() }
        )
    }
}
