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
    private String rtFa="Empty";
    private String FedAuth="Empty";
    private String spUrl="Empty";
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

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);

    }
    public void doAuth(View view){
        Intent int_sp_auth;
        int_sp_auth = new Intent(this,SharePointAuth.class);
        EditText txtSpUrl = (EditText) findViewById(R.id.txtSpUrl);
        String spUrl = txtSpUrl.getText().toString();
        int_sp_auth.putExtra("SERVER_URL",spUrl);
        //Activity SharePointAuth starten
        startActivityForResult(int_sp_auth, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            Bundle authData = data.getExtras();
            String Activity=authData.getString("Activity","unknown");
            if(Activity.equals("SP_AUTH"))
            {
                rtFa=authData.getString("rtFa","No Value");
                FedAuth=authData.getString("FedAuth","No Value");
                spUrl=authData.getString("Url","No Value");
                TextView lbl_test_result=(TextView) findViewById(R.id.lbl_test_result);

                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("rtFa", rtFa);
                editor.putString("FedAuth", FedAuth);
                editor.putString("spUrl", spUrl);
                editor.commit();
                if(rtFa!="No Value" && FedAuth!="No Value")
                {
                    lbl_test_result.setText("Login erfolgreich...");
                }
                else
                {
                    lbl_test_result.setText("Es gab ein Problem während des Anmeldevorgangs. Bitte versuchen Sie es erneut.");
                }
                /*SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String url = shared.getString("URL", "No Value");
                String RTFA = shared.getString("rtFa", "No Value");
                String FedAuth = shared.getString("FedAuth", "No Value");*/
            }
            if(Activity=="SELECT_CAL")
            {
                //Do something..
            }
        }
    }


    public void selectCalendars(View view) {
        Intent int_select_calendars;
        int_select_calendars = new Intent(this,SelectCalendar.class);
        int_select_calendars.putExtra("spUrl",spUrl);
        int_select_calendars.putExtra("rtFa",rtFa);
        int_select_calendars.putExtra("FedAuth",FedAuth);
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
            View rootView = inflater.inflate(R.layout.fragment_add_server, container, false);
            return rootView;
        }
    }

}
