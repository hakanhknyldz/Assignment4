package com.example.dilkom_hak.assignment4;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HAKKE";
    private Button btnMap;
    private Switch switchNotification;

    NotificationManager notificationManager;
    boolean isNotificActive = false;
    int NotifID = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //starting broadcast receiver
        GPSReceiver gpsReceiver = new GPSReceiver();
        IntentFilter intentFilter = new IntentFilter("detect_location_status");
        registerReceiver(gpsReceiver, intentFilter);


        //starting service..
        Intent intent = new Intent(getBaseContext(), GPSService.class);
        startService(intent);

        setup();


        if (!isNetworkAvailable()) {
            //Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle("Closing the App");
            builder.setMessage("No Internet Connection,check your settings");
            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }

            })
                    .show();


        } else {
            if (!checkGPS()) // gps is closing!
            {
                openGPS();
            } else {
                Toast.makeText(this, "Internet And GPS is Activated ;)", Toast.LENGTH_SHORT).show();
                // ACTIVITY ÇALIŞMAYA HAZIR DURUMDA..


            }

        }

    }

    private void openGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "For using the application, you must open GPS!";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        });

        builder.create().show();
    }

    private boolean checkGPS() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "Current GPS status : " + status);
        return status;
    }

    private void setup() {
        btnMap = (Button) findViewById(R.id.btnOpenMap);
        btnMap.setOnClickListener(this);

        switchNotification = (Switch) findViewById(R.id.switchNotification);

        switchNotification.setChecked(false);
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)  //TRUE - NOTIFICATION ACTIVATED!
                {
                    switchNotification.setText("Switch is currently ON");

                    createNotification();
                }
                else //notification inactive!
                {
                    cancelNotification();
                    switchNotification.setText("Switch is currently OFF");

                }
            }
        });
    }

    private void cancelNotification() {
        if(isNotificActive)
        {
            notificationManager.cancel(NotifID);
        }
    }

    private void createNotification()
    {
        android.support.v4.app.NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Are you around?")
                .setContentText("Do not forget to visit us!")
                .setTicker("Assignment 4")
                .setSmallIcon(R.drawable.ic_alert_map);

        Intent goIntent = new Intent(this,MapsActivity.class);


        TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);

        tStackBuilder.addParentStack(MapsActivity.class);

        tStackBuilder.addNextIntent(goIntent);

        PendingIntent pendingIntent = tStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        notificBuilder.setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NotifID,notificBuilder.build());

        isNotificActive = true;
    }

    public void SetAlarm()
    {
        Long alertTime = new GregorianCalendar().getTimeInMillis() + 5 * 1000;

        Intent alertIntent = new Intent(this,GPSReceiver.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime , PendingIntent.getBroadcast(this,1,alertIntent,PendingIntent.FLAG_UPDATE_CURRENT));



    }



    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btnOpenMap:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            default:
                break;
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getApplication().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
