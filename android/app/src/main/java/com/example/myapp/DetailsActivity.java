package com.example.myapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapp.New.New;
import com.example.myapp.New.NewViewAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

/*This code has been referenced from the following blog/article/tutorial
URL:https://androidwave.com/viewpager2-with-tablayout-android-example/ */
public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    JSONArray mynews_list;
    String tickerName;

    private Bundle savedInstanceState;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ArrayList<New> news = new ArrayList<>();
    private RecyclerView newsView;

    NewViewAdapter n_adapter= new NewViewAdapter(this);

    ArrayList<JSONObject> fav_arrList=new ArrayList<> ();
    RelativeLayout rl;
    RelativeLayout pb;
    Button trade;
    CardView news1;
    Boolean isFavorite;
    String company_name;

    double s_v=0.0;
    String ss="";
    double trade_cost;
    double price;

    String share;
    String total;
    double avg;
    double change;
    double market_value;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        getIncomingIntent();
        rl=findViewById (R.id.alldata);
        pb=findViewById (R.id.progressbar1);
        rl.setVisibility (View.GONE);
        pb.setVisibility (View.VISIBLE);
        loadData();
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(createChartAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                        tab.setText("Tab " + (position + 1));
                        if(position==0){
                            tab.setIcon(R.drawable.ic_chart_line);
                        }
                        else{
                            tab.setIcon(R.drawable.ic_clock_line);
                        }
                    }
                }).attach();
    }

    private void addToFavorites(String name, String company) throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        JSONObject fav_json=new JSONObject ();
        fav_json.put("name",name);
        fav_json.put("company",company);
        SharedPreferences.Editor editor=sp.edit();
        if(sp.contains ("fav")){
            String fav_values=sp.getString ("fav","[]");
            JSONArray fav_array=new JSONArray (fav_values);
            fav_array.put(fav_json);
            editor.putString ("fav", fav_array.toString ());
            editor.commit ();
        }
        else{
            JSONArray fav_array=new JSONArray ();
            fav_array.put(fav_json);
            editor.putString ("fav", fav_array.toString ());
            editor.commit ();
        }
    }

    private void removeFromFavorites(String name) throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        String fav_str=sp.getString ("fav","");
        JSONArray fav_array=new JSONArray (fav_str);
        for(int i=0;i<fav_array.length ();i++){
            String name_val=fav_array.getJSONObject (i).getString ("name");
            if(name_val.equals (name)){
                fav_array.remove (i);
            }
        }
        editor.putString ("fav", fav_array.toString ());
        editor.commit ();
    }

    private boolean isTickerInFavorites(String ticker){
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        String fav_str=sp.getString ("fav","");
        if(fav_str.contains (ticker.toUpperCase ())){
            return true;
        }
        else{
            return false;
        }
    }

    private void updateCashBalance(Double curr_total,String trade_type){
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        if(sp.contains ("cashBalance")){
            String bal_value=sp.getString ("cashBalance","");
            double bal=Double.parseDouble (bal_value);
            if(trade_type.equals ("buy")){
                double balance=(double) bal-curr_total;
                editor.putString ("cashBalance", String.valueOf (balance));
                editor.commit ();
            }
            else{
                double balance=(double) bal+curr_total;
                editor.putString ("cashBalance", String.valueOf (balance));
                editor.commit ();
            }
        }
        else{
            editor.putString ("cashBalance", "25000.00");
            editor.commit ();
        }
    }

    private double getCashBalance(){
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        String bal_value=sp.getString ("cashBalance","");
        return Double.parseDouble (bal_value);
    }

    private double getTotalPortfolio() throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        String port_values=sp.getString ("port","[]");
        JSONArray port_array=new JSONArray (port_values);
        JSONObject portobj= new JSONObject ();
        double total_port = 0.0;
        for(int i=0;i<port_array.length ();i++){
            portobj=port_array.getJSONObject (i);
            String total=portobj.getString("marketValue");
            double tot=Double.parseDouble (total);
            total_port += tot;
        }
        return total_port*100;
    }

    private void updateNetWorth() throws JSONException {
        double cash_bal=getCashBalance ();
        double total_portfolio_cost=getTotalPortfolio();
        double net=cash_bal+total_portfolio_cost;
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString ("netWorth", String.valueOf (net));
        editor.commit ();

    }
    private void sellPortfolio(double curr_shares,double curr_trade_cost,String curr_ticker) throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        if(sp.contains ("port")){
            String port_values=sp.getString ("port","[]");
            if(port_values.contains (curr_ticker)){
                JSONArray port_array=new JSONArray (port_values);
                JSONObject portobj= new JSONObject ();
                for(int i=0;i<port_array.length ();i++){
                    portobj=port_array.getJSONObject (i);
                    String port_ticker=portobj.getString ("ticker");
                    if(port_ticker.equals(curr_ticker)){
                        String prev_share=portobj.getString ("shares");
                        String prev_total=portobj.getString("total");
                        double del_share=Double.parseDouble (prev_share)-curr_shares;
                        double del_total=Double.parseDouble (prev_total)-curr_trade_cost;
                        if(del_share>0){
                            portobj.put ("ticker",curr_ticker);
                            portobj.put("shares",del_share);
                            portobj.put("total",del_total);
                        }
                        else{
                            port_array.remove (i);
                        }
                        break;
                    }
                }
                //port_array.put(portobj);
                editor.putString ("port", port_array.toString ());
                editor.commit ();
            }
        }
        Log.d("SAVED!",sp.getString ("port",""));
        updateCashBalance(curr_trade_cost,"sell");
        updateNetWorth();
        updatePortfolioSection ();
    }
    private void saveToPortfolio(double curr_shares,double curr_trade_cost,String curr_ticker,double market_value) throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        if(sp.contains ("port")){
            String port_values=sp.getString ("port","[]");
            if(port_values.contains (curr_ticker)){
                JSONArray port_array=new JSONArray (port_values);
                JSONObject portobj= new JSONObject ();
                for(int i=0;i<port_array.length ();i++){
                    portobj=port_array.getJSONObject (i);
                    String port_ticker=portobj.getString ("ticker");
                    if(port_ticker.equals(curr_ticker)){
                        String prev_share=portobj.getString ("shares");
                        String prev_total=portobj.getString("total");
                        double add_share=Double.parseDouble (prev_share)+curr_shares;
                        double add_total=Double.parseDouble (prev_total)+curr_trade_cost;
                        portobj.put ("ticker",curr_ticker);
                        portobj.put("shares",add_share);
                        portobj.put("total",add_total);
                        portobj.put("marketValue",market_value);
                        break;
                    }
                }
                //port_array.put(portobj);
                editor.putString ("port", port_array.toString ());
                editor.commit ();
            }
            else{
                JSONObject jobj=new JSONObject ();
                jobj.put("ticker",curr_ticker);
                jobj.put("shares",curr_shares);
                jobj.put("total",curr_trade_cost);
                jobj.put("marketValue",market_value);
                JSONArray port_array=new JSONArray (port_values);
                port_array.put(jobj);
                editor.putString ("port", port_array.toString ());
                editor.commit ();
            }
        }
        else{
            JSONArray port_array=new JSONArray ();
            JSONObject jobj=new JSONObject ();
            jobj.put("ticker",curr_ticker);
            jobj.put("shares",curr_shares);
            jobj.put("total",curr_trade_cost);
            jobj.put("marketValue",market_value);
            port_array.put(jobj);
            editor.putString ("port", port_array.toString ());
            editor.commit ();
        }
        Log.d("SAVED!",sp.getString ("port",""));
        updateCashBalance(curr_trade_cost,"buy");
        updateNetWorth();
        //updatePortfolioSection ();
    }

    private void updatePortfolioSection() throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        if(sp.contains ("port")){
            String port_values=sp.getString ("port","[]");
            Log.d("UPDATE",port_values);
            if(port_values.contains (tickerName)){
                JSONArray port_array=new JSONArray (port_values);
                JSONObject portobj= new JSONObject ();
                for(int i=0;i<port_array.length ();i++){
                    portobj=port_array.getJSONObject (i);
                    String port_ticker=portobj.getString ("ticker");
                    if(port_ticker.equals(tickerName)){
                        share=portobj.getString ("shares");
                        total=portobj.getString("total");
                        avg=(Double.parseDouble (total)/Double.parseDouble (share));
                        Log.d("AVG",String.valueOf (avg));
                        //change=Math.round(((price-avg)*Double.parseDouble(share))*100)/100;
                        change=(price-avg)*Double.parseDouble(share);
                        market_value=((price*Double.parseDouble(share))*100)/100;
                        break;
                    }
                }
            }
            else{
                share="0";
                total="0.00";
                avg=0.00;
                change=0.00;
                market_value=0.00;
            }
        }
        else{
            share="0";
            total="0.00";
            avg=0.00;
            change=0.00;
            market_value=0.00;
        }
        TextView port_shares=findViewById (R.id.port_shares);
        port_shares.setText (share);
        TextView port_avg_cost=findViewById (R.id.port_avg_cost);
        port_avg_cost.setText (String.format ("$%,.2f",avg));
        TextView port_total_cost=findViewById (R.id.port_total_cost);
        port_total_cost.setText (String.format ("$%,.2f",Double.parseDouble (total)));
        TextView port_change=findViewById (R.id.port_change);
        port_change.setText (String.format("$%,.2f",change));
        TextView port_market_value=findViewById(R.id.port_market_value);
        port_market_value.setText (String.format("$%,.2f",market_value));
        Log.d("change1",String.valueOf (change));
        if(change>0){
            port_change.setTextColor (Color.parseColor ("#077B37"));
            port_market_value.setTextColor (Color.parseColor ("#077B37"));
        }
        if(change<0){
            port_change.setTextColor (Color.parseColor ("#9B1621"));
            port_market_value.setTextColor (Color.parseColor ("#9B1621"));
        }
        else
        {
            port_change.setText (String.format("$%,.2f",change));
            port_market_value.setText (String.format("$%,.2f",market_value));
            port_change.setTextColor (Color.parseColor ("#878787"));
            port_market_value.setTextColor (Color.parseColor ("#878787"));
        }
        Log.d("GETDATA!",sp.getString ("port",""));
    }

    public static boolean isNumeric(String string) {
        Log.d("Number",string);
        int intValue;

        System.out.println(String.format("Parsing string: \"%s\"", string));

        if(string == null || string.equals("")) {
            System.out.println("String cannot be parsed, it is null or empty.");
            return false;
        }

        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
        }
        return false;
    }


    private void loadData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://stock-frontend-1609.wl.r.appspot.com/alldata/"+tickerName;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    Double price_d,price_dp,h,l,o,pc;
                    String url;
                    String ipo,industry,weburl,c_peers;
                    String redtot,twitot,redpos,twipos,redneg,twineg;
                    String source,headline,news_image,pubdate,summary,news_url,news_hours;
                    JSONArray news_list;
                    JSONArray peers_list;
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONArray response) {
                        pb.setVisibility (View.GONE);
                        rl.setVisibility (View.VISIBLE);
                        //rl.setVisibility (View.VISIBLE);
                        try {
                            company_name=response.getJSONObject(0).getString ("name");
                            price=response.getJSONObject (1).getDouble ("c");
                            price=(price*100)/100;
                            price_d=response.getJSONObject (1).getDouble ("d");
                            price_dp=response.getJSONObject (1).getDouble ("dp");
                            url=response.getJSONObject (0).getString ("logo");
                            h=response.getJSONObject (1).getDouble ("h");
                            l=response.getJSONObject (1).getDouble ("l");
                            o=response.getJSONObject (1).getDouble ("o");
                            pc=response.getJSONObject (1).getDouble ("pc");
                            ipo=response.getJSONObject(0).getString ("ipo");
                            industry=response.getJSONObject(0).getString ("finnhubIndustry");
                            weburl=response.getJSONObject(0).getString ("weburl");
                            redtot=response.getJSONObject (4).getString ("redtot");
                            redpos=response.getJSONObject (4).getString ("redpos");
                            redneg=response.getJSONObject (4).getString ("redneg");
                            twipos=response.getJSONObject (4).getString ("twipos");
                            twitot=response.getJSONObject (4).getString ("twitot");
                            twineg=response.getJSONObject (4).getString ("twineg");
                            source=response.getJSONObject (2).getString ("source");
                            headline=response.getJSONObject (2).getString ("headline");
                            news_image= response.getJSONObject (2).getString ("image");
                            pubdate=response.getJSONObject (2).getString ("datetime");
                            summary=response.getJSONObject (2).getString ("summary");
                            news_url=response.getJSONObject (2).getString ("url");
                            news_hours=response.getJSONObject (2).getString ("diff");
                            news_list=response.getJSONArray (3);
                            mynews_list=news_list;
                            peers_list=response.getJSONArray (5);

                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }
                        TextView company=findViewById (R.id.text_company);
                        TextView c=findViewById (R.id.text_c);
                        TextView d=findViewById (R.id.text_d);
                        TextView dp=findViewById (R.id.text_dp);
                        ImageView img=findViewById (R.id.img_trending);
                        ImageView logo=findViewById (R.id.txt_img);
                        TextView hp=findViewById (R.id.txt_h);
                        TextView lp=findViewById (R.id.txt_l);
                        TextView op=findViewById (R.id.txt_o);
                        TextView pcc=findViewById (R.id.txt_pc);
                        TextView v_ipo=findViewById (R.id.txt_ipo);
                        TextView v_industry=findViewById (R.id.txt_industry);
                        TextView v_weburl=findViewById (R.id.txt_webpage);
                        TextView ss_redtot=findViewById (R.id.ss_redtot);
                        TextView ss_twitot=findViewById (R.id.ss_twitot);
                        TextView ss_redpos=findViewById (R.id.ss_redpos);
                        TextView ss_twipos=findViewById (R.id.ss_twipos);
                        TextView ss_redneg=findViewById (R.id.ss_redneg);
                        TextView ss_twineg=findViewById (R.id.ss_twineg);
                        TextView n_source=findViewById (R.id.n_source);
                        TextView n_headline=findViewById (R.id.n_headline);
                        ImageView n_image=findViewById (R.id.n_image);
                        TextView n_hours=findViewById (R.id.n_hours);
                        news1=findViewById (R.id.news1);
                        try {
                            updatePortfolioSection ();
                        } catch (JSONException e) {
                            e.printStackTrace ();
                        }
                        trade=findViewById (R.id.trade_button);
                        trade.setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick(View view) {
                                Dialog dialog = new Dialog(view.getContext ());
                                dialog.setContentView(R.layout.trade_dialog_layout);
                                dialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog.setCancelable(true);
                                TextView trade_head=dialog.findViewById (R.id.trade_head);
                                trade_head.setText ("Trade "+company_name+" shares");
                                EditText editText=dialog.findViewById (R.id.edit_text);
                                TextView formula=dialog.findViewById (R.id.formula);
                                formula.setText(String.format("%,.1f * $%,.2f/share = $%,.2f",0.0,price,0.0));
                                TextView to_buy=dialog.findViewById (R.id.to_buy);
                                String cash_bal=String.valueOf (Math.round((getCashBalance ()*100)/100));
                                to_buy.setText (String.format ("$ %,.2f to buy %s",getCashBalance (),tickerName));
                                //to_buy.setText ("$"+cash_bal+" to buy "+tickerName);
                                editText.addTextChangedListener (new TextWatcher () {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        Log.d("EDIT",String.valueOf (s_v));
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        Log.d("EDIT",String.valueOf (s_v));
                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        Log.d("EDIT",String.valueOf (s_v));
                                        if(editText.getText ().toString().length ()>0){
                                            ss=editText.getText ().toString ();
                                            if(isNumeric (ss)){
                                                s_v=Double.parseDouble (editText.getText ().toString ());
                                            }
                                        }
                                        else {
                                            s_v = 0.0;
                                        }
                                        trade_cost=((s_v*price)*100)/100;
                                        formula.setText(String.format("%,.1f * $%,.2f/share = $%,.2f",s_v,price,trade_cost));
                                    }
                                });
                                Button buy=dialog.findViewById (R.id.buy);
                                buy.setOnClickListener (new View.OnClickListener () {
                                    @Override
                                    public void onClick(View view1) {
                                        //String ss=editText.getText ().toString ();
                                        if(s_v>0 && isNumeric (ss)){
                                            try {
                                                double my_cash=getCashBalance();
                                                if(trade_cost>my_cash){
                                                    Toast.makeText(view.getContext (), "Not enough money to buy",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    market_value=s_v*price;
                                                    //updatePortfolioSection();
                                                    saveToPortfolio(s_v,trade_cost,tickerName,market_value);
                                                    updatePortfolioSection();
                                                    dialog.dismiss ();
                                                    Dialog dialog2 = new Dialog(view1.getContext ());
                                                    dialog2.setContentView(R.layout.trade_success_layout);
                                                    dialog2.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
                                                    dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                    dialog2.setCancelable(false);
                                                    dialog2.show();
                                                    TextView success=dialog2.findViewById (R.id.success);
                                                    success.setText ("You have successfully bought "+(int)s_v+" shares of "+tickerName);
                                                    Button done=dialog2.findViewById (R.id.done);
                                                    done.setOnClickListener (new View.OnClickListener () {
                                                        @Override
                                                        public void onClick(View view3) {
                                                            editText.setText ("");
                                                            dialog2.dismiss ();

                                                        }
                                                    });
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace ();
                                            }
                                        }
                                        else{
                                            Toast.makeText(view.getContext (), "Please enter a valid amount",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                Button sell=dialog.findViewById (R.id.sell);
                                sell.setOnClickListener (new View.OnClickListener () {
                                    @Override
                                    public void onClick(View view13) {
                                        Log.d("SELL",String.valueOf (s_v));
                                        //String ss=editText.getText ().toString ();
                                        if(s_v>0 && isNumeric (ss)){
                                            double sh=Double.parseDouble (share);
                                            if(s_v>sh){
                                                Toast.makeText(view.getContext (), "Not enough shares to sell",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                try {
                                                    sellPortfolio(s_v,trade_cost,tickerName);
                                                    dialog.dismiss ();
                                                    Dialog dialog23 = new Dialog(view13.getContext ());
                                                    dialog23.setContentView(R.layout.trade_sell_success_layout);
                                                    dialog23.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
                                                    dialog23.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                    dialog23.setCancelable(false);
                                                    dialog23.show();
                                                    TextView sell_success=dialog23.findViewById (R.id.sell_success);
                                                    sell_success.setText ("You have successfully sold "+(int)s_v+" shares of "+tickerName);
                                                    Button done=dialog23.findViewById (R.id.done);
                                                    done.setOnClickListener (new View.OnClickListener () {
                                                        @Override
                                                        public void onClick(View view33) {
                                                            editText.setText ("");
                                                            dialog23.dismiss ();
                                                        }
                                                    });
                                                } catch (JSONException e) {
                                                    e.printStackTrace ();
                                                }
                                            }
                                        }
                                        else{
                                            Toast.makeText(view.getContext (), "Please enter a valid amount",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                                dialog.show();
                            }
                        });
                        LinearLayout text_peers=(LinearLayout) findViewById (R.id.txt_peers);
                        company.setText (company_name);
                        c.setText (String.format(" $%,.2f ",price));
                        Glide.with(getApplicationContext ())
                                .load(url)
                                .into(logo);
                        Log.d("price_dp",price_dp.toString ());
                        if(price_dp>0){
                            d.setText(String.format(" $%,.2f ",price_d));
                            dp.setText(String.format("(%,.2f%%)",price_dp));
                            d.setTextColor(Color.parseColor("#008000"));
                            dp.setTextColor(Color.parseColor("#008000"));
                            img.setBackgroundResource(R.drawable.ic_trending_up);
                        }
                        else if (price_dp<0){
                            d.setText(String.format(" $%,.2f ",price_d));
                            dp.setText(String.format("(%,.2f%%)",price_dp));
                            d.setTextColor(Color.parseColor("#af041d"));
                            dp.setTextColor(Color.parseColor("#af041d"));
                            img.setBackgroundResource(R.drawable.ic_trending_down);
                        }
                        else{
                            d.setText(String.format(" $%,.2f ",price_d));
                            dp.setText(String.format("(%,.2f%%)",price_dp));
                        }
                        hp.setText (String.format ("High Price: $%,.2f ",h));
                        lp.setText (String.format ("Low Price: $%,.2f ",l));
                        op.setText (String.format ("Open Price: $%,.2f ",o));
                        pcc.setText (String.format ("Prev. Close: $%,.2f ",pc));
                        String[] ipoSpilt=ipo.split("-");
                        v_ipo.setText(ipoSpilt[1]+"-"+ipoSpilt[2]+"-"+ipoSpilt[0]);
                        v_industry.setText (industry);
                        v_weburl.setText (weburl);
                        ss_redtot.setText (redtot);
                        ss_redpos.setText (redpos);
                        ss_redneg.setText (redneg);
                        ss_twitot.setText (twitot);
                        ss_twineg.setText (twineg);
                        ss_twipos.setText (twipos);
                        n_source.setText(source);
                        n_headline.setText(headline);
                        n_hours.setText (news_hours+" hours ago");
                        Glide.with(getApplicationContext ())
                                .load(news_image)
                                .into(n_image);
                        news1.setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick(View view) {
                                Dialog dialog1 = new Dialog(view.getContext ());
                                dialog1.setContentView(R.layout.custome_layout_dialog);
                                TextView d_source=dialog1.findViewById (R.id.d_source);
                                d_source.setText (source);
                                TextView d_date=dialog1.findViewById (R.id.d_date);
                                d_date.setText (pubdate);
                                TextView d_head=dialog1.findViewById (R.id.d_headline);
                                d_head.setText (headline);
                                TextView d_summary=dialog1.findViewById (R.id.d_summary);
                                d_summary.setText (summary);
                                ImageButton chrome=dialog1.findViewById (R.id.chrome);
                                ImageButton facebook=dialog1.findViewById (R.id.facebook);
                                ImageButton twitter=dialog1.findViewById (R.id.twitter);
                                String facebook_link="https://www.facebook.com/sharer/sharer.php?u="+news_url+"&amp;src=sdkpreparse";
                                String twitter_link="https://twitter.com/intent/tweet?text="+headline+"&url="+news_url;
                                chrome.setOnClickListener (new View.OnClickListener () {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent (Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(news_url));
                                        view.getContext ().startActivity(i);
                                    }
                                });
                                facebook.setOnClickListener (new View.OnClickListener () {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent (Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(facebook_link));
                                        view.getContext ().startActivity(i);
                                    }
                                });
                                twitter.setOnClickListener (new View.OnClickListener () {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent (Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(twitter_link));
                                        view.getContext ().startActivity(i);
                                    }
                                });

                                dialog1.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
                                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog1.setCancelable(true);
                                dialog1.show();
                            }
                        });
                        for(int i=0;i<news_list.length ();i++){
                            try {
                                JSONObject jobj=news_list.getJSONObject (i);
                                String j_headline=jobj.getString ("headline");
                                String j_source=jobj.getString ("source");
                                String j_image=jobj.getString ("image");
                                String j_pubdate=jobj.getString("datetime");
                                String j_summary=jobj.getString ("summary");
                                String j_url=jobj.getString ("url");
                                String j_diff=jobj.getString ("diff");
                                news.add(new New(j_pubdate,j_headline,j_image,j_source,j_summary,j_url,j_diff));
                            } catch (JSONException e) {
                                e.printStackTrace ();
                            }
                        }
                        newsView=findViewById(R.id.newView);
                        n_adapter.setNews(news);
                        newsView.setAdapter(n_adapter);
                        newsView.setLayoutManager(new LinearLayoutManager(getApplicationContext ()));

                        for(int k=0;k<peers_list.length ();k++){
                            try {
                                String val=peers_list.getString (k);
                                if(val.length ()>0) {
                                    Log.d ("PEERS", val);
                                    TextView value = new TextView (getApplicationContext ());
                                    value.setText (val + ",");
                                    value.setId (k);
                                    value.setLayoutParams (new LinearLayout.LayoutParams (
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    value.setPadding (0, 0, 40, 0);
                                    value.getPaint ().setUnderlineText (true);
                                    value.setClickable (true);
                                    value.setTextColor (Color.parseColor ("#3700B3"));
                                    text_peers.addView (value);
                                    value.setOnClickListener (new View.OnClickListener () {
                                        @Override
                                        public void onClick(View view) {
                                            Intent in = new Intent (getBaseContext(), DetailsActivity.class);
                                            in.putExtra("tickerName",val);
                                            startActivity(in);
                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace ();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("ERROR",error.toString());

                    }
                });
        queue.add(jsonObjectRequest);
    }

    private ViewPagerAdapter createChartAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this,tickerName);
        return adapter;
    }

    private void getIncomingIntent(){
        Log.d(TAG,"getIncomingIntent: checking for incoimng intents.");
        if(getIntent().hasExtra("tickerName")){
            Log.d(TAG, "getIncomingIntent: founs intent extras");
            tickerName=getIntent().getStringExtra("tickerName");
            setIntent(tickerName);
        }
    }

    private void setIntent(String tickerName){
        Log.d(TAG, "setIntent: setting ticker name to widgets");
        TextView ticker=findViewById(R.id.text_ticker);
        ticker.setText(tickerName);
        getSupportActionBar().setTitle(tickerName);
        isFavorite=isTickerInFavorites (tickerName);
        WebView maWebView = findViewById(R.id.recChart);
        WebSettings webaSettings = maWebView.getSettings();
        webaSettings.setJavaScriptEnabled(true);
        maWebView.loadUrl("file:///android_asset/recommendations.html");
        maWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                maWebView.loadUrl("javascript:fetchCharts('"+tickerName+"')");
            }
        });

        WebView m1WebView = findViewById(R.id.epsChart);
        WebSettings web1Settings = m1WebView.getSettings();
        web1Settings.setJavaScriptEnabled(true);
        m1WebView.loadUrl("file:///android_asset/earnings.html");
        m1WebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                m1WebView.loadUrl("javascript:fetchCharts('"+tickerName+"')");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu,menu);
        if(isFavorite){
            menu.findItem (R.id.action_star).setIcon(R.drawable.ic_star_filled);
        }
        else{
            menu.findItem (R.id.action_star).setIcon(R.drawable.ic_star_outline);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_star:
                if(isFavorite==true){
                    //filled star
                    try {
                        removeFromFavorites (tickerName);
                        isFavorite=false;
                        Toast.makeText(this, tickerName+" removed Favorites",
                                Toast.LENGTH_LONG).show();
                        item.setIcon(R.drawable.ic_star_outline);
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
                }
                else{
                    try {
                        addToFavorites(tickerName,company_name);
                        isFavorite=true;
                        Toast.makeText(this, tickerName+" added to Favorites",
                                Toast.LENGTH_LONG).show();
                        item.setIcon(R.drawable.ic_star_filled);
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
