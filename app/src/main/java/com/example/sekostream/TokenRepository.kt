package com.example.sekostream

import com.example.sekostream.media.RtcTokenBuilder2


class TokenRepository(private val tokenBuilder: RtcTokenBuilder2) {

    fun getToken(
        appId: String,
        appCertificate: String,
        channelName: String,
        uid: Int,
        timestamp: Int,
    ): String? {

        return tokenBuilder.buildTokenWithUid(appId, appCertificate, channelName, uid,
            RtcTokenBuilder2.Role.ROLE_PUBLISHER, timestamp, timestamp)

    }

}