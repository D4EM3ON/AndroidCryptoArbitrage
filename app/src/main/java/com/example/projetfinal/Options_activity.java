package com.example.projetfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class Options_activity extends AppCompatActivity {

    Switch binanceSwitch,coinbaseSwitch,krakenSwitch,gateIoSwitch,upbitSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binanceSwitch = findViewById(R.id.switch1);
        coinbaseSwitch = findViewById(R.id.switch2);
        krakenSwitch = findViewById(R.id.switch3);
        gateIoSwitch = findViewById(R.id.switch4);
        upbitSwitch = findViewById(R.id.switch5);
    }
    //toast mais peut etre utile pour dautres choses
    public void onSwitchClick(View view){
        if (binanceSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }
    public void onSwitchClick1(View view){
        if (coinbaseSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }
    public void onSwitchClick2(View view){
        if (krakenSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }
    public void onSwitchClick3(View view){
        if (gateIoSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }
    public void onSwitchClick4(View view){
        if (upbitSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }
}