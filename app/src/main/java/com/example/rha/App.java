package com.example.rha;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String FCM_CHANNEL_ID1="Drive";
    public static final String FCM_CHANNEL_ID2="Join";
    public static  NotificationManager notificationManager1;
    public static NotificationManager notificationManager2;


    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel1=new NotificationChannel(
                    FCM_CHANNEL_ID1,"Channel 1", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationChannel notificationChannel2=new NotificationChannel(
                    FCM_CHANNEL_ID2,"Channel 2", NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager1=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager1.createNotificationChannel(notificationChannel1);

            notificationManager2=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager2.createNotificationChannel(notificationChannel2);

        }

    }
}
