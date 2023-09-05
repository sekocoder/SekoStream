package com.example.sekostream

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.example.sekostream.MainActivity.Companion.ANY
import com.example.sekostream.MainActivity.Companion.WIFI
import com.example.sekostream.MainActivity.Companion.sPref

class NetworkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = conn.activeNetworkInfo

        // Checks the user prefs and the network connection. Based on the result, decides whether
        // to refresh the display or keep the current display.
        // If the user preference is Wi-Fi only, checks to see if the device has a Wi-Fi connection.

//        if (WIFI == sPref && networkInfo?.type == ConnectivityManager.TYPE_WIFI) {
//            // If device has its Wi-Fi connection, sets refreshDisplay to true
//            refreshDisplay = true
//            Toast.makeText(context, "wifi connected", Toast.LENGTH_SHORT).show()
//
//        }
//
//        else if (networkInfo != null) {
//
//            if (ANY == sPref) {
//
//                // If the setting is ANY network and there is a network connection
//                // (which by process of elimination would be mobile), sets refreshDisplay to true.
//                refreshDisplay = true
//            } else {
//                //The pref setting is WIFI, and there is no Wi-Fi connection. Sets refreshDisplay to false.
//                refreshDisplay = true
//                Toast.makeText(context, "wifi not connected", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            // There is no network connection (mobile or Wi-Fi)
//            // Sets refreshDisplay to false.
//            refreshDisplay = false
//            Toast.makeText(context, "No Connection", Toast.LENGTH_SHORT).show()
//        }


    }
}