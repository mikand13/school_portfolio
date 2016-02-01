package no.wsact.mikand.pg4600.assignment2.MapApp.utils.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import no.wsact.mikand.pg4600.assignment2.MapApp.Map;
import no.wsact.mikand.pg4600.assignment2.MapApp.R;

import static com.google.android.gms.common.api.GoogleApiClient.*;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp
 *
 * This class controls and administers all location updates.
 *
 * @author Anders Mikkelsen
 * @version 11.05.15
 */
@SuppressWarnings("FieldCanBeLocal")
public class LocationHandler implements ConnectionCallbacks, OnConnectionFailedListener,
                                        LocationSource, LocationListener,
                                        com.google.android.gms.location.LocationListener {
    private LatLng currentPos;
    private OnLocationChangedListener onLocationChangedListener;

    private final LocationRequest locationRequest;
    private final GoogleApiClient googleApiClient;
    private final Map parent;

    private boolean fetchingLocations;

    private final int ACCURACY = LocationRequest.PRIORITY_LOW_POWER;
    private final int UPDATE_INTERVAL = 5000;
    private final int FASTEST_UPDATE_INTERVAL = 1000;

    /**
     * Connects to Google Api and sets up a recurring locationrequest.
     *
     * @param parent no.wsact.mikand.pg4600.assignment2.MapApp.Map
     */
    public LocationHandler(Map parent) {
        this.parent = parent;
        fetchingLocations = false;
        currentPos = new LatLng(0.0, 0.0);

        googleApiClient = new GoogleApiClient.Builder(parent)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setPriority(ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

        googleApiClient.connect();
    }

    /**
     * Starts locationupdates.
     *
     * @param bundle android.os.Bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    /**
     * On connection suspended, stop location updates.
     *
     * @param i java.lang.Integer
     */
    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    /**
     * Updates current pos and map pos.
     *
     * @param location android.location.Location
     */
    @Override
    public void onLocationChanged(Location location) {
        if (currentPos.latitude == 0.0 && currentPos.longitude == 0.0) {
            parent.setInitialPos(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        currentPos = new LatLng(location.getLatitude(), location.getLongitude());
        onLocationChangedListener.onLocationChanged(location);

        Log.d(parent.getString(R.string.currentPosition),
                currentPos.latitude + " " + currentPos.longitude);
    }

    /**
     * Starts and stops location service based on availiblity.
     *
     * @param provider java.lang.String
     * @param status java.lang.Integer
     * @param extras android.os.Bundle
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (!fetchingLocations && status == LocationProvider.AVAILABLE) {
            startLocationUpdates();
        } else if (fetchingLocations) {
            stopLocationUpdates();
        }
    }

    /**
     * Start when provider enabled.
     *
     * @param provider java.lang.String
     */
    @Override
    public void onProviderEnabled(String provider) {
        startLocationUpdates();
    }

    /**
     * Stop on provider disabled.
     *
     * @param provider java.lang.String
     */
    @Override
    public void onProviderDisabled(String provider) {
        stopLocationUpdates();
    }

    /**
     * Inform user when connection failed.
     *
     * @param connectionResult com.google.android.gms.common.ConnectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            Toast.makeText(parent,
                    parent.getString(R.string.cannotConnectToLocationServices),
                    Toast.LENGTH_LONG)
                    .show();
            }
        });
    }

    /**
     * Active locationsource.
     *
     * @param onLocationChangedListener OnLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    /**
     * Disable location source.
     */
    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
    }

    /**
     * Starts location updates.
     */
    public void startLocationUpdates() {
        // set current if nothing, will also reset if offline for a long time
        if (googleApiClient.isConnected() &&
                getCurrentPos().latitude == 0 && getCurrentPos().longitude == 0) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);

            fetchingLocations = true;
        }
    }

    /**
     * Stops all location updates.
     */
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

        fetchingLocations = false;
    }

    /**
     * Gets calculated currentpos.
     *
     * @return com.google.android.gms.maps.model.LatLng
     */
    private LatLng getCurrentPos() {
        return currentPos;
    }

    /**
     * Shuts down location updates.
     */
    public void close() {
        stopLocationUpdates();
    }
}
