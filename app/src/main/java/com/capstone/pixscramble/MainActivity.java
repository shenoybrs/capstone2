package com.capstone.pixscramble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.capstone.pixscramble.service.AppSyncService;

public class MainActivity extends ActionBarActivity {
    private Intent intent;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        intent = new Intent(this, PixGridActivity.class);
        checkButtonClick();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.startgamebtn);
        new Thread() {
            public void run() {
                AppSyncService.setAlarm(getApplicationContext());
            }
        }.run();
        if (myButton != null) {
            myButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(intent);


                }

            });
        }
    }

}
