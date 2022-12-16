package com.example.projetfinal;

import org.knowm.xchange.currency.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The type Possibilities per currency.
 * For each currency, all that you can buy it in.
 */
public class PossibilitiesPerCurrency {
    private ArrayList<TickerWithExchange> possibleTickers;
    private TickerWithExchange toUSD = null;
    private Currency currency = null;

    /**
     * Instantiates a new Possibilities per currency.
     *
     * @param currency the currency
     */
    public PossibilitiesPerCurrency(Currency currency){
        this.currency = currency;
        possibleTickers = new ArrayList<>();
    }

    /**
     * Gets possible tickers.
     *
     * @return the possible tickers
     */
    public ArrayList<TickerWithExchange> getPossibleTickers() {
        return possibleTickers;
    }

    /**
     * Sets possible tickers.
     *
     * @param possibleTickers the possible tickers
     */
    public void setPossibleTickers(ArrayList<TickerWithExchange> possibleTickers) {
        this.possibleTickers = possibleTickers;
    }

    /**
     * Gets the price to usd.
     *
     * @return the to usd
     */
    public TickerWithExchange getToUSD() {
        return toUSD;
    }

    /**
     * Sets to usd.
     *
     * @param toUSD the to usd
     */
    public void setToUSD(TickerWithExchange toUSD) {
        this.toUSD = toUSD;
    }

    /**
     * Add.
     *
     * @param ticker the ticker
     */
    public void add(TickerWithExchange ticker){
        possibleTickers.add(ticker);
        
        if (toUSD == null && ticker.getInstrument().toString().split("/")[1].contains("USD")){
            toUSD = ticker;
        }
    }
}
