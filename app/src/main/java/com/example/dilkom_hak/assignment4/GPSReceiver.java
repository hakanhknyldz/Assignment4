package com.example.dilkom_hak.assignment4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean status = intent.getBooleanExtra("check_status",false);
        //if status is true , notification manager is started..
        //because, we are closing to target(this homework its Iki Eylul)
    }
}
