package com.bpf.sharepoint.project.dates;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class SelectCalendar extends Activity {
    // Initialize the array
    String[] monthsArray = { "JAN", "FEB", "MAR", "APR", "MAY", "JUNE", "JULY",
            "AUG", "SEPT", "OCT", "NOV", "DEC" };

    // Declare the UI components
    private ListView calendarsListView;
    private ArrayAdapter arrayAdapter;

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
        calendarsListView = (ListView) findViewById(R.id.lst_calendars);
        // For this moment, you have ListView where you can display a list.
        // But how can we put this data set to the list?
        // This is where you need an Adapter

        // context - The current context.
        // resource - The resource ID for a layout file containing a layout
        // to use when instantiating views.
        // From the third parameter, you plugged the data set to adapter
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, monthsArray);

        // By using setAdapter method, you plugged the ListView with adapter
        calendarsListView.setAdapter(arrayAdapter);
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




        GetSharePointData spdata =new GetSharePointData(this,(TextView) findViewById(R.id.lbl_json));
        spdata.execute("https://bcwgruppe.sharepoint.com/_api/Lists/?$filter=BaseTemplate%20eq%20106&$select=Id,Title");


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
