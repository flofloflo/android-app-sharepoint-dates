package com.bpf.sharepoint.project.dates;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by fh on 08.12.13.
 */
public class getSharePointData extends AsyncTask<String,Void,HttpResponse[]> {
    protected String RTFA;
    protected String FedAuth;
    //Application Context per Konstruktor übergeben, da hier nicht verfügbar
    public getSharePointData(Context appcontext){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(appcontext);
        RTFA = shared.getString("rtFa", "No Value");
        FedAuth = shared.getString("FedAuth", "No Value");
    }

    @Override
    protected void onPreExecute(){
        //Vorbereitende Maßnahmen für den Background-Task
        //TODO: Login Prüfung und eventullen Re-Login
    }

    @Override
    protected HttpResponse[] doInBackground(String... urls) {
        int count = urls.length;
        HttpResponse[] response = null;
        for (int i = 0; i < count; i++) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[i]);
            httpGet.addHeader("Cookie","rtFa=" + RTFA + "; FedAuth=" + FedAuth);
            httpGet.setHeader("Accept","application/json;odata=verbose");
            httpGet.setHeader("Content-type","application/json;odata=verbose");
            try {
                response[i] = httpClient.execute(httpGet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        //https://YourSite.sharepoint.com/_api/lists/getbytitle('Android_Tasks')/items
        return response;
    }

    @Override
    protected void onPostExecute(HttpResponse[] result) {
        //Abschließende Aufgaben
    }
}
