package com.inved.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAlarmBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("debago","in alarm receiver");

        if(intent.getAction()!=null){
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                App myApp = new App();
                myApp.launchAlarm();
            }
        }





    }


}
