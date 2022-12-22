package com.example.projetfinal;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import java.util.Objects;
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
    private MutableLiveData<ArrayList<Exchange>> exchanges = new MutableLiveData<>();
    private MutableLiveData<ArrayList<CurrencyPair>> allPairs = new MutableLiveData<>();
    private MutableLiveData<ArrayList<TickerWithExchange>> allTickers = new MutableLiveData<>();
    private Map<Currency, PossibilitiesPerCurrency> allPossibilities;
    private MutableLiveData<Map<String, Double>> basesToUSD = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Currency>> allCurrencies = new MutableLiveData<>();
    private MutableLiveData<ArrayList<TickerWithExchange>> maxGainers = new MutableLiveData<>();
    private MutableLiveData<ArrayList<TickerWithExchange>> minGainers = new MutableLiveData<>();


    Exchange binance;
    Exchange coinbasepro;
    Exchange kraken;
    Exchange gateio;
    Exchange upbit;
    /**
     * Instantiates a new Registry.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Registry(ArrayList<Integer> validExchanges) {

        exchanges.observeForever(e -> {
            try {
                getAllTickers(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            getAllCurrencyPairs(e);
        });

        allTickers.observeForever(e ->{
            try {
                makeTopGainers(e, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                makeTopGainers(e, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        allPairs.observeForever(this::updateAllCurrencies);

        Executors.newSingleThreadExecutor().execute(() -> {
            binance = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
            coinbasepro = ExchangeFactory.INSTANCE.createExchange(CoinbaseProExchange.class.getName());
            kraken = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());
            gateio = ExchangeFactory.INSTANCE.createExchange(GateioExchange.class.getName());
            upbit = ExchangeFactory.INSTANCE.createExchange(UpbitExchange.class.getName());

            ArrayList<Exchange> allExchanges = new ArrayList<>(Arrays.asList(binance, coinbasepro, kraken, upbit, gateio));
            ArrayList<Exchange> tempList = new ArrayList<>();
            for (int i = 0; i < allExchanges.size(); i++){
                if (validExchanges.get(i) != 0){
                    tempList.add(allExchanges.get(i));
                }
            }
            exchanges.postValue(tempList);
        });
    }

    public void setExchanges(ArrayList<Integer> validExchanges){
        ArrayList<Exchange> allExchanges = new ArrayList<>(Arrays.asList(binance, coinbasepro, kraken, upbit, gateio));
        ArrayList<Exchange> tempList = new ArrayList<>();
        for (int i = 0; i < allExchanges.size(); i++){
            if (validExchanges.get(i) != 0){
                tempList.add(allExchanges.get(i));
            }
        }
        exchanges.postValue(tempList);
    }

    /**
     * Updates the private allPairs ArrayList of all available pairs.
     *
     */
    protected void getAllCurrencyPairs(ArrayList<Exchange> validExchanges) {

        ArrayList<CurrencyPair> tempCurrencies = new ArrayList<>();
        for (Exchange exchange : validExchanges) {
            ArrayList<CurrencyPair> exchangePairs = (ArrayList<CurrencyPair>) exchange.getExchangeSymbols();
            tempCurrencies.addAll(exchangePairs);
        }
        this.allPairs.postValue(tempCurrencies);
    }

    protected void getAllTickers(ArrayList<Exchange> validExchanges) throws IOException {
        ArrayList<TickerWithExchange> tempTickers = new ArrayList<>();
        HashMap<String, Double> tempBases = new HashMap<>();

        tempBases.put(Currency.USDT.toString(), 1d);
        tempBases.put(Currency.BUSD.toString(), 1d);
        tempBases.put(Currency.USDC.toString(), 1d);

        Executors.newSingleThreadExecutor().execute(()->{
            for (Exchange exchange : validExchanges) {
                CurrencyPairsParam tickerList = new CurrencyPairsParam() {
                    @Override
                    public Collection<CurrencyPair> getCurrencyPairs() {
                        return exchange.getExchangeSymbols();
                    }
                };

                try {
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

                        tempTickers.add(newTicker);

                        Currency base = new Currency(instrument.toString().split("/")[0]);

                        if (instrument.toString().contains("USD") && instrument.toString().indexOf("USD") > 1 && !tempBases.containsKey(base)){
                            tempBases.put(base.toString(), price);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.allTickers.postValue(tempTickers);
            this.basesToUSD.postValue(tempBases);

        });

    }

    private void updateAllCurrencies(ArrayList<CurrencyPair> currencyPairs) {
        ArrayList<Currency> tempCurrencies = new ArrayList<>();

        for (CurrencyPair pair : currencyPairs) {
            tempCurrencies.add(pair.base);
        }
        this.allCurrencies.postValue(tempCurrencies);
    }

    public LiveData<ArrayList<Currency>> getAllCurrencies(){
        return this.allCurrencies;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<TickerWithExchange>[] getArbitrage(Currency currencySelected) {
        ArrayList<TickerWithExchange> opportunities = new ArrayList<>();
        this.allPossibilities = new HashMap<>();

        // we build a hashmap for every currency to look for all possibilities
        for (TickerWithExchange ticker : Objects.requireNonNull(this.allTickers.getValue())){
            CurrencyPair currencyPair = (CurrencyPair) ticker.getInstrument();
            Currency base = currencyPair.base;
            Currency counter = currencyPair.counter;

            allPossibilities.putIfAbsent(base, new PossibilitiesPerCurrency(base));
            allPossibilities.get(base).add(ticker);
        }

        return new ArrayList[]{getTopOpportunities(false, currencySelected), getTopOpportunities(true, currencySelected)};
    }

    public double setTickerUSD(TickerWithExchange ticker){
        try {
            ticker.setToUSD(basesToUSD.getValue().get(ticker.getCounter().toString()));
        } catch (NullPointerException ignored){
        }
        return ticker.getToUSD();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<TickerWithExchange> getTopOpportunities(boolean top, Currency currencySelected){
        // currently pretty much works, only thing is sometimes (for current case with ETH), it decides that OAX is worth 0 but still adds it. Needs to check up on that.
        // Check if price in USD is good! i do not yet know

        ArrayList<TickerWithExchange> topOpportunities = new ArrayList<>(); // we will have 3 at most

        TickerWithExchange minGainer;
        if (top) { minGainer = new TickerWithExchange(15000d); }
        else { minGainer = new TickerWithExchange(0d); }

        for (TickerWithExchange ticker : this.allPossibilities.get(currencySelected).getPossibleTickers()){

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
    public void makeTopGainers(ArrayList<TickerWithExchange> validTickers, boolean top) throws IOException {
        ArrayList<TickerWithExchange> topGainers = new ArrayList<>();


        Map<String, Integer> counter = new HashMap<>();

        TickerWithExchange minGainer;

        if (top) minGainer = new TickerWithExchange(15000d);
        else minGainer = new TickerWithExchange(0d);

        boolean thereAreFive = false;

        for (TickerWithExchange ticker : validTickers) {

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
        if (top){
            Collections.reverse(topGainers);
            maxGainers.postValue(topGainers);
        }
        else{
            minGainers.postValue(topGainers);
        }
    }

    public LiveData<ArrayList<TickerWithExchange>> getMaxGainers(){
        return maxGainers;
    }

    public LiveData<ArrayList<TickerWithExchange>> getMinGainers(){
        return minGainers;
    }

}