package com.example.sekostream

import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value= Parameterized::class)
class JoinRoomViewModelTest(private val input: String, private val expectedValue: Boolean) {

    lateinit var viewModelInstance: JoinRoomViewModel

    @Before
    fun setUp() {
        //Arrange
        viewModelInstance = JoinRoomViewModel()
    }

    @Test
    fun checkEmptyChannelName() {
        //Act
        val result = viewModelInstance.checkEmptyChannelName(input)
        //Assert
        assertEquals(expectedValue, result)

    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "{index} : {0} is valid name - {1}")
        fun data(): List<Array<Any>> {
            return listOf(
                arrayOf("", false),
                arrayOf("Friend Call", true),
                arrayOf(" ", false)
            )
        }
    }
}