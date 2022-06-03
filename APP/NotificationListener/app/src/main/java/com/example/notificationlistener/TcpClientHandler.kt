package com.example.notificationlistener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.HandlerThread
import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

class TcpClientHandler(private val dataInputStream: DataInputStream, private val dataOutputStream: DataOutputStream) : Thread("TcpHandler") {
    private var isStarted : Boolean = false;
    override fun run() {
//        while (true) {
//            try {
//                Log.d(TAG, dataInputStream.available().toString())
//                if(dataInputStream.available() > 0){
//                    Log.i(TAG, "Received: " + dataInputStream.read())
//                    dataOutputStream.writeUTF("Hello Client")
//                    sleep(2000L)
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//                try {
//                    dataInputStream.close()
//                    dataOutputStream.close()
//                } catch (ex: IOException) {
//                    ex.printStackTrace()
//                }
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//                try {
//                    dataInputStream.close()
//                    dataOutputStream.close()
//                } catch (ex: IOException) {
//                    ex.printStackTrace()
//                }
//            }
//        }
    }
    public fun send_message(message : String) {
        if (isStarted)
            dataOutputStream.writeUTF(message)
    }
    companion object {
        private val TAG = TcpClientHandler::class.java.simpleName
    }

}