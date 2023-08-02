package com.example.myapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class BackendApi {
    private static BackendApi mInstance;
    private RequestQueue mRequestQueue;
    private static Context mcontext;

    public BackendApi(Context context){
        mcontext=context;
        mRequestQueue=getRequestQueue();
    }

    public static synchronized BackendApi getInstance(Context context){
        if(mInstance==null){
            mInstance=new BackendApi(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue= Volley.newRequestQueue(mcontext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public<T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

    public static void makeCall(Context context, String query, Response.Listener<String>listener, Response.ErrorListener errorListener){
        String url="https://stock-frontend-1609.wl.r.appspot.com/autocomplete/"+query;
        StringRequest stringRequest=new StringRequest(Request.Method.GET,url,listener,errorListener);
        BackendApi.getInstance(context).addToRequestQueue(stringRequest);
    }
}
