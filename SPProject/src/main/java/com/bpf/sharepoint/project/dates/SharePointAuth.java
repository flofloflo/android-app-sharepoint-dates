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
import android.webkit.WebView;
import android.webkit.CookieManager;
import android.webkit.WebViewClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SharePointAuth extends Activity {

    protected SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharepoint_auth);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //SharePoint URL aus SharedPreferences laden
        shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Wenn keine URL gespeichert ist auf Microsoft Seite springen
        //(hier ist dann nachträglich ein manuelles aufrufen der SharePoint Seite durch den User notwendig)
        String server_url = shared.getString("spUrl","https://login.microsoftonline.com");
        //WebView Element ausfindig machen
        WebView wv = (WebView) findViewById(R.id.webView);
        //User-Agent auf Desktop setzen um richtige Ansicht zu laden..
        //(Nur wichtig wenn User die falsche URL angibt und manuell zu Sharepoint Seite navigiert)
        wv.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
        //JavaScript aktivieren
        wv.getSettings().setJavaScriptEnabled(true);
        //Vorhandene Cookies löschen
        CookieManager.getInstance().removeSessionCookie();
        //Übergebene Login-Seite aufrufen (damit der Benutzer seine Anmeldedaten eingeben kann)
        wv.loadUrl(server_url);
        //WebViewClient UrlLoading überschreiben um Auth-Cookies abzugreifen
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //URL laden
                view.loadUrl(url);
                //Cookies abfangen
                CookieManager cookieManager = CookieManager.getInstance();
                String Cookies = cookieManager.getCookie(url);
                //Wenn "rtFa" im Cookie enthalten ist, Inhalt auslesen
               if(Cookies != null && Cookies.contains("rtFa"))
                {
                    //Cookie String aufsplitten
                    String[] seperated = Cookies.split(";");
                    boolean RTFA = false;
                    boolean FedAuth = false;
                    String RTFA_Value = "";
                    String FedAuth_Value = "";
                    Intent it=new Intent();
                    //Cookie Inhalt überprüfen
                    for(int i=0;i <= seperated.length - 1;i++)
                    {
                        if(seperated[i].contains("rtFa") && RTFA != true)
                        {
                            RTFA_Value = seperated[i].trim().substring(5);
                            RTFA =true;
                        }
                        if(seperated[i].contains("FedAuth") && FedAuth != true)
                        {
                            FedAuth_Value = seperated[i].trim().substring(8);
                            FedAuth =true;
                        }
                        if(RTFA == true && FedAuth == true)
                        {
                            //Hauptpart der SharePoint URL herausfiltern
                            Pattern p = Pattern.compile("https://[a-zA-Z0-9]*?\\.sharepoint\\.com/");
                            Matcher m = p.matcher(url);
                            if(m.find())
                                url= m.group(0);
                            else
                                return false;
                            //Ermittelte Werte in den SharedPreferences speichern
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("rtFa", RTFA_Value);
                            editor.putString("FedAuth", FedAuth_Value);
                            editor.putString("spUrl", url);
                            editor.commit();
                            setResult(RESULT_OK, it);
                            finish(); //Authentifizierung vollständig ==> Activity beenden
                        }}}
                return true;
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sharepoint_auth, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_sharepoint_auth, container, false);

            return rootView;
        }
    }
}
