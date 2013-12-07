package com.bpf.sharepoint.project.dates;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.CookieManager;
import android.webkit.WebViewClient;
import android.widget.EditText;



public class AddProject extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
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
        getMenuInflater().inflate(R.menu.add_project, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_project, container, false);

            return rootView;
        }
    }

    public void doLogin(View view) {
        WebView wv = (WebView) findViewById(R.id.webView);
        EditText txtURL = (EditText) findViewById(R.id.edit_cal_address);
        String calAddress = txtURL.getText().toString();

        wv.loadUrl(calAddress);

        wv.getSettings().setJavaScriptEnabled(true);
        CookieManager.getInstance().removeSessionCookie();

        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                CookieManager cookieManager = CookieManager.getInstance();
                //"https://bcwgruppe.sharepoint.com/SitePages/Homepage.aspx?AjaxDelta=1"
                String Cookies = cookieManager.getCookie(url);
                if(Cookies.contains("rtFa"))
                {
                    String[] seperated = Cookies.split(";");
                    boolean RTFA = false;
                    boolean FedAuth = false;
                    String RTFA_Value = "";
                    String FedAuth_Value = "";
                    for(int i=0;i <= seperated.length - 1;i++)
                    {
                        if(seperated[i].contains("rtFa") && RTFA != true)
                        {
                            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(AddProject.this);
                            SharedPreferences.Editor editor = shared.edit();
                            RTFA_Value = seperated[i].trim().substring(5);
                            editor.putString("rtFa", RTFA_Value);
                            editor.commit();
                            RTFA =true;
                        }
                        if(seperated[i].contains("FedAuth") && FedAuth != true)
                        {
                            SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(AddProject.this);
                            SharedPreferences.Editor editor = shared.edit();
                            FedAuth_Value = seperated[i].trim().substring(8);
                            editor.putString("FedAuth", FedAuth_Value);
                            editor.commit();
                            FedAuth =true;
                        }
                        if(RTFA == true && FedAuth == true)
                        {
                            finish(); //Beendet Activity
                            //TODO: Seperate Activity für Login bauen!
                        }}}
                return true;
            }});
    }



    public void saveURL(View view) {

    }
}
