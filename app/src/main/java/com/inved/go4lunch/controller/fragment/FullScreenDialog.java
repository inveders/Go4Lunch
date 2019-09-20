package com.inved.go4lunch.controller.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.inved.go4lunch.R;
import com.inved.go4lunch.auth.ProfileActivity;

public class FullScreenDialog extends DialogFragment implements View.OnClickListener {


    private SeekBar distanceSeekbar;
    private SeekBar workmatesInSeekbar;
    private Switch openForLunch;
    private RatingBar ratingBar;
    private TextView distanceValue;
    private TextView restaurantCustomerValue;

    private boolean openForLunchChoice;
    private int ratingChoice;
    private Double distanceChoice;
    private int restaurantCustomersChoice;


    public static final String TAG = "CREATE_DIALOG";
    private Callback callback;
    private Dialog dialog;

    static FullScreenDialog newInstance() {
        return new FullScreenDialog();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.layout_full_screen_dialog, container, false);
        ImageButton close = mView.findViewById(R.id.fullscreen_dialog_close);
        TextView action = mView.findViewById(R.id.fullscreen_dialog_action);
        distanceSeekbar = mView.findViewById(R.id.dialog_seekbar_distance);
        openForLunch = mView.findViewById(R.id.dialog_switch_open_for_lunch);
        workmatesInSeekbar = mView.findViewById(R.id.dialog_seekbar_workmates_in);
        restaurantCustomerValue= mView.findViewById(R.id.dialog_workmates_in_value);

        distanceValue = mView.findViewById(R.id.dialog_distance_value);

        ratingBar = mView.findViewById(R.id.dialog_rating);

        actionButton();



        close.setOnClickListener(this);
        action.setOnClickListener(this);

        return mView;
    }

    private void actionButton() {
        this.distanceValue.setText(getString(R.string.fullscreen_dialog__value_in_meter,distanceSeekbar.getProgress()));
        this.restaurantCustomerValue.setText(getString(R.string.fullscreen_dialog__value_workmate_in,workmatesInSeekbar.getProgress()));


        this.distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            // When Progress value changed.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                distanceValue.setText(getString(R.string.fullscreen_dialog__value_in_meter,progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceValue.setText(getString(R.string.fullscreen_dialog__value_in_meter,progress));
                distanceChoice= (double) progress;

            }
        });

        this.workmatesInSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            // When Progress value changed.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                restaurantCustomerValue.setText(getString(R.string.fullscreen_dialog__value_workmate_in,progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                restaurantCustomerValue.setText(getString(R.string.fullscreen_dialog__value_workmate_in,progress));
                restaurantCustomersChoice=progress;

            }
        });

        openForLunch.setOnCheckedChangeListener((compoundButton, bChecked) ->{
            openForLunchChoice = bChecked;
        });

        ratingChoice = this.ratingBar.getNumStars();

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.fullscreen_dialog_close:
                dismiss();
                break;

            case R.id.fullscreen_dialog_action:
                sortAction();
                //  callback.onActionClick("Whatever");
                dismiss();
                break;

        }

    }

    private void sortAction() {
    }

    public interface Callback {

        void onActionClick(String name);

    }

    @Nullable
    @Override
    public Dialog getDialog() {
        return super.getDialog();
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}
