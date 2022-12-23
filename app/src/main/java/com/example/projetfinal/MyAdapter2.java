package com.example.projetfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter2  extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {

    ArrayList<String> instruments = null, exchanges = null, percentChanges = null, prices = null, instrumentNames = null;

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
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView instrument, nameCurrency, percentage, price, exchange;

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
