package com.bpf.sharepoint.project.dates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
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
    private Exception exception;
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
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //Vorbereitende Maßnahmen für den Background-Task
        //TODO: Login Prüfung und eventuller Re-Login




    }

    @Override
    protected String doInBackground(String... urls) {
        CheckLogin lc = new CheckLogin(appContext);
        if(lc.validate()==false){
            //progressDialog.dismiss();

            return null;
        }
        else{
            try{

                URI url=new URI(urls[0]);
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
                calendars=new String[ja.length()];
                calID=new String[ja.length()];
                for(int i=0;i<ja.length();i++) {
                    calendars[i]=ja.getJSONObject(i).optString("Title");
                    calID[i]=ja.getJSONObject(i).optString("Id");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(appContext, android.R.layout.simple_list_item_1, calendars);

            this.listView.setAdapter(arrayAdapter);
            ArrayAdapter arrayAdapter2 = new ArrayAdapter(appContext, android.R.layout.simple_list_item_1, calID);

            this.listViewIDs.setAdapter(arrayAdapter2);
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
