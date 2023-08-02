package com.example.myapp.New;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapp.DetailsActivity;
import com.example.myapp.Favorite.FavoriteViewAdapter;
import com.example.myapp.R;

import java.util.ArrayList;
import java.util.Collections;

/* Part of this code has been referenced from the following blog/article/tutorial
URL:https://www.youtube.com/watch?v=fis26HvvDII - 9:16:35*/
public class NewViewAdapter extends RecyclerView.Adapter<NewViewAdapter.ViewHolder> {

    private ArrayList<New> news=new ArrayList<>();
    Context context;
    public NewViewAdapter(Context context){
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_list_item,parent,false);
        ViewHolder holder = new NewViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        New new_=news.get(position);
        holder.headline.setText(new_.getHeadline());
        Glide.with(context)
                .load(new_.getImage())
                .into(holder.image);
        holder.source.setText(new_.getSource());
        holder.hours.setText (new_.getDiff ()+" hours ago");
        holder.card.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custome_layout_dialog);
                TextView d_source=dialog.findViewById (R.id.d_source);
                d_source.setText (new_.getSource());
                TextView d_date=dialog.findViewById (R.id.d_date);
                d_date.setText (new_.getPubdate ());
                TextView d_head=dialog.findViewById (R.id.d_headline);
                d_head.setText (new_.getHeadline ());
                TextView d_summary=dialog.findViewById (R.id.d_summary);
                d_summary.setText (new_.getSummary ());
                ImageButton chrome=dialog.findViewById (R.id.chrome);
                ImageButton facebook=dialog.findViewById (R.id.facebook);
                ImageButton twitter=dialog.findViewById (R.id.twitter);
                String facebook_link="https://www.facebook.com/sharer/sharer.php?u="+new_.getUrl ()+"&amp;src=sdkpreparse";
                String twitter_link="https://twitter.com/intent/tweet?text="+new_.getHeadline ()+"&url="+new_.getUrl ();
                chrome.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent (Intent.ACTION_VIEW);
                        i.setData(Uri.parse(new_.getUrl ()));
                        context.startActivity(i);
                    }
                });
                facebook.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent (Intent.ACTION_VIEW);
                        i.setData(Uri.parse(facebook_link));
                        context.startActivity(i);
                    }
                });
                twitter.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent (Intent.ACTION_VIEW);
                        i.setData(Uri.parse(twitter_link));
                        context.startActivity(i);
                    }
                });

                dialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public void setNews(ArrayList<New> news){
        this.news=news;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        news.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(New item,int position){
        news.add(position,item);
        notifyItemInserted(position);
    }

    public ArrayList<New> getNews(){
        return this.news;
    }

    public void onRowMoved(int fromPosition,int toPosition){
        if(fromPosition<toPosition){
            for(int i=fromPosition;i<toPosition;i++){
                Collections.swap(news,i,i+1);
            }
        } else {
            for(int i=fromPosition;i>toPosition;i--){
                Collections.swap(news,i,i-1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
    }

    public void onRowSelected(ViewHolder myViewHolder){
        myViewHolder.itemView.setBackgroundColor(Color.GRAY);
    }

    public void onRowClear(FavoriteViewAdapter.ViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        //private TextView pubdate;
        private TextView headline;
        private ImageView image;
        private TextView source;
        //private TextView summary;
        //private TextView url;
        private CardView card;
        private TextView hours;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headline=itemView.findViewById(R.id.news_headline);
            image=itemView.findViewById(R.id.news_image);
            source=itemView.findViewById(R.id.news_source);
            card=itemView.findViewById (R.id.card);
            hours=itemView.findViewById (R.id.news_hours);
        }
    }
}
