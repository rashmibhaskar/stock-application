package com.example.myapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/*This code has been referenced from the following blog/article/tutorial
URL:https://androidwave.com/viewpager2-with-tablayout-android-example/ */
public class ChartFragment extends Fragment {

    private static final String ARG_COUNT = "param1";
    private Integer counter;


    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance(Integer counter) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        String ticker = args.getString("ticker");
        Log.d("TICKER",ticker);
        TextView textViewCounter = view.findViewById(R.id.tv_counter);
        Integer counter=args.getInt("counter");
        textViewCounter.setText("Fragment No " + (counter+1));
        Log.d("COUNTER",counter.toString());
        WebView mWebView = view.findViewById(R.id.myHtmlView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if(counter==0) {
            mWebView.loadUrl("file:///android_asset/hourlycharts.html");
            mWebView.setWebViewClient(new WebViewClient(){
                public void onPageFinished(WebView view, String url){
                    mWebView.loadUrl("javascript:fetchStockPrice('"+ticker+"')");
                }
            });
        }
        else{
            mWebView.loadUrl("file:///android_asset/dailycharts.html");
            mWebView.setWebViewClient(new WebViewClient(){
                public void onPageFinished(WebView view, String url){
                    mWebView.loadUrl("javascript:fetchCharts('"+ticker+"')");
                }
            });
        }
    }
}