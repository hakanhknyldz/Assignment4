package com.example.dilkom_hak.assignment4;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class GPSReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("HAKKE","GPS Receiver active");
        boolean status = intent.getBooleanExtra("check_status",false);
        //if status is true , notification manager is started..
        //because, we are closing to target(in this homework its Iki Eylul)

        if(status)
        {
            createNotification(context,"Are you around?", "Do not forget to visit us!","Assignment 4");
        }

    }

    private void createNotification(Context context, String msg, String msgText, String msgAlert)
    {
        PendingIntent notificIntent = PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0);

        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_alert_map)
                .setContentTitle(msg)
                .setContentText(msgText)
                .setTicker(msgAlert);

        mBuilder.setContentIntent(notificIntent);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());

    }
}
