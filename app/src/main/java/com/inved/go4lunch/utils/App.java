package com.inved.go4lunch.utils;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import com.crashlytics.android.Crashlytics;

import java.util.Calendar;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static App mInstance;
    private static Resources res;
    public static final int REQUEST_CODE=101;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Fabric.with(this, new Crashlytics());
        launchAlarm();
        res = getResources();


    }

    public void launchAlarm(){
        Context context=getApplicationContext();
        if(context!=null){
            AlarmManager alarmManager= (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent myItent=new Intent(this, MyAlarmService.class);
            PendingIntent alarmIntent = PendingIntent.getService(this,REQUEST_CODE,myItent,PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 14);
            calendar.set(Calendar.MINUTE, 0);
            //Wake up the device to fire the alarm at approximately 2:00 p.m., and repeat once a day at the same time:

            // Set the alarm to start at approximately 2:00 p.m.
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, alarmIntent);
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, alarmIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, 0, alarmIntent);
                }
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
