package com.example.projetfinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;

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
        try {
            ArrayList<TickerWithExchange> highestPercentage = registry.topGainers(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // dans le 2e recycler view dans le main
        try {
            ArrayList<TickerWithExchange> lowestPercentage = registry.topGainers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        ArrayList<Currency> allCurrencies = registry.getAllCurrencies();

        // 2e page:



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