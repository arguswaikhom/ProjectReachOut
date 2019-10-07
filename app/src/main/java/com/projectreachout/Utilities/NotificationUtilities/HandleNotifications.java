package com.projectreachout.Utilities.NotificationUtilities;

import android.content.Context;

public class HandleNotifications {
    public static final int ACTION_NOTIFICATION_NEW_ARTICLE = 1;
    public static final int ACTION_NOTIFICATION_NEW_MY_EVENT = 2;
    public static final int ACTION_NOTIFICATION_DISMISS_MY_EVENT = 10;
    public static final int ACTION_NOTIFICATION_DISMISS_ARTICLE = 20;

    public static void executeTask(Context context, int action) {
        switch (action) {
            case ACTION_NOTIFICATION_NEW_ARTICLE: {
                NotificationUtilities.clearNewArticleNotifications(context);
                NotificationUtilities.showNewArticleNotification(context);
                break;
            }
            case ACTION_NOTIFICATION_NEW_MY_EVENT: {
                NotificationUtilities.clearNewMyEventNotifications(context);
                NotificationUtilities.showNewMyEventNotification(context);
                break;
            }
            case ACTION_NOTIFICATION_DISMISS_ARTICLE: {
                NotificationUtilities.clearNewArticleNotifications(context);
                break;
            }
            case ACTION_NOTIFICATION_DISMISS_MY_EVENT: {
                NotificationUtilities.clearNewMyEventNotifications(context);
                break;
            }
        }
    }
}
