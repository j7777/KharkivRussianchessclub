package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
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
import java.util.Map;

import ua.kh.ruschess.kharkivrussianchessclub.Adapter.HistoryAdapter;
import ua.kh.ruschess.kharkivrussianchessclub.Class.RecyclerItemClickListener;
import ua.kh.ruschess.kharkivrussianchessclub.R;
import ua.kh.ruschess.kharkivrussianchessclub.Sql.HistoryGamesSql;

public class HistoryGamesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    int idSel;
    Date date;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    private HistoryGamesSql HGHelper;
    private SQLiteDatabase HGDatabase;
    private ArrayList<HashMap<String, String>> gamesList;
    private HistoryAdapter adapter;
    private RecyclerView recyclerView;
    int indexList;
    private int pos;
    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_games);
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

        HGHelper = new HistoryGamesSql(this);
        HGDatabase = HGHelper.getWritableDatabase();

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);

        gamesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hm;

        String query = "SELECT * FROM " + HGHelper.DATABASE_TABLE_GAMES + " ORDER BY " + HGHelper.COLUMN_ID + " DESC";
        Cursor cursor = HGDatabase.rawQuery(query, null);

        if(cursor != null) {
            int counter = 0;
            while (cursor.moveToNext()) {
                hm = new HashMap<>();
                hm.put("id", cursor.getString(cursor.getColumnIndex(HGHelper.COLUMN_ID)));
                hm.put("name", cursor.getString(cursor.getColumnIndex(HGHelper.NAME_COLUMN)));
                hm.put("white_figures", countToElem(cursor.getInt(cursor.getColumnIndex(HGHelper.WHITE_FIGURES_COLUMN))));
                hm.put("brown_figures", countToElem(cursor.getInt(cursor.getColumnIndex(HGHelper.BROWN_FIGURES_COLUMN))));
                hm.put("black_figures", countToElem(cursor.getInt(cursor.getColumnIndex(HGHelper.BLACK_FIGURES_COLUMN))));
                hm.put("total_figures", countToElem(cursor.getInt(cursor.getColumnIndex(HGHelper.TOTAL_FIGURES_COLUMN))));

                date = new Date();
                date.setTime(cursor.getLong(cursor.getColumnIndex(HGHelper.TIME_START_COLUMN)));
                hm.put("time_start", dateFormat.format(date));

                date = new Date();
                date.setTime(cursor.getLong(cursor.getColumnIndex(HGHelper.TIME_END_COLUMN)));
                hm.put("time_end", dateFormat.format(date));

                gamesList.add(hm);
                counter++;
            }

            if(counter == 0) {
                Snackbar.make(findViewById(android.R.id.content), R.string.history_empty, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
        cursor.close();

        adapter = new HistoryAdapter(gamesList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(HistoryGamesActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        String list[] = getResources().getStringArray(R.array.games_list);
                        final Map<String, String> hashmap = (Map<String, String>) gamesList.get(position);

                        new AlertDialog.Builder(HistoryGamesActivity.this)
                                .setTitle(R.string.sel_game)
                                .setItems(list, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        idSel = Integer.parseInt(hashmap.get("id"));
                                        indexList = which;
                                        pos = position;
                                        onClickListGames(which);
                                    }
                                }).show();
                    }
                })
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.quest_dell_all, Snackbar.LENGTH_LONG);
                final View viewSnack = snack.getView();
                TextView textView = (TextView) viewSnack.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                Snackbar but_snack = snack.setAction(R.string.str_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            HGHelper = new HistoryGamesSql(HistoryGamesActivity.this);
                            HGDatabase = HGHelper.getWritableDatabase();

                            HGDatabase.delete(HGHelper.DATABASE_TABLE_GAMES, null, null);
                            HGDatabase.delete(HGHelper.DATABASE_TABLE_MOVES, null, null);

                            gamesList.removeAll(gamesList);
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

    private void onClickListGames(int which){
        final Map<String, String> hashmap = (Map<String, String>) gamesList.get(pos);
        switch (which) {
            case 0:
                //Подробнее
                Intent indent = new Intent(HistoryGamesActivity.this, HistoryFullActivity.class);
                indent.putExtra("ua.kh.ruschess.kharkivrussianchessclub.gameId", Integer.parseInt(hashmap.get("id")));
                indent.putExtra("ua.kh.ruschess.kharkivrussianchessclub.gameName", hashmap.get("name"));
                startActivity(indent);
                break;
            case 1:
                //Удалить
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.quest_dell, Snackbar.LENGTH_LONG);
                final View viewSnack = snack.getView();
                TextView textView = (TextView) viewSnack.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                Snackbar but_snack = snack.setAction(R.string.str_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            HGHelper = new HistoryGamesSql(HistoryGamesActivity.this);
                            HGDatabase = HGHelper.getWritableDatabase();

                            HGDatabase.delete(HGHelper.DATABASE_TABLE_GAMES, HGHelper.COLUMN_ID + "= " + Integer.parseInt(hashmap.get("id")), null);
                            HGDatabase.delete(HGHelper.DATABASE_TABLE_MOVES, HGHelper.ID_GAME + " = " + Integer.parseInt(hashmap.get("id")), null);

                            gamesList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                        }
                        catch(Exception e){
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.error_clear, Snackbar.LENGTH_LONG);
                            TextView textView = (TextView)viewSnack .findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            snack.show();
                        }
                    }
                });
                but_snack.setActionTextColor(Color.WHITE);
                snack.show();
                break;
        }
    }

    private String countToElem(int conter){
        int hour, min, sec;
        hour = conter / 3600;
        String arr[] = new String[10];

        arr[0] = Integer.toString(hour);
        arr[1] = Integer.toString(hour / 100 % 10);
        arr[2] = Integer.toString(hour / 10 % 10);
        arr[3] = Integer.toString(hour % 10);

        min = conter / 60 % 60;
        arr[4] = Integer.toString(min);
        arr[5] = Integer.toString(min / 10 % 10);
        arr[6] = Integer.toString(min % 10);

        sec = conter % 60;
        arr[7] = Integer.toString(sec);
        arr[8] = Integer.toString(sec / 10 % 10);
        arr[9] = Integer.toString(sec % 10);

        return arr[1] + arr[2] + arr[3] + ":" + arr[5] + arr[6] + ":" + arr[8] + arr[9];
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
            Intent intent = new Intent(HistoryGamesActivity.this, SettingsActivity.class);
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
            Intent intent = new Intent(HistoryGamesActivity.this, AboutClubActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            Intent intent = new Intent(HistoryGamesActivity.this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rules) {
            Intent intent = new Intent(HistoryGamesActivity.this, RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clock) {
            Intent intent = new Intent(HistoryGamesActivity.this, GameClockActivity.class);
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
            startActivity(new Intent(HistoryGamesActivity.this, ShowMapActivity.class));
        } else if (id == R.id.nav_author) {
            Intent intent = new Intent(HistoryGamesActivity.this, AuthorActivity.class);
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
