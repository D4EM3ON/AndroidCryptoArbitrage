package com.example.projetfinal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 *  About me activity.
 */
public class AboutMe_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}