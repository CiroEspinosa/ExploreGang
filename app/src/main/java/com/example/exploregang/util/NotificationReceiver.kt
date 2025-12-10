package com.example.exploregang.util


import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.exploregang.R
import com.example.exploregang.data.model.Actividad
import com.example.exploregang.ui.MainActivity
import com.example.exploregang.util.Constants.collectionActivity
import com.example.exploregang.util.NotificationUtils.CHANNEL_ID

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationType = intent.getStringExtra(Constants.notificationType)
        val activity = intent.getParcelableExtra<Actividad>(collectionActivity)!! as Actividad

        if (notificationType == "before") {
            val notificationTitle = context.resources.getString(R.string.notification_title_before) + activity.name
            val notificationMessage = context.resources.getString(R.string.notification_message_before)


            val notificationIntent = Intent(context, MainActivity::class.java)
            notificationIntent.putExtra(collectionActivity, activity)
            notificationIntent.action = Constants.ACTION_SHOW_ACTIVITY_DETAIL

            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(notificationIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }


            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.activities2)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_logo))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(0, notificationBuilder.build())

        } else if (notificationType == "exact") {
            val notificationTitle = context.resources.getString(R.string.notification_title_before) + activity.name
            val notificationMessage = context.resources.getString(R.string.notification_message_exact)

            val notificationIntent = Intent(context, MainActivity::class.java)
            notificationIntent.putExtra(collectionActivity, activity)
            notificationIntent.action = Constants.ACTION_SHOW_ACTIVITY_DETAIL
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(notificationIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.activities2)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_logo))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1, notificationBuilder.build())
        }
    }
}
