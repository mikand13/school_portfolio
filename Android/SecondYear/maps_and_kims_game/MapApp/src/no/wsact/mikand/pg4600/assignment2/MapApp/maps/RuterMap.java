package no.wsact.mikand.pg4600.assignment2.MapApp.maps;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import no.wsact.mikand.pg4600.assignment2.MapApp.Map;
import no.wsact.mikand.pg4600.assignment2.MapApp.R;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.coordinates.Converter;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.location.LocationHandler;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter.RuterStop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp
 *
 * This class defines the specific MapFragment for ruter. It handles input of ruterstops.
 *
 * @author Anders Mikkelsen
 * @version 11.05.15
 */
@SuppressWarnings("FieldCanBeLocal")
public class RuterMap extends GenericMap {
    private String CLASS_NAME;
    private Map parent;

    private final double OSLO_LATITUDE = 59.913869;
    private final double OSLO_LONGITUDE = 10.752245;

    // initial zoom corresponds to oslo centrum
    private final int INITIAL_ZOOM = 14;

    // default zoom corresponds to initial zoom in on user location capture
    private final int DEFAULT_ZOOM = 16;

    // hardcoded because ruter only uses UTM32
    private String RUTER_MAP_UTM_MAP_AREA;

    // This should be a minimum of 2, increasing it will give better ui response during
    // markerloading but will increare time of markerloading giving a worse user experience
    private final int WAIT_BETWEEN_EACH_MARKER_ADDITION_IN_MILLIS = 2;

    private HashMap<String, RuterStop> allStopsByName;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(OSLO_LATITUDE, OSLO_LONGITUDE), INITIAL_ZOOM));

        UiSettings mapSettings = googleMap.getUiSettings();
        mapSettings.setCompassEnabled(true);
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setMapToolbarEnabled(true);
    }

    /**
     * This method starts the async task for adding all markers.
     *
     * @param allStopsByName java.util.HashMap;
     */
    public void populateMap(HashMap<String, RuterStop> allStopsByName) {
        this.allStopsByName = allStopsByName;

        Log.i(CLASS_NAME, getActivity().getString(R.string.addingAllMarkers));
        new Populater().execute();
    }

    /**
     * Sets parent Activity
     *
     * @param parent no.wsact.mikand.pg4600.assignment2.MapApp.Map
     */
    public void setParent(Map parent) {
        this.parent = parent;
        CLASS_NAME = parent.getString(R.string.RuterMap);
        RUTER_MAP_UTM_MAP_AREA = parent.getString(R.string.ruterUtmBase);
    }

    /**
     * Sets the LocationHandler for the map.
     *
     * @param locationHandler no.wsact.mikand.pg4600.assignment2.MapApp.utils.location.LocationHandler
     */
    public void setLocationHandler(LocationHandler locationHandler) {
        this.locationHandler = locationHandler;
    }

    /**
     * Sets the initial location of the user on map.
     *
     * @param initialLocation com.google.android.gms.maps.model.LatLng
     */
    public void setInitialLocation(LatLng initialLocation) {
        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(initialLocation.latitude, initialLocation.longitude), DEFAULT_ZOOM));
    }

    /**
     * This private class is an AsyncTask which populates the map with all RuterStops.
     */
    private class Populater extends AsyncTask<Void, Integer, Void> {
        // progressbar for backup if user cancels dialog
        private ProgressBar progressBar;
        private ProgressDialog progressBarDialog;

        /**
         * Prepares progressbar.
         */
        @Override
        protected void onPreExecute() {
            if (progressBar == null) {
                progressBar = (ProgressBar) parent.findViewById(R.id.progressBar);
            }

            progressBarDialog = new ProgressDialog(getActivity());
            progressBarDialog.setTitle("Adding stops to map...");
            progressBarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBarDialog.setProgress(0);
            progressBarDialog.setMax(allStopsByName.size());
            progressBarDialog.show();
        }

        /**
         * Builds and deploys all markers to the map, based on the list of ruterstops.
         *
         * @param params Void[]
         * @return Void
         */
        @Override
        protected Void doInBackground(Void... params) {
            double progressCounter = 0.0;
            boolean userDismissedDialog = false;

            for (final Entry<String, RuterStop> stopEntry : allStopsByName.entrySet()) {
                if (!userDismissedDialog && !progressBarDialog.isShowing()) {
                    progressBarDialog.dismiss();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });

                    userDismissedDialog = true;
                }

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                addMarker(stopEntry);
                            } catch (NullPointerException npe) {
                                doExceptions(npe);
                            }
                        }
                    });

                    publishProgress((int) (((progressCounter + 1) /
                            (double) allStopsByName.size()) * 100));
                    progressCounter++;

                    Thread.sleep(WAIT_BETWEEN_EACH_MARKER_ADDITION_IN_MILLIS);
                } catch (Exception e) {
                    doExceptions(e);
                    break;
                }
            }

            // cannot use R, creates NPE
            Log.d(CLASS_NAME, "All markers added.");

            parent.allMarkersAdded();

            return null;
        }

        /**
         * Builds and deploys marker.
         *
         * @param stopEntry java.util.Map.Entry
         */
        private void addMarker(Entry<String, RuterStop> stopEntry) {
            LatLng newLatLng =
                    Converter.utmToLatLon(stopEntry.getValue().getLatitude(),
                            stopEntry.getValue().getLongitude(),
                            RUTER_MAP_UTM_MAP_AREA);

            getMap().addMarker(makeMapMarker(newLatLng.latitude, newLatLng.longitude,
                    stopEntry.getKey() + parent.getString(R.string.sone) +
                    stopEntry.getValue().getZone() + parent.getString(R.string.soneEnd),
                    stopEntry.getValue().getShortName()));
        }

        /**
         * Updates progressbar.
         *
         * @param values Integer[]
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (progressBarDialog.isShowing()) {
                progressBarDialog.incrementProgressBy(1);
            } else {
                progressBar.setProgress(values[0]);
            }
        }

        /**
         * Informs user and removes progressbar.
         *
         * @param aVoid Void
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            if (!progressBarDialog.isShowing() && isVisible()) {
                Toast.makeText(parent,
                     parent.getString(R.string.allMarkersAdded), Toast.LENGTH_LONG)
                     .show();
            }

            progressBarDialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Handler for various Exceptions
     *
     * @param exception T
     * @param <T> T
     */
    private <T> void doExceptions(T exception) {
        if (exception instanceof InterruptedException) {
            InterruptedException intEx = (InterruptedException) exception;
            Log.e(getActivity().getString(R.string.interruptAtMapPopulate),
                    Arrays.toString(intEx.getStackTrace()));
        } else if (exception instanceof NullPointerException) {
            NullPointerException nullEx = (NullPointerException) exception;
            // cannot get R value, crashes app
            Log.e(CLASS_NAME, Arrays.toString(nullEx.getStackTrace()));
        }
    }
}
