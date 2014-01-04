package com.bpf.sharepoint.project.dates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * Created by fh on 28.12.13.
 */
public class SharePointLogout extends AsyncTask<String,Void,Void> {

    protected ProgressDialog pDialog;
    protected Context appContext;

    public SharePointLogout(Context ac){
        appContext=ac;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog=new ProgressDialog(appContext);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(appContext.getString(R.string.logout));
        pDialog.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        try{
            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(appContext);
            URI spUrl=new URI(shared.getString("spUrl","None"));
            if(spUrl.equals("None"))
               throw new Resources.NotFoundException(appContext.getString(R.string.url_not_found));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(spUrl+"_layouts/15/SignOut.aspx"));
            httpGet.addHeader("Cookie","rtFa=" + shared.getString("rtFa","") + "; FedAuth=" + shared.getString("FedAuth",""));
            HttpResponse response = httpClient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode!=200)
                throw new Exception(appContext.getString(R.string.bad_response));
            SharedPreferences.Editor editor = shared.edit();
            editor.remove("rtFa");
            editor.remove("FedAuth");
            editor.commit();
            }
        catch (Exception e) {
            //TODO: Hier Fehlerbehandlung einfügen
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pDialog.dismiss();
    }
}

