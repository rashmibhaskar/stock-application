package com.example.myapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/*This code has been referenced from the following blog/article/tutorial
URL:https://androidwave.com/viewpager2-with-tablayout-android-example/ */
public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 2;
    String ticker;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,String ticker) {
        super(fragmentActivity);
        this.ticker=ticker;
    }


    @NonNull @Override public Fragment createFragment(int position) {
        ChartFragment f = new ChartFragment();
//        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("ticker", ticker);
        args.putInt("counter",position);
        f.setArguments(args);
        return f;
        //return ChartFragment.newInstance(position);
    }
    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}
