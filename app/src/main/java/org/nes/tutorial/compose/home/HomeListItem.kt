package org.nes.tutorial.compose.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import org.nes.tutorial.compose.common.deleteIconButtonWidth
import org.nes.tutorial.compose.common.deleteTextButtonWidth
import org.nes.tutorial.compose.common.dividerNormalThickness
import org.nes.tutorial.compose.common.noRadius
import org.nes.tutorial.compose.common.noSpace
import org.nes.tutorial.compose.common.normalRadius
import org.nes.tutorial.compose.common.normalSpace
import org.nes.tutorial.compose.common.primaryColor
import org.nes.tutorial.compose.common.primaryTextColor
import org.nes.tutorial.compose.common.rowHeight
import org.nes.tutorial.compose.common.smallSpace

enum class RowType {
    TOP, MIDDLE, BOTTOM, SINGLE
}

@Preview
@Composable
fun TestSingle() {
    HomeListItem("Item Name", "03/13/21", RowType.SINGLE, false, {}, {})
}

@Preview
@Composable
fun TestTop() {
    HomeListItem("Item Name", "03/13/21", RowType.TOP, false, {}, {})
}

@Preview
@Composable
fun TestMiddle() {
    HomeListItem("Item Name", "03/13/21", RowType.MIDDLE, false, {}, {})
}

@Preview
@Composable
fun TestBottom() {
    HomeListItem("Item Name", "03/13/21", RowType.BOTTOM, false, {}, {})
}

@Preview
@Composable
fun TestBottomEdit() {
    HomeListItem("Item Name", "03/13/21", RowType.BOTTOM, true, {}, {})
}

@Composable
fun HomeListItem(
    title: String,
    subTitle: String,
    type: RowType,
    editMode: Boolean = false,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val shape = when (type) {
        RowType.TOP -> {
            RoundedCornerShape(normalRadius, normalRadius)
        }
        RowType.BOTTOM -> {
            RoundedCornerShape(
                noRadius,
                noRadius,
                normalRadius,
                normalRadius
            )
        }
        RowType.SINGLE -> {
            RoundedCornerShape(normalRadius)
        }
        else -> {
            RoundedCornerShape(noRadius)
        }
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(normalSpace, noSpace, normalSpace, noSpace)
                .fillMaxWidth()
                .height(rowHeight)
                .clip(shape)
                .background(color = primaryColor)
        ) {
            var deleteMode by remember { mutableStateOf(false) }
            if (editMode && !deleteMode) {
                Button(
                    onClick = {
                        deleteMode = !deleteMode
                    },
                    modifier = Modifier
                        .width(deleteIconButtonWidth)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(noRadius),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red,
                        backgroundColor = primaryColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = "delete",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            Column(Modifier.weight(1f)) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(normalSpace)
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.subtitle1,
                        color = primaryTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(smallSpace))
                    Text(
                        subTitle,
                        style = MaterialTheme.typography.subtitle2,
                        color = primaryTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (!editMode && !deleteMode) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "forward",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(Modifier.width(normalRadius))
            }
            if (editMode && deleteMode) {
                Button(
                    onClick = {
                        onDelete()
                        deleteMode = false
                    },
                    modifier = Modifier
                        .width(deleteTextButtonWidth)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(noRadius),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        backgroundColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            }
        }
        if (type != RowType.BOTTOM && type != RowType.SINGLE) {
            Divider(
                modifier = Modifier
                    .padding(normalSpace, noSpace, normalSpace, noSpace),
                color = Color.White,
                thickness = dividerNormalThickness
            )
        }
    }
}
