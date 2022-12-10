package com.example.projetfinal;

import com.google.common.base.Ticker;

import java.util.ArrayList;

public class PossibilitiesPerCurrency {
    private ArrayList<TickerWithExchange> possibleTickers;
    private TickerWithExchange toUSD = null;

    public PossibilitiesPerCurrency(){
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
