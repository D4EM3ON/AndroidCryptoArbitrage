package com.example.projetfinal;

import java.util.Comparator;

public class TickerWithExchangeComparator implements Comparator<TickerWithExchange> {
    @Override
    public int compare(TickerWithExchange t1, TickerWithExchange t2) {
        return t1.compareTo(t2);
    }
}
