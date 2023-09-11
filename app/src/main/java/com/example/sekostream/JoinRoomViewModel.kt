package com.example.sekostream

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class JoinRoomViewModel :ViewModel() {

    fun checkEmptyChannelName(channelName : String) : Boolean{
        return (channelName.isNotBlank())
    }
}


