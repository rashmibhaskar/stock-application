package com.example.myapp.Favorite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.example.myapp.Portfolio.Portfolio;
import com.example.myapp.R;

import java.util.ArrayList;
import java.util.Collections;

/* Part of this code has been referenced from the following blog/article/tutorial
URL:https://www.youtube.com/watch?v=fis26HvvDII - 9:16:35*/
public class FavoriteViewAdapter extends RecyclerView.Adapter<FavoriteViewAdapter.ViewHolder> {

    private ArrayList<Favorite> favorites=new ArrayList<>();
    Context context;
    public FavoriteViewAdapter(Context context){
        this.context=context;
        Log.d("LOL", "FavoriteViewAdapter: jkjk");
        System.out.println(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Favorite favorite=favorites.get(position);
        holder.tickerName.setText(favorite.getTicker());
        holder.companyName.setText(favorite.getCompanyName());
        holder.amount.setText(String.format("$%,.2f",favorite.getAmount()));
        if(favorite.getChangepercent()>0){
            holder.changePrice.setText(String.format(" $%,.2f ",favorite.getChangedprice()));
            holder.changePercent.setText(String.format("(%,.2f%%)",favorite.getChangepercent()));
            holder.changePrice.setTextColor(ContextCompat.getColor(context,R.color.green));
            holder.changePercent.setTextColor(ContextCompat.getColor(context,R.color.green));
            holder.trending.setBackgroundResource(R.drawable.ic_trending_up);
        }
        else{
            holder.changePrice.setText(String.format(" $%,.2f ",favorite.getChangedprice()));
            holder.changePercent.setText(String.format("(%,.2f%%)",favorite.getChangepercent()));
            holder.changePrice.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.changePercent.setTextColor(ContextCompat.getColor(context,R.color.red));
            holder.trending.setBackgroundResource(R.drawable.ic_trending_down);
        }
        holder.chevronButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent (context, DetailsActivity.class);
                i.putExtra("tickerName",favorite.getTicker());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public void setFavorites(ArrayList<Favorite> favorites){
        this.favorites=favorites;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        Log.d("REMOVEE",favorites.toString ());
        favorites.remove(position);
        notifyItemRemoved(position);
        Log.d("REMOVE",favorites.toString ());
    }

    public void restoreItem(Favorite item, int position) {
        favorites.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<Favorite> getFavorites(){
        return this.favorites;
    }

    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(favorites, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(favorites, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.GRAY);

    }

    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tickerName;
        private TextView companyName;
        private ImageButton chevronButton;
        private TextView amount;
        private TextView changePrice;
        private TextView changePercent;
        private ImageView trending;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tickerName=itemView.findViewById(R.id.tickerName);
            companyName=itemView.findViewById(R.id.companyName);
            chevronButton=itemView.findViewById(R.id.chevronId);
            amount=itemView.findViewById(R.id.fav_amount);
            changePrice=itemView.findViewById(R.id.changePrice);
            changePercent=itemView.findViewById(R.id.changePercent);
            trending=itemView.findViewById(R.id.trending);
        }
    }

}
