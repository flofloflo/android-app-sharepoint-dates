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
import android.widget.ListView;
import android.widget.TextView;

public class ProjectOverview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        TextView txtNoCal = (TextView) findViewById(R.id.txtNoCalendarHint);
        ListView lstCalEntries=(ListView) findViewById(R.id.lst_cal_entries);
        if(shared.getString("CalGUID","None").equals("None"))
        {
            //Kein Kalender ausgewählt
            txtNoCal.setVisibility(View.VISIBLE);
            lstCalEntries.setVisibility(View.GONE);
        }
        else
        {
            //Kalendereinträge auflisten
            lstCalEntries.setVisibility(View.VISIBLE);
            txtNoCal.setVisibility(View.GONE);
            SharePointGetCalendarEntries spdata =new SharePointGetCalendarEntries(this,lstCalEntries);
            spdata.execute(shared.getString("CalGUID", "0"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent int_server_settings=new Intent(this,ServerSettings.class);
                startActivity(int_server_settings);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
