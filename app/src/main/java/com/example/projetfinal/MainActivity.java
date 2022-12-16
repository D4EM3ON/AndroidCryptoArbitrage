package com.example.projetfinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.knowm.xchange.currency.Currency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The type Main activity.
 * From the top 6 exchanges:
 * Binance
 * CoinbasePro
 * Kraken
 * GateIO
 * UpBit
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewTop;
    String[] s1, s2, s3, s4;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Integer> validExchanges = new ArrayList<>(Arrays.asList(1, 1, 1, 1, 1)); // changer les valid exchanges ici selon les settings.
        // changer les valid exchanges dans le futur:
        // registry.setExchanges(validExchanges) avec validExchanges comme plus haut. dans l'ordre: binance, coinbasepro, kraken, upbit, gateio

        Registry registry = new Registry(validExchanges); // here we would pass the exchanges
        this.setTitle(R.string.title);

        // dans le 1er recycler view dans le main
        LiveData<ArrayList<TickerWithExchange>> highestPercentage = registry.getMaxGainers();

        // dans le 2e recycler view dans le main
        LiveData<ArrayList<TickerWithExchange>> lowestPercentage = registry.getMinGainers();

        // POUR UTILISER TickerWithExchange
        // TickerWithExchange ticker = highestPercentage.get(0)
        // nom du exchange : ticker.getExchange().toString();
        // nom ticker : ticker.getName();
        // prix : ticker.getPrice();
        // prix en USD : ticker.getPriceToUSD();
        // percentage : ticker.getPercentage();


        // tous les currencies possible qu'on peut chercher, symboles et noms
        // pour get symboles: allCurrencies.get(index).toString()
        // pour get noms : allCurrencies.get(index).getDisplayName();
        // since it is a LiveData, we want to create default values for start of application (they can be empty) and update them in the observe method.
        // e will be like what you want to call. So, for allCurrencies, e would be an ArrayList<Currency>

        LiveData<ArrayList<Currency>> allCurrencies = registry.getAllCurrencies();

        allCurrencies.observe(this, e->{
            // do the code needed for, ie the search bar in here
        });

        highestPercentage.observe(this, e->{
            // do the code to insert the ArrayList<TickerWithExchange> in the 1st recycler view here
        });

        lowestPercentage.observe(this, e->{
            // do the code to insert the ArrayList<TickerWithExchange> in the 2nd recycler view here
        });


        // 2e page:
        int index = 0;
        // index gotten from search/click. we can also just take what was clicked on and put
        // it in getArbitrage
        // in [0] is the top, in [1] is losers. All are already in order
        // ArrayList<TickerWithExchange>[] arbitrage = registry.getArbitrage(allCurrencies.get(index));



        recyclerViewTop=findViewById(R.id.recyclerViewTop);

        s1 = getResources().getStringArray(R.array.companyAB);
        s2 = getResources().getStringArray(R.array.company_name);
        s3 = getResources().getStringArray(R.array.percentage);
        s4 = getResources().getStringArray(R.array.price);

        MyAdapter myAdapter = new MyAdapter(s1, s2,s3,s4);
        recyclerViewTop.setAdapter(myAdapter);
        recyclerViewTop.setLayoutManager(new LinearLayoutManager(this));

    }





}