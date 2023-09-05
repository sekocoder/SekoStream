package com.example.sekostream

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.example.sekostream.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null


    // The BroadcastReceiver that tracks network connectivity changes.
    // private lateinit var receiver: NetworkReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val tokenBuilderInstance = RtcTokenBuilder2()
        //val repository = TokenRepository(tokenBuilderInstance)
        // viewModel = ViewModelProvider(this,ViewModelFactory(repository)).get(MainViewModel::class.java)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Registers BroadcastReceiver to track network connection changes.
//        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        receiver = NetworkReceiver()
//        this.registerReceiver(receiver, filter)

        viewModel.channelName = intent.getStringExtra("roomId").toString()

        if (!checkSelfPermission()) {
            //if not granted then this if will work to ask for those permissions
            ActivityCompat.requestPermissions(this, viewModel.requestedPermissions, viewModel.permissionReqId);
        }

        viewModel.getToken(viewModel.channelName!!)

        setupVideoSDKEngine()

        joinCall()


        viewModel.connectivityObserver = NetworkConnectivityObserver(applicationContext)

            viewModel.connectivityObserver.observe().onEach{

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    updateConnectedFlags()
                    loadPage()
                }, 500)

            }.launchIn(lifecycleScope)

        binding.LeaveButton.setOnClickListener {
            leaveCall()
        }
    }

    public override fun onStart() {
        super.onStart()

        // Gets the user's network preference settings
        viewModel.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = viewModel.sharedPrefs.getString("listPref", "Any")

        updateConnectedFlags()

        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connMgr.apply {
            // Checks if the device is on a metered network
            if (isActiveNetworkMetered) {
                // Checks user’s Data Saver settings.
                when (restrictBackgroundStatus) {
                    RESTRICT_BACKGROUND_STATUS_ENABLED -> {
                        viewModel.dataSaverInfo = "active"
                        Toast.makeText(this@MainActivity, "data saver active", Toast.LENGTH_SHORT)
                            .show()
                    }
                    RESTRICT_BACKGROUND_STATUS_WHITELISTED -> {
                        viewModel.dataSaverInfo = "inactive"
                    }
                      RESTRICT_BACKGROUND_STATUS_DISABLED -> {
                        viewModel.dataSaverInfo = "inactive"
                        Toast.makeText(this@MainActivity, "data saver inactive", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        if(!mobileConnected && !wifiConnected) leaveCall()

    }

    //function for checking if both record audio and camera permission granted then will return true
    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            applicationContext,
            viewModel.requestedPermissions[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    viewModel.requestedPermissions[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }


    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private fun updateConnectedFlags() {

        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        viewModel.activeInfo = connMgr.activeNetworkInfo
        if (viewModel.activeInfo?.isConnected == true) {
            wifiConnected = viewModel.activeInfo!!.type == ConnectivityManager.TYPE_WIFI
            mobileConnected = viewModel.activeInfo!!.type == ConnectivityManager.TYPE_MOBILE
        } else {
            wifiConnected = false
            mobileConnected = false
        }
    }

    private fun loadPage() {

        if(viewModel.remoteJoined){

            if ((sPref == ANY && (wifiConnected || mobileConnected)) || (sPref == WIFI && wifiConnected)) {

                if (wifiConnected || (viewModel.dataSaverInfo == "inactive")) {

                    viewModel.agoraEngine?.setRemoteVideoStreamType(viewModel.fetchedUid, Constants.VIDEO_STREAM_HIGH);
                    Toast.makeText(this, "high performance mode", Toast.LENGTH_SHORT).show()

                } else {

                    viewModel.agoraEngine?.setRemoteVideoStreamType(viewModel.fetchedUid, Constants.VIDEO_STREAM_LOW);
                    Toast.makeText(this, "data saving mode", Toast.LENGTH_SHORT).show()
                }

            }

            else if (mobileConnected) {

                viewModel.agoraEngine?.setRemoteVideoStreamType(viewModel.fetchedUid, Constants.VIDEO_STREAM_LOW);
                Toast.makeText(this, "low performance mode", Toast.LENGTH_SHORT).show()
            }

            else leaveCall()

        }

    }

    companion object {

        const val WIFI = "Wi-Fi"
        const val ANY = "Any"

        // Whether there is a Wi-Fi connection.
        private var wifiConnected = false

        // Whether there is a mobile connection.
        private var mobileConnected = false

        // The user's current network preference setting.
        var sPref: String? = null

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroyEngine()
        // Unregisters BroadcastReceiver when app is destroyed.
        // this.unregisterReceiver(receiver)

    }


    fun showMessage(message: String?) {
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupVideoSDKEngine() {
        try {
            viewModel.config.mContext = baseContext
            viewModel.setupVideoSDKEngine(mRtcEventHandler)
        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }

    private fun setupRemoteVideo(uid: Int) { //for remote user
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteSurfaceView)
        viewModel.agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                uid
            )
        )
        remoteSurfaceView!!.visibility = View.VISIBLE
    }

    private fun setupLocalVideo() { //for local user
        localSurfaceView = SurfaceView(baseContext)
        binding.localVideoViewContainer.addView(localSurfaceView)
        viewModel.agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    private fun joinCall() {
        if (checkSelfPermission()) {
            viewModel.joinCallInitials()
            setupLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            viewModel.joinCallFinal(viewModel.channelName!!)
        } else {
            Toast.makeText(applicationContext, "Permissions were not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun leaveCall() {
        viewModel.leaveCall()
        showMessage("You left the channel")
        if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
        if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
        onBackPressed()
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")
            // Set the remote video view
            viewModel.fetchedUid = uid
            viewModel.remoteJoined = true
            runOnUiThread { setupRemoteVideo(uid) }
            loadPage()
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Remote user offline $uid $reason")
            viewModel.remoteJoined = false
            runOnUiThread { remoteSurfaceView!!.visibility = View.GONE }
        }
    }

}