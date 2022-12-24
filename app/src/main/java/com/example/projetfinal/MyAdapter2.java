package com.example.projetfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * The type My adapter 2.
 */
public class MyAdapter2  extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    /**
     * The Instruments.
     */
    ArrayList<String> instruments = null, /**
     * The Exchanges.
     */
    exchanges = null, /**
     * The Percent changes.
     */
    percentChanges = null, /**
     * The Prices.
     */
    prices = null, /**
     * The Instrument names.
     */
    instrumentNames = null;

    /**
     * Instantiates a new My adapter 2.
     *
     * @param s1 the instruments
     * @param s2 the exchanges
     * @param s3 the percent changes
     * @param s4 the prices
     * @param s5 the instrument names
     */
    public MyAdapter2(ArrayList<String> s1, ArrayList<String> s2, ArrayList<String> s3, ArrayList<String> s4, ArrayList<String> s5){
        this.instruments = s1;
        this.exchanges = s2;
        this.percentChanges = s3;
        this.prices = s4;
        this.instrumentNames = s5;

    }

    @NonNull
    @Override
    public MyAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_row,parent, false);
        return new MyAdapter2.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter2.MyViewHolder holder, int position) {
        holder.instrument.setText(instruments.get(position));
        holder.exchange.setText(exchanges.get(position));
        holder.percentage.setText(percentChanges.get(position));
        holder.price.setText(prices.get(position));
        holder.nameCurrency.setText(instrumentNames.get(position));
    }

    @Override
    public int getItemCount() {
        return instruments.size();
    }

    /**
     * MyViewHolder , to hold the view
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        /**
         * The Instrument.
         */
        TextView instrument, /**
         * The Name currency.
         */
        nameCurrency, /**
         * The Percentage.
         */
        percentage, price, exchange;

        /**
         * Instantiates a new My view holder.
         *
         * @param itemView the item view
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            instrument = itemView.findViewById(R.id.instrument);
            nameCurrency = itemView.findViewById(R.id.nameCurrency);
            percentage = itemView.findViewById(R.id.percentage_txt);
            price = itemView.findViewById(R.id.price_txt);
            exchange = itemView.findViewById(R.id.exchange);

        }

    }

    }
