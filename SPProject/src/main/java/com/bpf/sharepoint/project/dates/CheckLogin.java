package com.bpf.sharepoint.project.dates;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * Created by fh on 02.01.14.
 */
public class CheckLogin {
    Context appContext;
    public CheckLogin(Context ac){
        appContext=ac;

    }
    public boolean validate(){
        try{
            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(appContext);
            String spUrlString=shared.getString("spUrl","No Value").concat("_api/Lists/?$filter=BaseTemplate%20eq%20106&$select=Id,Title");
            URI spUrl=new URI(spUrlString);
            String RTFA = shared.getString("rtFa", "No Value");
            String FedAuth = shared.getString("FedAuth", "No Value");

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(spUrl);
            httpGet.addHeader("Cookie","rtFa=" + RTFA + "; FedAuth=" + FedAuth);
            httpGet.setHeader("Accept","application/json;odata=verbose");
            httpGet.setHeader("Content-type","application/json;odata=verbose");
            HttpResponse response = httpClient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode==200)
                return true;
            else
                return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}
