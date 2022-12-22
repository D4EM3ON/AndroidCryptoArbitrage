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

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.knowm.xchange.currency.Currency;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity {
    String names;
    private RecyclerView recyclerViewTop2 = null;
    private RecyclerView recyclerViewBottom2 = null;
    private MyAdapter2 myAdapterTop2, myAdapterBottom2;

    private ArrayList<String> name = null;


    private SwipeRefreshLayout swipeRefreshLayout2;
    private LiveData<ArrayList<TickerWithExchange>> highestPercentage;
    private ArrayList<Integer> validExchanges;
    private long startTime = System.currentTimeMillis();
    private int aa,bb,cc,dd,ff;
    private LiveData<ArrayList<TickerWithExchange>> lowestPercentage;
    private LiveData<ArrayList<Currency>> allCurrencies;
    ArrayList[] opportunities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            names = extras.getString("type");
        }
        Intent intent = getIntent();

        this.setTitle(R.string.title);

        update();

        swipeRefreshLayout2 = findViewById(R.id.refreshLayout2);
        swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                long elapsedTime = System.currentTimeMillis() - startTime;
                long timeTillNextDisplayChange = 60000 - (elapsedTime % 60000);
                if (elapsedTime > 60000){
                    update();
                    Toast.makeText(getApplicationContext(),"Updating", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout2.setRefreshing(false);
                    startTime =0;
                } else {
                    Toast.makeText(getApplicationContext(),"wait " + Long.toString(timeTillNextDisplayChange/ 1000L) + " s", Toast.LENGTH_SHORT).show();
                }

                swipeRefreshLayout2.setRefreshing(false);
            }
        });

    }

    private void update(){
        SharedPreferences mPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        Boolean a = mPreferences.getBoolean(SWITCH1,true);
        aa = (a) ? 1:0;
        Boolean b = mPreferences.getBoolean(SWITCH2,true);
        bb = (b) ? 1:0;
        Boolean c = mPreferences.getBoolean(SWITCH3,true);
        cc = (c) ? 1:0;
        Boolean d = mPreferences.getBoolean(SWITCH4,true);
        dd = (d) ? 1:0;
        Boolean f = mPreferences.getBoolean(SWITCH5,true);
        ff = (f) ? 1:0;

        validExchanges = new ArrayList<>(Arrays.asList(aa, bb, cc, ff, dd)); // changer les valid exchanges ici selon les settings.

        recyclerViewTop2 = findViewById(R.id.recyclerViewTop2);

        recyclerViewBottom2 = findViewById(R.id.recyclerViewBottom2);


        Registry registry = null; // here we would pass the exchanges
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registry = new Registry(validExchanges);
        }

        // dans la 1ere partie du recycler view dans le main
        highestPercentage = registry.getMaxGainers();

        // dans la 2e partie du recycler view dans le main
        lowestPercentage = registry.getMinGainers();

        allCurrencies = registry.getAllCurrencies();
        String type = names.toString().split("/")[0];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          opportunities =  registry.getArbitrage(new Currency(type));
        }

        allCurrencies.observe(this, e->{
            if (name == null){
                name = new ArrayList<>();
            }

            for (Currency currency : e){
                name.add(currency.toString());
                name.add(currency.getDisplayName());
            }



        });

        //Mettre les éléments dans des ArrayList pour la premiere partie du recyclerView
        highestPercentage.observe(this, e->{

            ArrayList<String> instruments = new ArrayList<>();
            ArrayList<String> exchanges = new ArrayList<>();
            ArrayList<String> percentChanges = new ArrayList<>();
            ArrayList<String> prices = new ArrayList<>();
            ArrayList<String> instrumentNames = new ArrayList<>();

            for(TickerWithExchange ticker:e){
                instruments.add(ticker.getInstrument().toString());

                exchanges.add(ticker.getExchange().toString().split("#")[0]);

                percentChanges.add(Double.toString(ticker.getPercentChange()));

                prices.add(Double.toString(ticker.getPriceInUSD()));

                instrumentNames.add(ticker.getName());
            }

            myAdapterTop2 = new MyAdapter2(instruments, exchanges, percentChanges, prices, instrumentNames);
            recyclerViewTop2.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTop2.setAdapter(myAdapterTop2);
            recyclerViewTop2.getAdapter().notifyDataSetChanged();

            Toast.makeText(this,getString(R.string.finish),Toast.LENGTH_SHORT).show();
        });

        //Mettre les éléments dans des ArrayList pour la deuxieme partie du recyclerView
        lowestPercentage.observe(this, e->{

            ArrayList<String> instruments = new ArrayList<>();
            ArrayList<String> exchanges = new ArrayList<>();
            ArrayList<String> percentChanges = new ArrayList<>();
            ArrayList<String> prices = new ArrayList<>();
            ArrayList<String> instrumentNames = new ArrayList<>();

            for(TickerWithExchange ticker: e){
                instruments.add(ticker.getInstrument().toString());

                exchanges.add(ticker.getExchange().toString().split("#")[0]);

                percentChanges.add(Double.toString(ticker.getPercentChange()));

                prices.add(Double.toString(ticker.getPriceInUSD()));

                instrumentNames.add(ticker.getName());
            }

            myAdapterBottom2 = new MyAdapter2(instruments, exchanges, percentChanges, prices, instrumentNames);
            recyclerViewBottom2.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewBottom2.setAdapter(myAdapterBottom2);
            recyclerViewBottom2.getAdapter().notifyDataSetChanged();
        });
    }
}