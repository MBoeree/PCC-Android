package com.example.matthijs.pcc_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class DriveActivity extends AppCompatActivity {

    CharSequence vroem = "VROEM!";
    int toast_duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        ImageButton forward = (ImageButton) findViewById(R.id.forward);
//        ImageButton back = (ImageButton) findViewById(R.id.back);
//        ImageButton left = (ImageButton) findViewById(R.id.left);
//        ImageButton right = (ImageButton) findViewById(R.id.right);

        forward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, vroem, toast_duration);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                return true;
            }
        });

    }
}