package com.csci448.claratran.claratran_a3;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.Date;

/*
 * Created by Clara on 4/8/2018.
 */

public class MapsFragment extends SupportMapFragment {

    private static final String TAG = "MapsFragment";
    private static final String[] LOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;

    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private WeatherItem mMapItem;
    private Location mCurrentLocation;
    private FloatingActionButton mFab;


    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String dateTime = mMapItem.getDate();
                        String temperature = Double.toString(mMapItem.getTemperature());
                        String description = mMapItem.getDescription();
                        String snackbarMsg = getString(R.string.snackbar_msg, dateTime, temperature, description);

                        final Snackbar munchie = Snackbar.make(getView(), snackbarMsg, Snackbar.LENGTH_LONG);
                        /*munchie.setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                munchie.dismiss();
                            }
                        });*/

                        munchie.show();
                        marker.showInfoWindow();
                        return true;
                    }
                });
                mFab = (FloatingActionButton) getActivity().findViewById(R.id.map_FAB);
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hasLocationPermission()) {
                            findLocation();
                        } else {
                            requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_items, menu);

        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());

        MenuItem clearMap = menu.findItem(R.id.clear_map);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                if (hasLocationPermission()) {
                    findLocation();
                } else {
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                }
                return true;
            case R.id.clear_map:
                // add in method that clears database and updates ui

                mMap.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    findLocation();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasLocationPermission() {
        int result = ContextCompat
                .checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);

        return (result == PackageManager.PERMISSION_GRANTED);
    }

    private void findLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        try {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mClient, request, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.i(TAG, "Got a fix: " + location);
                            new SearchTask().execute(location);
                        }
                    });
        } catch (SecurityException se) {
            Log.i(TAG, "Failed request for location update", se);
        }
    }

    private class SearchTask extends AsyncTask<Location, Void, Void> {
        private WeatherItem mWeatherItem;
        private Location mLocation;

        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];
            Log.d(TAG, "Latitude in SearchTask: " + mLocation.getLatitude());
            Log.d(TAG, "Longitude in SearchTask: " + mLocation.getLongitude());

            WeatherFetcher fetcher = new WeatherFetcher();

            mWeatherItem = fetcher.setWeatherLocation(params[0]);
            mWeatherItem.setDate(new Date());

            try {
                byte[] bytes = fetcher.getUrlBytes(mWeatherItem.getUrl());
                Log.d(TAG, "Url: " + mWeatherItem.getUrl());
            } catch (IOException io) {
                Log.i(TAG, "Unable to download bitmap", io);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            mMapItem = mWeatherItem;
            mCurrentLocation = mLocation;

            updateUI();
        }
    }

    private void updateUI() {
        if (mMap == null) {
            Log.d(TAG, "Map or map image null");
            return;
        }

        LatLng itemPoint = null;
        if (mCurrentLocation != null) {
            itemPoint = new LatLng(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude());
        } else {
            // alert user that current location could not be found
        }

        //BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromBitmap(mMapImage);
        MarkerOptions itemMarker = new MarkerOptions()
                .position(itemPoint)
                .title("lat/lng: (" + itemPoint.latitude + ", " + itemPoint.longitude + ")");

        mMap.addMarker(itemMarker);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(itemPoint)
                .build();

        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);
    }
}
