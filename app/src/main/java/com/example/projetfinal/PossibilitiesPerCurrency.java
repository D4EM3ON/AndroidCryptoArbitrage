package com.example.projetfinal;

import java.util.ArrayList;

/**
 * The type Possibilities per currency.
 * For each currency, all that you can buy it in.
 * @author Justin Quirion
 */
public class PossibilitiesPerCurrency {
    private ArrayList<TickerWithExchange> possibleTickers;
    private TickerWithExchange toUSD = null;

    /**
     * Instantiates a new Possibilities per currency.
     *
     */
    public PossibilitiesPerCurrency(){
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
     * Adds a ticker to the list.
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
