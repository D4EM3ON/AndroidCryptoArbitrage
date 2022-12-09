package com.example.projetfinal;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.commons.lang3.ObjectUtils;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import org.knowm.xchange.currency.Currency;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Binance
 * CoinbasePro
 * Kraken
 * GateIO
 * UpBit
 * @author Justin Quirion
 */
public class Registry {
    private ArrayList<Exchange> exchanges;
    private ArrayList<CurrencyPair> allPairs = null;
    private ArrayList<TickerWithExchange> gainers;
    private ArrayList<TickerWithExchange> allTickers = null;
    private ArrayList<Currency> allCurrencies;
    private Map<Currency, PossibilitiesPerCurrency> allPossibilities;

    /**
     * Instantiates a new Registry.
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
            getAllCurrencyPairs(exchanges);

            boolean isItTopGainers = true;

            /*long startTime = System.nanoTime();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime)/1000000 / 1000;
            Log.i("Timer", String.valueOf(duration));
            */
            try {
                gainers = topGainers(exchanges, isItTopGainers);
                Log.i("Gainers", String.valueOf(gainers));
                Log.i("ArbitrageAVA", String.valueOf(getArbitrage(exchanges, isItTopGainers, Currency.ETH)[0]));
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
    protected void getAllCurrencyPairs(ArrayList<Exchange> validExchanges) {
        this.allPairs = new ArrayList<>();
        for (Exchange exchange : validExchanges) {
            ArrayList<CurrencyPair> exchangePairs = (ArrayList<CurrencyPair>) exchange.getExchangeSymbols();
            this.allPairs.addAll(exchangePairs);
        }
    }

    protected void getAllTickers(ArrayList<Exchange> validExchanges) throws IOException {
        this.allTickers = new ArrayList<>();

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

                try {
                    percentChange = ticker.getPercentageChange().doubleValue();
                } catch (NullPointerException e) {
                    percentChange = ticker.getLow().doubleValue() / ticker.getLast().doubleValue();
                }

                try {
                    price = ticker.getLast().doubleValue();
                } catch (NullPointerException e) {
                    price = ticker.getLow().doubleValue();
                }

                Instrument instrument = ticker.getInstrument();

                this.allTickers.add(new TickerWithExchange(instrument, percentChange, exchange, price));
            }
        }

    }

    protected ArrayList<Currency> getAllCurrencies(ArrayList<Exchange> validExchanges) {
        this.allCurrencies = new ArrayList<Currency>();
        if (this.allPairs == null) {
            getAllCurrencyPairs(validExchanges);
        }

        for (CurrencyPair pair : this.allPairs) {
            this.allCurrencies.add(pair.base);
        }

        return this.allCurrencies;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<TickerWithExchange>[] getArbitrage(ArrayList<Exchange> validExchanges, boolean top, Currency currencySelected) {
        ArrayList<TickerWithExchange> opportunities = new ArrayList<>();
        allPossibilities = new HashMap<>();

        if (this.allTickers == null){
            try {
                getAllTickers(validExchanges);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // we build a hashmap for every currency to look for all possibilities
        for (TickerWithExchange ticker : this.allTickers){
            CurrencyPair currencyPair = (CurrencyPair) ticker.getInstrument();
            Currency base = currencyPair.base;
            Currency counter = currencyPair.counter;
            // counter.merge(instrument.toString().split("/")[0], 1, Integer::sum);
            allPossibilities.putIfAbsent(base, new PossibilitiesPerCurrency());
            allPossibilities.get(base).add(ticker);

            allPossibilities.putIfAbsent(counter, new PossibilitiesPerCurrency());
            allPossibilities.get(counter).add(ticker);
        }

        return new ArrayList[]{getTopOpportunities(false, currencySelected), getTopOpportunities(true, currencySelected)};
    }

    protected ArrayList<TickerWithExchange> getTopOpportunities(boolean top, Currency currencySelected){
        // currently pretty much works, only thing is sometimes (for current case with ETH), it decides that OAX is worth 0 but still adds it. Needs to check up on that.
        // Check if price in USD is good! i do not yet know

        ArrayList<TickerWithExchange> topOpportunities = new ArrayList<>(); // we will have 3 at most

        TickerWithExchange minGainer;
        if (top) { minGainer = new TickerWithExchange(15000d); }
        else { minGainer = new TickerWithExchange(0d); }

        for (TickerWithExchange ticker : this.allPossibilities.get(currencySelected).getPossibleTickers()){
            Log.i("Ticker", String.valueOf(ticker) + ", " + String.valueOf(0==ticker.getPrice()));
            try{
                ticker.setToUSD(allPossibilities.get(ticker.getBase()).getToUSD().getPrice());
            } catch (NullPointerException e){
                continue;
            }


            if (topOpportunities.size() == 3){
                for (TickerWithExchange opportunity : topOpportunities){
                    if ((top && minGainer.getPriceInUSD() < opportunity.getPriceInUSD())  || (!top && minGainer.getPriceInUSD() > opportunity.getPriceInUSD())){
                        minGainer = opportunity;
                    }
                }
            }

            if (topOpportunities.size() < 3){
                if (ticker.getPrice() != 0){
                    topOpportunities.add(ticker);
                    minGainer = ticker;
                }
            }
            else if (ticker.getPrice() != 0 && (top && minGainer.getPriceInUSD() < ticker.getPriceInUSD()) || (!top && minGainer.getPriceInUSD() > ticker.getPriceInUSD())) {
                topOpportunities.remove(minGainer);
                topOpportunities.add(ticker);

                minGainer = topOpportunities.get(topOpportunities.size() - 1);

                for (TickerWithExchange opportunity : topOpportunities){
                    if ((top && minGainer.getPriceInUSD() < opportunity.getPriceInUSD()) || (!top && minGainer.getPriceInUSD() > opportunity.getPriceInUSD())) {
                        minGainer = opportunity;
                    }
                }
            }

        }
        Collections.sort(topOpportunities, new TickerWithExchangeComparator());
        if (!top) Collections.reverse(topOpportunities);
        return topOpportunities;
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

        if (this.allTickers == null) {
            getAllTickers(validExchanges);
        }

        for (TickerWithExchange ticker : this.allTickers) {

            Instrument instrument = ticker.getInstrument();
            double percentChange = ticker.getPercentChange();


            counter.merge(instrument.toString().split("/")[0], 1, Integer::sum);

            if (!thereAreFive && topGainers.size() >= 5) thereAreFive = true;

            if (!thereAreFive || topGainers.size() < 5) {

                topGainers.add(ticker);

                if ((top && minGainer.getPercentChange() > percentChange) || (!top && minGainer.getPercentChange() < percentChange)) {
                    minGainer = topGainers.get(topGainers.size() - 1);
                }
            } else if (thereAreFive && 2 <= counter.get(instrument.toString().split("/")[0]) && ((top && percentChange > minGainer.getPercentChange()) || (!top && percentChange < minGainer.getPercentChange()))) {
                topGainers.remove(minGainer);
                topGainers.add(ticker);

                minGainer = topGainers.get(topGainers.size() - 1);

                for (TickerWithExchange topGainer : topGainers) {
                    if ((top && topGainer.getPercentChange() < minGainer.getPercentChange()) || (!top && topGainer.getPercentChange() > minGainer.getPercentChange()))
                        minGainer = topGainer;
                }

            }

        }
        return topGainers;
    }


}
