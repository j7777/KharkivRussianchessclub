package ua.kh.ruschess.kharkivrussianchessclub.Sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class HistoryGamesSql extends SQLiteOpenHelper implements BaseColumns {
    public static final String DATABASE_HISTORY_GAMES = "history_games";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE_GAMES = "games";
    public static final String DATABASE_TABLE_MOVES = "moves";

    public static final String NAME_COLUMN = "name";
    public static final String WHITE_FIGURES_COLUMN = "white_figures";
    public static final String BROWN_FIGURES_COLUMN = "brown_figures";
    public static final String BLACK_FIGURES_COLUMN = "black_figures";
    public static final String TOTAL_FIGURES_COLUMN = "total_figures";
    public static final String TIME_START_COLUMN = "time_start";
    public static final String TIME_END_COLUMN = "time_end";

    public static final String FIGURE_COLUMN = "figure";
    public static final String STAMP_COLUMN = "stamp";
    public static final String ID_GAME = "id_game";

    public static final String COLUMN_ID = "id";

    public HistoryGamesSql(Context context) {
        super(context, DATABASE_HISTORY_GAMES, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+DATABASE_TABLE_GAMES+" (" +
                COLUMN_ID+" INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
                NAME_COLUMN+" VARCHAR(255)  NULL,"+
                WHITE_FIGURES_COLUMN+" INTEGER  NULL," +
                BROWN_FIGURES_COLUMN+" INTEGER  NULL," +
                BLACK_FIGURES_COLUMN+" INTEGER  NULL," +
                TOTAL_FIGURES_COLUMN+" INTEGER  NULL," +
                TIME_START_COLUMN+" LONG  NULL," +
                TIME_END_COLUMN+" LONG  NULL" +
                ");");

        db.execSQL("CREATE TABLE "+DATABASE_TABLE_MOVES+" (" +
                COLUMN_ID+" INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
                FIGURE_COLUMN+" TINUINT  NULL," +
                STAMP_COLUMN+" LONG  NULL," +
                ID_GAME+" INTEGER  NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS "+DATABASE_TABLE_GAMES);
        onCreate(db);
        db.execSQL("DROP TABLE IF IT EXISTS "+DATABASE_TABLE_MOVES);
        onCreate(db);
    }
}
