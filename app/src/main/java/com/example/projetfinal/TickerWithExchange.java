package com.example.projetfinal;

import androidx.annotation.NonNull;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.CandleStick;
import org.knowm.xchange.dto.marketdata.CandleStickData;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.trade.params.DefaultCandleStickParam;

import java.io.IOException;
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

    public double getToUSD() {
        return toUSD;
    }

    public void setToUSD(double toUSD) {
        this.toUSD = toUSD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets candle sticks of the current ticker on the current exchange.
     *
     * @return the candle sticks
     */
    public CandleStickData getCandleSticks() {
        DefaultCandleStickParam candleStickDataParams = new DefaultCandleStickParam(new Date(new Date().getTime() - 1000*60*60*24), new Date(), 60);
        CandleStickData data = new CandleStickData(instrument, new ArrayList<CandleStick>());
        try {
            data = exchange.getMarketDataService().getCandleStickData((CurrencyPair) instrument, candleStickDataParams);
        } catch (IOException e) {
            data = null;
        }
        return data;
    }

    public Currency getBase(){
        return new Currency(this.instrument.toString().split("/")[0]);
    }

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
     * Gets price.
     *
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     */
    public void setPrice(double price) {
        this.price = price;
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

    public double getPriceInUSD(){
        return this.toUSD * this.price;
    }

    /**
     * Gets instrument.
     *
     * @return the instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * Sets instrument.
     *
     * @param instrument the instrument
     */
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Gets percent change.
     *
     * @return the percent change
     */
    public double getPercentChange() {
        return percentChange;
    }

    /**
     * Sets percent change.
     *
     * @param percentChange the percent change
     */
    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    /**
     * Gets exchange.
     *
     * @return the exchange
     */
    public Exchange getExchange() {
        return exchange;
    }

    /**
     * Sets exchange.
     *
     * @param exchange the exchange
     */
    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
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


    public int compareTo(TickerWithExchange t2) {
        boolean tf = this.getPriceInUSD() == t2.getPriceInUSD();
        if (tf) return 0;
        return 1;
    }
}
