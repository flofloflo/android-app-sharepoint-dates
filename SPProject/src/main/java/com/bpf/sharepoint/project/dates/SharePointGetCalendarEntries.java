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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;


public class SharePointGetCalendarEntries extends AsyncTask<String,Void,String> {
    protected String RTFA;
    protected String FedAuth;
    protected String spURL;
    protected String[] entries;
    protected ListView listView;
    protected Context appContext;
    protected ProgressDialog progressDialog;
    private Exception exception;
    //Application Context per Konstruktor übergeben, da hier nicht verfügbar
    public SharePointGetCalendarEntries(Context ac, ListView lv){
        appContext=ac;
        listView=lv;
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(appContext);
        RTFA = shared.getString("rtFa", "No Value");
        FedAuth = shared.getString("FedAuth", "No Value");
        spURL = shared.getString("spUrl","No Value");
    }

    @Override
    protected void onPreExecute(){
        //ProgressBar Popup anzeigen
        progressDialog=new ProgressDialog(appContext);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(appContext.getString(R.string.wait_while_loading));
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //Vorbereitende Maßnahmen für den Background-Task
        //TODO: Login Prüfung und eventuller Re-Login




    }

    @Override
    protected String doInBackground(String... guid) {
        CheckLogin lc = new CheckLogin(appContext);
        if(lc.validate()==false){
            //progressDialog.dismiss();

            return null;
        }
        else{
            try{

                URI url=new URI(spURL+"_api/Web/Lists(guid'"+guid[0]+"')/Items?$Select=Id,Title,EventDate");
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet();
                httpGet.setURI(url);
                httpGet.addHeader("Cookie","rtFa=" + RTFA + "; FedAuth=" + FedAuth);
                httpGet.setHeader("Accept","application/json;odata=verbose");
                httpGet.setHeader("Content-type","application/json;odata=verbose");
                HttpResponse response = httpClient.execute(httpGet);
                int responseCode = response.getStatusLine().getStatusCode();
                switch(responseCode)
                {
                    case 200:
                        HttpEntity entity = response.getEntity();
                        if(entity != null)
                        {
                            String responseBody = EntityUtils.toString(entity);
                            return responseBody;
                        }
                        break;

                }
                //https://YourSite.sharepoint.com/_api/lists/getbytitle('Android_Tasks')/items
               return null;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(String response) {
        // TODO: check this.exception
        // TODO: do something with the feed
        if(response!=null)
        {
            progressDialog.setMessage(appContext.getString(R.string.parsing_data));
            try {
                //JSONArray js = new JSONArray(response);
                JSONObject jo = new JSONObject(response);
                JSONArray ja = jo.getJSONObject("d").getJSONArray("results");
                // Initialize the array
                entries=new String[ja.length()];
                for(int i=0;i<ja.length();i++) {
                    entries[i]=ja.getJSONObject(i).optString("ID")+": "+ja.getJSONObject(i).optString("Title")+" ("+ja.getJSONObject(i).optString("EventDate")+")";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(appContext, android.R.layout.simple_list_item_1, entries);

            this.listView.setAdapter(arrayAdapter);

            progressDialog.dismiss();
        }
        else
        {
            progressDialog.dismiss();
            Toast toast= Toast.makeText(appContext,R.string.please_relogin,Toast.LENGTH_SHORT);
            toast.show();
            appContext.startActivity(new Intent(appContext, SharePointAuth.class));
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }
}
