package com.example.projetfinal;

import com.google.common.base.Ticker;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;

import java.util.Objects;

public class TickerWithExchange {
    private Instrument instrument;
    private double percentChange;
    private Exchange exchange;

    public TickerWithExchange(Instrument instrument, double percentChange, Exchange exchange) {
        this.instrument = instrument;
        this.percentChange = percentChange;
        this.exchange = exchange;
    }

    public TickerWithExchange(double percentChange){
        this.instrument = null;
        this.percentChange = percentChange;
        this.exchange = null;
    }

    public TickerWithExchange(TickerWithExchange ticker){
        this.percentChange = ticker.percentChange;
        this.instrument = ticker.instrument;
        this.exchange = ticker.exchange;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return "TickerWithExchange{" +
                "instrument=" + instrument +
                ", percentChange=" + percentChange +
                ", exchange=" + exchange +
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
        return Objects.hash(instrument, percentChange, exchange);
    }
}
