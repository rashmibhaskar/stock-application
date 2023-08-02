package com.example.myapp.Portfolio;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.DetailsActivity;
import com.example.myapp.R;

import java.io.ObjectInputStream;
import java.util.ArrayList;

/* Part of this code has been referenced from the following blog/article/tutorial
URL:https://www.youtube.com/watch?v=fis26HvvDII - 9:16:35*/
public class PortfolioViewAdapter extends RecyclerView.Adapter<PortfolioViewAdapter.ViewHolder> {

    private ArrayList<Portfolio> portfolios=new ArrayList<>();
    Context context;
    public PortfolioViewAdapter(Context context){
    this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    public ArrayList<Portfolio> getPortfolios() {
        return portfolios;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        Portfolio portfolio = portfolios.get(position);

        holder.tickName.setText(portfolio.getTicker());
        holder.shares.setText(String.format("%d shares",portfolio.getShares()));
        holder.amount.setText(String.format("$%,.2f",portfolio.getMarketValue ()));
        Log.d("GET",portfolio.getChangedprice().toString ());
        Log.d("GET",portfolio.getChangepercent ().toString ());
        if(portfolio.getChangedprice ()>0){
            holder.p_changePrice.setText(String.format(" $%,.2f ",portfolio.getChangedprice()));
            holder.p_changePercent.setText(String.format("(%,.2f%%)",portfolio.getChangepercent()));
            holder.p_changePrice.setTextColor(ContextCompat.getColor(context,R.color.green));
            holder.p_changePercent.setTextColor(ContextCompat.getColor(context,R.color.green));
            holder.trending.setBackgroundResource(R.drawable.ic_trending_up);
        }
        else if(portfolio.getChangedprice ()<0){
            holder.p_changePrice.setText(String.format(" $%,.2f ",portfolio.getChangedprice()));
            holder.p_changePercent.setText(String.format("(%,.2f%%)",portfolio.getChangepercent()));
            holder.p_changePrice.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.p_changePercent.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.trending.setBackgroundResource(R.drawable.ic_trending_down);
        }
        else{
            holder.p_changePrice.setText(String.format(" $%,.2f ",portfolio.getChangedprice()));
            holder.p_changePercent.setText(String.format("(%,.2f%%)",portfolio.getChangepercent()));
            holder.p_changePrice.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.p_changePercent.setTextColor(ContextCompat.getColor(context,R.color.black));
            holder.trending.setBackgroundResource (0);
        }
        holder.chevronButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent (context, DetailsActivity.class);
                i.putExtra("tickerName",portfolio.getTicker());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return portfolios.size();
    }

    public void setPortfolios(ArrayList<Portfolio> portfolios){
        this.portfolios=portfolios;
//        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tickName;
        private TextView shares;
        private TextView amount;
        private ImageView trending;
        private TextView p_changePrice;
        private TextView p_changePercent;
        private ImageButton chevronButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tickName=itemView.findViewById(R.id.tickName);
            shares=itemView.findViewById(R.id.shares);
            amount=itemView.findViewById(R.id.amount);
            trending=itemView.findViewById(R.id.trending);
            p_changePrice=itemView.findViewById(R.id.p_changePrice);
            p_changePercent=itemView.findViewById(R.id.p_changePercent);
            chevronButton=itemView.findViewById(R.id.chevronId);
        }
    }
}
