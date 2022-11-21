package org.nes.tutorial.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nes.tutorial.compose.common.NiceButton

@Composable
fun NiceEditableHeader(
    title: String?,
    screenMode: ListScreenState,
    buttonTitle: String,
    background: Color = Color.White,
    rightClickHandler: () -> Unit,
    editHandler: (String) -> Unit,
    doneHandler: (String) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(background)
            .padding(0.dp, 16.dp, 16.dp, 16.dp)
            .height(64.dp)
    ) {
        Spacer(Modifier.width(16.dp))
        title?.let {
            if (!screenMode.isEditNameMode) {
                Text(
                    it,
                    style = MaterialTheme.typography.h5,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.Start)
                        .align(CenterVertically),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                val textFieldState = remember { mutableStateOf(TextFieldValue(title)) }
                TextField(
                    value = textFieldState.value,
                    singleLine = true,
                    onValueChange = { textFieldValue ->
                        if (textFieldState.value != textFieldValue) {
                            textFieldState.value = textFieldValue
                            editHandler.invoke(textFieldState.value.text)
                        }
                    },
                    modifier = Modifier.background(color = Color.White),
                    placeholder = {
                        Text(
                            text = "Deck name",
                            textAlign = TextAlign.Center,
                            color = Color.LightGray,
                        )
                    },
                    textStyle = TextStyle(
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            doneHandler.invoke(textFieldState.value.text)
                        })
                )
            }
        }
        if (screenMode.isAddButtonVisible) {
            NiceButton(buttonTitle, onClick = rightClickHandler)
        }
    }
}

@Composable
fun StudyCardTableRow(
    editMode: Boolean,
    height: Int,
    index: Int = -1,
    padding: Int = 16,
    selectedIndex: Int = -1,
    contentLeft: @Composable () -> Unit,
    contentRight: @Composable () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .height(height.dp)
    ) {
        var deleteMode by remember { mutableStateOf(false) }
        val selected = (selectedIndex > -1 && selectedIndex == index)
        val backgroundLeft = if (selected) Color.White else Color.Gray
        val backgroundRight = if (selected) Color.White else Color.LightGray
        if (editMode && !deleteMode) {
            Button(
                onClick = {
                    deleteMode = !deleteMode
                }, modifier = Modifier.width(60.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(0),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red,
                    backgroundColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "delete",
                    modifier = Modifier
                        .align(CenterVertically)
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(backgroundLeft)
                .align(alignment = Alignment.CenterVertically)
        ) {
            val paddingLeft = if (editMode) {
                0.dp
            } else {
                padding.dp
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(paddingLeft, 0.dp, padding.dp, 0.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                contentLeft()
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .background(color = backgroundRight)
                .fillMaxHeight()
        ) {
            val paddingRight = if (deleteMode && editMode) {
                0.dp
            } else {
                padding.dp
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(padding.dp, 0.dp, paddingRight, 0.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                contentRight()
            }
        }
        if (deleteMode && editMode) {
            Button(
                onClick = {
                    deleteMode = false
                    onDelete.invoke()
                },
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(0),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    backgroundColor = Color.Red
                )
            ) {
                Text("Delete")
            }
        } else {
            deleteMode = false
        }
    }
}