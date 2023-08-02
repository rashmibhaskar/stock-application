package com.example.myapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/* Part of this code has been referenced from the following blog/article/tutorial
URL:https://www.truiton.com/2018/06/android-autocompletetextview-suggestions-from-webservice-call/ */
public class AutocompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> mlistData;
    public AutocompleteAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mlistData=new ArrayList<>();
    }

    @Override
    public int getCount(){
        return mlistData.size();
    }

    @Nullable
    @Override
    public String getItem(int position){
        return mlistData.get(position);
    }

    public String getObject(int position) {
        return mlistData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = mlistData;
                    filterResults.count = mlistData.size();
                }
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }

    public void setData(List<String> stringList) {
        mlistData.clear();
        mlistData.addAll(stringList);
    }
}
