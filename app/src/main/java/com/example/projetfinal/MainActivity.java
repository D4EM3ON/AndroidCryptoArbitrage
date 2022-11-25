package com.example.projetfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;

import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;

/**
 * The type Main activity.
 * From the top 10 exchanges:
 * Binance
 * Bitfinex
 * CoinbasePro
 * Kraken
 * bitFlyer
 * Bitstamp
 * Coincheck
 * Gemini
 */
public class MainActivity extends AppCompatActivity {

    private StreamingExchange bitstamp;

    public MainActivity() throws IOException {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitstamp = StreamingExchangeFactory.INSTANCE.createExchange(BitstampStreamingExchange.class);
        bitstamp.connect().blockingAwait();
        getAllExchangePrices("BTC");
    }




    /**
     * Gets all the exchange prices for a particular ticker
     *
     * @param ticker the ticker - "BTC" or "LTC" or "ETH"
     * @return the prices
     */
    protected boolean getAllExchangePrices(String ticker){
        Disposable subscription1 = bitstamp.getStreamingMarketDataService()
                .getTrades(CurrencyPair.BTC_USD)
                .subscribe(
                        trade -> Log.i("MainActivity", String.valueOf(trade)),
                        throwable -> Log.e("MainActivity","Error in trade subscription", throwable));
        return true;
    }
}