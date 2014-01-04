package com.bpf.sharepoint.project.dates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.net.URI;

public class SharePointGetCalendarList extends AsyncTask<String,Void,String> {
    protected String RTFA;
    protected String FedAuth;
    protected String[] calendars;
    protected String[] calID;
    protected ListView listView;
    protected ListView listViewIDs;
    protected Context appContext;
    protected ProgressDialog progressDialog;

    //Application Context per Konstruktor übergeben, da hier nicht verfügbar
    public SharePointGetCalendarList(Context ac, ListView lv, ListView idlv){
        appContext=ac;
        listView=lv;
        listViewIDs=idlv;
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(appContext);
        RTFA = shared.getString("rtFa", "No Value");
        FedAuth = shared.getString("FedAuth", "No Value");
    }

    @Override
    protected void onPreExecute(){
        //ProgressBar Popup anzeigen
        progressDialog=new ProgressDialog(appContext);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(appContext.getString(R.string.wait_while_loading));
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... urls) {
        //Login überprüfen
        CheckLogin lc = new CheckLogin(appContext);
        if(lc.validate()==false){
            return null;
        }
        else{ //Prüfung erfolgreich -> Daten abrufen
            try{
                URI url=new URI(urls[0]);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(url);
                //Authentifizierungs Cookies und gewünschten Antwort Typ (JSON) in Header schreiben
                httpGet.addHeader("Cookie","rtFa=" + RTFA + "; FedAuth=" + FedAuth);
                httpGet.setHeader("Accept","application/json;odata=verbose");
                httpGet.setHeader("Content-type","application/json;odata=verbose");
                //HTTP-Anfrage ausführen
                HttpResponse response = httpClient.execute(httpGet);
                int responseCode = response.getStatusLine().getStatusCode();
                switch(responseCode)
                {
                    case 200: //HTTP Code 200 = Erfolg
                        HttpEntity entity = response.getEntity();
                        if(entity != null)
                        {
                            String responseBody = EntityUtils.toString(entity);
                            return responseBody; //Antwort als String zurückliefern
                        }
                        break;
                }
               return null;
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if(response!=null)
        {
            //Server lieferte Daten zurück
            //==>Meldung ausgeben und JSON parsen
            progressDialog.setMessage(appContext.getString(R.string.parsing_data));
            try {
                //Zur Verarbeitung werden die Android Klassen JSONObject und JSONArray genutzt
                JSONObject jo = new JSONObject(response);
                JSONArray ja = jo.getJSONObject("d").getJSONArray("results");
                //Arrays für Titel und GUIDs initialisieren
                calendars=new String[ja.length()];
                calID=new String[ja.length()];
                //Titel und GUIDs in Arrays laden
                for(int i=0;i<ja.length();i++) {
                    calendars[i]=ja.getJSONObject(i).optString("Title");
                    calID[i]=ja.getJSONObject(i).optString("Id");
                }
            } catch (JSONException e) {
                //JSON-Antwort unverständlich ==> Sollte nicht vorkommen. Falls doch: Fehler ausgeben
                //TODO: Ausgabe des Stapelfehlers sollte durch vernünftige Fehlerbehandlung ersetzt werden
                e.printStackTrace();
            }
            //ListView mit Kalendernamen füllen
            ArrayAdapter arrayAdapter = new ArrayAdapter(appContext, android.R.layout.simple_list_item_1, calendars);
            this.listView.setAdapter(arrayAdapter);
            //Weitere ListView (unsichtbar) mit den GUIDs der Kalender füllen
            //(zur späteren eindeutigen Zuordnung des angeklickten Kalenders)
            ArrayAdapter arrayAdapter2 = new ArrayAdapter(appContext, android.R.layout.simple_list_item_1, calID);
            this.listViewIDs.setAdapter(arrayAdapter2);
            //Aktivitätsmeldung verbergen
            progressDialog.dismiss();
        }
        else
        {
            //Ungültige Serverantwort==>Login wahrscheinlich nicht mehr gültig
            progressDialog.dismiss(); //Aktivitätsmeldung verbergen
            //"Toast" Meldung anzeigen und SharePoint-Login Activity aufrufen um Logincookies zu erneuern
            Toast toast = Toast.makeText(appContext,R.string.please_relogin,Toast.LENGTH_SHORT);
            toast.show();
            appContext.startActivity(new Intent(appContext, SharePointAuth.class));
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
