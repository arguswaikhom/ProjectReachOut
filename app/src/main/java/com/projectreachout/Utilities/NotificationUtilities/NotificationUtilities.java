package com.projectreachout.Utilities.NotificationUtilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.projectreachout.MainActivity;
import com.projectreachout.R;

public class NotificationUtilities {
    private static final int NOTIFICATION_ID_NEW_ARTICLE = 1138;
    private static final int NOTIFICATION_ID_NEW_MY_EVENT = 1139;
    private static final String NOTIFICATION_CHANNEL_ID_NEW_ARTICLE = "new_article_notification_channel";
    private static final String NOTIFICATION_CHANNEL_ID_NEW_MY_EVENT = "new_my_event_notification_channel";
    private static final String NOTIFICATION_CHANNEL_NAME_ARTICLES = "Articles";
    private static final String NOTIFICATION_CHANNEL_NAME_EVENTS = "Events";
    private static final int PENDING_INTENT_ID_ARTICLES = 1024;
    private static final int PENDING_INTENT_ID_MY_EVENTS = 2014;

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
                .setContentText(context.getString(R.string.notification_body_new_article))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_body_new_article)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, MainActivity.class, PENDING_INTENT_ID_ARTICLES))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
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
                .setContentText(context.getString(R.string.notification_body_new_event))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_body_new_event)))
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(contentIntent(context, MainActivity.class, PENDING_INTENT_ID_MY_EVENTS));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(NOTIFICATION_ID_NEW_MY_EVENT, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context, Class<?> activityClass, int intentID) {
        Intent startActivityIntent = new Intent(context, activityClass);
        return PendingIntent.getActivity(
                context,
                intentID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
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
