package com.example.myapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//String url="http://10.0.2.2:8080/"
public class BackendUtility {
    public static JSONObject makeApiCall(String url, Context context){
        //RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Display the first 500 characters of the response string.
//                        Log.i("RESS",response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("RESS","ERROROROROR");
//            }
//        });
        JSONObject response = null;
        RequestQueue requestQueue = Volley.newRequestQueue(context);


        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(0,url,null,future,future);
        requestQueue.add(request);


        try {
            response = future.get(3, TimeUnit.SECONDS); // Blocks for at most 10 seconds.
        } catch (InterruptedException e) {
            Log.d("RESS","interrupted");
        } catch (ExecutionException e) {
            Log.d("RESS","execution");
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Log.d("RESS",response.toString());

        return response;
    }



    public static void fetchCompanyDescription(String ticker, Context context){
        String url="https://stock-frontend-1609.wl.r.appspot.com/company-description/"+ticker.toUpperCase();
        JSONObject response=makeApiCall(url,context);
        Log.i("backend", response.toString());
    }
}
