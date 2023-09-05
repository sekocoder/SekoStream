package com.example.sekostream

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.NetworkInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.agora.rtc2.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
) : ViewModel() {

    val permissionReqId = 15
    val requestedPermissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
    var activeInfo: NetworkInfo? = null
    lateinit var sharedPrefs: SharedPreferences
    lateinit var connectivityObserver: ConnectivityObserver
    var expirationTimeInSeconds = 100
    private val appId = "91846962361747a78118c68ad3357f32"
    var appCertificate = "c361280e836047dd99208c2611899c76"
    private var token: String? = null
    private val uid = 0
    var agoraEngine: RtcEngine? = null
    private var options = ChannelMediaOptions()
    val config = RtcEngineConfig()
    var remoteJoined: Boolean = false
    var fetchedUid: Int = 100
    var dataSaverInfo: String = "inactive"
    var channelName: String? = null


    fun getToken(channelName: String) {
        val timestamp = (System.currentTimeMillis() / 1000 + expirationTimeInSeconds).toInt()
        token = tokenRepository.getToken(appId, appCertificate, channelName, uid, timestamp)
    }

    fun destroyEngine() {
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        // Destroy the engine in a sub-thread to avoid congestion
        Thread { //can also use coroutines but thread simpler
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    fun setupVideoSDKEngine(mRtcEventHandler: IRtcEngineEventHandler) {
        config.mAppId = appId
        config.mEventHandler = mRtcEventHandler
        agoraEngine = RtcEngine.create(config)
        // By default, the video module is disabled, call enableVideo to enable it.
        agoraEngine!!.enableVideo()
        agoraEngine!!.enableDualStreamMode(true)
    }

    fun joinCallInitials() {
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
    }

    fun joinCallFinal(channelName: String) {
        agoraEngine!!.startPreview()
        agoraEngine!!.joinChannel(token, channelName, uid, options)
    }

    fun leaveCall() {
        agoraEngine!!.leaveChannel()
    }

}