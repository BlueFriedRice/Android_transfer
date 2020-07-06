package com.example.transfer.Service;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.example.transfer.Fragment.recentFragment;
import com.example.transfer.MainActivity;

import static android.content.Context.MODE_PRIVATE;

public class NotificationCancel extends BroadcastReceiver {

    private Context mContext;
    public boolean isRunning = false;


    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        recentFragment rf = new recentFragment();

        CountDownTimer timer = rf.timer;

        if (timer != null) {
            try {
                timer.cancel();
                isRunning = true;
                ((MainActivity)MainActivity.mContext).save(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        stopService();
    }

    public void stopService() {

        Intent serviceIntent = new Intent(mContext, ForegroundService.class);
        mContext.stopService(serviceIntent);

    }

}