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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class SelectCalendar extends Activity {


    // Declare the UI components
   // private ListView calendarsListView;
   // private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_calendar);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Initialize the UI components
        ListView calList = (ListView) findViewById(R.id.lst_calendars);

        // React to user clicks on item
        calList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                // We know the View is a TextView so we can cast it
                TextView clickedView = (TextView) view;
                ListView calIdList = (ListView) findViewById(R.id.lst_calID);
                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("CalGUID", calIdList.getItemAtPosition(position).toString());
                editor.commit();
                Toast.makeText(SelectCalendar.this, "Kalender wurde gewählt: " + clickedView.getText() + "", Toast.LENGTH_SHORT).show();
                //SelectCalendar.this.finish();
                Intent int_main=new Intent(SelectCalendar.this,ProjectOverview.class);
                startActivity(int_main);
            }
        });
        //Liste aktualiseren
        doRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_refresh:
                    doRefresh();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void doRefresh() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        SharePointGetCalendarList spdata =new SharePointGetCalendarList(this,(ListView) findViewById(R.id.lst_calendars),(ListView) findViewById(R.id.lst_calID));
        spdata.execute(shared.getString("spUrl", getString(R.string.serverURL))+"_api/Lists/?$filter=BaseTemplate%20eq%20106&$select=Id,Title");
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
            View rootView = inflater.inflate(R.layout.fragment_select_calendar, container, false);
            return rootView;
        }
    }


}
