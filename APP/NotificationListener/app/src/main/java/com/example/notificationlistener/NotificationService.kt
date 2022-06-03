package com.example.notificationlistener

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


class NotificationService : NotificationListenerService() {
    private object ApplicationPackageNames {
        const val FACEBOOK_PACK_NAME = "com.facebook.katana"
        const val FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca"
        const val WHATSAPP_PACK_NAME = "com.whatsapp"
        const val INSTAGRAM_PACK_NAME = "com.instagram.android"
    }
    object InterceptedNotificationCode {
        const val FACEBOOK_CODE = 1
        const val WHATSAPP_CODE = 2
        const val INSTAGRAM_CODE = 3
        const val OTHER_NOTIFICATIONS_CODE = 4 // We ignore all notification with code == 4
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    fun acquireContent(sbn: StatusBarNotification?) : String{
        return sbn!!.notification.extras.getString(Notification.EXTRA_TEXT, "")
    }

    fun acquireTitle(sbn: StatusBarNotification?) : String{
        return sbn!!.notification.extras.getString(Notification.EXTRA_TITLE, "")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val intent = Intent("com.example.notificationlistener")

        Log.d("RECV:content", acquireContent(sbn))
        Log.d("RECV:title", acquireTitle(sbn))
        intent.putExtra("Notification Context", acquireContent(sbn))
        sendBroadcast(intent)
        Log.d("RECV", "Notification Posted")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        val notificationCode: Int = 1
        Log.d("REMV", "Notification Removed")
        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
            val activeNotifications = this.activeNotifications
            if (activeNotifications != null && activeNotifications.isNotEmpty()) {
                for (i in activeNotifications.indices) {
                    Log.d("REMV", i.toString())
                    if (notificationCode == 1) {
                        val intent = Intent("com.example.notificationlistener")
                        intent.putExtra("Notification Code", notificationCode)
                        Log.d("REMV", activeNotifications[i].notification.toString())
                        sendBroadcast(intent)
                        break
                    }
                }
            }
        }
    }
    private fun matchNotificationCode(sbn: StatusBarNotification?) {
        val packageName = sbn?.packageName
    }
}