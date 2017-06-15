package trainedge.bu_pro;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public final String service;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private Location mCurrentLocation;
    //SOUNDPROFILE MANAGER SP
    SoundProfileManager spm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public LocationService() {
        service = "MyLocationService";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildGoogleApiClient();
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        getUsersLocation();
        return START_REDELIVER_INTENT;
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                          .addConnectionCallbacks(this)
                           .addOnConnectionFailedListener(this)
                           .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
    }
    @Override
    public void onLocationChanged(Location location) {
    handle_geofire(location);
    }

    private void handle_geofire(Location location) {
        mCurrentLocation = location;
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("soundprofiles").child(uid).child("geofire");

        GeoFire geofire = new GeoFire(ref);
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()),0.5);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                spm.changeSoundProfile(GeofenceService.this,key,location);
            }

            @Override
            public void onKeyExited(String key) {
                spm.setToDefault(key);
                Toast.makeText(LocationService.this, "+", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void displayLocation(Location location) {
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }


    private void stopLocationUpdates()
    {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
    private void getUsersLocation() {
       handleLocationSetting();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
     LocationSettingsRequest.Builder builder = new LocationSettingsRequest().addLocationRequest(mLocationRequest);
        if(!Geocoder.isPresent()){
            Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }
        createLocationRequest();
        try {
            startLocationUpdates();
        }
        catch (Exception e){

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Network Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "Network Failed "+ connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}

