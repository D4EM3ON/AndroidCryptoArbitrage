package com.example.projetfinal;

import android.util.Log;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.gateio.GateioExchange;
import org.knowm.xchange.huobi.HuobiExchange;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam;
import org.knowm.xchange.service.marketdata.params.InstrumentsParams;
import org.knowm.xchange.service.marketdata.params.Params;
import org.knowm.xchange.upbit.UpbitExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Binance
 * CoinbasePro
 * Kraken - you need to pass CurrencyPairParams
 * GateIO
 * UpBit
 * Huobi
 */

public class Registry {
    private ArrayList<Exchange> exchanges;

    public Registry() throws IOException {



//        bitstamp = StreamingExchangeFactory.INSTANCE.createExchange(BitstampStreamingExchange.class);
//        bitstamp.connect().blockingAwait();
//        Log.i("MainActivity", String.valueOf(bitstamp.getExchangeMetaData().getCurrencyPairs()));
//        getAllExchangePrices("BTC");


        Executors.newSingleThreadExecutor().execute(() -> {
            Exchange binance = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
            Exchange coinbasepro = ExchangeFactory.INSTANCE.createExchange(CoinbaseProExchange.class.getName());
            Exchange kraken = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());
            Exchange gateio = ExchangeFactory.INSTANCE.createExchange(GateioExchange.class.getName());
            Exchange upbit = ExchangeFactory.INSTANCE.createExchange(UpbitExchange.class.getName());

            exchanges = new ArrayList<>(Arrays.asList(binance, coinbasepro, kraken, gateio, upbit));


            try {
                topGainers(exchanges);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            CurrencyPairsParam tickers = new CurrencyPairsParam() {
//                @Override
//                public Collection<CurrencyPair> getCurrencyPairs() {
//                    return exchanges.get(5).getExchangeInstruments();
//                }
//            };
//            Log.i("MainActivity", String.valueOf(exchanges.get(5).getExchangeInstruments()));
//            try {
//                Log.i("MainActivity", String.valueOf(exchanges.get(5).getMarketDataService().getTickers(tickers)));
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("getTickers", Arrays.toString(e.getStackTrace()));
//            }
        });
    }

    protected ArrayList<TickerWithExchange> topGainers(ArrayList<Exchange> validExchanges) throws IOException { // works, the only thing is that Gateio has .
        // weird shit. to normalize, create a database with all tickers from all exchanges and look if they appear at least twice.
        // we will also be able to just do this with "gainers" and change
        ArrayList<TickerWithExchange> topGainers = new ArrayList<>();

        TickerWithExchange minGainer = new TickerWithExchange(15000d);
        int minGainerPos = 0;
        boolean thereAreFive = false;

        for (Exchange exchange : validExchanges) {
            CurrencyPairsParam tickerList = new CurrencyPairsParam() {
                @Override
                public Collection<CurrencyPair> getCurrencyPairs() {
                    return exchange.getExchangeSymbols();
                }
            };

            for (Ticker ticker : exchange.getMarketDataService().getTickers(tickerList)) {
                double percentChange;

                try{
                    percentChange = ticker.getPercentageChange().doubleValue();
                } catch (NullPointerException e){
                    percentChange = ticker.getLow().doubleValue() / ticker.getLast().doubleValue();
                }

                Instrument instrument = ticker.getInstrument();

                if (!thereAreFive && topGainers.size() == 5) thereAreFive = true;

                if (!thereAreFive || topGainers.size() < 5){

                    topGainers.add(new TickerWithExchange(new CurrencyPair(String.valueOf(instrument)), percentChange, exchange));

                    if (minGainer.getPercentChange() > percentChange){
                        minGainer = topGainers.get(topGainers.size() - 1);
                    }
                }

                else if (thereAreFive && percentChange > minGainer.getPercentChange()){
                    topGainers.remove(minGainer);
                    topGainers.add(new TickerWithExchange(new CurrencyPair(String.valueOf(instrument)), percentChange, exchange));

                    minGainer = topGainers.get(topGainers.size() - 1);

                    for (TickerWithExchange topGainer : topGainers){
                        if (topGainer.getPercentChange() < minGainer.getPercentChange()) minGainer = topGainer;
                    }

                }


            }


        }

        return topGainers;
    }


}
