package com.example.projetfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * création d'un adaptateur pour l'utilisation des recyclerViews
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<String> instruments = null, exchanges = null, percentChanges = null, prices = null, instrumentNames = null;
    private RecyclerViewClickListener listener;

    /**
     * constructeur adapteur avec les informations pre déterminé
     *
     * @param s1 List instruments
     * @param s2 list exchanges
     * @param s3 list percentChanges
     * @param s4 list Prices
     * @param s5 list instrumentNames
     * @param listener listener
     */
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
    /**
     * @Return le viewHolder
     */
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_row,parent, false);
        return new MyViewHolder(view);

    }

    @Override
    /**
     * set le text selon les différentes positions des lists.
     *
     */
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.instrument.setText(instruments.get(position));
        holder.exchange.setText(exchanges.get(position));
        holder.percentage.setText(percentChanges.get(position));
        holder.price.setText(prices.get(position));
        holder.nameCurrency.setText(instrumentNames.get(position));

    }

    @Override
    /**
     * retourne le nombre de fois que le recyclerView va se faire.
     *
     * @Return size
     */
    public int getItemCount() {
        return instruments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView instrument, nameCurrency, percentage, price, exchange;

        /**
         *link by ID.
         * @param itemView connecter les bonnes informations des différentes lists  au bon item.
         */
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


}
