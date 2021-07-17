package org.nes.tutorial.compose.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import org.nes.tutorial.compose.list.ListScreenState.*


@Preview
@Composable
fun TestStateMachine1() {
    val stateMachine = ScreenModel()
    stateMachine.Init()

    Column {
        Text(" ${stateMachine.state().print()}")
    }

}

@Preview
@Composable
fun TestStateMachine2() {
    val stateMachine = ScreenModel()
    stateMachine.Init()


//    Row {
//        Column {
//            Text(" ${stateMachine.state().print()}")
//        }
//    }
}

@Preview
@Composable
fun TestStateMachine3() {
    val stateMachine = ScreenModel()
    stateMachine.Init()
    //stateMachine.onEvent(event = OnAddNewRowChanged)
    Column {
        Text(" ${stateMachine.state().print()}")
    }

}

interface ListScreenContract {
    val editButtonTitle: String
    val isAddButtonVisible: Boolean
    val isTitleVisible: Boolean
    val isTableVisible: Boolean

    val back: ListScreenActions
    val forward: ListScreenActions

    fun print() =
        "${javaClass.simpleName} [$editButtonTitle|$isAddButtonVisible|$isTitleVisible|$isTableVisible]"
}

sealed class ListScreenState : ListScreenContract {
    override val editButtonTitle = "Edit"
    override val isAddButtonVisible = true
    override val isTitleVisible = true
    override val isTableVisible = true

    override val back: ListScreenActions = ListScreenActions.OnDone(Idle)
    override val forward: ListScreenActions = ListScreenActions.OnSave(Idle)

    class LanguageChanged(source: ListScreenState) : ListScreenState() {
        override val editButtonTitle = "Save"
        override val isAddButtonVisible = source.isAddButtonVisible
        override val isTitleVisible = source.isTitleVisible
        override val isTableVisible = source.isTableVisible
    }

    object Idle : ListScreenState()
    object NameEdit : ListScreenState() {
        override val editButtonTitle = "Done"
        override val isAddButtonVisible = false
        override val isTitleVisible = false

        override val forward = ListScreenActions.OnNameChanged
    }

    object NameChanged : ListScreenState() {
        override val editButtonTitle = "Save"
        override val isAddButtonVisible = false
        override val isTitleVisible = false

        override val back = ListScreenActions.OnNameEdit
    }

    object RowEdit : ListScreenState() {
        override val editButtonTitle = "Done"
        override val isAddButtonVisible = false
        override val isTableVisible = false

        override val forward = ListScreenActions.OnSelectedRowChanged
    }

    object RowNew : ListScreenState() {
        override val editButtonTitle = "Done"
        override val isAddButtonVisible = false
        override val isTableVisible = false

        override val forward = ListScreenActions.OnAddNewRowChanged
    }

    object RowEditChanged : ListScreenState() {
        override val editButtonTitle = "Save"
        override val isAddButtonVisible = false
        override val isTableVisible = false

        override val back = ListScreenActions.OnSelectRow
    }

    object RowNewChanged : ListScreenState() {
        override val editButtonTitle = "Save"
        override val isAddButtonVisible = false
        override val isTableVisible = false

        override val back = ListScreenActions.OnAddNewRow
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

    private fun onAction(action: ListScreenActions, sideEffect: () -> Unit = {}) {
        if (state() == action.from) {
            screenState.value = action.to
            sideEffect()
        }
    }

    fun editName() {
        onAction(ListScreenActions.OnNameEdit)
    }

    fun editNameChanged() {
        onAction(ListScreenActions.OnNameChanged)
    }

    fun addNewRow(sideEffect: () -> Unit) {
        onAction(ListScreenActions.OnAddNewRow) {
            sideEffect()
        }
    }

    fun editNewRow() {
        onAction(ListScreenActions.OnAddNewRowChanged)
    }

    fun selectedRow(sideEffect: () -> Unit) {
        onAction(ListScreenActions.OnSelectRow) {
            sideEffect()
        }
    }

    fun editSelectedRow() {
        onAction(ListScreenActions.OnSelectedRowChanged)
    }

    fun done() {
        onAction(ListScreenActions.OnDone(state()))
    }

    fun save() {
        onAction(ListScreenActions.OnSave(state()))
    }

    fun languageChange() {
        onAction(ListScreenActions.OnLanguageChanged(state()))
    }

    fun backToPrevState() {
        onAction(state().back)
    }

    fun state(): ListScreenState = screenState.value
}