package com.example.projetfinal;

import org.knowm.xchange.currency.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PossibilitiesPerCurrency {
    private ArrayList<TickerWithExchange> possibleTickers;
    private TickerWithExchange toUSD = null;
    private Currency currency = null;

    public PossibilitiesPerCurrency(Currency currency){
        this.currency = currency;
        possibleTickers = new ArrayList<>();
    }

    public ArrayList<TickerWithExchange> getPossibleTickers() {
        return possibleTickers;
    }

    public void setPossibleTickers(ArrayList<TickerWithExchange> possibleTickers) {
        this.possibleTickers = possibleTickers;
    }

    public TickerWithExchange getToUSD() {
        return toUSD;
    }

    public void setToUSD(TickerWithExchange toUSD) {
        this.toUSD = toUSD;
    }

    public void add(TickerWithExchange ticker){
        possibleTickers.add(ticker);
        
        if (toUSD == null && ticker.getInstrument().toString().split("/")[1].contains("USD")){
            toUSD = ticker;
        }
    }
}
