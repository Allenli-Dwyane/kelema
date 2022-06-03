package com.example.notificationlistener

import android.content.*
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
    private val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
    private lateinit var interceptedNotificationImageView : ImageView
//    private lateinit var sendMessageReceiver : SendMessageReceiver
    private lateinit var enableNotificationAlertDialog : AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get permission for notification
        if (!isNotificationServiceEnabled()) {
            enableNotificationAlertDialog = buildNotificationAlertDialog()
            enableNotificationAlertDialog.show()
            Log.d("Deb", "Done enable")
        }

        // Start socket handler
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(applicationContext, TcpService::class.java))
        } else {
            startService(Intent(applicationContext, TcpService::class.java))
        }

        // show ip
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = "IP: " + Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        val textView : TextView = findViewById<TextView>(R.id.textView)
        textView.setText(ipAddress)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(applicationContext, TcpService::class.java))
    }

    private fun isNotificationServiceEnabled() : Boolean {
        val pkgName = packageName
//        Log.d("Deb", pkgName)
        val flat = Settings.Secure.getString(contentResolver, ENABLED_NOTIFICATION_LISTENERS)
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":")
            for (item in names) {
                val unflattenName = ComponentName.unflattenFromString(item)
                if (unflattenName != null)
                    Log.d("Debug", unflattenName.packageName)
                if (unflattenName != null && TextUtils.equals(pkgName, unflattenName.packageName)) {
                    return true
                }
            }
        }
        return false
    }
    private fun buildNotificationAlertDialog() : AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.notification_listener_service)
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation)
        alertDialogBuilder.setPositiveButton(R.string.yes,
            DialogInterface.OnClickListener { dialog, id ->
                startActivity(
                    Intent(
                        ACTION_NOTIFICATION_LISTENER_SETTINGS
                    )
                )
            })
        alertDialogBuilder.setNegativeButton(R.string.no,
            DialogInterface.OnClickListener { dialog, id ->
                // If you choose to not enable the notification listener
                // the app. will not work as expected
            })
        return alertDialogBuilder.create()
    }
}