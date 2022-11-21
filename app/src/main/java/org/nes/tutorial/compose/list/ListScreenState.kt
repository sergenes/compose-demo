package org.nes.tutorial.compose.list

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.nes.tutorial.compose.common.NiceButton
import org.nes.tutorial.compose.list.ListScreenState.*

val stateMachine = ScreenModel()

@Preview
@Composable
fun TestStateMachine1() {
    stateMachine.Init()

    var originalName = "The Name"
    var deckName by remember { mutableStateOf(originalName) }
    var selectedIndex by remember { mutableStateOf(-1) }

    Scaffold(
        topBar = {
            val title = stateMachine.state().editButtonTitle
            Column {
                Row(Modifier.padding(16.dp, 16.dp, 16.dp, 0.dp)) {
                    Spacer(Modifier.weight(1f))
                    NiceButton(title = title) {
                        when {
                            stateMachine.state() == Idle -> {
                                stateMachine.editName()
                            }
                            stateMachine.state() == NameChanged -> {
                                stateMachine.save {
                                    originalName = deckName
                                    selectedIndex = -1
                                }
                            }
                            else -> {
                                selectedIndex = -1
                                stateMachine.done()
                            }
                        }
                    }
                }
            }
        }
    ) {
        Column {
            NiceEditableHeader(
                deckName,
                stateMachine.state(),
                "Add",
                rightClickHandler = {
                    stateMachine.addNewRow { }
                }, editHandler = {
                    if (it == deckName && stateMachine.state() == NameChanged) {
                        stateMachine.backToPrevState()
                    } else {
                        deckName = it
                        stateMachine.editNameChanged()
                    }
                }, doneHandler = {

                    stateMachine.save {
                        originalName = it
                    }
                })

            Column {
                StudyCardTableRow(
                    editMode = stateMachine.state().isEditNameMode,
                    height = 84,
                    index = 0,
                    selectedIndex = selectedIndex,
                    contentLeft = {
                        Text(
                            "Test",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }, contentRight = {
                        Text(
                            "Test",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        stateMachine.selectedRow {
                            selectedIndex = 0
                        }
                    },
                    onDelete = {})
                Divider(
                    color = Color.LightGray,
                    thickness = 0.5.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
sealed class ListScreenState(
    val back: ListScreenState? = Idle
) {
    open val editButtonTitle = "Edit"
    open val isAddButtonVisible = true
    open val isEditNameMode = false
    open val isTableVisible= true

    class LanguageChanged(source: ListScreenState) :
        ListScreenState(Idle) {
        override val editButtonTitle = "Save"
        override val isAddButtonVisible = source.isAddButtonVisible
        override val isEditNameMode = source.isEditNameMode
        override val isTableVisible = source.isTableVisible
    }

    object Idle : ListScreenState()
    object NameEdit : ListScreenState() {
        override val editButtonTitle = "Done"
        override val isAddButtonVisible = false
        override val isEditNameMode = true
    }

    object NameChanged : ListScreenState(NameEdit) {
        override val editButtonTitle = "Save"
        override val isAddButtonVisible = false
        override val isEditNameMode = true
    }

    object RowEdit : ListScreenState() {
        override val editButtonTitle = "Done"
        override val isAddButtonVisible = false
        override val isTableVisible = false
    }

    object RowNew : ListScreenState() {
        override val editButtonTitle = "Done"
        override val isAddButtonVisible = false
        override val isTableVisible = false
    }

    object RowEditChanged : ListScreenState(RowEdit) {
        override val editButtonTitle = "Save"
        override val isAddButtonVisible = false
        override val isTableVisible = false
    }

    object RowNewChanged : ListScreenState(RowNew) {
        override val editButtonTitle = "Save"
        override val isAddButtonVisible = false
        override val isTableVisible = false
    }
}


sealed class ListScreenActions(val from: ListScreenState, val to: ListScreenState) {
    object OnNameEdit : ListScreenActions(Idle, NameEdit)
    object OnNameChanged : ListScreenActions(NameEdit, NameChanged)
    object OnAddNewRow : ListScreenActions(Idle, RowNew)
    object OnAddNewRowChanged : ListScreenActions(RowNew, RowNewChanged)
    object OnSelectRow : ListScreenActions(Idle, RowEdit)
    object OnSelectedRowChanged : ListScreenActions(RowEdit, RowEditChanged)

    class OnLanguageChanged(fromState: ListScreenState) :
        ListScreenActions(fromState, LanguageChanged(fromState))

    class OnDone(fromState: ListScreenState) : ListScreenActions(fromState, Idle)
    class OnSave(fromState: ListScreenState) : ListScreenActions(fromState, Idle)
}

class ScreenModel {
    private lateinit var screenState: MutableState<ListScreenState>

    @Composable
    fun Init(state: ListScreenState = Idle) {
        screenState = remember {
            mutableStateOf(state)
        }
    }

    private fun onAction(
        action: ListScreenActions,
        runBefore: () -> Unit = {},
        runAfter: () -> Unit = {}
    ) {
        if (state() == action.from) {
            runBefore()
            screenState.value = action.to
            runAfter()
        }
    }

    fun editName() {
        onAction(ListScreenActions.OnNameEdit)
    }

    fun editNameChanged() {
        onAction(ListScreenActions.OnNameChanged)
    }

    fun addNewRow(runAfter: () -> Unit) {
        onAction(ListScreenActions.OnAddNewRow, runAfter = runAfter)
    }

    fun editNewRow() {
        onAction(ListScreenActions.OnAddNewRowChanged)
    }

    fun selectedRow(runAfter: () -> Unit) {
        onAction(ListScreenActions.OnSelectRow, runAfter = runAfter)
    }

    fun editSelectedRow() {
        onAction(ListScreenActions.OnSelectedRowChanged)
    }

    fun done() {
        onAction(ListScreenActions.OnDone(state()))
    }

    fun save(runBefore: () -> Unit) {
        onAction(ListScreenActions.OnSave(state()), runBefore = runBefore)
    }

    fun languageChange() {
        onAction(ListScreenActions.OnLanguageChanged(state()))
    }

    fun backToPrevState() {
        state().back?.let {
            screenState.value = it
        }
    }

    fun state(): ListScreenState = screenState.value
}