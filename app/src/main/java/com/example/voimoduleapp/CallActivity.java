package com.example.voimoduleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

public class CallActivity extends AppCompatActivity implements CallView {

    private static final String TAG = "debug-main";
    // presenter
    private CallPresenter callPresenter;

    // permissions
    private Permission permission;
    private static final String[] permissionStrings = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int PERMISSION_CODE = 501;

    // UI
    private TextView callStateTV, usernameTV, logMessagesTV;
    private Button callButton;
    private EditText callRecTI;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // audio config
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // UI
        callStateTV = findViewById(R.id.callStateTextView);
        callButton = findViewById(R.id.callButton);
        callRecTI = findViewById(R.id.callRecTextInput);
        usernameTV = findViewById(R.id.usernameTextView);
        logMessagesTV = findViewById(R.id.logTextView);

        // set up presenter
        callPresenter = new CallPresenter(this, this);
        // callPresenter.setupVoi(); will get called on onResume()

        promptPermission();

    }

    private void promptPermission() {

        permission = new Permission(this, permissionStrings, PERMISSION_CODE);

        if(!permission.checkPermissions())
            permission.askPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case PERMISSION_CODE:

                try {
                    permission.resolvePermissions(permissions, grantResults);
                }catch (Exception e){
                    Log.d(TAG, "onRequestPermissionsResult: error = "+e.getMessage());
                }

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        callPresenter.setupVoi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        callPresenter.terminateVoi();
    }

    // methods from presenter


    @Override
    public void setUsernameUI(String username) {
        usernameTV.setText("username: "+username);
    }

    @Override
    public void onCallOutgoingUI() {

        callStateTV.setText("call outgoing...");
        callRecTI.setEnabled(false);
        callButton.setText("calling...");
        callButton.setEnabled(false);

    }

    @Override
    public void onCallIncomingUI(String callerId) {

        showAlertDialog(
                "You have an incoming call from: " + callerId + "!",

                "answer",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // receive the call

                        callPresenter.answerTheCall();

                        dialog.dismiss();
                    }
                },

                "decline",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // hang up

                        callPresenter.hangUpThisCall();

                        callStateTV.setText("not on call (you declined up the last call)");
                        callRecTI.setEnabled(true);

                        dialog.dismiss();
                    }
                }
        );

    }

    @Override
    public void onCallConnectedUI(String callerId) {

        // set volume up-down button control call volume
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        audioManager.setSpeakerphoneOn(true);

        callStateTV.setText("call connected with "+callerId+"! you can talk now.");
        callRecTI.setEnabled(false);
        callButton.setText("Hang Up");
        callButton.setBackgroundColor(getColor(R.color.colorPrimary));
        callButton.setEnabled(true);
        callRecTI.setEnabled(false);

    }

    @Override
    public void onCallDisconnectedUI() {

        // reset volume up-down button
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        audioManager.setSpeakerphoneOn(false);

        showToast("call disconnected!");

        callStateTV.setText("not on call (last call was disconnected)");
        callRecTI.setEnabled(true);
        callButton.setText("Call");
        callButton.setBackgroundColor(getColor(R.color.colorAccent));

    }

    @Override
    public void onSuccess(String message) {

        showToast(message);
        logMessagesTV.setText(message);

    }

    @Override
    public void onFail(String message) {

        showAlertDialog("An unexpected error occured! make sure you have active internet connection",
                    "", null, "", null);
        //showToast("an unexpected error occured!");
        logMessagesTV.setText(message);

    }

    public void callButtonPressed(View view) {

        if(callPresenter.isOnCall()){
            callPresenter.hangUpThisCall();
        }

        else {

            if(callRecTI.getText().toString().isEmpty()){
                callPresenter.onFailed("username empty");
                callRecTI.setError("field empty");
                return;
            }

            String calleeUsername = callRecTI.getText().toString();
            calleeUsername.trim();
            calleeUsername.toLowerCase();
            callPresenter.makeCallToUser(calleeUsername);

        }

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showAlertDialog(String message, String positiveMessage, DialogInterface.OnClickListener positiveListener,
                             String negativeMessage , DialogInterface.OnClickListener negativeListener)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);

        if(positiveListener!=null)
            builder.setPositiveButton(positiveMessage, positiveListener);
        if(negativeListener!=null)
            builder.setNegativeButton(negativeMessage, negativeListener);
        if(positiveListener!=null || negativeListener!=null)
            builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}