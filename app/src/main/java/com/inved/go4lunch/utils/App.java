package com.inved.go4lunch.utils;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class App extends Application {

    private static App mInstance;
    private static Resources res;
    public static final int REQUEST_CODE=101;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        res = getResources();
        launchAlarm();


    }

    public void launchAlarm(){
        AlarmManager alarmManager= (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Toast.makeText(getApplicationContext(), "Alarm Triggered", Toast.LENGTH_SHORT).show();
        Log.e("debago","we are in alarmManager");
        Intent myItent=new Intent(getApplicationContext(), MyAlarm.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(),REQUEST_CODE,myItent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Wake up the device to fire the alarm at approximately 2:00 p.m., and repeat once a day at the same time:

        // Set the alarm to start at approximately 2:00 p.m.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 21); /**Remettre 14 après*/

            // With setInexactRepeating(), you have to use one of the MyAlarm interval
            // constants--in this case, MyAlarm.INTERVAL_DAY.
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), alarmIntent);
            }


        }
    }

    public static App getInstance() {
        return mInstance;
    }

    public static Resources getResourses() {
        return res;
    }
}
