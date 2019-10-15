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






    @Override
    public void onReceive(Context context, Intent intent) {

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            UserHelper.updateRestaurantName(null, FirebaseAuth.getInstance().getCurrentUser().getUid());

        }





    }


}
