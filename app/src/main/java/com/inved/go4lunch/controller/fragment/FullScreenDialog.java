package com.inved.go4lunch.controller.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.inved.go4lunch.R;

public class FullScreenDialog extends DialogFragment implements View.OnClickListener {


    public static final String TAG = "CREATE_DIALOG" ;
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

        close.setOnClickListener(this);
        action.setOnClickListener(this);

        return mView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.fullscreen_dialog_close:
                dismiss();
                break;

            case R.id.fullscreen_dialog_action:
              //  callback.onActionClick("Whatever");
                dismiss();
                break;

        }

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
