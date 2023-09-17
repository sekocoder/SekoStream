package com.example.sekostream


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class JoinRoomActivityUiTest{

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(JoinRoomActivity::class.java)

    @Test
    fun testJoinButton(){
        onView(withId(R.id.enterRoomId)).perform(typeText("Chat Lobby"), closeSoftKeyboard())
        onView(withId(R.id.joinRoom)).perform(click())
        onView(withId(R.id.channel_name_display)).check(matches(withText("Chat Lobby")))

    }
}