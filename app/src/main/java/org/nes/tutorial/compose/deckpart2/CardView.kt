package org.nes.tutorial.compose.deckpart2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import org.nes.tutorial.compose.common.*
import org.nes.tutorial.compose.common.NiceButton
import org.nes.tutorial.compose.common.primaryColor

@Preview
@Composable
fun TestStudyCardFrontView() {
    StudyCardView(
        backgroundColor = primaryColor,
        side = CardFlipState.FRONT_FACE,
        modifier = Modifier.size(cardWidth, cardHeight),
        content = { frontSideColor ->
            StudyCardsContent(
                LOREM_IPSUM_FRONT,
                frontSideColor
            )
        },
        bottomBar = { frontSideColor ->
            StudyCardsBottomBar(
                0, 1, CardFlipState.FRONT_FACE, frontSideColor,
                leftActionHandler = { },
                rightActionHandler = { }
            )
        }
    )
}

@Preview
@Composable
fun TestStudyCardBackView() {
    StudyCardView(
        backgroundColor = primaryColor,
        side = CardFlipState.BACK_FACE,
        modifier = Modifier.size(cardWidth, cardHeight),
        content = { frontSideColor ->
            StudyCardsContent(LOREM_IPSUM_BACK, frontSideColor)
        },
        bottomBar = { frontSideColor ->
            StudyCardsBottomBar(
                index = 0, count = 1, CardFlipState.BACK_FACE, frontSideColor,
                leftActionHandler = { },
                rightActionHandler = { }
            )
        }
    )
}

enum class CardFlipState {
    FRONT_FACE, FLIP_BACK, BACK_FACE, FLIP_FRONT
}

@Composable
fun StudyCardView(
    modifier: Modifier = Modifier,
    side: CardFlipState = CardFlipState.FRONT_FACE,
    backgroundColor: Color = backSideColor,
    content: @Composable (Color) -> Unit,
    bottomBar: @Composable (Color) -> Unit
) {
    val color = if (side == CardFlipState.FRONT_FACE) backgroundColor
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
