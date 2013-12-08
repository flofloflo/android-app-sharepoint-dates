package com.bpf.sharepoint.project.dates;

import android.app.Activity;
import android.app.ActionBar;
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
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

public class AddServer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);
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
        getMenuInflater().inflate(R.menu.add_server, menu);
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

    public void doLogin(View view) {
        Intent int_sp_auth;
        int_sp_auth = new Intent(this,SharePointAuth.class);
        EditText txt_server_url=(EditText) findViewById(R.id.txt_server_url);
        String server_url = txt_server_url.getText().toString();
        int_sp_auth.putExtra("SERVER_URL",server_url);
        startActivity(int_sp_auth);
    }
    public void selectCalendars(View view) {
        Intent int_select_calendars;
        int_select_calendars = new Intent(this,SelectCalendar.class);
        startActivity(int_select_calendars);
    }
    public void doTest(View view) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String RTFA = shared.getString("rtFa", "No Value");
        String FedAuth = shared.getString("FedAuth", "No Value");
        TextView lbl_test_result=(TextView) findViewById(R.id.lbl_test_result);
        lbl_test_result.setText("RTFA= "+RTFA+"\nFedAuth= "+FedAuth);
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
            View rootView = inflater.inflate(R.layout.fragment_add_server, container, false);
            return rootView;
        }
    }

}
