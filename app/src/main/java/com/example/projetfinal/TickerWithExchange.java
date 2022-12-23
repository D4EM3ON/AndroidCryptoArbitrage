package com.example.projetfinal;

import androidx.annotation.NonNull;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.CandleStick;
import org.knowm.xchange.dto.marketdata.CandleStickData;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.params.DefaultCandleStickParam;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * The type Ticker with exchange, could have been a Ticker extension but this was only discovered after everything was built. Therefore, we won't be adding the extension
 */
public class TickerWithExchange {
    private Instrument instrument;
    private double percentChange;
    private Exchange exchange;
    private double price;
    private String name;
    private double toUSD = 0;

    /**
     * Gets the conversion to usd.
     *
     * @return the to usd
     */
    public double getToUSD() {
        return toUSD;
    }

    /**
     * Sets the conversion to usd.
     *
     * @param toUSD the to usd
     */
    public void setToUSD(double toUSD) {
        this.toUSD = toUSD;
    }

    /**
     * Gets the display name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets candle sticks of the current ticker on the current exchange.
     *
     * @return the candle sticks
     */
    public CandleStickData getCandleSticks() {
        DefaultCandleStickParam candleStickDataParams = new DefaultCandleStickParam(new Date(new Date().getTime() - 1000*60*60*24), new Date(), 60);
        CandleStickData data = new CandleStickData(instrument, new ArrayList<>());
        try {
            data = exchange.getMarketDataService().getCandleStickData((CurrencyPair) instrument, candleStickDataParams);
        } catch (IOException e) {
            data = null;
        }
        return data;
    }

    /**
     * Gets the counter currency.
     *
     * @return the currency
     */
    public Currency getCounter(){
        return new Currency(this.instrument.toString().split("/")[1]);
    }

    /**
     * Refreshes the price and percent change of the ticker.
     */
    public void refresh() {
        try {
            price = exchange.getMarketDataService().getTicker(instrument).getLast().doubleValue();
            percentChange = exchange.getMarketDataService().getTicker(instrument).getLast().doubleValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Instantiates a new Ticker with exchange, from all predetermined variables
     *
     * @param instrument    the instrument
     * @param percentChange the percent change
     * @param exchange      the exchange
     * @param price         the price
     */
    public TickerWithExchange(Instrument instrument, double percentChange, Exchange exchange, double price) {
        this.instrument = instrument;
        this.percentChange = percentChange;
        this.exchange = exchange;
        this.price = price;
        CurrencyPair currencyPair = (CurrencyPair) instrument;
        this.name = currencyPair.base.getDisplayName();
    }

    /**
     * Instantiates a new Ticker with exchange, from solely a percent change
     *
     * @param percentChange the percent change
     */
    public TickerWithExchange(double percentChange){
        this.instrument = null;
        this.percentChange = percentChange;
        this.exchange = null;
        this.name = null;
        this.price = 0d;
    }

    /**
     * Instantiates a new Ticker with exchange, from another TickerWithExchange ticker
     *
     * @param ticker the ticker
     */
    public TickerWithExchange(TickerWithExchange ticker){
        this.percentChange = ticker.percentChange;
        this.instrument = ticker.instrument;
        this.exchange = ticker.exchange;
        this.name = ticker.name;
        this.price = ticker.price;
        this.toUSD = ticker.toUSD;
    }

    /**
     * Get price in USD .
     *
     * @return the double
     */
    public Double getPriceInUSD(){

        return this.price * this.toUSD;
    }

    /**
     * Gets the instrument.
     *
     * @return the instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * Gets the percent change.
     *
     * @return the percent change
     */
    public double getPercentChange() {
        return percentChange;
    }

    /**
     * Gets the exchange.
     *
     * @return the exchange
     */
    public Exchange getExchange() {
        return exchange;
    }

    @NonNull
    @Override
    public String toString() {
        return "TickerWithExchange{" +
                "instrument=" + instrument +
                ", percentChange=" + percentChange +
                ", exchange=" + exchange +
                ", price=" + price +
                ", price in USD=" + getPriceInUSD() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TickerWithExchange that = (TickerWithExchange) o;
        return Double.compare(that.percentChange, percentChange) == 0 && Objects.equals(instrument, that.instrument) && Objects.equals(exchange, that.exchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrument, percentChange, exchange, price);
    }


    /**
     * Compare to int.
     *
     * @param t2 the ticker we are comparing to
     * @return if it is equal or not
     */
    public int compareTo(TickerWithExchange t2) {
        boolean tf = Objects.equals(this.getPriceInUSD(), t2.getPriceInUSD());
        if (tf) return 0;
        return 1;
    }
}
