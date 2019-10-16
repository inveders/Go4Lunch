package com.inved.go4lunch.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import com.inved.go4lunch.R;
import com.inved.go4lunch.controller.activity.RestaurantActivity;
import com.inved.go4lunch.model.ResultModel;

public class CheckDistanceFromWork {

    private Context context = App.getInstance().getApplicationContext();
    private Dialog mDialog;
    private RestaurantActivity restaurantActivity = new RestaurantActivity();
    private ResultModel resultModel;

    public CheckDistanceFromWork(Context context) {
        resultModel = ViewModelProviders.of(restaurantActivity).get(ResultModel.class);
    }

    //HANDLING MODE

    //Handle two modes for user : work mode and normal mode to be located everywhere
    public void checkDistanceFromWork(String origins,String destinations,Double lat, Double longi){

        //Log.d("debago","origins :"+origins+" et destination :"+destinations);
        resultModel.getMatrixDistance(origins,destinations).observe((LifecycleOwner) context, result -> {

            int distance;
            String appMode = ManageAppMode.getAppMode(context);
            try {
                distance = (result.get(0).getElements().get(0).getDistance().getValue())/1000;
            }catch (Exception e){
                Log.e("error","Error try catch " + e.getMessage());
                distance=3;

            }




            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    // Add the buttons
                    .setPositiveButton(R.string.alert_dialog_location_far_from_works_pos_button, (dialog, which) -> {
                        // do something like...
                        changeMode(false,lat,longi);
                    })
                    .setNegativeButton(R.string.alert_dialog_location_far_from_works_neg_button, (dialog, which) -> {
                        // do something like...
                        changeMode(true,lat,longi);
                    })
                    .setMessage(R.string.alert_dialog_location_far_from_works_text);

            // Create the AlertDialog
            // AlertDialog dialog = builder.create();

            if(distance>2 && appMode.equals(App.getResourses().getString(R.string.app_mode_work))){

                // Dismiss any old dialog.
                if (mDialog != null) {
                    mDialog.dismiss();
                }

                // Show the new dialog.
                mDialog = builder.show();

            }
            else if(distance<2 && appMode.equals(App.getResourses().getString(R.string.app_mode_normal))){
                changeMode(true,lat,longi);

            }
            else if(distance<2 && appMode.equals(App.getResourses().getString(R.string.app_mode_forced_work))){
                changeMode(true,lat,longi);

            }

        });

    }

    private void changeMode(boolean workModeDesired, Double lat, Double longi) {
        if(workModeDesired){
            if(ManageAppMode.getAppMode(context).equals(App.getResourses().getString(R.string.app_mode_work))){
                ManageAppMode.saveAppMode(context,App.getResourses().getString(R.string.app_mode_forced_work));
            }else{
                ManageAppMode.saveAppMode(context,App.getResourses().getString(R.string.app_mode_work));
            }

            Toast.makeText(context, App.getResourses().getString(R.string.app_mode_change_to_work_mode), Toast.LENGTH_SHORT).show();

        }else{
            ManageAppMode.saveAppMode(context,App.getResourses().getString(R.string.app_mode_normal));
            Toast.makeText(context, App.getResourses().getString(R.string.app_mode_change_to_normal_mode), Toast.LENGTH_SHORT).show();
        }
        restaurantActivity.fillFirebase(lat,longi);
    }
}
