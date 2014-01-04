package com.bpf.sharepoint.project.dates;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;

public class ServerSettings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);
        //Up-Button aktivieren
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        EditText txtSpUrl = (EditText) findViewById(R.id.txtSpUrl);
        txtSpUrl.setText(shared.getString("spUrl", getString(R.string.serverURL)));

    }
    public void doAuth(View view){
        Intent int_sp_auth;
        int_sp_auth = new Intent(this,SharePointAuth.class);
        EditText txtSpUrl = (EditText) findViewById(R.id.txtSpUrl);
        String spUrl = txtSpUrl.getText().toString();
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("spUrl", spUrl);
        editor.commit();
        startActivityForResult(int_sp_auth, 0);
    }

    public void doLogout(View view){
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        SharePointLogout spLogout =new SharePointLogout(this);
        spLogout.execute(shared.getString("spUrl", getString(R.string.serverURL)));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            TextView lbl_test_result=(TextView) findViewById(R.id.lbl_test_result);
                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String rtFa = shared.getString("rtFa","No Value");
                String FedAuth = shared.getString("FedAuth","No Value");
                if(rtFa!="No Value" && FedAuth!="No Value")
                {
                    lbl_test_result.setText(getString(R.string.login_successful));
                }
                else
                {
                    lbl_test_result.setText(getString(R.string.login_error));
                }
        }
    }


    public void selectCalendars(View view) {
        Intent int_select_calendars;
        int_select_calendars = new Intent(this,SelectCalendar.class);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        int_select_calendars.putExtra("spUrl",shared.getString("spUrl", getString(R.string.serverURL)));
        int_select_calendars.putExtra("rtFa",shared.getString("rtFa", "Empty"));
        int_select_calendars.putExtra("FedAuth",shared.getString("FedAuth", "Empty"));
        startActivityForResult(int_select_calendars, 0);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragmen_server_settings, container, false);
            return rootView;
        }
    }

}
