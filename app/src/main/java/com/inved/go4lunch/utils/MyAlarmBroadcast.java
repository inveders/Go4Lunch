package com.inved.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.inved.go4lunch.controller.activity.RestaurantActivity;

public class MyAlarmBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction()!=null){
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                App myApp = new App();
                myApp.launchAlarm(context);
            }
        }





    }


}
