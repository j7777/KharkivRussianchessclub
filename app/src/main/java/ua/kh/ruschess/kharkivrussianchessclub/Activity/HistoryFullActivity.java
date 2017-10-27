package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import ua.kh.ruschess.kharkivrussianchessclub.Adapter.HistoryFullAdapter;
import ua.kh.ruschess.kharkivrussianchessclub.R;
import ua.kh.ruschess.kharkivrussianchessclub.Sql.HistoryGamesSql;

public class HistoryFullActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    int gameId;
    String gameName;
    Date date;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    private HistoryGamesSql HGHelper;
    private SQLiteDatabase HGDatabase;
    private HistoryFullAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String, String>> movesList;
    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_full);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gameId = getIntent().getExtras().getInt("ua.kh.ruschess.kharkivrussianchessclub.gameId");
        gameName = getIntent().getExtras().getString("ua.kh.ruschess.kharkivrussianchessclub.gameName");
        HistoryFullActivity.this.setTitle(gameName);

        setting = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        HGHelper = new HistoryGamesSql(this);
        HGDatabase = HGHelper.getWritableDatabase();

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);

        movesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hm;

        String query = "SELECT * FROM " + HGHelper.DATABASE_TABLE_MOVES + " WHERE " + HGHelper.ID_GAME + " = " + gameId + " ORDER BY " + HGHelper.COLUMN_ID + " ASC";
        Cursor cursor = HGDatabase.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                hm = new HashMap<>();

                switch (cursor.getInt(cursor.getColumnIndex(HGHelper.FIGURE_COLUMN))) {
                    case 1:
                        hm.put("icon", String.valueOf(R.drawable.ic_action_white_fgr));
                        break;
                    case 2:
                        hm.put("icon", String.valueOf(R.drawable.ic_action_brown_fgr));
                        break;
                    default:
                        hm.put("icon", String.valueOf(R.drawable.ic_action_black_fgr));
                }

                date = new Date();
                date.setTime(cursor.getLong(cursor.getColumnIndex(HGHelper.STAMP_COLUMN)));
                hm.put("time", dateFormat.format(date));

                movesList.add(hm);
            }
        }
        cursor.close();

        adapter = new HistoryFullAdapter(movesList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(HistoryFullActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.quest_dell_game, Snackbar.LENGTH_LONG);
                final View viewSnack = snack.getView();
                TextView textView = (TextView) viewSnack.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                Snackbar but_snack = snack.setAction(R.string.str_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            HGHelper = new HistoryGamesSql(HistoryFullActivity.this);
                            HGDatabase = HGHelper.getWritableDatabase();

                            HGDatabase.delete(HGHelper.DATABASE_TABLE_GAMES, HGHelper.COLUMN_ID + "= " + gameId, null);
                            HGDatabase.delete(HGHelper.DATABASE_TABLE_MOVES, HGHelper.ID_GAME + " = " + gameId, null);

                            movesList.removeAll(movesList);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.error_clear, Snackbar.LENGTH_LONG);
                            TextView textView = (TextView) viewSnack.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snack.show();
                        }
                    }
                });
                but_snack.setActionTextColor(Color.WHITE);
                snack.show();
            }
        });
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
            Intent intent = new Intent(HistoryFullActivity.this, SettingsActivity.class);
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
            Intent intent = new Intent(HistoryFullActivity.this, AboutClubActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            Intent intent = new Intent(HistoryFullActivity.this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rules) {
            Intent intent = new Intent(HistoryFullActivity.this, RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clock) {
            Intent intent = new Intent(HistoryFullActivity.this, GameClockActivity.class);
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
            startActivity(new Intent(HistoryFullActivity.this, ShowMapActivity.class));
        } else if (id == R.id.nav_author) {
            Intent intent = new Intent(HistoryFullActivity.this, AuthorActivity.class);
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
