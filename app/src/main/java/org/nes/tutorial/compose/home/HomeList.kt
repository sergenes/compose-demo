package org.nes.tutorial.compose.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nes.tutorial.compose.common.NiceButton

data class ListFile(var fileName: String, var created: String)

@Preview
@Composable
fun ListTest() {
    var editMode by remember { mutableStateOf(false) }
    val data = remember {
        mutableStateListOf<ListFile>(
            ListFile("Test 1", "11/06/2021"),
            ListFile("Test 2", "11/07/2021"),
            ListFile("Test 3", "11/08/2021"),
            ListFile("Test 4", "11/09/2021")
        )
    }
    Scaffold(
        topBar = {
            val title = if (editMode) "Cancel" else "Edit"
            Column {
                Row(Modifier.padding(16.dp)) {
                    NiceButton(title = title) {
                        editMode = !editMode
                    }
                    Spacer(Modifier.weight(1f))
                    NiceButton(title = "Add") {
                        data.add(ListFile("Test ${data.size + 1}", "14/06/2021"))
                    }
                }
            }
        }
    ) {
        HomeListLazy(editMode = editMode, itemsSource = data, clickItemHandler = {}) { index ->
            data.removeAt(index)
        }
    }
}

@Composable
fun HomeList(
    editMode: Boolean,
    items: SnapshotStateList<ListFile>,
    clickItemHandler: (Int) -> Unit,
    deleteItemHandler: (Int) -> Unit
) {
    Column {
        when (items.size) {
            1 -> items.first().let {
                HomeListItem(
                    it.fileName, it.created, RowType.SINGLE, editMode,
                    { clickItemHandler(0) },
                    { deleteItemHandler(0) }
                )
            }
            else -> {
                items.forEachIndexed { index, data ->
                    val rowType = when (index) {
                        0 -> RowType.TOP
                        items.lastIndex -> RowType.BOTTOM
                        else -> RowType.MIDDLE
                    }
                    HomeListItem(
                        data.fileName, data.created, rowType, editMode,
                        { clickItemHandler(index) },
                        { deleteItemHandler(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeListLazy(
    editMode: Boolean,
    itemsSource: SnapshotStateList<ListFile>,
    clickItemHandler: (Int) -> Unit,
    deleteItemHandler: (Int) -> Unit
) {
    LazyColumn {
        when (itemsSource.size) {
            1 -> itemsSource.first().let {
                item {
                    HomeListItem(
                        it.fileName, it.created, RowType.SINGLE, editMode,
                        { clickItemHandler(0) },
                        { deleteItemHandler(0) }
                    )
                }
            }
            else -> {
                itemsIndexed(itemsSource) { index, item ->
                    val rowType = when (index) {
                        0 -> RowType.TOP
                        itemsSource.lastIndex -> RowType.BOTTOM
                        else -> RowType.MIDDLE
                    }
                    HomeListItem(
                        item.fileName, item.created, rowType, editMode,
                        { clickItemHandler(index) },
                        { deleteItemHandler(index) }
                    )
                }
            }
        }
    }
}
