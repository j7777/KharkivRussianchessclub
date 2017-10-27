package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import ua.kh.ruschess.kharkivrussianchessclub.Class.GetDateMetods;
import ua.kh.ruschess.kharkivrussianchessclub.R;

public class EventFullActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private GetDateMetods cldt = new GetDateMetods();
    Intent intent;
    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_full);
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

        LinearLayout layoutDesc = (LinearLayout)findViewById(R.id.layoutDesc);
        LinearLayout layoutLocation = (LinearLayout)findViewById(R.id.layoutLocation);
        LinearLayout layoutUser = (LinearLayout)findViewById(R.id.layoutUser);
        LinearLayout layoutPhone = (LinearLayout)findViewById(R.id.layoutPhone);
        LinearLayout layoutEmail = (LinearLayout)findViewById(R.id.layoutEmail);
        LinearLayout layoutUrl = (LinearLayout)findViewById(R.id.layoutUrl);
        LinearLayout layoutCost = (LinearLayout)findViewById(R.id.layoutCost);

        TextView descEvent = (TextView)findViewById(R.id.descEvent);
        TextView start_date = (TextView)findViewById(R.id.start_date);
        TextView end_date = (TextView)findViewById(R.id.end_date);
        TextView locationEvent = (TextView)findViewById(R.id.locationEvent);
        TextView contact = (TextView)findViewById(R.id.contact);
        TextView phone = (TextView)findViewById(R.id.phone);
        TextView email = (TextView)findViewById(R.id.email);
        TextView url = (TextView)findViewById(R.id.url);
        TextView cost = (TextView)findViewById(R.id.cost);

        EventFullActivity.this.setTitle(getIntent().getExtras().getString("title"));

        if(getIntent().getExtras().getString("content").isEmpty()) {
            layoutDesc.setVisibility(View.GONE);
        }
        else{
            descEvent.setText(getIntent().getExtras().getString("content"));
        }

        start_date.setText(cldt.getFullTime(Long.parseLong(getIntent().getExtras().getString("start_stamp"))));
        end_date.setText(cldt.getFullTime(Long.parseLong(getIntent().getExtras().getString("end_stamp"))));

        if(getIntent().getExtras().getString("venue").isEmpty() || getIntent().getExtras().getString("address").isEmpty()) {
            layoutLocation.setVisibility(View.GONE);
        }
        else{
            locationEvent.setText(getIntent().getExtras().getString("venue")+" "+getIntent().getExtras().getString("address"));
        }

        if(getIntent().getExtras().getString("contact_name").isEmpty()) {
            layoutUser.setVisibility(View.GONE);
        }
        else{
            contact.setText(getIntent().getExtras().getString("contact_name"));
        }

        if(getIntent().getExtras().getString("contact_phone").isEmpty()) {
            layoutPhone.setVisibility(View.GONE);
        }
        else{
            phone.setText(getIntent().getExtras().getString("contact_phone"));
        }

        if(getIntent().getExtras().getString("contact_email").isEmpty()) {
            layoutEmail.setVisibility(View.GONE);
        }
        else{
            email.setText(getIntent().getExtras().getString("contact_email"));
        }

        if(getIntent().getExtras().getString("url").isEmpty()) {
            layoutUrl.setVisibility(View.GONE);
        }
        else{
            url.setText(getIntent().getExtras().getString("url"));
        }

        if(getIntent().getExtras().getString("cost").isEmpty()) {
            layoutCost.setVisibility(View.GONE);
        }
        else{
            cost.setText(getIntent().getExtras().getString("cost"));
        }
    }

    public void onClickDate(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.confirm_event_calendar)
                    .setCancelable(false)
                    .setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                        public void onClick(DialogInterface dialog, int id) {
                            intent = new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(CalendarContract.Events.TITLE, getIntent().getExtras().getString("title"))
                                    .putExtra(CalendarContract.Events.DESCRIPTION, getIntent().getExtras().getString("content"))
                                    .putExtra(CalendarContract.Events.EVENT_LOCATION, getIntent().getExtras().getString("address"))
                                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cldt.getStampTime(Long.parseLong(getIntent().getExtras().getString("start_stamp"))))
                                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cldt.getStampTime(Long.parseLong(getIntent().getExtras().getString("end_stamp"))))
                                    .putExtra(Intent.EXTRA_EMAIL, getIntent().getExtras().getString("contact_email"));
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    }).setNegativeButton(R.string.str_no, null).show();
        }
    }

    public void onClickLocation(View view) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_event_map)
                .setCancelable(false)
                .setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri location = Uri.parse("geo:"+getIntent().getExtras().getString("latitude")+","+getIntent().getExtras().getString("longitude")+"?q="+getIntent().getExtras().getString("address"));
                        intent = new Intent(Intent.ACTION_VIEW, location);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                }).setNegativeButton(R.string.str_no, null).show();
    }

    public void onClickPhone(View view) {
        String phoneList[] = getResources().getStringArray(R.array.phone_list);

        new AlertDialog.Builder(EventFullActivity.this)
                .setTitle(R.string.sel_game)
                .setItems(phoneList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onClickListPhone(which);
                    }
                }).show();
    }

    private void onClickListPhone(int which){
        switch (which) {
            case 0:
                //Отправить смс
                intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("smsto:"));
                intent.putExtra("address", getIntent().getExtras().getString("contact_phone"));
                intent.putExtra("sms_body", getResources().getString(R.string.quest_to_event) + " "+getIntent().getExtras().getString("title"));
                intent.setType("text/plain");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case 1:
                //Позвонить
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + getIntent().getExtras().getString("contact_phone")));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
        }
    }

    public void onClickEmail(View view) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_event_email)
                .setCancelable(false)
                .setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + getIntent().getExtras().getString("contact_email")));
                        intent.putExtra(Intent.EXTRA_SUBJECT, getIntent().getExtras().getString("title"));
                        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.quest_to_event) + " " + getIntent().getExtras().getString("title"));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                }).setNegativeButton(R.string.str_no, null).show();
    }

    public void onClickUrl(View view) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_event_site)
                .setCancelable(false)
                .setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri webpage = Uri.parse(getIntent().getExtras().getString("url"));
                        intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                }).setNegativeButton(R.string.str_no, null).show();
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
            Intent intent = new Intent(EventFullActivity.this, SettingsActivity.class);
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
            Intent intent = new Intent(EventFullActivity.this, AboutClubActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            Intent intent = new Intent(EventFullActivity.this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rules) {
            Intent intent = new Intent(EventFullActivity.this, RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clock) {
            Intent intent = new Intent(EventFullActivity.this, GameClockActivity.class);
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
            startActivity(new Intent(EventFullActivity.this, ShowMapActivity.class));
        } else if (id == R.id.nav_author) {
            Intent intent = new Intent(EventFullActivity.this, AuthorActivity.class);
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
