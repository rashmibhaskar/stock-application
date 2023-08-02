package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapp.Favorite.Favorite;
import com.example.myapp.Favorite.FavoriteViewAdapter;
import com.example.myapp.Portfolio.Portfolio;
import com.example.myapp.Portfolio.PortfolioViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import java.util.concurrent.TimeUnit;

/* Part of this code has been referenced from the following blog/article/tutorial
URL:https://www.truiton.com/2018/06/android-autocompletetextview-suggestions-from-webservice-call/
URL:https://www.youtube.com/watch?v=fis26HvvDII - 9:16:35
URL:https://www.youtube.com/watch?v=H9D_HoOeKWM*/
public class MainActivity extends AppCompatActivity {

    private static final int TRIGGER_AUTOCOMPLETE=100;
    private static final long AUTOCOMPLETE_DELAY=300;
    private Handler autocompletehandler;
    private AutocompleteAdapter autocompleteAdapter;
    TextView today_date;
    BackendUtility b=new BackendUtility();
    ArrayList<Portfolio> portfolios = new ArrayList<>();
    ArrayList<Favorite> favorites = new ArrayList<>();
    private RecyclerView portfolioView;
    private RecyclerView favoriteView;
    PortfolioViewAdapter p_adapter= new PortfolioViewAdapter(this);
    FavoriteViewAdapter f_adapter= new FavoriteViewAdapter(this);
    SharedPreferences sp;

    String value;

    JSONArray portData=new JSONArray ();
    JSONArray favData=new JSONArray ();

