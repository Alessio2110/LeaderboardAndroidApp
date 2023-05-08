package uk.ac.stir.cs.practical5;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class SQLiteManager extends SQLiteOpenHelper {
    private static SQLiteManager sqLiteManager;

    /** Database and table name*/
    private ArrayList<Game> array = new ArrayList<Game>();
    private static final String DATABASE_NAME = "GameDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Games";
    private static final String COUNTER = "COUNTER";

    /** Fields in the database */
    private static final String HT_FIELD = "home";
    private static final String AT_FIELD = "away";
    private static final String HT_SCORE_FIELD = "score_h";
    private static final String AT_SCORE_FIELD = "score_a";
    private static final String DATE_FIELD = "date";
    private static final String LANG_FIELD = "language";

    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanteOfDB(Context context) {
        if (sqLiteManager == null) sqLiteManager = new SQLiteManager(context);

        return sqLiteManager;
    }

    /** Create Database with fields and table name*/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ").append(TABLE_NAME).append("(")
                .append(COUNTER).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(HT_FIELD).append(" TEXT, ")
                .append(AT_FIELD).append(" TEXT, ")
                .append(HT_SCORE_FIELD).append(" INT, ")
                .append(AT_SCORE_FIELD).append(" INT, ")
                .append(DATE_FIELD).append(" TEXT, ")
                .append(LANG_FIELD).append(" TEXT)");

        sqLiteDatabase.execSQL(sql.toString());
    }

    /**
     * Add game to the database
     * @param game, the game
     * */
    public void addGameToDB(Game game) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HT_FIELD, game.getHome_team());
        contentValues.put(AT_FIELD, game.getAway_team());
        contentValues.put(HT_SCORE_FIELD, game.getHome_score());
        contentValues.put(AT_SCORE_FIELD, game.getAway_score());
        contentValues.put(DATE_FIELD, getStringFromDate(game.getDate()));
        contentValues.put(LANG_FIELD, game.getLeague());

        //It returns -1 if it did not manage to add game to database
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            Log.e(TAG, "Error inserting game in database");
        else
            Log.i(TAG, "Game inserted successfully");
    }

    /**
     * @return ArrayList<Game>: arraylist of all games played
     */
    public ArrayList<Game> populateGameArrList() {
        /** Instantiate database*/
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        array.clear();
        /** Get all games with simple SQL query*/
        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if (result.getCount() != 0) { //If there are games
                while (result.moveToNext()) {
                    Game game = getGame(result);
                    array.add(game);
                }
            }
        }
        return array;
    }

    /**
     * deletes all rows in the database and resets the autoincrement id
     */
    public void deleteAll() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME);
        sqLiteDatabase.close();
    }

    /**
     * Find game in database
     * @param ht, the home team
     * @param at, the away team
     * @param d, the date of the game
     * @return true if the game already exists, else false
     */
    public boolean findOne(String ht, String at, LocalDate d){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        /** Query to get a list of games with same name*/
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME +
                        " where " + HT_FIELD + "= ?" +
                        " AND " + AT_FIELD + "=?" +
                        " AND " + DATE_FIELD + "=?"
                , new String[]{ht, at, d.toString()});
        /** If there is a game*/
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return true;
        }
        /** Now swap home team with away team, the order dooesn't matter.
         * Two teams should not play against each other on the same day */
        Cursor cursor2 = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME +
                        " where " + HT_FIELD + "= ?" +
                        " AND " + AT_FIELD + "=?" +
                        " AND " + DATE_FIELD + "=?"
                , new String[]{at, ht, d.toString()});
        /** If there is a game*/
        if (cursor2.getCount() > 0) {
            cursor2.moveToNext();
            return true;
        }

        return false;
    }
    /**
     * Delete one entry from the database
     * @param ht, the home team
     * @param at, the away team
     * @param d, the date of the game
     * @return whether the game was deleted
     * */
    public boolean deleteOne(String ht, String at, LocalDate d){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor cursor = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME +
                " where " + HT_FIELD + "= ?" +
                " AND " + AT_FIELD + "=?" +
                " AND " + DATE_FIELD + "=?"
                , new String[]{ht, at, d.toString()});

        if (cursor.getCount() > 0) {
            String[] parameters = {ht, at, d.toString()};
            long result = sqLiteDatabase.delete(TABLE_NAME,
                    HT_FIELD + "=?" + " AND " + AT_FIELD + "=?" + " AND " + DATE_FIELD + "=?",
                        parameters);
            if (result == 1) return true;
        }
        return false; //else
    }

    private String getStringFromDate(LocalDate date) {
        return date.toString();
    }

    @SuppressLint("NewApi")
    private LocalDate getDateFromString(String s) {
        return LocalDate.parse(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    /**
     * get arraylist of  games filtered by team
     * @param team, the team name
     * @return the array list filtered by team
     * */
    public ArrayList<Game> filteredArr(String team){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        array.clear();
        Cursor result = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME +
                        " where " + HT_FIELD + "= ?" +
                        " OR " + AT_FIELD + "=?"
                , new String[]{team, team});

        if (result.getCount() != 0) {
            while (result.moveToNext()) {
                Game g = getGame(result);
                array.add(g);
            }
        }
        return array;
    }

    /**
     * get arraylist of  games filtered by league
     * @param league, the league name
     * @return the array list filtered by league
     * */
    public ArrayList<Game> league_filter_arr(String league){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        array.clear();
        Cursor result = sqLiteDatabase.rawQuery("Select * from " + TABLE_NAME +
                        " where " + LANG_FIELD + "= ?"
                , new String[]{league});

        if (result.getCount() != 0) {
            while (result.moveToNext()) {
                Game g = getGame(result);
                array.add(g);
            }
        }
        return array;
    }

    /**
     * Instantiate game object from a cursor
     * @param r, the cursor
     * @return the game
     * */
    public Game getGame(Cursor r){
        return new Game(
                r.getString(1), r.getString(2),
                r.getInt(3), r.getInt(4),
                getDateFromString(r.getString(5)), r.getString(6));
    }

    /**
     *  This method is needed if you deleted your entire table by mistake
     * I learnt this the hard way, do not delete your table, just the entries!
     */

//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
//
//        StringBuilder sql;
//        sql = new StringBuilder()
//                .append("CREATE TABLE ")
//                .append(TABLE_NAME)
//                .append("(")
//                .append(COUNTER)
//                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
////                .append(ID_FIELD)
////                .append(" INT, ")
//                .append(HT_FIELD)
//                .append(" TEXT, ")
//                .append(AT_FIELD)
//                .append(" TEXT, ")
//                .append(HT_SCORE_FIELD)
//                .append(" INT, ")
//                .append(AT_SCORE_FIELD)
//                .append(" INT, ")
//                .append(DATE_FIELD)
//                .append(" TEXT, ")
//                .append(LANG_FIELD)
//                .append(" TEXT)");
////                .append(DATE_FIELD)
////                .append(" TEXT)");
//
//        db.execSQL(sql.toString());
//
//    }
}
