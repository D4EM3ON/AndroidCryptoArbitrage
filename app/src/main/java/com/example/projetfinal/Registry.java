package com.example.projetfinal;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.gateio.GateioExchange;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam;
import org.knowm.xchange.upbit.UpbitExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Binance
 * CoinbasePro
 * Kraken - you need to pass CurrencyPairParams
 * GateIO
 * UpBit
 * Huobi
 * @author Justin Quirion
 */
public class Registry {
    private ArrayList<Exchange> exchanges;
    private ArrayList<CurrencyPair> allPairs = null;

    /**
     * Instantiates a new Registry.
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Registry() {

        Executors.newSingleThreadExecutor().execute(() -> {
            Exchange binance = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
            Exchange coinbasepro = ExchangeFactory.INSTANCE.createExchange(CoinbaseProExchange.class.getName());
            Exchange kraken = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());
            Exchange gateio = ExchangeFactory.INSTANCE.createExchange(GateioExchange.class.getName());
            Exchange upbit = ExchangeFactory.INSTANCE.createExchange(UpbitExchange.class.getName());

            exchanges = new ArrayList<>(Arrays.asList(binance, coinbasepro, kraken, upbit, gateio));

            allCurrencyPairs(exchanges);

            boolean isItTopGainers = true;


            try {
                long startTime = System.nanoTime();

                Log.i("MainActivity", String.valueOf(topGainers(exchanges, isItTopGainers)));

                long endTime = System.nanoTime();
                long duration = (endTime - startTime)/1000000 / 1000;
                Log.i("Timer", String.valueOf(duration));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * Updates the private allPairs ArrayList of all available pairs.
     *
     * @param validExchanges the valid exchanges for which to look for the pairs into
     */
    protected void allCurrencyPairs(ArrayList<Exchange> validExchanges){
        this.allPairs = new ArrayList<>();
        for (Exchange exchange : validExchanges) {
            ArrayList<CurrencyPair> exchangePairs = (ArrayList<CurrencyPair>) exchange.getExchangeSymbols();
            this.allPairs.addAll(exchangePairs);
        }
    }


    /**
     * Returns an arraylist, with either the highest % gainers, or the lowest % gainers.
     * Tickers pair must be present at least in one other exchange.
     * This is what you need to call for homepage. Will return 5 tickers.
     *
     * @param validExchanges the valid exchanges
     * @param top            if we are looking for highest % change (true) or lowest % change (false)
     * @return the top gainers list
     * @throws IOException the io exception if ever .getTickers does not work (they have worked up to date of project. This was confirmed at 19:41 2022-12-08
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<TickerWithExchange> topGainers(ArrayList<Exchange> validExchanges, boolean top) throws IOException {
        ArrayList<TickerWithExchange> topGainers = new ArrayList<>();
        Map<String, Integer> counter = new HashMap<String, Integer>();

        TickerWithExchange minGainer;

        if (top) minGainer = new TickerWithExchange(15000d);
        else minGainer = new TickerWithExchange(0d);

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
                double price;

                try{
                    percentChange = ticker.getPercentageChange().doubleValue();
                } catch (NullPointerException e){
                    percentChange = ticker.getLow().doubleValue() / ticker.getLast().doubleValue();
                }

                try{
                    price = ticker.getLast().doubleValue();
                } catch (NullPointerException e){
                    price = ticker.getLow().doubleValue();
                }

                Instrument instrument = ticker.getInstrument();

                TickerWithExchange newTicker = new TickerWithExchange(instrument, percentChange, exchange, price);

                counter.merge(instrument.toString().split("/")[0], 1, Integer::sum);

                if (!thereAreFive && topGainers.size() >= 5) thereAreFive = true;

                if (!thereAreFive || topGainers.size() < 5){

                    topGainers.add(new TickerWithExchange(new CurrencyPair(String.valueOf(instrument)), percentChange, exchange, price));

                    if ((top && minGainer.getPercentChange() > percentChange) || (!top && minGainer.getPercentChange() < percentChange)) {
                        minGainer = topGainers.get(topGainers.size() - 1);
                    }
                }

                else if (thereAreFive && 2 <= counter.get(instrument.toString().split("/")[0]) && ((top && percentChange > minGainer.getPercentChange()) || (!top && percentChange < minGainer.getPercentChange()))) {
                    topGainers.remove(minGainer);
                    topGainers.add(new TickerWithExchange(new CurrencyPair(String.valueOf(instrument)), percentChange, exchange, price));

                    minGainer = topGainers.get(topGainers.size() - 1);

                    for (TickerWithExchange topGainer : topGainers){
                        if ((top && topGainer.getPercentChange() < minGainer.getPercentChange()) || (!top && topGainer.getPercentChange() > minGainer.getPercentChange())) minGainer = topGainer;
                    }

                }

            }

        }
        return topGainers;
    }

}
