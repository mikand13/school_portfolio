package no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import no.wsact.mikand.pg4600.assignment2.MapApp.R;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter.ruterExceptions.RuterNotAccesibleException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp.utils
 *
 * This class is responsible for getting information from the ruter API.
 *
 * @author Anders Mikkelsen
 * @version 23.05.15
 */
@SuppressWarnings("FieldCanBeLocal")
class RuterDownloader extends AsyncTask<URL, Void, Void> {
    private final String CLASS_NAME;
    private final int MAXIMUM_THRESHOLD_BEFORE_ABORT_IN_SECONDS = 10;

    private final RuterAPIHandler parent;
    private ProgressDialog progressBarDialog;

    public RuterDownloader(RuterAPIHandler parent) {
        this.parent = parent;
        CLASS_NAME = parent.getActivity().getString(R.string.RuterDownloader);
    }

    /**
     * Sets up indeterminate progressdialog as download is attempted.
     */
    @Override
    protected void onPreExecute() {
        parent.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (parent.getActivity() != null) {
                    progressBarDialog = new ProgressDialog(parent.getActivity());
                    progressBarDialog.setTitle(parent.getActivity().getString(
                            R.string.downloadingStopsFromRuter));
                    progressBarDialog.setCancelable(false);
                    progressBarDialog.setIndeterminate(true);
                    progressBarDialog.show();
                }
            }
        });
    }

    /**
     * Does actual download and sends result to RuterAPIHandler.
     *
     * @param params java.util.Array of java.net.URL
     * @return Void
     */
    @Override
    protected Void doInBackground(URL... params) {
        boolean downloaded = false;

        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(
                    httpParams, MAXIMUM_THRESHOLD_BEFORE_ABORT_IN_SECONDS * 1000);
            DefaultHttpClient ruterHttpClient = new DefaultHttpClient(httpParams);
            HttpResponse ruterResponse = ruterHttpClient.execute(new HttpGet(params[0].toURI()));

            downloaded = true;
            parent.downloadCompleted(
                    new JSONArray(EntityUtils.toString(ruterResponse.getEntity())));
        } catch (SocketTimeoutException e) {
            Log.e(parent.getActivity().getString(R.string.timeOutAtRuterFetch),
                    new RuterNotAccesibleException().getMessage());

            parent.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(parent.getActivity(),
                            parent.getActivity().getString(R.string.tooSlowInternetConnection),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        } catch (IOException ioe) {
            Log.e(parent.getActivity().getString(R.string.errorAtRuterFetch),
                    new RuterNotAccesibleException().getMessage());
        } catch (Exception e) {
            // cannot use R, creates NPE
            Log.e(CLASS_NAME, Arrays.toString(e.getStackTrace()));
        }

        if (!downloaded) {
            parent.downloadFailed();
        }

        return null;
    }

    /**
     * Sets completion in RuterAPIHandler
     *
     * @param aVoid Void
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        progressBarDialog.dismiss();
    }
}
