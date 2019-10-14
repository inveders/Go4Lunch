package com.inved.go4lunch.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.inved.go4lunch.firebase.UserHelper;

import static android.content.Context.ALARM_SERVICE;

public class MyAlarm extends BroadcastReceiver {

    private Context myContext = App.getInstance().getApplicationContext();


    public static final int REQUEST_CODE=101;



    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager alarmManager= (AlarmManager) myContext.getSystemService(ALARM_SERVICE);
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_SHORT).show();
        Log.e("debago","we are in alarmManager");
        Intent myItent=new Intent(context, MyAlarm.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,REQUEST_CODE,myItent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Wake up the device to fire the alarm at approximately 2:00 p.m., and repeat once a day at the same time:

        // Set the alarm to start at approximately 2:00 p.m.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 14);

            // With setInexactRepeating(), you have to use one of the MyAlarm interval
            // constants--in this case, MyAlarm.INTERVAL_DAY.
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), alarmIntent);
            }


        }

        UserHelper.updateRestaurantName(null, FirebaseAuth.getInstance().getCurrentUser().getUid(),ManageJobPlaceId.getJobPlaceId(context));

    }


}
