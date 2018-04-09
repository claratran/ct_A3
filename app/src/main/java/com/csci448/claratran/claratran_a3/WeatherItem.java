package com.csci448.claratran.claratran_a3;

/*
 * Created by Clara on 4/8/2018.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherItem {
    private String mId;
    private String mUrl;
    private String mDate;
    private double mLat;
    private double mLon;
    private double mTemperature;
    private String mDescription;

    private String[] monthString = new String[]{
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December",
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = new SimpleDateFormat("MM dd, yyyy HH:mm:ss").format(date);
        String month = monthString[Integer.parseInt(mDate.substring(0, 2)) - 1];
        mDate = month + mDate.substring(2);
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
