package com.example.sekostream

import org.junit.*
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


class MainViewModelTest {

    private lateinit var sut: MainViewModel

//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var tokenRepository: TokenRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        sut = MainViewModel(tokenRepository)
    }

    //any does not work properly with kotlin to doing it explicitly
    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    @Test
    fun test_getToken(){

        Mockito.`when`(tokenRepository.getToken(any(String::class.java), any(String::class.java), any(String::class.java),
            any(Int::class.java), any(Int::class.java))).thenReturn("Test Token")

        sut.getToken("Channel Name")
        val result = sut.token
        assertEquals("Test Token",result)
    }

}