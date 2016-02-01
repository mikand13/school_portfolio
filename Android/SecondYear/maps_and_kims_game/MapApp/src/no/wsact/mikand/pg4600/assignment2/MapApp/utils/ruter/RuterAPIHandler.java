package no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import no.wsact.mikand.pg4600.assignment2.MapApp.Map;
import no.wsact.mikand.pg4600.assignment2.MapApp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp.utils
 *
 * This class is responsible for processing information returned from the Ruter Api.
 *
 * @author Anders Mikkelsen
 * @version 11.05.15
 */
@SuppressWarnings("FieldCanBeLocal")
public class RuterAPIHandler extends AsyncTask<URL, Integer, Long>
        implements Runnable, OnRuterDownloadEventListener {
    private final String CLASS_NAME;
    private final int MAXIMUM_ATTEMPTS_BEFORE_DELAY = 5;

    private final Map parent;
    private AsyncTask<URL, Void, Void> downloader;

    private final String RUTER_API;
    private final String RUTER_ALL_STOPS;

    private JSONArray stops;
    private HashMap<String, RuterStop> allStopsByName;

    private ProgressBar progressBar;
    private boolean downloading, downloaded;

    public RuterAPIHandler(Map parent) {
        this.parent = parent;
        CLASS_NAME = parent.getString(R.string.RuterAPIHandler);
        RUTER_API = parent.getString(R.string.ruterAPI);
        RUTER_ALL_STOPS = RUTER_API + parent.getString(R.string.ruterAPIAllStopEndPoint);

        stops = null;
        allStopsByName = null;
        downloaded = false;
        downloading = false;
    }

    /**
     * Keeps checking for online status and downloads when availible.
     *
     * If now connection is made within 10 seconds, rests for one hour.
     */
    @Override
    public void run() {
        int attempts = 0;

        while (true) {
            try {
                if (downloaded) {
                    break;
                } else if (!downloaded && !downloading && parent.isOnline()) {
                    try {
                        downloader = new RuterDownloader(this).execute(new URL(RUTER_ALL_STOPS));
                        downloading = true;
                    } catch (MalformedURLException e) {
                        Log.d(parent.getString(R.string.badRuterApiUrlAllStops),
                                Arrays.toString(e.getStackTrace()));
                        break;
                    }
                }

                if (!downloaded && !downloading) {
                    if (attempts < MAXIMUM_ATTEMPTS_BEFORE_DELAY) {
                        attempts++;
                        Thread.sleep(2000);
                    } else if (!downloading) {
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(parent,
                                        parent.getString(R.string.noNetwork),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });

                        Thread.sleep(60000 * 60);
                    }
                }
            } catch (InterruptedException e) {
                attempts = 0;

                if (downloaded || parent.isDestroyed() || parent.isFinishing()) {
                    if (downloader != null) {
                        downloader.cancel(true);
                    }
                    break;
                }
            }
        }
    }

    /**
     * This getter is accessed by the RuterDownloader
     */
    Activity getActivity() {
        return parent;
    }

    /**
     * Called when RuterDownloader has executed its operation.
     */
    @Override
    public void downloadCompleted(JSONArray stops) {
        this.stops = stops;
        downloading = false;
        downloaded = true;

        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(parent,
                        parent.getString(R.string.processingResultsFromRuter),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        execute();
    }

    /**
     * Called if RuterDownloader fails.
     */
    @Override
    public void downloadFailed() {
        downloading = false;
    }

    /**
     * Prepares Progressbar.
     */
    @Override
    protected void onPreExecute() {
        if (progressBar == null) {
            progressBar = (ProgressBar) parent.findViewById(R.id.progressBar);
        }

        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Downloads all stops and enters them into the list.
     *
     * @param params java.net.URL
     * @return java.lang.Long
     */
    @Override
    protected Long doInBackground(URL... params) {
        allStopsByName = new HashMap<String, RuterStop>();

        try {
            for (int i = 0; i < stops.length(); i++) {
                addToList(buildStop(stops.getJSONObject(i)));
                publishProgress((int) (((double) (i + 1) / (double) stops.length()) * 100));
            }

            Log.i(parent.getString(R.string.numberOfStops),
                    parent.getString(R.string.allStopsByNameSize) + allStopsByName.size());
        } catch (Exception e) {
            // cannot use R, creates NPE
            Log.e(CLASS_NAME, Arrays.toString(e.getStackTrace()));
        }

        return (long) allStopsByName.size();
    }

    /**
     * Updates progressbar.
     *
     * @param values Integer[]
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }

    /**
     * Removes progressbar, informs user of ending and delivers result to parent.
     *
     * @param stopsDownloaded java.lang.Long
     */
    @Override
    protected void onPostExecute(Long stopsDownloaded) {
        if (!parent.isDestroyed() && ! parent.isFinishing()) {
            Toast.makeText(parent,
                    parent.getString(R.string.processed) +
                            stopsDownloaded +
                            parent.getString(R.string.stopsFromRuter),
                    Toast.LENGTH_SHORT)
                    .show();
            progressBar.setVisibility(View.INVISIBLE);

            parent.deliverStopsFromRuter(allStopsByName);
        }
    }

    /**
     * Adds a RuterStop to the list.
     *
     * @param rs RuterStop
     */
    private void addToList(RuterStop rs) {
        allStopsByName.put(rs.getName(), rs);
    }

    /**
     * Builds a RuterStop from JSON
     *
     * @param jo org.json.JSONObject
     * @return RuterStop
     * @throws JSONException
     */
    private RuterStop buildStop(JSONObject jo) throws JSONException {
        return new RuterStop(jo.getInt(parent.getString(R.string.ruterStopIDName)),
                             jo.getString(parent.getString(R.string.ruterStopNameName)),
                             jo.getString(parent.getString(R.string.ruterStopShortNameName)),
                             jo.getInt(parent.getString(R.string.ruterStopUTM32XName)),
                             jo.getInt(parent.getString(R.string.ruterStopUTM32YName)),
                             jo.getString(parent.getString(R.string.ruterStopZoneName)));
    }

    /**
     * Clears the stop list.
     */
    public void close() {
        allStopsByName.clear();
    }
}
