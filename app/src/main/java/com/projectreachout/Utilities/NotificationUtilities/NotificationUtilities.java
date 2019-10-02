package com.projectreachout.Utilities.NotificationUtilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.projectreachout.R;

public class NotificationUtilities {
    private static final int NOTIFICATION_ID_NEW_ARTICLE = 1138;
    private static final int NOTIFICATION_ID_NEW_MY_EVENT = 1139;
    private static final String NOTIFICATION_CHANNEL_ID_NEW_ARTICLE = "new_article_notification_channel";
    private static final String NOTIFICATION_CHANNEL_ID_NEW_MY_EVENT = "new_my_event_notification_channel";
    private static final String NOTIFICATION_CHANNEL_NAME_ARTICLES = "Articles";
    private static final String NOTIFICATION_CHANNEL_NAME_EVENTS = "Events";

    public static void showNewArticleNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_NEW_ARTICLE,
                    NOTIFICATION_CHANNEL_NAME_ARTICLES,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_NEW_ARTICLE)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher_foreground_pro_image)
                .setContentTitle(context.getString(R.string.notification_title_new_article))
                //.setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_body_new_article)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                /*.setContentIntent(contentIntent(context))
                .addAction(drinkWaterAction(context))
                .addAction(ignoreReminderAction(context))*/
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(NOTIFICATION_ID_NEW_ARTICLE, notificationBuilder.build());
    }

    public static void showNewMyEventNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID_NEW_MY_EVENT,
                    NOTIFICATION_CHANNEL_NAME_EVENTS,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_NEW_MY_EVENT)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher_foreground_pro_image)
                .setContentTitle(context.getString(R.string.notification_title_new_event))
                //.setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_body_new_event)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                /*.setContentIntent(contentIntent(context))
                .addAction(drinkWaterAction(context))
                .addAction(ignoreReminderAction(context))*/
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(NOTIFICATION_ID_NEW_MY_EVENT, notificationBuilder.build());
    }

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void clearNewArticleNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID_NEW_ARTICLE);
    }

    public static void clearNewMyEventNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID_NEW_MY_EVENT);
    }
}
