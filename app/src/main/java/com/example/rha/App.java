package com.example.rha;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String FCM_CHANNEL_ID1="Drive";
    public static final String FCM_CHANNEL_ID2="Join";
    public static final String FCM_CHANNEL_ID3="Likes";
    public static final String FCM_CHANNEL_ID4="Comment";
    public static final String FCM_CHANNEL_ID5="Post";
    public static  NotificationManager notificationManager1;
    public static NotificationManager notificationManager2;
    public static NotificationManager notificationManager3;
    public static NotificationManager notificationManager4;
    public static NotificationManager notificationManager5;

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel1=new NotificationChannel(
                    FCM_CHANNEL_ID1,"Drive", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel notificationChannel2=new NotificationChannel(
                    FCM_CHANNEL_ID2,"Join", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel notificationChannel3=new NotificationChannel(
                    FCM_CHANNEL_ID3,"Likes", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel notificationChannel4=new NotificationChannel(
                    FCM_CHANNEL_ID4,"Comment", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel notificationChannel5=new NotificationChannel(
                    FCM_CHANNEL_ID5,"Post", NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager1=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager1.createNotificationChannel(notificationChannel1);

            notificationManager2=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager2.createNotificationChannel(notificationChannel2);

            notificationManager3=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager3.createNotificationChannel(notificationChannel3);

            notificationManager4=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager4.createNotificationChannel(notificationChannel4);

            notificationManager5=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager5.createNotificationChannel(notificationChannel5);

        }

    }
}
