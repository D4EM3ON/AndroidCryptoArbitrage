package com.example.projetfinal;

import android.util.Log;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.bitfinex.BitfinexExchange;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.coinbasepro.CoinbasePro;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.Arrays;
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
            Exchange binance = ExchangeFactory.INSTANCE.createExchange(CoinbasePro.class.getName());

            // Interested in the public market data feed (no authentication)
            MarketDataService marketDataService = binance.getMarketDataService();

            try {
                List<CurrencyPair> tickers = binance.getExchangeSymbols();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("getExchangeSymbols", Arrays.toString(e.getStackTrace()));
            }



            try {
                Log.i("MainActivity", String.valueOf(marketDataService.getTickers(null)));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("getTickers", Arrays.toString(e.getStackTrace()));
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
