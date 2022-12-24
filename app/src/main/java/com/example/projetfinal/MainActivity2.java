package com.example.projetfinal;

import static com.example.projetfinal.Options_activity.SHARED_PREFS;
import static com.example.projetfinal.Options_activity.SWITCH1;
import static com.example.projetfinal.Options_activity.SWITCH2;
import static com.example.projetfinal.Options_activity.SWITCH3;
import static com.example.projetfinal.Options_activity.SWITCH4;
import static com.example.projetfinal.Options_activity.SWITCH5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import org.knowm.xchange.currency.Currency;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The type Main activity 2.
 */
public class MainActivity2 extends AppCompatActivity {

    private RecyclerView recyclerViewTop2 = null;
    private RecyclerView recyclerViewBottom2 = null;
    private MyAdapter2 myAdapterTop2, myAdapterBottom2;

    private ArrayList<String> name = null;



    private ArrayList<Integer> validExchanges;
    private long startTime = System.currentTimeMillis();
    private int aa, bb, cc, dd, ff;

    /**
     * The Opportunities.
     */
    private ArrayList<ArrayList<String>> opportunities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        opportunities = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("opps");

        this.setTitle(R.string.title);

        update();

    }

    private void update() {
        SharedPreferences mPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        Boolean a = mPreferences.getBoolean(SWITCH1, true);
        aa = (a) ? 1 : 0;
        Boolean b = mPreferences.getBoolean(SWITCH2, true);
        bb = (b) ? 1 : 0;
        Boolean c = mPreferences.getBoolean(SWITCH3, true);
        cc = (c) ? 1 : 0;
        Boolean d = mPreferences.getBoolean(SWITCH4, true);
        dd = (d) ? 1 : 0;
        Boolean f = mPreferences.getBoolean(SWITCH5, true);
        ff = (f) ? 1 : 0;

        validExchanges = new ArrayList<>(Arrays.asList(aa, bb, cc, ff, dd)); // changer les valid exchanges ici selon les settings.

        recyclerViewTop2 = findViewById(R.id.recyclerViewTop2);

        recyclerViewBottom2 = findViewById(R.id.recyclerViewBottom2);

        //Mettre les éléments dans des ArrayList pour la premiere partie du recyclerView

        ArrayList<String> instruments = new ArrayList<>();
        ArrayList<String> exchanges = new ArrayList<>();
        ArrayList<String> percentChanges = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> instrumentNames = new ArrayList<>();
        //le premier ctop ou bottom
        // le deuxieme c 15 string chaque

        double top, bottom;
        int indiceTop, indiceBottom;
        top = Double.parseDouble(opportunities.get(0).get(3));
        bottom = Double.parseDouble(opportunities.get(1).get(3));

        if (top > bottom){
            indiceTop = 1;
            indiceBottom = 0;
        }
        else{
            indiceTop = 0;
            indiceBottom = 1;
        }

        for (int i = 0; i < opportunities.get(indiceTop).size()/5 ; i++) {

            instruments.add(opportunities.get(indiceTop).get(0+(5*i)));
            instrumentNames.add(opportunities.get(indiceTop).get(1+(5*i)));
            exchanges.add(opportunities.get(indiceTop).get(2+(5*i)));
            prices.add(opportunities.get(indiceTop).get(3+(5*i)));
            percentChanges.add(opportunities.get(indiceTop).get(4+(5*i)));
        }

        myAdapterTop2 = new MyAdapter2(instruments, exchanges, percentChanges, prices, instrumentNames);
        recyclerViewTop2.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTop2.setAdapter(myAdapterTop2);
        recyclerViewTop2.getAdapter().notifyDataSetChanged();

        Toast.makeText(this, getString(R.string.finish), Toast.LENGTH_SHORT).show();

        ArrayList<String> instrument = new ArrayList<>();
        ArrayList<String> exchange = new ArrayList<>();
        ArrayList<String> percentChange = new ArrayList<>();
        ArrayList<String> price = new ArrayList<>();
        ArrayList<String> instrumentName = new ArrayList<>();

        for (int i = 0; i < opportunities.get(indiceBottom).size()/5 ; i++) {

            instrument.add(opportunities.get(indiceBottom).get(0+(5*i)));
            instrumentName.add(opportunities.get(indiceBottom).get(1+(5*i)));
            exchange.add(opportunities.get(indiceBottom).get(2+(5*i)));
            price.add(opportunities.get(indiceBottom).get(3+(5*i)));
            percentChange.add(opportunities.get(indiceBottom).get(4+(5*i)));
        }

        myAdapterBottom2 = new MyAdapter2(instrument, exchange, percentChange, price, instrumentName);
        recyclerViewBottom2.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBottom2.setAdapter(myAdapterBottom2);
        recyclerViewBottom2.getAdapter().notifyDataSetChanged();
    }
}
