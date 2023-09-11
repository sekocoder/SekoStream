package com.example.sekostream

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sekostream.databinding.ActivityJoinRoomBinding

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinRoomBinding
    private lateinit var viewModel: JoinRoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[JoinRoomViewModel::class.java]

        binding.joinRoom.setOnClickListener {

            if (viewModel.checkEmptyChannelName(binding.enterRoomId.text.toString())) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("roomId", binding.enterRoomId.text.toString())
                startActivity(intent)
            } else Toast.makeText(this, "Enter a channel name", Toast.LENGTH_SHORT).show()
        }

        binding.settingButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}