    RelativeLayout rl;
    RelativeLayout pb;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 15000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Stocks);
        setContentView(R.layout.activity_main);

        rl=findViewById (R.id.alldata);
        pb=findViewById (R.id.progressbar1);
        rl.setVisibility (View.GONE);
        pb.setVisibility (View.VISIBLE);

        today_date=findViewById(R.id.today_date);
        String pattern = "dd MMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        today_date.setText(date);

        try {
            getData();
            portfolioView=findViewById(R.id.portfolioView);
            p_adapter.setPortfolios(portfolios);
            portfolioView.setAdapter(p_adapter);
            portfolioView.setLayoutManager(new LinearLayoutManager(this));

            ItemTouchHelper p_itemTouchHelper=new ItemTouchHelper(portfolio_simpleCallback);
            p_itemTouchHelper.attachToRecyclerView(portfolioView);

            favoriteView=findViewById(R.id.favoriteView);
            f_adapter.setFavorites(favorites);
            favoriteView.setAdapter(f_adapter);
            favoriteView.setLayoutManager(new LinearLayoutManager(this));

            enableSwipeToDeleteAndUndo();
            ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(favoriteView);
        } catch (JSONException e) {
            e.printStackTrace ();
        }

        TextView finnhub_link=(TextView)findViewById(R.id.finnhub_link);
        finnhub_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://finnhub.io/"));
                startActivity(i);
            }
        });


        setCashBalance();
        try {
            setNetWorth ();
        } catch (JSONException e) {
            e.printStackTrace ();
        }
    }

   @Override
    protected void onResume() {
//        setCashBalance ();
//       try {
//           setNetWorth();
//       } catch (JSONException e) {
//           e.printStackTrace ();
//       }
//       try {
////            portfolios.clear ();
////            favorites.clear ();
//            getData ();
//            //loadPortfolios (portData);
//            //loadFavorites (favData);
//            f_adapter.notifyDataSetChanged ();
//            p_adapter.notifyDataSetChanged();
//        } catch (JSONException e) {
//            e.printStackTrace ();
//        }
       handler.postDelayed(runnable = new Runnable() {
           public void run() {
               handler.postDelayed(runnable, delay);
               Log.d("ONRESUME","jkjkjkjk");
//               setCashBalance ();
//               try {
//                   setNetWorth();
//               } catch (JSONException e) {
//                   e.printStackTrace ();
//               }
               try {
                   //portfolios.clear ();
                   //favorites.clear ();
                   getData ();
//                   loadPortfolios (portData);
//                   loadFavorites (favData);
                   //setNetWorth();
//                   p_adapter.notifyDataSetChanged();
//                   f_adapter.notifyDataSetChanged ();
               } catch (JSONException e) {
                   e.printStackTrace ();
               }
           }
       }, delay);
        super.onResume ();
    }

    @Override
    protected void onStop() {
        super.onStop ();
    }


    private void updateFavorites(ArrayList<Favorite> favs) throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        JSONArray fav_array=new JSONArray ();
        SharedPreferences.Editor editor=sp.edit();
        for(int i=0;i<favs.size ();i++){
            JSONObject fav_json=new JSONObject ();
            fav_json.put("name",favs.get(i).getTicker ());
            fav_json.put("company",favs.get(i).getCompanyName ());
            fav_array.put(fav_json);
        }
        editor.putString ("fav", fav_array.toString ());
        editor.commit ();
    }

    private void updatePortfolio(ArrayList<Portfolio> ports) throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        JSONArray port_array=new JSONArray ();
        SharedPreferences.Editor editor=sp.edit();
        for(int i=0;i<ports.size ();i++){
            JSONObject port_json=new JSONObject ();
            port_json.put("ticker",ports.get(i).getTicker ());
            port_json.put("shares",ports.get(i).getShares ());
            port_json.put("total",String.valueOf (ports.get(i).getAmount ()));
            port_json.put("marketValue",String.valueOf (ports.get(i).getMarketValue ()));
            port_array.put(port_json);
        }
        editor.putString ("port", port_array.toString ());
        editor.commit ();
    }

    //

    private void loadPortfolios(JSONArray portResponse) throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        String port_values=sp.getString ("port","[]");
        JSONArray port_array=new JSONArray (port_values);
        JSONObject portobj= new JSONObject ();
        JSONObject portResobj= new JSONObject ();
        portfolios.clear();
        for(int i=0;i<port_array.length ();i++){
            portobj=port_array.getJSONObject (i);
            portResobj=portResponse.getJSONObject (i);
            double c=Double.parseDouble (portResobj.getString("c"));
            double shares=Double.parseDouble (portobj.getString("shares"));
            double market_value=c*shares;
            double total=Double.parseDouble (portobj.getString("total"));
            double avg=total/shares;
//            double changeInPrice=(c-avg)*shares;
//            double changePercent=Math.round((changeInPrice/total)*100)/100;
            double changeInPrice1=Math.round(((c-avg)*shares) * 100.00) / 100.00;
//            double changePercent1=Math.ceil((changeInPrice/total) * 100.00) / 100.00;
            double changePercent2=(changeInPrice1/total)*100.00;
//            Log.d("PORT C",String.valueOf (c));
//            Log.d("PORT shares",String.valueOf (shares));
//            Log.d("PORT market_value",String.valueOf (market_value));
//            Log.d("PORT total",String.valueOf (total));
//            Log.d("PORT avg",String.valueOf (avg));
//            Log.d("PORT changeInPrice",String.valueOf (changeInPrice));
//            Log.d("PORT changePercent",String.valueOf (changePercent));
//            Log.d("PORT changeInPrice1",String.valueOf (changeInPrice1));
//            Log.d("PORT changePercent1",String.valueOf (changePercent1));
//            Log.d("PORT changePercent11",String.valueOf ((changeInPrice/total)));
//            Log.d("PORT changePercent2",String.valueOf (changePercent2));

            portfolios.add(new Portfolio(portobj.getString ("ticker"),Integer.parseInt (portobj.getString ("shares")),total,changeInPrice1,changePercent2,market_value));
        }
        setNetWorth ();
        updatePortfolio (portfolios);
