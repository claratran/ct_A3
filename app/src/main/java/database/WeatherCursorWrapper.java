package database;

/*
 * Created by Clara on 4/8/2018.
 */

import android.database.Cursor;
import android.database.CursorWrapper;

import com.csci448.claratran.claratran_a3.WeatherItem;

import java.util.Date;

import static database.WeatherDbSchema.WeatherTable;

public class WeatherCursorWrapper extends CursorWrapper {
    public WeatherCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public WeatherItem getWeatherItem() {
        double latitude = getDouble(getColumnIndex(WeatherTable.Cols.LATITUDE));
        double longitude = getDouble(getColumnIndex(WeatherTable.Cols.LONGITUDE));
        long date = getLong(getColumnIndex(WeatherTable.Cols.DATE));
        double temperature = getDouble(getColumnIndex(WeatherTable.Cols.TEMPERATURE));
        String description = getString(getColumnIndex(WeatherTable.Cols.DESCRIPTION));

        WeatherItem item = new WeatherItem();
        item.setLat(latitude);
        item.setLon(longitude);
        item.setDate(new Date(date));
        item.setTemperature(temperature);
        item.setDescription(description);

        return item;
    }
}
