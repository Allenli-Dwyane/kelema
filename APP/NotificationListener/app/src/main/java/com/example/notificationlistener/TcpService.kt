package com.example.notificationlistener

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.core.app.NotificationCompat
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class TcpService : Service() {
    private lateinit var sendMessageReceiver : SendMessageReceiver
    lateinit var handlerThread : TcpClientHandler
    private var serverSocket: ServerSocket? = null
    private val working = AtomicBoolean(true)
    private val runnable = Runnable {
        var socket: Socket? = null
        try {
            serverSocket = ServerSocket(PORT)
            while (working.get()) {
                if (serverSocket != null) {
                    socket = serverSocket!!.accept()
                    Log.i(TAG, "New client: $socket")
                    val dataInputStream = DataInputStream(socket.getInputStream())
                    val dataOutputStream = DataOutputStream(socket.getOutputStream())

                    // Use threads for each client to communicate with them simultaneously
                    Thread {
                        try {
                            dataOutputStream.writeUTF("Connection established!")
                            Log.d("SEND", "Written in Stream")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                    sendMessageReceiver.setIOStream(dataInputStream, dataOutputStream)
                    Log.i(TAG, "after t started!")
                } else {
                    Log.e(TAG, "Couldn't create ServerSocket!")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            try {
                socket?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }

    override fun onCreate() {
        startForeground()
        Thread(runnable).start()
        sendMessageReceiver = SendMessageReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.notificationlistener")
        registerReceiver(sendMessageReceiver, intentFilter)
    }
    override fun onDestroy() {
        super.onDestroy()
        working.set(false)
        unregisterReceiver(sendMessageReceiver)
    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = packageName
            val channelName = "Tcp Server Background Service"
            val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Tcp Server is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
        } else {
            startForeground(1, Notification())
        }
    }

    class SendMessageReceiver() : BroadcastReceiver() {
        var socketBuild : Boolean = false

        private lateinit var dataOutputStream : DataOutputStream;
        private lateinit var dataInputStream : DataInputStream;

        fun setIOStream(socketDataInputStream: DataInputStream, socketDataOutputStream: DataOutputStream) {
            dataInputStream = socketDataInputStream
            dataOutputStream = socketDataOutputStream
            socketBuild = true
        }
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d("ONRE", "notification received")
            val message = p1?.extras?.getString("Notification Context")
            if (message != null) {
                Log.d("ONRE", message)
                if (socketBuild) {
                    Log.d("SEND", message)
                    Thread {
                        try {
                            dataOutputStream.writeUTF(message)
                            Log.d("SEND", "Written in Stream")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                }
            }
        }

    }
    companion object {
        private val TAG = TcpService::class.java.simpleName
        private const val PORT = 9876
    }
}