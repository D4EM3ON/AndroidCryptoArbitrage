package com.example.projetfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    String[] ab, name,percentage,price;

    public MyAdapter(String[] s1, String[] s2, String[] s3, String[] s4){
        ab = s1;
        name = s2;
        percentage = s3;
        price = s4;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_row,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.companyAB.setText(ab[position]);
        holder.company.setText(name[position]);
        holder.percentage.setText(percentage[position]);
        holder.price.setText(price[position]);

    }

    @Override
    public int getItemCount() {
        return ab.length;
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
