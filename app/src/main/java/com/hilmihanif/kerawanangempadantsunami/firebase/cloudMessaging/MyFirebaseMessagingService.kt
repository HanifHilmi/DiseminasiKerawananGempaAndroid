package com.hilmihanif.kerawanangempadantsunami.firebase.cloudMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hilmihanif.kerawanangempadantsunami.MainActivity
import com.hilmihanif.kerawanangempadantsunami.R

const val channelId = "notification_gempa"
const val channelName = "com.hilmihanif.kerawanangempadantsunami"

class MyFirebaseMessagingService :FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification != null){
            generateNotification(
                title = remoteMessage.notification!!.title!!,
                message = remoteMessage.notification!!.body!!,
                image = remoteMessage.notification!!.imageUrl ?: Uri.EMPTY
            )
        }
    }

    fun generateNotification(title:String,message:String,image:Uri){
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.img_earthquake)
            .setContentTitle(title)
            .setContentText(message)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        notificationManager.notify(0,builder.build())
    }


}