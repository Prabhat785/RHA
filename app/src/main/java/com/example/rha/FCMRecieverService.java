package com.example.rha;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;
import java.util.UUID;

import static com.example.rha.App.FCM_CHANNEL_ID1;
import static com.example.rha.App.FCM_CHANNEL_ID2;
import static com.example.rha.App.notificationManager1;
import static com.example.rha.App.notificationManager2;

public class FCMRecieverService extends FirebaseMessagingService {
    private DatabaseReference userref,tokenref,Driveref;
    String currentuserid;

    private FirebaseAuth mAuth;
    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        final String notificationtype=remoteMessage.getData().get("notificationType");
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(notificationtype.equals("DriveNotification")){
                    String pid=remoteMessage.getData().get("Sender");
                    String NOTIFICATION_TITLE=remoteMessage.getData().get("pTitle");
                    String NOTIFICATION_MESSAGE=remoteMessage.getData().get("pDescription");
                        showpostnotificationdrive(pid,NOTIFICATION_TITLE,NOTIFICATION_MESSAGE);

                }else if(notificationtype.equals("JoinNotification")){
                    String pid=remoteMessage.getData().get("Sender");
                    String NOTIFICATION_TITLE=remoteMessage.getData().get("pTitle");
                    String PostKey=remoteMessage.getData().get("Postkey");
                    String NOTIFICATION_MESSAGE=remoteMessage.getData().get("pDescription");
                    showpostnotificationjoin(pid,NOTIFICATION_TITLE,NOTIFICATION_MESSAGE,PostKey);
                }
                else if(notificationtype.equals("EndDriveNotification")){
                    String pid=remoteMessage.getData().get("Sender");
                    String NOTIFICATION_TITLE=remoteMessage.getData().get("pTitle");
                    String PostKey=remoteMessage.getData().get("Postkey");
                    String NOTIFICATION_MESSAGE=remoteMessage.getData().get("pDescription");
                    showpostnotificationend(pid,NOTIFICATION_TITLE,NOTIFICATION_MESSAGE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(remoteMessage.getNotification()!=null){
            String title=remoteMessage.getNotification().getTitle();
            String body=remoteMessage.getNotification().getBody();
            Notification notification=new NotificationCompat.Builder(this,FCM_CHANNEL_ID1).setSmallIcon(R.drawable.rha).
                    setContentTitle(title).setContentText(body).setColor(Color.GREEN).build();
            NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1002,notification);
        }
        if(remoteMessage.getData()!=null){

        }

    }

    private void showpostnotificationdrive(String pid, String notification_title, String notification_message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int NotificationID=new Random().nextInt(100000);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    FCM_CHANNEL_ID1, "Drive Notification", NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(notificationChannel);
            Intent intent=new Intent(this,MainActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this, FCM_CHANNEL_ID1).setSmallIcon(R.drawable.rha).
                    setContentTitle(notification_title).setContentText(notification_message).setColor(Color.GREEN).setContentIntent(pendingIntent);
            notificationManager.notify(1002,notificationBuilder.build());
        }
    }
    private void showpostnotificationend(String pid, String notification_title, String notification_message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int NotificationID=new Random().nextInt(100000);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    FCM_CHANNEL_ID1, "Drive Notification", NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(notificationChannel);
            Intent intent=new Intent(this,MainActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this, FCM_CHANNEL_ID1).setSmallIcon(R.drawable.rha).
                    setContentTitle(notification_title).setContentText(notification_message).setColor(Color.GREEN).setContentIntent(pendingIntent);
            notificationManager.notify(1002,notificationBuilder.build());
        }
    }
    private void showpostnotificationjoin(String pid, String notification_title, String notification_message,String Postkey) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int NotificationID=new Random().nextInt(100000);

            Intent intent=new Intent(this,Driveview.class);
            intent.putExtra("Postkey",Postkey);
            PendingIntent pendingIntent=PendingIntent.getActivity(this, UUID.randomUUID().hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this, FCM_CHANNEL_ID2).setSmallIcon(R.drawable.rha).
                    setContentTitle(notification_title).setContentText(notification_message).setColor(Color.GREEN).setContentIntent(pendingIntent);
            notificationManager2.notify(1002,notificationBuilder.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NonNull final String s) {
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(currentuserid);
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String chapter=dataSnapshot.child("Chapter").getValue().toString();
                tokenref= FirebaseDatabase.getInstance().getReference().child("Tokens").child(chapter);
                tokenref.child(currentuserid).setValue(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onNewToken(s);
    }
}
