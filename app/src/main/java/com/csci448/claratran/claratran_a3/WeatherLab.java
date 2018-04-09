package com.csci448.claratran.claratran_a3;

/*
 * Created by Clara on 4/8/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import database.WeatherBaseHelper;
import database.WeatherCursorWrapper;
import database.WeatherDbSchema;

import static database.WeatherDbSchema.WeatherTable;

public class WeatherLab {
    private static WeatherLab sWeatherLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public WeatherLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new WeatherBaseHelper(mContext).getWritableDatabase();
    }

    public void addWeatherItem(WeatherItem item) {
        ContentValues values = getContentValues(item);

        mDatabase.insert(WeatherTable.NAME, null, values);
    }

    public static WeatherLab get (Context context) {
        if (sWeatherLab == null) {
            sWeatherLab = new WeatherLab(context);
        }

        return sWeatherLab;
    }

    public List<WeatherItem> getWeatherItems() {
        List<WeatherItem> items = new ArrayList<>();

        WeatherCursorWrapper cursor = queryItems(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getWeatherItem());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public WeatherItem getItem (double lat, double lon) {
        WeatherCursorWrapper cursor = queryItems(
                WeatherTable.Cols.LATITUDE + " = ?"
                + "AND " + WeatherTable.Cols.LONGITUDE + " = ?",
                new String[] {Double.toString(lat), Double.toString(lon)}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getWeatherItem();
        } finally {
            cursor.close();
        }
    }

    private WeatherCursorWrapper queryItems(String whereCluase, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                WeatherTable.NAME,
                null,
                whereCluase,
                whereArgs,
                null,
                null,
                null
        );

        return new WeatherCursorWrapper(cursor);
    }

    private ContentValues getContentValues(WeatherItem item) {
        ContentValues values = new ContentValues();
        values.put(WeatherTable.Cols.LATITUDE, item.getLat());
        values.put(WeatherTable.Cols.LONGITUDE, item.getLon());
        values.put(WeatherTable.Cols.DATE, item.getDate());
        values.put(WeatherTable.Cols.TEMPERATURE, item.getTemperature());
        values.put(WeatherTable.Cols.DESCRIPTION, item.getDescription());

        return values;
    }

    public void updateItem(WeatherItem item) {
        ContentValues values = getContentValues(item);

        mDatabase.update(WeatherTable.NAME, values,
                WeatherTable.Cols.LATITUDE + " = ?"
                        + "AND " + WeatherTable.Cols.LONGITUDE + " = ?",
                new String[] {Double.toString(item.getLat()), Double.toString(item.getLon())});
    }

    //TO DO: connect MapsActivity with database to actually save items
}
