package no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter;

import org.json.JSONArray;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp.utils
 *
 * This class is responsible for getting information from the ruter API.
 *
 * @author Anders Mikkelsen
 * @version 23.05.15
 */
@SuppressWarnings("unused")
interface OnRuterDownloadEventListener {
    void downloadCompleted(JSONArray stops);
    void downloadFailed();
}
