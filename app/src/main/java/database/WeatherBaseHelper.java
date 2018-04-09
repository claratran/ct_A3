package database;

/*
 * Created by Clara on 4/8/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.WeatherDbSchema.WeatherTable;

public class WeatherBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weatherBase.db";

    public WeatherBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + WeatherTable.NAME + "("
                + " _id integer primary key autoincrement, "
                + WeatherTable.Cols.LATITUDE + ", "
                + WeatherTable.Cols.LONGITUDE + ", "
                + WeatherTable.Cols.DATE + ", "
                + WeatherTable.Cols.TEMPERATURE + ", "
                + WeatherTable.Cols.DESCRIPTION +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
