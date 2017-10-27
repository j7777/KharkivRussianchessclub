package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import ua.kh.ruschess.kharkivrussianchessclub.R;
import ua.kh.ruschess.kharkivrussianchessclub.Sql.HistoryGamesSql;

public class GameClockActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Общий, белые, коричневые, чёрные
    private int counters[] = new int[4];
    private byte type_per = 1;
    private Boolean cancelGame = false;
    private int counterPeriod;
    private boolean firstTouch = true;

    private Timer myTimer;
    //Часы 1,2,3 Минуты 1,2 Секунды 1,2
    private ViewFlipper viewFlipper[] = new ViewFlipper[7];

    private String[] timerList;

    public LinearLayout linearLayoutTouchImage;
    public LinearLayout linearLayoutTimer;

    private TextView StrWhite, StrBrown, StrBlack, StrTotal;

    private SharedPreferences setting;
    private String timer_sound;
    private Ringtone ringtone;
    private Uri ringtoneUri;
    private int time_signal;
    private String timeIntervalSetting;
    private int periodUserTime;
    private int periodTotalTime;
    private Byte numPeriod;
    private Boolean vibroCheck = false;

    private Boolean switch_periods = false;
    private Boolean switch_period_user = false;
    private Boolean switch_period_total = false;

    private Vibrator vibr;
    private long[] pattern = {0, 100, 100, 100};

    private float touchOldX = 0;
    private byte flagTouch = 1;

    ArrayList<String> logGame = new ArrayList<>();
    Calendar calendar;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_clock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setting = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        timer_sound = setting.getString("timer_sound_sel", "");
        timeIntervalSetting = setting.getString("periods_clock_signal", "5");
        periodUserTime = Integer.parseInt(setting.getString("period_user_time", "30"));
        periodTotalTime = Integer.parseInt(setting.getString("period_total_time", "120"));

        try{
            numPeriod = Byte.parseByte(timeIntervalSetting);
        } catch (Exception e){
            numPeriod = 0;
        }

        vibr = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibroCheck = setting.getBoolean("timer_vibro_sel", false);

        switch_periods = setting.getBoolean("switch_periods_clock_signal", false);
        switch_period_user = setting.getBoolean("switch_period_user_time", false);
        switch_period_total = setting.getBoolean("switch_period_total_time", false);

        ringtoneUri = Uri.parse(timer_sound);
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        time_signal = Integer.parseInt(setting.getString("time_signal", "3"));

        StrWhite = (TextView) findViewById(R.id.textViewWhite);
        StrBrown = (TextView) findViewById(R.id.textViewBrown);
        StrBlack = (TextView) findViewById(R.id.textViewBlack);
        StrTotal = (TextView) findViewById(R.id.textViewTotal);

        linearLayoutTouchImage = (LinearLayout) findViewById(R.id.linearLayoutTouchImage);

        linearLayoutTimer = (LinearLayout) findViewById(R.id.linearLayout);

        timerList = getResources().getStringArray(R.array.timer_list);

        for(int i = 0, max = viewFlipper.length; i < max; i++){
            switch (i){
                case 1:
                    viewFlipper[i] = (ViewFlipper) findViewById(R.id.timer_block_hour2);
                    break;
                case 2:
                    viewFlipper[i] = (ViewFlipper) findViewById(R.id.timer_block_hour3);
                    break;
                case 3:
                    viewFlipper[i] = (ViewFlipper) findViewById(R.id.timer_block_min1);
                    break;
                case 4:
                    viewFlipper[i] = (ViewFlipper) findViewById(R.id.timer_block_min2);
                    break;
                case 5:
                    viewFlipper[i] = (ViewFlipper) findViewById(R.id.timer_block_sec1);
                    break;
                case 6:
                    viewFlipper[i] = (ViewFlipper) findViewById(R.id.timer_block_sec2);
                    break;
                default:
                    viewFlipper[i] = (ViewFlipper) findViewById(R.id.timer_block_hour1);
            }
        }

        setSizeTimer();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstTouch) {
                    linearLayoutTouchImage.setVisibility(View.GONE);
                    firstTouch = false;
                }
                moveDialog();
            }
        });

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.CoordinatorLayout);
        coordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                long millVibr = 25;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        touchOldX = x;
                        flagTouch = 1;
                        break;
                    case MotionEvent.ACTION_MOVE: // движение
                        if(x < touchOldX - 50){
                            flagTouch = 2;
                            Animation startAnimation = AnimationUtils.loadAnimation(GameClockActivity.this, R.anim.shiftinleft);
                            linearLayoutTimer.startAnimation(startAnimation);
                        }
                        else if(x > touchOldX + 50){
                            flagTouch = 3;
                            Animation startAnimation2 = AnimationUtils.loadAnimation(GameClockActivity.this, R.anim.shiftinright);
                            linearLayoutTimer.startAnimation(startAnimation2);
                        }
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        if(firstTouch){
                            linearLayoutTouchImage.setVisibility(View.GONE);
                            firstTouch = false;
                            moveDialog();
                        }
                        else{
                            switch (flagTouch){
                                case 2:
                                    //движение влево
                                    if(vibroCheck) {
                                        vibr.vibrate(millVibr);
                                    }
                                    Animation endAnimation = AnimationUtils.loadAnimation(GameClockActivity.this, R.anim.shiftoutleft);
                                    linearLayoutTimer.startAnimation(endAnimation);
                                    switch (type_per){
                                        case 1:
                                            //Ход коричневых
                                            onStartBrown();
                                            GameClockActivity.this.setTitle(R.string.move_brown);
                                            setFloatIcon(2);
                                            break;
                                        case 2:
                                            //Ход чёрных
                                            onStartBlack();
                                            GameClockActivity.this.setTitle(R.string.move_black);
                                            setFloatIcon(3);
                                            break;
                                        case 3:
                                            //Ход белых
                                            onStartWhite();
                                            GameClockActivity.this.setTitle(R.string.move_white);
                                            setFloatIcon(1);
                                            break;
                                    }
                                    break;
                                case 3:
                                    //движение вправо
                                    if(vibroCheck) {
                                        vibr.vibrate(millVibr);
                                    }
                                    Animation endAnimation2 = AnimationUtils.loadAnimation(GameClockActivity.this, R.anim.shiftoutright);
                                    linearLayoutTimer.startAnimation(endAnimation2);
                                    switch (type_per){
                                        case 1:
                                            //Ход чёрных
                                            onStartBlack();
                                            GameClockActivity.this.setTitle(R.string.move_black);
                                            setFloatIcon(3);
                                            break;
                                        case 2:
                                            //Ход белых
                                            onStartWhite();
                                            GameClockActivity.this.setTitle(R.string.move_white);
                                            setFloatIcon(1);
                                            break;
                                        case 3:
                                            //Ход коричневых
                                            onStartBrown();
                                            GameClockActivity.this.setTitle(R.string.move_brown);
                                            setFloatIcon(2);
                                            break;
                                    }
                                    break;
                            }
                        }

                        break;
                }

                return true;
            }
        });
    }

    /*
    * 1 - white
    * 2 - brown
    * 3 - black
     */
    private void setFloatIcon(int flag){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch(flag){
                case 1:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.text_white_fgr, getApplicationContext().getTheme()));
                    break;
                case 2:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.text_brown_fgr, getApplicationContext().getTheme()));
                    break;
                case 3:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.text_black_fgr, getApplicationContext().getTheme()));
                    break;
                default:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.but_list_white, getApplicationContext().getTheme()));
            }
        }
        else {
            switch(flag){
                case 1:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.text_white_fgr));
                    break;
                case 2:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.text_brown_fgr));
                    break;
                case 3:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.text_black_fgr));
                    break;
                default:
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.but_list_white));
            }
        }
    }

    private void TimerMethod() {
        switch(type_per){
            case 1: this.runOnUiThread(Timer_Tick_White);
                break;
            case 2: this.runOnUiThread(Timer_Tick_Brown);
                break;
            default: this.runOnUiThread(Timer_Tick_Black);
        }
    }

    private Runnable Timer_Tick_White = new Runnable() {
        public void run() {
            counters[0]++;
            counters[1]++;
            counterPeriod++;

            if((counters[1] / 60) >= periodUserTime && switch_period_user) {
                if (!ringtone.isPlaying() && !"".equals(timer_sound)) {
                    mySound();
                }

                if (vibroCheck) {
                    vibr.vibrate(pattern, -1);
                }

                //Закончить партию
                finishGame();
                GameClockActivity.this.setTitle(R.string.time_white_finish);
            }

            if((counters[0] / 60) >= periodTotalTime && switch_period_total) {
                if (!ringtone.isPlaying() && !"".equals(timer_sound)) {
                    mySound();
                }

                if (vibroCheck) {
                    vibr.vibrate(pattern, -1);
                }

                //Закончить партию
                finishGame();
                GameClockActivity.this.setTitle(R.string.time_total_finish);
            }

            setFlipper(counters[1]);
            countToElem(counters[1]);
        }
    };

    private Runnable Timer_Tick_Brown = new Runnable() {
        public void run() {
            counters[0]++;
            counters[2]++;
            counterPeriod++;

            if((counters[2] / 60) >= periodUserTime && switch_period_user) {
                if (!ringtone.isPlaying() && !"".equals(timer_sound)) {
                    mySound();
                }

                if (vibroCheck) {
                    vibr.vibrate(pattern, -1);
                }

                //Закончить партию
                finishGame();
                GameClockActivity.this.setTitle(R.string.time_brown_finish);
            }

            if((counters[0] / 60) >= periodTotalTime && switch_period_total) {
                if (!ringtone.isPlaying() && !"".equals(timer_sound)) {
                    mySound();
                }

                if (vibroCheck) {
                    vibr.vibrate(pattern, -1);
                }

                //Закончить партию
                finishGame();
                GameClockActivity.this.setTitle(R.string.time_total_finish);
            }

            setFlipper(counters[2]);
            countToElem(counters[2]);
        }
    };

    private Runnable Timer_Tick_Black = new Runnable() {
        public void run() {
            counters[0]++;
            counters[3]++;
            counterPeriod++;

            if((counters[3] / 60) >= periodUserTime && switch_period_user) {
                if (!ringtone.isPlaying() && !"".equals(timer_sound)) {
                    mySound();
                }

                if (vibroCheck) {
                    vibr.vibrate(pattern, -1);
                }

                //Закончить партию
                finishGame();
                GameClockActivity.this.setTitle(R.string.time_black_finish);
            }

            if((counters[0] / 60) >= periodTotalTime && switch_period_total) {
                if (!ringtone.isPlaying() && !"".equals(timer_sound)) {
                    mySound();
                }

                if (vibroCheck) {
                    vibr.vibrate(pattern, -1);
                }

                //Закончить партию
                finishGame();
                GameClockActivity.this.setTitle(R.string.time_total_finish);
            }

            setFlipper(counters[3]);
            countToElem(counters[3]);
        }
    };

    private void setFlipper(int conter){
        viewFlipper[6].showNext();
        if(conter % 10 == 0) {viewFlipper[5].showNext();}
        if(conter % 60 == 0) {viewFlipper[4].showNext();}
        if(conter % 600 == 0) {viewFlipper[3].showNext();}
        if(conter % 3600 == 0) {viewFlipper[2].showNext();}
        if(conter % 36000 == 0) {viewFlipper[1].showNext();}
        if(conter % 360000 == 0) {viewFlipper[0].showNext();}
    }

    private void setFlipperStart(int conter){
        String arr[] = countToElem(conter);

        viewFlipper[0].setDisplayedChild(Integer.parseInt(arr[1]));
        viewFlipper[1].setDisplayedChild(Integer.parseInt(arr[2]));
        viewFlipper[2].setDisplayedChild(Integer.parseInt(arr[3]));
        viewFlipper[3].setDisplayedChild(Integer.parseInt(arr[5]));
        viewFlipper[4].setDisplayedChild(Integer.parseInt(arr[6]));
        viewFlipper[5].setDisplayedChild(Integer.parseInt(arr[8]));
        viewFlipper[6].setDisplayedChild(Integer.parseInt(arr[9]));
    }

    /*
    return arr[0 - Часы общ., 1 - Часы 1, 2 - Часы 2, 3 - Часы 3, 4 - Мин общ., 5 - Мин 1, 6 - Мин 2, 7 - Сек общ., 8 - Сек 1, 9 - Сек 2, 10 - Строка]
     */
    private String[] countToElem(int conter){
        String out[] = new String[11];
        int hour, min, sec;
        hour = conter / 3600;

        if(hour > 999){
            conter = 0;
            hour = 0;
        }

        if(switch_periods) {
            if (!ringtone.isPlaying() && !"".equals(timer_sound) && (counterPeriod - 60) % 60 == numPeriod && (counterPeriod / 60) >= numPeriod) {
                mySound();
            }

            if (vibroCheck && (conter - 60) % 60 == numPeriod && (counterPeriod / 60) >= numPeriod) {
                vibr.vibrate(pattern, -1);
            }
        }

        out[0] = Integer.toString(hour);
        out[1] = Integer.toString(hour / 100 % 10);
        out[2] = Integer.toString(hour / 10 % 10);
        out[3] = Integer.toString(hour % 10);

        min = conter / 60 % 60;
        out[4] = Integer.toString(min);
        out[5] = Integer.toString(min / 10 % 10);
        out[6] = Integer.toString(min % 10);

        sec = conter % 60;
        out[7] = Integer.toString(sec);
        out[8] = Integer.toString(sec / 10 % 10);
        out[9] = Integer.toString(sec % 10);

        out[10] = out[1] + out[2] + out[3] + ":" + out[5] + out[6] + ":" + out[8] + out[9];

        switch (type_per){
            case 1:
                StrWhite.setText(out[10]);
                break;
            case 2:
                StrBrown.setText(out[10]);
                break;
            case 3:
                StrBlack.setText(out[10]);
                break;
        }

        conter = counters[0];
        hour = conter / 3600;
        String arr[] = new String[11];

        if(hour > 999){
            conter = 0;
            hour = 0;
        }

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

        arr[10] = arr[1] + arr[2] + arr[3] + ":" + arr[5] + arr[6] + ":" + arr[8] + arr[9];
        StrTotal.setText(arr[10]);

        return out;
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if(ringtone.isPlaying()){
            ringtone.stop();
        }

        vibr.cancel();

        if (myTimer != null){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.answ_stop_clock)
                    .setCancelable(false)
                    .setPositiveButton(R.string.str_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            myTimer.cancel();
                            myTimer = null;
                            finish();
                        }
                    }).setNegativeButton(R.string.str_no, null).show();
        }
        else{finish();}
    }

    private void onStartWhite() {
        if (myTimer != null)
            myTimer.cancel();
        myTimer = null;

        if(cancelGame){
            StrWhite.setText(R.string.time_string);
            StrBrown.setText(R.string.time_string);
            StrBlack.setText(R.string.time_string);
            StrTotal.setText(R.string.time_string);
            cancelGame = false;
        }

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        logGame.add("1::"+calendar.getTimeInMillis());

        type_per = 1;
        setFlipperStart(counters[1]);
        counterPeriod = 0;

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, 1000);
    }

    private void onStartBrown() {
        if (myTimer != null)
            myTimer.cancel();
        myTimer = null;

        if(cancelGame){
            StrWhite.setText(R.string.time_string);
            StrBrown.setText(R.string.time_string);
            StrBlack.setText(R.string.time_string);
            StrTotal.setText(R.string.time_string);
            cancelGame = false;
        }

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        logGame.add("2::"+calendar.getTimeInMillis());

        type_per = 2;
        setFlipperStart(counters[2]);
        counterPeriod = 0;

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, 1000);
    }

    private void onStartBlack() {
        if (myTimer != null)
            myTimer.cancel();
        myTimer = null;

        if(cancelGame){
            StrWhite.setText(R.string.time_string);
            StrBrown.setText(R.string.time_string);
            StrBlack.setText(R.string.time_string);
            StrTotal.setText(R.string.time_string);
            cancelGame = false;
        }

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        logGame.add("3::"+calendar.getTimeInMillis());

        type_per = 3;
        setFlipperStart(counters[3]);
        counterPeriod = 0;

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, 1000);
    }

    //Меню таймера (диалог)
    private void onClickListTimer(int which){
        switch (which) {
            case 0:
                //Ход белых
                onStartWhite();
                GameClockActivity.this.setTitle(R.string.move_white);
                setFloatIcon(1);
                break;
            case 1:
                //Ход коричневых
                onStartBrown();
                GameClockActivity.this.setTitle(R.string.move_brown);
                setFloatIcon(2);
                break;
            case 2:
                //Ход чёрных
                onStartBlack();
                GameClockActivity.this.setTitle(R.string.move_black);
                setFloatIcon(3);
                break;
            case 3:
                //Закончить партию
                finishGame();
                GameClockActivity.this.setTitle(R.string.game_finish);
                setFloatIcon(4);
                break;
            default:
                //История
                startActivity(new Intent(GameClockActivity.this, HistoryGamesActivity.class));
                break;
        }
    }

    //Закончить игру
    public void finishGame(){
        if (myTimer != null)
            myTimer.cancel();
        myTimer = null;

        if(counters[0] > 0) {
            final long stampStart = Long.parseLong(logGame.get(0).substring(3));

            Date date = new Date();
            date.setTime(stampStart);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

            final long stampEnd = Long.parseLong(logGame.get(logGame.size()-1).substring(3));
            date.setTime(stampEnd);

            Dialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(GameClockActivity.this);

            final EditText editText = new EditText(GameClockActivity.this);
            editText.setText(getResources().getString(R.string.str_game)+" "+dateFormat.format(date));

            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    HistoryGamesSql HGHelper = new HistoryGamesSql(GameClockActivity.this);
                    SQLiteDatabase HGDatabase = HGHelper.getWritableDatabase();
                    ContentValues newValues = new ContentValues();

                    newValues.put(HGHelper.NAME_COLUMN, editText.getText().toString());
                    newValues.put(HGHelper.WHITE_FIGURES_COLUMN, counters[1]);
                    newValues.put(HGHelper.BROWN_FIGURES_COLUMN, counters[2]);
                    newValues.put(HGHelper.BLACK_FIGURES_COLUMN, counters[3]);
                    newValues.put(HGHelper.TOTAL_FIGURES_COLUMN, counters[0]);
                    newValues.put(HGHelper.TIME_START_COLUMN, stampStart);
                    newValues.put(HGHelper.TIME_END_COLUMN, stampEnd);

                    long rowID = HGDatabase.insert(HGHelper.DATABASE_TABLE_GAMES, null, newValues);

                    int num;
                    long stamp;

                    for (int i = 0; i < logGame.size(); i++) {
                        num = Integer.parseInt(logGame.get(i).substring(0, 1));
                        stamp = Long.parseLong(logGame.get(i).substring(3));

                        newValues = new ContentValues();
                        newValues.put(HGHelper.FIGURE_COLUMN, num);
                        newValues.put(HGHelper.STAMP_COLUMN, stamp);
                        newValues.put(HGHelper.ID_GAME, rowID);

                        HGDatabase.insert(HGHelper.DATABASE_TABLE_MOVES, null, newValues);
                    }

                    cancelGame = true;
                    type_per = 3;

                    for (int i = 0, max = viewFlipper.length; i < max; i++) {
                        viewFlipper[i].setDisplayedChild(0);
                    }

                    for (int i = 0, max = counters.length; i < max; i++) {
                        counters[i] = 0;
                    }

                    logGame.clear();

                    startActivity(new Intent(GameClockActivity.this, HistoryGamesActivity.class));
                }
            };

            dialog = builder
                    .setMessage(R.string.mess_name_game)
                    .setPositiveButton(R.string.but_finish, onClickListener)
                    .setView(editText).create();

            dialog.show();
        }
    }

    private void moveDialog(){
        if(ringtone.isPlaying()){
            ringtone.stop();
        }

        vibr.cancel();

        String timerList[] = getResources().getStringArray(R.array.timer_list);

        new AlertDialog.Builder(this)
                .setTitle(R.string.setting_timer)
                .setItems(timerList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onClickListTimer(which);
                    }
                }).show();
    }

    private void mySound(){
        ringtone.play();
        new CountDownTimer(time_signal * 1000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(ringtone.isPlaying()){
                    ringtone.stop();
                }
            }
        }.start();
    }

    private void setSizeTimer(){
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        int widthImg = 559;
        int heightImg = 171;
        int widthDisplay = metricsB.widthPixels;
        double resWidth = widthDisplay - widthDisplay * 10 / 100;
        long resHeight = Math.round((resWidth / widthImg) * heightImg);

        linearLayoutTimer.getLayoutParams().height = (int)resHeight;
        linearLayoutTimer.getLayoutParams().width = (int)resWidth;
        linearLayoutTimer.requestLayout();

        widthImg = 47;
        long resWidthSlide = Math.round(widthImg * (resHeight / (double)heightImg));

        ViewFlipper viewFlipper[] = new ViewFlipper[7];
        viewFlipper[0] = (ViewFlipper) findViewById(R.id.timer_block_hour1);
        viewFlipper[1] = (ViewFlipper) findViewById(R.id.timer_block_hour2);
        viewFlipper[2] = (ViewFlipper) findViewById(R.id.timer_block_hour3);
        viewFlipper[3] = (ViewFlipper) findViewById(R.id.timer_block_min1);
        viewFlipper[4] = (ViewFlipper) findViewById(R.id.timer_block_min2);
        viewFlipper[5] = (ViewFlipper) findViewById(R.id.timer_block_sec1);
        viewFlipper[6] = (ViewFlipper) findViewById(R.id.timer_block_sec2);

        for(int i = 0; i < viewFlipper.length; i++) {
            viewFlipper[i].getLayoutParams().height = (int)resHeight;
            viewFlipper[i].getLayoutParams().width = (int)resWidthSlide;
            viewFlipper[i].requestLayout();
        }


        ImageView imageView[] = new ImageView[64];
        imageView[0] = (ImageView) findViewById(R.id.tmhr1img_0);
        imageView[1] = (ImageView) findViewById(R.id.tmhr1img_1);
        imageView[2] = (ImageView) findViewById(R.id.tmhr1img_2);
        imageView[3] = (ImageView) findViewById(R.id.tmhr1img_3);
        imageView[4] = (ImageView) findViewById(R.id.tmhr1img_4);
        imageView[5] = (ImageView) findViewById(R.id.tmhr1img_5);
        imageView[6] = (ImageView) findViewById(R.id.tmhr1img_6);
        imageView[7] = (ImageView) findViewById(R.id.tmhr1img_7);
        imageView[8] = (ImageView) findViewById(R.id.tmhr1img_8);
        imageView[9] = (ImageView) findViewById(R.id.tmhr1img_9);

        imageView[10] = (ImageView) findViewById(R.id.tmhr2img_0);
        imageView[11] = (ImageView) findViewById(R.id.tmhr2img_1);
        imageView[12] = (ImageView) findViewById(R.id.tmhr2img_2);
        imageView[13] = (ImageView) findViewById(R.id.tmhr2img_3);
        imageView[14] = (ImageView) findViewById(R.id.tmhr2img_4);
        imageView[15] = (ImageView) findViewById(R.id.tmhr2img_5);
        imageView[16] = (ImageView) findViewById(R.id.tmhr2img_6);
        imageView[17] = (ImageView) findViewById(R.id.tmhr2img_7);
        imageView[18] = (ImageView) findViewById(R.id.tmhr2img_8);
        imageView[19] = (ImageView) findViewById(R.id.tmhr2img_9);

        imageView[20] = (ImageView) findViewById(R.id.tmhr3img_0);
        imageView[21] = (ImageView) findViewById(R.id.tmhr3img_1);
        imageView[22] = (ImageView) findViewById(R.id.tmhr3img_2);
        imageView[23] = (ImageView) findViewById(R.id.tmhr3img_3);
        imageView[24] = (ImageView) findViewById(R.id.tmhr3img_4);
        imageView[25] = (ImageView) findViewById(R.id.tmhr3img_5);
        imageView[26] = (ImageView) findViewById(R.id.tmhr3img_6);
        imageView[27] = (ImageView) findViewById(R.id.tmhr3img_7);
        imageView[28] = (ImageView) findViewById(R.id.tmhr3img_8);
        imageView[29] = (ImageView) findViewById(R.id.tmhr3img_9);

        imageView[30] = (ImageView) findViewById(R.id.timer_img_hour);

        imageView[31] = (ImageView) findViewById(R.id.tmmn1img_0);
        imageView[32] = (ImageView) findViewById(R.id.tmmn1img_1);
        imageView[33] = (ImageView) findViewById(R.id.tmmn1img_2);
        imageView[34] = (ImageView) findViewById(R.id.tmmn1img_3);
        imageView[35] = (ImageView) findViewById(R.id.tmmn1img_4);
        imageView[36] = (ImageView) findViewById(R.id.tmmn1img_5);

        imageView[37] = (ImageView) findViewById(R.id.tmmn2img_0);
        imageView[38] = (ImageView) findViewById(R.id.tmmn2img_1);
        imageView[39] = (ImageView) findViewById(R.id.tmmn2img_2);
        imageView[40] = (ImageView) findViewById(R.id.tmmn2img_3);
        imageView[41] = (ImageView) findViewById(R.id.tmmn2img_4);
        imageView[42] = (ImageView) findViewById(R.id.tmmn2img_5);
        imageView[43] = (ImageView) findViewById(R.id.tmmn2img_6);
        imageView[44] = (ImageView) findViewById(R.id.tmmn2img_7);
        imageView[45] = (ImageView) findViewById(R.id.tmmn2img_8);
        imageView[46] = (ImageView) findViewById(R.id.tmmn2img_9);

        imageView[47] = (ImageView) findViewById(R.id.timer_img_min);

        imageView[48] = (ImageView) findViewById(R.id.tmsc1img_0);
        imageView[49] = (ImageView) findViewById(R.id.tmsc1img_1);
        imageView[50] = (ImageView) findViewById(R.id.tmsc1img_2);
        imageView[51] = (ImageView) findViewById(R.id.tmsc1img_4);
        imageView[52] = (ImageView) findViewById(R.id.tmsc1img_5);

        imageView[53] = (ImageView) findViewById(R.id.tmsc2img_0);
        imageView[54] = (ImageView) findViewById(R.id.tmsc2img_1);
        imageView[55] = (ImageView) findViewById(R.id.tmsc2img_2);
        imageView[56] = (ImageView) findViewById(R.id.tmsc2img_3);
        imageView[57] = (ImageView) findViewById(R.id.tmsc2img_4);
        imageView[58] = (ImageView) findViewById(R.id.tmsc2img_5);
        imageView[59] = (ImageView) findViewById(R.id.tmsc2img_6);
        imageView[60] = (ImageView) findViewById(R.id.tmsc2img_7);
        imageView[61] = (ImageView) findViewById(R.id.tmsc2img_8);
        imageView[62] = (ImageView) findViewById(R.id.tmsc2img_9);

        imageView[63] = (ImageView) findViewById(R.id.timer_img_sec);

        for(int i = 0; i < imageView.length; i++) {
            imageView[i].getLayoutParams().height = (int)resHeight;
            imageView[i].getLayoutParams().width = (int)resWidthSlide;
            imageView[i].requestLayout();
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
            Intent intent = new Intent(GameClockActivity.this, SettingsActivity.class);
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
            Intent intent = new Intent(GameClockActivity.this, AboutClubActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            Intent intent = new Intent(GameClockActivity.this, EventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rules) {
            Intent intent = new Intent(GameClockActivity.this, RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_clock) {
            Intent intent = new Intent(GameClockActivity.this, GameClockActivity.class);
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
            startActivity(new Intent(GameClockActivity.this, ShowMapActivity.class));
        } else if (id == R.id.nav_author) {
            Intent intent = new Intent(GameClockActivity.this, AuthorActivity.class);
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
