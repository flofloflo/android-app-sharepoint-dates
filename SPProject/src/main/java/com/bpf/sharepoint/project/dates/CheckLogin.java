package com.bpf.sharepoint.project.dates;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.net.URI;

public class CheckLogin {
    Context appContext;
    public CheckLogin(Context ac){
        appContext=ac;

    }
    public boolean validate(){
        try{
            //Verbindungsdaten aus den SharedPreferences laden
            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(appContext);
            String spUrlString=shared.getString("spUrl","No Value").concat("_api/Lists/?$filter=BaseTemplate%20eq%20106&$select=Id,Title");
            URI spUrl=new URI(spUrlString);
            String RTFA = shared.getString("rtFa", "No Value");
            String FedAuth = shared.getString("FedAuth", "No Value");
            //HTTP-Verbindung vorbereiten
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(spUrl);
            httpGet.addHeader("Cookie","rtFa=" + RTFA + "; FedAuth=" + FedAuth);
            httpGet.setHeader("Accept","application/json;odata=verbose");
            httpGet.setHeader("Content-type","application/json;odata=verbose");
            //HTTP-Anfrage ausführen
            HttpResponse response = httpClient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            //Bei fehlerhaftem Login Cooki kommt hier ein Fehlercode und nicht die 200
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
