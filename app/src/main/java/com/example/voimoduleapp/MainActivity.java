package com.example.voimoduleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements VoiHandler.CallClientSetupObserver {

    private VoiHandler voiHandler;
    private String username = "user"+(int)(Math.random()*100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        init();

    }

    private void init() {

        voiHandler = new SinchHandler(username,
                getApplicationContext(), this);

        voiHandler.setUpClient();

    }

    @Override
    public void onClientSetupDone() {

        Intent intent = new Intent(this, CallActivity.class);

        CallActivity.setVoiHandler(voiHandler);

        intent.putExtra("username", username);

        startActivity(intent);

    }

    @Override
    public void onClientStopped() {
        Toast.makeText(this, "client stopped!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClientSetupFailed(String errorMessage) {
        Toast.makeText(this, "client setup failed!", Toast.LENGTH_LONG).show();
    }
}