package com.bpf.sharepoint.project.dates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.net.URI;

public class SharePointLogout extends AsyncTask<String,Void,Void> {

    protected ProgressDialog pDialog;
    protected Context appContext;

    public SharePointLogout(Context ac){
        appContext=ac;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Aktivitätsanzeige einblenden
        pDialog=new ProgressDialog(appContext);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(appContext.getString(R.string.logout));
        pDialog.show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        try{
            //URL aus den SharedPrefernces laden
            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(appContext);
            URI spUrl=new URI(shared.getString("spUrl","None"));
            //Wenn keine URL hinterlegt ist Fehler ausgeben
            if(spUrl.equals("None"))
               throw new Resources.NotFoundException(appContext.getString(R.string.url_not_found));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();
            //Logout URL des SharePoint Servers aufrufen
            httpGet.setURI(new URI(spUrl+"_layouts/15/SignOut.aspx"));
            //Cokkies mitsenden, damit Server die richtige Sitzung beendet
            httpGet.addHeader("Cookie","rtFa=" + shared.getString("rtFa","") + "; FedAuth=" + shared.getString("FedAuth",""));
            HttpResponse response = httpClient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            //Wenn Serverantwort nicht 200 (Erfolg) ist, dann Fehler ausgeben
            if(responseCode!=200)
                throw new Exception(appContext.getString(R.string.bad_response));
            //Gespeicherte Authentifizierungscookies entfernen
            SharedPreferences.Editor editor = shared.edit();
            editor.remove("rtFa");
            editor.remove("FedAuth");
            editor.commit();
            }
        catch (Exception e) {
            //TODO: Hier genauere Fehlerbehandlung einfügen
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Logout fertig, Aktivitätsanzeige weider ausblenden
        pDialog.dismiss();
    }
}

