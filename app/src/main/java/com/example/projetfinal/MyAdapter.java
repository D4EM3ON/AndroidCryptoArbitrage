package com.example.projetfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    ArrayList<String> instruments = null, exchanges = null, percentChanges = null, prices = null, instrumentNames = null;
    private RecyclerViewClickListener listener;
    public MyAdapter(ArrayList<String> s1, ArrayList<String> s2, ArrayList<String> s3, ArrayList<String> s4, ArrayList<String> s5,RecyclerViewClickListener listener){
        this.instruments = s1;
        this.exchanges = s2;
        this.percentChanges = s3;
        this.prices = s4;
        this.instrumentNames = s5;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_row,parent, false);
        return new MyViewHolder(view);

    }

    @Override
    //éléments du recyclerView selon les list assccié au ticker
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.instrument.setText(instruments.get(position));
        holder.exchange.setText(exchanges.get(position));
        holder.percentage.setText(percentChanges.get(position));
        holder.price.setText(prices.get(position));
        holder.nameCurrency.setText(instrumentNames.get(position));

    }

    @Override
    //faire des recyclerView autant qu'il y a d'éléments s1.
    public int getItemCount() {
        return instruments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView instrument, nameCurrency, percentage, price, exchange;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            instrument = itemView.findViewById(R.id.instrument);
            nameCurrency = itemView.findViewById(R.id.nameCurrency);
            percentage = itemView.findViewById(R.id.percentage_txt);
            price = itemView.findViewById(R.id.price_txt);
            exchange = itemView.findViewById(R.id.exchange);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition());
        }
    }
    public interface RecyclerViewClickListener{
         void onClick(View v, int position);
    }

    public ArrayList<String> getInstruments() {
        return instruments;
    }
}
