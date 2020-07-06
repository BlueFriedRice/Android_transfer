package com.example.transfer.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.transfer.MainActivity;
import com.example.transfer.R;

public class ForegroundService extends Service {
    String CHANNEL_ID = "";
    String input, TagID;
    NotificationCompat.Builder notification;
    NotificationChannel serviceChannel;
    NotificationManager manager;

    public static int id2 = (int) System.currentTimeMillis();
    int REQUEST_CODE = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        input = intent.getStringExtra("time");
        TagID = intent.getStringExtra("TagID");

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("TagID2", TagID);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                REQUEST_CODE, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIntent = new Intent(this, NotificationCancel.class);

        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                REQUEST_CODE, broadcastIntent, 0);


        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        notification.setContentTitle("환승가능시간")
                .setContentText(input)
                .setSmallIcon(R.drawable.seoul_icon_png)
//                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .addAction(R.drawable.ic_launcher_background, "앱으로 이동", pendingIntent)
                .addAction(R.drawable.ic_launcher_background, "알림종료", actionIntent)
                .build();


        startForeground(id2, notification.build());

        //Second
        notification.setContentText(input);
        startForeground(id2, notification.build());

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CHANNEL_ID = "notification";

            serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }

        }
    }


}


