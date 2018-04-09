package database;

/*
 * Created by Clara on 4/8/2018.
 */

public class WeatherDbSchema {
    public static final class WeatherTable {
        public static final String NAME = "location_weather";

        public static final class Cols {
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String DATE = "date";
            public static final String TEMPERATURE = "temperature";
            public static final String DESCRIPTION = "description";
        }
    }
}
