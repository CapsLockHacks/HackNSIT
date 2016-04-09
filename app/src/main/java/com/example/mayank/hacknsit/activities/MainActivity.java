package com.example.mayank.hacknsit.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mayank.hacknsit.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.container, null).commit();
        }
    }
}
