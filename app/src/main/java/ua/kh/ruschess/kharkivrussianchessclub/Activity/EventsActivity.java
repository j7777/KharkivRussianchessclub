package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.kh.ruschess.kharkivrussianchessclub.Adapter.EventAdapter;
import ua.kh.ruschess.kharkivrussianchessclub.Class.GetDateMetods;
import ua.kh.ruschess.kharkivrussianchessclub.Class.RecyclerItemClickListener;
import ua.kh.ruschess.kharkivrussianchessclub.R;

public class EventsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences setting;
    private String setCountEvents;
    private static String setEventsUrl;
    private JSONArray data = null;
    private GetDateMetods cldt = new GetDateMetods();

    private EventAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout loader;
    private ArrayList<HashMap<String, String>> eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loader = (LinearLayout) findViewById(R.id.loader);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()){
                    eventsList.removeAll(eventsList);
                    adapter.notifyDataSetChanged();

                    loader.setVisibility(View.VISIBLE);
                    new JSONParse().execute();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setting = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setCountEvents = setting.getString("count_load_events", "10");

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);
        setEventsUrl = "http://ruschess.kh.ua/?chess_data_events=1&count="+setCountEvents;

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Map<String, String> hashmap;
                        hashmap = (Map<String, String>) eventsList.get(position);

                        Intent intent = new Intent(EventsActivity.this, EventFullActivity.class);
                        intent.putExtra("start_stamp", hashmap.get("start_stamp"));
                        intent.putExtra("end_stamp", hashmap.get("end_stamp"));
                        intent.putExtra("venue", hashmap.get("venue"));
                        intent.putExtra("country", hashmap.get("country"));
                        intent.putExtra("address", hashmap.get("address"));
                        intent.putExtra("city", hashmap.get("city"));
                        intent.putExtra("province", hashmap.get("province"));
                        intent.putExtra("contact_name", hashmap.get("contact_name"));
                        intent.putExtra("contact_phone", hashmap.get("contact_phone"));
                        intent.putExtra("contact_email", hashmap.get("contact_email"));
                        intent.putExtra("contact_url", hashmap.get("contact_url"));
                        intent.putExtra("cost", hashmap.get("cost"));
                        intent.putExtra("is_free", hashmap.get("is_free"));
                        intent.putExtra("ticket_url", hashmap.get("ticket_url"));
                        intent.putExtra("latitude", hashmap.get("latitude"));
                        intent.putExtra("longitude", hashmap.get("longitude"));
                        intent.putExtra("title", hashmap.get("title"));
                        intent.putExtra("content", hashmap.get("content"));
                        intent.putExtra("url", hashmap.get("url"));
                        intent.putExtra("start_date", hashmap.get("start_date"));
                        intent.putExtra("end_date", hashmap.get("end_date"));
                        startActivity(intent);
                    }
                })
        );

        if(isOnline()){
            new JSONParse().execute();
        }
        else{
            loader.setVisibility(View.GONE);
        }
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = null;
            try {
                json = jParser.getJSONFromUrl(setEventsUrl);
            } catch (IOException e) {
                Log.e("JSON Parser", "Error parsing data " + e.getMessage());
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            loader.setVisibility(View.GONE);

            try {
                // Getting JSON Array
                data = json.getJSONArray("data");

                HashMap<String, String> hm;
                eventsList = new ArrayList<HashMap<String, String>>();

                for(int i = 0, mx = data.length(); i < mx; i++){
                    JSONObject obj = data.getJSONObject(i);
                    hm = new HashMap<>();

                    hm.put("start_stamp", obj.getString("start_stamp"));
                    hm.put("end_stamp", obj.getString("end_stamp"));
                    hm.put("venue", obj.getString("venue"));
                    hm.put("country", obj.getString("country"));
                    hm.put("address", obj.getString("address"));
                    hm.put("city", obj.getString("city"));
                    hm.put("province", obj.getString("province"));
                    hm.put("contact_name", obj.getString("contact_name"));
                    hm.put("contact_phone", obj.getString("contact_phone"));
                    hm.put("contact_email", obj.getString("contact_email"));
                    hm.put("contact_url", obj.getString("contact_url"));
                    hm.put("cost", obj.getString("cost"));
                    hm.put("is_free", obj.getString("is_free"));
                    hm.put("ticket_url", obj.getString("ticket_url"));
                    hm.put("latitude", obj.getString("latitude"));
                    hm.put("longitude", obj.getString("longitude"));
                    hm.put("title", obj.getString("title"));
                    hm.put("content", obj.getString("content"));
                    hm.put("url", obj.getString("url").replaceAll("#038;", ""));
                    hm.put("start_date", cldt.getFullTime(Long.parseLong(obj.getString("start_stamp"))));
                    hm.put("end_date", cldt.getFullTime(Long.parseLong(obj.getString("end_stamp"))));

                    eventsList.add(hm);
                }

                adapter = new EventAdapter(eventsList);
                recyclerView.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(EventsActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
            } catch (JSONException e) {
                Snackbar.make(findViewById(android.R.id.content), R.string.error_load, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public class JSONParser {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // constructor
        public JSONParser(){}

        public JSONObject getJSONFromUrl(String url) throws IOException {
            URL requestUrl = new URL(url);
            URLConnection con = requestUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            int cp;
            while((cp = in.read())!= -1){
                sb.append((char)cp);
            }
            json = sb.toString();

            try {
                jObj = new JSONObject(json);
            }
            catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            return jObj;
        }
    }

    public boolean isOnline() {
        Boolean use_connect = setting.getBoolean("use_connect", true);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE && !use_connect) {
                return true;
            }
            else{
                Snackbar.make(findViewById(android.R.id.content), R.string.error_wifi, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), R.string.error_connect, Snackbar.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(EventsActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent intent = new Intent(EventsActivity.this, AboutClubActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            Intent intent = new Intent(EventsActivity.this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rules) {
            Intent intent = new Intent(EventsActivity.this, RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clock) {
            Intent intent = new Intent(EventsActivity.this, GameClockActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_site) {
            if(isOnline()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBaseContext().getString(R.string.site_url)));
                startActivity(intent);
            }
        } else if (id == R.id.nav_buy) {
            if(isOnline()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBaseContext().getString(R.string.buy_url)));
                startActivity(intent);
            }
        } else if (id == R.id.nav_club) {
            startActivity(new Intent(EventsActivity.this, ShowMapActivity.class));
        } else if (id == R.id.nav_author) {
            Intent intent = new Intent(EventsActivity.this, AuthorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