//        p_adapter.notifyDataSetChanged ();
    }

    private void loadFavorites(JSONArray favResponse) throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        String fav_values=sp.getString ("fav","[]");
        Log.d("LOAD FAV",fav_values);
        JSONArray fav_array=new JSONArray (fav_values);
        JSONObject favobj= new JSONObject ();
        JSONObject favResobj= new JSONObject ();
        favorites.clear();
        for(int i=0;i<fav_array.length ();i++){
            favobj=fav_array.getJSONObject (i);
            favResobj=favResponse.getJSONObject (i);
            double c=(Double.parseDouble (favResobj.getString("c")))*100/100;
            double d=(Double.parseDouble (favResobj.getString("d"))*100)/100;
            double dp=(Double.parseDouble (favResobj.getString("dp"))*100)/100;
            favorites.add(new Favorite(favobj.getString ("name"),favobj.getString ("company"),c,d,dp));
            //favorites.add(new Portfolio(favobj.getString ("ticker"),Integer.parseInt (portobj.getString ("shares")),561.1,12.5,-1.9));
        }
//        f_adapter.notifyDataSetChanged ();
    }

    private String getFav_str_api() throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        String fav_values=sp.getString ("fav","[]");
        JSONArray fav_array=new JSONArray (fav_values);
        JSONObject favobj= new JSONObject ();
        ArrayList<String> arrayList = new ArrayList<String>();
        for(int i=0;i<fav_array.length ();i++){
            favobj=fav_array.getJSONObject (i);
            arrayList.add(favobj.getString ("name"));
        }
        return String.join (",",arrayList);
    }

    private String getPort_str_api() throws JSONException {
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        String port_values=sp.getString ("port","[]");
        JSONArray port_array=new JSONArray (port_values);
        JSONObject portobj= new JSONObject ();
        ArrayList<String> arrayList = new ArrayList<String>();
        for(int i=0;i<port_array.length ();i++){
            portobj=port_array.getJSONObject (i);
            arrayList.add(portobj.getString ("ticker"));
        }
        return String.join (",",arrayList);
    }

    private void savePortfolio(String ticker, Integer shares, Double total) throws JSONException {
        JSONObject jobj=new JSONObject ();
        jobj.put("ticker",ticker);
        jobj.put("shares",shares);
        jobj.put("total",total);
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        if(sp.contains ("port")){
            String port_values=sp.getString ("port","[]");
            JSONArray port_array=new JSONArray (port_values);
            port_array.put(jobj);
            editor.putString ("port", port_array.toString ());
            editor.commit ();
        }
        else{
            JSONArray port_array=new JSONArray ();
            port_array.put(jobj);
            editor.putString ("port", port_array.toString ());
            editor.commit ();
        }
        Log.d("SAVED!",sp.getString ("port",""));
    }

    private void setCashBalance(){
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        if(sp.contains ("cashBalance")){
            value = sp.getString ("cashBalance","");
            Log.d("cash",value);
            editor.commit ();
        }
        else{
            value="25000.00";
            editor.putString ("cashBalance", value);
            editor.commit ();
        }
        TextView cash_balance=findViewById (R.id.cash_balance_val);
        double cash_value = (double) Math.round(Double.parseDouble (value)* 100) / 100;
        cash_balance.setText (String.format("$%,.2f",cash_value));
    }

    private double getTotalPortfolio() throws JSONException {
        Log.d("SET NETWORTH",portfolios.toString ());
        double total_port = 0.0;
        for(int i=0;i<portfolios.size ();i++){
            double tot=portfolios.get(i).getMarketValue ();
            total_port += tot;
        }
//        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
//        String port_values=sp.getString ("port","[]");
//        JSONArray port_array=new JSONArray (port_values);
//        JSONObject portobj= new JSONObject ();
//        double total_port = 0.0;
//        for(int i=0;i<port_array.length ();i++){
//            portobj=port_array.getJSONObject (i);
//            String total=portobj.getString("total");
//            double tot=Double.parseDouble (total);
//            Log.d("TotalPort pp",String.valueOf (tot));
//            total_port += tot;
//        }
        return total_port;
    }

    private void setNetWorth() throws JSONException {
        double totalport=getTotalPortfolio ();
        Log.d("TotalPort",String.valueOf (totalport));
        double val=0.00;
        SharedPreferences sp = getSharedPreferences ("localStorage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        String bal_value=sp.getString ("cashBalance","");
        val=Double.parseDouble (bal_value)+totalport;
        Log.d("TotalPort",String.valueOf (val));
        editor.putString ("netWorth", String.valueOf (val));
        editor.commit ();
        TextView net_worth=findViewById (R.id.net_worth_val);
        double net_worth_value = (double) val;
        net_worth.setText (String.format("$%,.2f",net_worth_value));
    }

    private void getData() throws JSONException {
        Log.d("UPDATE","inside getdata");
        String port_str=getPort_str_api();
        String fav_str=getFav_str_api ();
        Log.d("URL",port_str+fav_str);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://stock-frontend-1609.wl.r.appspot.com/home/"+port_str+";"+fav_str;
        Log.d("URL",url);
//        if(url.equals("https://stock-frontend-1609.wl.r.appspot.com/home/;")==false){}
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                    pb.setVisibility (View.GONE);
                    rl.setVisibility (View.VISIBLE);
                try {
                    portData=response.getJSONArray (0);
                    favData=response.getJSONArray (1);
                    loadFavorites (favData);
                    loadPortfolios (portData);
                    Log.d("ALL P",portData.toString ());
                    Log.d("ALL F",favData.toString ());
                    p_adapter.notifyDataSetChanged();
                    f_adapter.notifyDataSetChanged ();
                    setCashBalance ();
                    setNetWorth ();
                } catch (JSONException e) {
                    pb.setVisibility (View.GONE);
                    rl.setVisibility (View.VISIBLE);
                    e.printStackTrace ();
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

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,0) {
        
        
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition =viewHolder.getAdapterPosition();
            int toPosition =target.getAdapterPosition();
            Collections.swap(favorites,fromPosition,toPosition);
            try {
                updateFavorites(favorites);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    ItemTouchHelper.SimpleCallback portfolio_simpleCallback=new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,0) {

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition =viewHolder.getAdapterPosition();
            int toPosition =target.getAdapterPosition();
            Collections.swap(portfolios,fromPosition,toPosition);
            try {
                updatePortfolio(portfolios);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                Log.d("REM",favorites.toString ());
                final int position = viewHolder.getAdapterPosition ();
                final Favorite item = f_adapter.getFavorites().get(position);
                f_adapter.removeItem(position);
                Log.d("REM",favorites.toString ());
                try {
                    updateFavorites(favorites);
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            }
        };
        RecyclerView rec = (RecyclerView) findViewById(R.id.favoriteView);
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rec);

    }



    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        autocompleteAdapter=new AutocompleteAdapter(this,android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(autocompleteAdapter);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent (getApplicationContext(), DetailsActivity.class);
                String[] arrOfStr = autocompleteAdapter.getObject(i).split(" | ");
                intent.putExtra("tickerName",arrOfStr[0]);
                startActivity(intent);
            }
        });
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                autocompletehandler.removeMessages(TRIGGER_AUTOCOMPLETE);
                autocompletehandler.sendEmptyMessageDelayed(TRIGGER_AUTOCOMPLETE,AUTOCOMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autocompletehandler=new Handler(new Handler.Callback(){

            @Override
            public boolean handleMessage(@NonNull Message message) {
                if(message.what==TRIGGER_AUTOCOMPLETE){
                    if(!TextUtils.isEmpty(searchAutoComplete.getText())){
                        fetchData(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void fetchData(String text){
        BackendApi.makeCall(this, text, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("result");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        if(row.getString("type").equals("Common Stock") && !row.getString("symbol").contains("."))
                        {
                            stringList.add(row.getString("symbol")+" | "+ row.getString("description"));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                autocompleteAdapter.setData(stringList);
                autocompleteAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}