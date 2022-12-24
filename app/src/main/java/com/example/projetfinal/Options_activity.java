package com.example.projetfinal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The type Options activity.
 */
public class Options_activity extends AppCompatActivity {

    /**
     * The Binance switch.
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch binanceSwitch, /**
     * The Coinbase switch.
     */
    coinbaseSwitch, /**
     * The Kraken switch.
     */
    krakenSwitch, /**
     * The Gate io switch.
     */
    gateIoSwitch, /**
     * The Upbit switch.
     */
    upbitSwitch;

    /**
     * The constant SHARED_PREFS.
     */
    public static final String SHARED_PREFS = "SHARED";
    /**
     * The constant SWITCH1.
     */
    public static final String SWITCH1 = "binance";
    /**
     * The constant SWITCH2.
     */
    public static final String SWITCH2 = "coinbase";
    /**
     * The constant SWITCH3.
     */
    public static final String SWITCH3 = "kraken";
    /**
     * The constant SWITCH4.
     */
    public static final String SWITCH4 = "gateIo";
    /**
     * The constant SWITCH5.
     */
    public static final String SWITCH5 = "upbit";
    private boolean switchOnoff1, switchOnoff2, switchOnoff3, switchOnoff4, switchOnoff5;
    /**
     * The Savebutton.
     */
    Button savebutton;

    @SuppressLint("MissingInflatedId")
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
        savebutton = findViewById(R.id.button);







        savebutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                saveData();

                Intent resultIntent = new Intent();

            }
        });
        loadData();
        updateViews();

    }

    /**
     * On switch click.
     *
     * @param view the view
     */
//toast mais peut etre utile pour dautres choses
    public void onSwitchClick(View view){
        if (binanceSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }

    /**
     * On switch click 1.
     *
     * @param view the view
     */
    public void onSwitchClick1(View view){
        if (coinbaseSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }

    /**
     * On switch click 2.
     *
     * @param view the view
     */
    public void onSwitchClick2(View view){
        if (krakenSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }

    /**
     * On switch click 3.
     *
     * @param view the view
     */
    public void onSwitchClick3(View view){
        if (gateIoSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }

    /**
     * On switch click 4.
     *
     * @param view the view
     */
    public void onSwitchClick4(View view){
        if (upbitSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(),"ON",Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_SHORT).show();}
    }

    /**
     * Save data.
     */
    public void saveData(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWITCH1,binanceSwitch.isChecked());
        editor.putBoolean(SWITCH2,coinbaseSwitch.isChecked());
        editor.putBoolean(SWITCH3,krakenSwitch.isChecked());
        editor.putBoolean(SWITCH4,gateIoSwitch.isChecked());
        editor.putBoolean(SWITCH5,upbitSwitch.isChecked());


        editor.apply();
        Toast.makeText(this,"Data saved",Toast.LENGTH_SHORT).show();

    }

    /**
     * Load data.
     */
    public void loadData(){
        SharedPreferences sharedPreferences= getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        switchOnoff1 = sharedPreferences.getBoolean(SWITCH1,true);
        switchOnoff2 = sharedPreferences.getBoolean(SWITCH2,true);
        switchOnoff3 = sharedPreferences.getBoolean(SWITCH3,true);
        switchOnoff4 = sharedPreferences.getBoolean(SWITCH4,true);
        switchOnoff5 = sharedPreferences.getBoolean(SWITCH5,true);
    }

    /**
     * Update views.
     */
    public void updateViews(){
        binanceSwitch.setChecked(switchOnoff1);
        coinbaseSwitch.setChecked(switchOnoff2);
        krakenSwitch.setChecked(switchOnoff3);
        gateIoSwitch.setChecked(switchOnoff4);
        upbitSwitch.setChecked(switchOnoff5);
    }
}