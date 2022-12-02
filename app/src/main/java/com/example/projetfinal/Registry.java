package com.example.projetfinal;

import android.util.Log;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.Observable;

public class Registry {
    private StreamingExchange bitstamp;

    public Registry() throws IOException {



//        bitstamp = StreamingExchangeFactory.INSTANCE.createExchange(BitstampStreamingExchange.class);
//        bitstamp.connect().blockingAwait();
//        Map<CurrencyPair, CurrencyPairMetaData> infoCurrencies = bitstamp.getExchangeMetaData().getCurrencyPairs();
//        Log.i("MainActivity", String.valueOf(bitstamp.getExchangeMetaData().getCurrencyPairs()));
//        getAllExchangePrices("BTC");


        Executors.newSingleThreadExecutor().execute(() -> {
            Exchange bitstamp2 = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());

            // Interested in the public market data feed (no authentication)
            MarketDataService marketDataService = bitstamp2.getMarketDataService();
            List<CurrencyPair> tickers = bitstamp2.getExchangeSymbols();


            try {
                Log.i("MainActivity", String.valueOf(marketDataService.getTickers(null)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    /**
     * Gets all the exchange prices for a particular ticker
     *
     * @param ticker the ticker - "BTC" or "LTC" or "ETH"
     * @return the prices
     */
    protected boolean getAllExchangePrices(String ticker){
        Observable<Ticker> subscription1 = bitstamp.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_USD);
        Log.i("MainActivity", String.valueOf(subscription1));

        return true;
    }
}
