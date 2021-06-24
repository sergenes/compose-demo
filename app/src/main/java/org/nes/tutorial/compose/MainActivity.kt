package org.nes.tutorial.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.nes.tutorial.compose.common.NiceButton
import org.nes.tutorial.compose.home.HomeList
import org.nes.tutorial.compose.home.ListFile
import org.nes.tutorial.compose.theme.ComposeDemoTheme

class MainActivity : ComponentActivity() {
    private val data = mutableStateListOf<ListFile>(
        ListFile("Test 1", "14/06/2021"),
        ListFile("Test 2", "14/06/2021"),
        ListFile("Test 3", "14/06/2021"),
        ListFile("Test 4", "14/06/2021")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    var editMode by remember { mutableStateOf(false) }
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
//                        StudyCardDeck(0, 3, org.nes.tutorial.compose.decklight.data)
                        HomeList(
                            editMode, data, {},
                            { index ->
                                data.removeAt(index)
                            }
                        )
                    }
                }
            }
        }
    }
}
