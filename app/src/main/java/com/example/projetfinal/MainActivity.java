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
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
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
        Registry registry = new Registry(); // here we would pass the exchanges
        this.setTitle(R.string.title);

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