package com.example.projetfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    //s1= abreviation
    //s2= nom
    //s3= pourcentage
    //s4= prix

    List<String> s1, s2,s3,s4;

    public MyAdapter(List<String> s1, List<String> s2, List<String> s3, List<String> s4){
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
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
        holder.companyAB.setText(s1.get(position));
        holder.company.setText(s2.get(position));
        holder.percentage.setText(s3.get(position));
        holder.price.setText(s4.get(position));

    }

    @Override
    //faire des recyclerView autant qu'il y a d'éléments s1.
    public int getItemCount() {
        return s1.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView companyAB, company, percentage, price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            companyAB = itemView.findViewById(R.id.companyAB_txt);
            company = itemView.findViewById(R.id.company_txt);
            percentage = itemView.findViewById(R.id.percentage_txt);
            price = itemView.findViewById(R.id.price_txt);
        }
    }
}
