package org.nes.tutorial.compose.list

import androidx.compose.ui.test.junit4.createComposeRule
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test

class ScreenModelTest : TestCase() {

    private val screenModel = ScreenModel()

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInit() {
        composeTestRule.setContent {
            screenModel.Init()
        }
    }
}