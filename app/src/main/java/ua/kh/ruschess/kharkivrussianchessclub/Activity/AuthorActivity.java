package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ua.kh.ruschess.kharkivrussianchessclub.R;

public class AuthorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setting = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        TextView aboutText = (TextView) findViewById(R.id.text_author);

        try {
            InputStream inStream = getResources().openRawResource(R.raw.author_text);

            if (inStream != null) {
                InputStreamReader sr = new InputStreamReader(inStream);
                BufferedReader reader = new BufferedReader(sr);
                String str;
                StringBuilder buffer = new StringBuilder();

                while ((str = reader.readLine()) != null) {
                    buffer.append(str).append("\n");
                }

                inStream.close();
                aboutText.setText(buffer.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            Intent intent = new Intent(AuthorActivity.this, SettingsActivity.class);
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
            Intent intent = new Intent(AuthorActivity.this, AboutClubActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            Intent intent = new Intent(AuthorActivity.this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rules) {
            Intent intent = new Intent(AuthorActivity.this, RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clock) {
            Intent intent = new Intent(AuthorActivity.this, GameClockActivity.class);
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
            startActivity(new Intent(AuthorActivity.this, ShowMapActivity.class));
        } else if (id == R.id.nav_author) {
            Intent intent = new Intent(AuthorActivity.this, AuthorActivity.class);
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
