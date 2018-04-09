package com.csci448.claratran.claratran_a3;

/*
 * Created by Clara on 4/8/2018.
 */

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {
    private static final String TAG = "WeatherFetcher";
    private static final String API_KEY = "92771a1b737de6e342ca77e01e89a6de";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public WeatherItem downloadWeatherItem(String url) {
        WeatherItem item = new WeatherItem();

        try {
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            item = parseItems(item, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return item;
    }

    private WeatherItem parseItems(WeatherItem item, JSONObject jsonBody) throws IOException, JSONException {
        JSONArray weatherJsonArray = jsonBody.getJSONArray("weather");

        JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);
        item.setId(weatherJsonObject.getString("id"));
        item.setDescription(weatherJsonObject.getString("description"));

        weatherJsonObject = jsonBody.getJSONObject("main");
        double kelvinTemp = weatherJsonObject.getDouble("temp");
        Log.d(TAG, "Kelvin: " + kelvinTemp);

        item.setTemperature(Math.round((kelvinTemp * (1.80) - 459.67) * 100) / 100.00);

        return item;
    }

    public WeatherItem setWeatherLocation(Location location) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="
                + location.getLatitude()
                + "&lon="
                + location.getLongitude()
                + "&appid="
                + API_KEY;
        Log.d(TAG, url);
        return downloadWeatherItem(url);
    }
}
