package no.wsact.mikand.pg4600.assignment2.MapApp;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import com.google.android.gms.maps.model.LatLng;
import no.wsact.mikand.pg4600.assignment2.MapApp.maps.RuterMap;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.connectivity.ConnectionHandler;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.connectivity.OnOnlineListener;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.location.LocationHandler;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter.RuterAPIHandler;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter.RuterStop;

import java.util.HashMap;

public class Map extends Activity implements OnOnlineListener {
    private ConnectionHandler connectionHandler;
    private LocationHandler locationHandler;
    private RuterAPIHandler ruterHandler;
    private Thread ruterThread;

    private RuterMap ruterMapFragment;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // lock to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        connectionHandler = new ConnectionHandler(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionHandler, intentFilter);
        ConnectionHandler.registerOnOnlineListener(this);

        locationHandler = new LocationHandler(this);
        ruterHandler = new RuterAPIHandler(this);
        ruterThread = new Thread(ruterHandler);
        ruterThread.start();

        ruterMapFragment = (RuterMap) getFragmentManager().findFragmentById(R.id.map);
        ruterMapFragment.setParent(this);
        ruterMapFragment.setLocationHandler(locationHandler);
        ruterMapFragment.initializeMap();
    }

    /**
     * Kills locationupdates on pause to preserve battery.
     */
    @Override
    protected void onPause() {
        super.onPause();

        locationHandler.stopLocationUpdates();
    }

    /**
     * Starts locationupdates on resume.
     */
    @Override
    protected void onResume() {
        super.onResume();

        locationHandler.startLocationUpdates();
    }

    /**
     * Shuts down the ruter thread that runs the API and closes locationservices.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        ConnectionHandler.unRegisterOnOnlineListener(this);
        unregisterReceiver(connectionHandler);
        ruterThread.interrupt();
        locationHandler.close();
    }

    /**
     * Gets the stops from the RuterAPIHandler when its done. Then calls to populate in the
     * MapFragment.
     *
     * @param allStopsByName java.util.HashMap
     */
    public void deliverStopsFromRuter(HashMap<String, RuterStop> allStopsByName) {
        if (!isFinishing()) {
            ruterMapFragment.populateMap(allStopsByName);
        }
    }

    /**
     * Sets user initial pos.
     *
     * @param initialPos com.google.android.gms.maps.model.LatLng
     */
    public void setInitialPos(LatLng initialPos) {
        ruterMapFragment.setInitialLocation(initialPos);
    }

    /**
     * Facade for checking online in the ConnectionHandler.
     *
     * @return java.lang.Boolean
     */
    public boolean isOnline() {
        return connectionHandler.isOnline();
    }

    /**
     * Called when all markers have been added by the MapFragment. Kills the API thread and empties
     * its list.
     */
    public void allMarkersAdded() {
        ruterHandler.close();
        ruterThread.interrupt();
    }

    @Override
    public void phoneOnline() {
        ruterThread.interrupt();
    }
}
