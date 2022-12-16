package com.example.projetfinal;

import android.os.Build;

import androidx.annotation.RequiresApi;

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
    private ArrayList<TickerWithExchange> allTickers = null;
    private Map<Currency, PossibilitiesPerCurrency> allPossibilities;
    private Map<Currency, Double> basesToUSD;
    Exchange binance ;
    Exchange coinbasepro;
    Exchange kraken;
    Exchange gateio;
    Exchange upbit;
    /**
     * Instantiates a new Registry.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Registry(ArrayList<Integer> validExchanges) {

        Executors.newSingleThreadExecutor().execute(() -> {
            binance = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
            coinbasepro = ExchangeFactory.INSTANCE.createExchange(CoinbaseProExchange.class.getName());
            kraken = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());
            gateio = ExchangeFactory.INSTANCE.createExchange(GateioExchange.class.getName());
            upbit = ExchangeFactory.INSTANCE.createExchange(UpbitExchange.class.getName());

            exchanges = new ArrayList<>(Arrays.asList(binance, coinbasepro, kraken, upbit, gateio));
            ArrayList<Exchange> tempList = new ArrayList<>();
            for (int i = 0; i < exchanges.size(); i++){
                if (validExchanges.get(i) != 0){
                    tempList.add(exchanges.get(i));
                }
            }
            exchanges = tempList;
        });
    }

    public void setExchanges(ArrayList<Integer> validExchanges){
        exchanges = new ArrayList<>(Arrays.asList(binance, coinbasepro, kraken, upbit, gateio));
        ArrayList<Exchange> tempList = new ArrayList<>();
        for (int i = 0; i < exchanges.size(); i++){
            if (validExchanges.get(i) != 0){
                tempList.add(exchanges.get(i));
            }
        }
        exchanges = tempList;
    }

    /**
     * Updates the private allPairs ArrayList of all available pairs.
     *
     */
    protected void getAllCurrencyPairs() {
        this.allPairs = new ArrayList<>();
        for (Exchange exchange : exchanges) {
            ArrayList<CurrencyPair> exchangePairs = (ArrayList<CurrencyPair>) exchange.getExchangeSymbols();
            this.allPairs.addAll(exchangePairs);
        }
    }

    protected void getAllTickers() throws IOException {
        this.allTickers = new ArrayList<>();
        this.basesToUSD = new HashMap<>();

        for (Exchange exchange : exchanges) {
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

                if (price == 0){
                    continue;
                }

                Instrument instrument = ticker.getInstrument();

                TickerWithExchange newTicker = new TickerWithExchange(instrument, percentChange, exchange, price);

                this.allTickers.add(newTicker);

                Currency base = new Currency(instrument.toString().split("/")[0]);

                if (instrument.toString().contains("USD") && instrument.toString().indexOf("USD") > 1 && !basesToUSD.containsKey(base)){
                    basesToUSD.put(base, price);
                }
            }
        }

    }

    public ArrayList<String> getAllCurrencies() {
        ArrayList<String> allCurrencies = new ArrayList<>();
        if (this.allPairs == null) {
            getAllCurrencyPairs();
        }

        for (CurrencyPair pair : this.allPairs) {
            allCurrencies.add(pair.base.toString());
        }

        return allCurrencies;
    }

    public ArrayList<String> getAllCurrencyNames() {
        ArrayList<String> allCurrencies = new ArrayList<>();
        if (this.allPairs == null) {
            getAllCurrencyPairs();
        }

        for (CurrencyPair pair : this.allPairs) {
            allCurrencies.add(pair.base.getDisplayName());
        }

        return allCurrencies;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<TickerWithExchange>[] getArbitrage(ArrayList<Exchange> validExchanges, Currency currencySelected) {
        ArrayList<TickerWithExchange> opportunities = new ArrayList<>();
        allPossibilities = new HashMap<>();

        if (this.allTickers == null){
            try {
                getAllTickers();
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
            allPossibilities.putIfAbsent(base, new PossibilitiesPerCurrency(base));
            allPossibilities.get(base).add(ticker);

            // allPossibilities.putIfAbsent(counter, new PossibilitiesPerCurrency(counter));
            // allPossibilities.get(counter).add(ticker);
        }

        return new ArrayList[]{getTopOpportunities(false, currencySelected), getTopOpportunities(true, currencySelected)};
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected ArrayList<TickerWithExchange> getTopOpportunities(boolean top, Currency currencySelected){
        // currently pretty much works, only thing is sometimes (for current case with ETH), it decides that OAX is worth 0 but still adds it. Needs to check up on that.
        // Check if price in USD is good! i do not yet know

        ArrayList<TickerWithExchange> topOpportunities = new ArrayList<>(); // we will have 3 at most

        TickerWithExchange minGainer;
        if (top) { minGainer = new TickerWithExchange(15000d); }
        else { minGainer = new TickerWithExchange(0d); }

        for (TickerWithExchange ticker : this.allPossibilities.get(currencySelected).getPossibleTickers()){
            try{
                ticker.setToUSD(basesToUSD.get(ticker.getCounter()));

            } catch (NullPointerException e){
                // Log.i("Error", ticker.toString() + ", " + ticker.getCounter());
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
            else if (ticker.getPrice() != 0 && (top && minGainer.getPriceInUSD() < ticker.getPriceInUSD() || (!top && minGainer.getPriceInUSD() > ticker.getPriceInUSD()))) {
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
        topOpportunities.sort((o1, o2) -> Double.compare(o2.getPriceInUSD(), o1.getPriceInUSD()));
        if (!top) Collections.reverse(topOpportunities);
        return topOpportunities;
    }

    /**
     * Returns an arraylist, with either the highest % gainers, or the lowest % gainers.
     * Tickers pair must be present at least in one other exchange.
     * This is what you need to call for homepage. Will return 5 tickers.
     *
     * @param top            if we are looking for highest % change (true) or lowest % change (false)
     * @return the top gainers list
     * @throws IOException the io exception if ever .getTickers does not work (they have worked up to date of project. This was confirmed at 19:41 2022-12-08
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<TickerWithExchange> topGainers(boolean top) throws IOException {
        ArrayList<TickerWithExchange> topGainers = new ArrayList<>();
        Map<String, Integer> counter = new HashMap<String, Integer>();

        TickerWithExchange minGainer;

        if (top) minGainer = new TickerWithExchange(15000d);
        else minGainer = new TickerWithExchange(0d);

        boolean thereAreFive = false;

        if (this.allTickers == null) {
            getAllTickers();
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
        topGainers.sort((o1, o2) -> Double.compare(o1.getPercentChange(), o2.getPercentChange()));
        if (top) Collections.reverse(topGainers);
        return topGainers;
    }


}
