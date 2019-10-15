package com.inved.go4lunch.controller.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.inved.go4lunch.R;
import com.inved.go4lunch.base.BaseActivity;

import butterknife.BindView;

public class PermissionActivity extends BaseActivity  {

    @BindView(R.id.activity_permission_btn_grant)
    Button btnGrant;

    private static final int PERMS_CALL_ID = 1234;


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_permission;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(PermissionActivity.this, FindMyJobAddressActivity.class));
            finish();
            return;
        }

        btnGrant.setOnClickListener(v -> checkPermissions());
    }

    private void checkPermissions(){
        //We check permission to know if they are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },PERMS_CALL_ID);

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMS_CALL_ID){
            startActivity(new Intent(PermissionActivity.this, FindMyJobAddressActivity.class));
            finish();
        }
    }



}
