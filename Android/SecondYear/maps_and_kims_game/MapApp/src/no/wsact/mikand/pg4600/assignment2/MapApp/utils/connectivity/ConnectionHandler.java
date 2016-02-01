package no.wsact.mikand.pg4600.assignment2.MapApp.utils.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import no.wsact.mikand.pg4600.assignment2.MapApp.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp.utils.connectivity
 *
 * This class is a facade for checking connectivity on active network.
 *
 * @author Anders Mikkelsen
 * @version 12.05.15
 */
public class ConnectionHandler extends BroadcastReceiver {
    private static List<OnOnlineListener> onOnlineListeners;

    private final Map parent;
    private boolean online;

    public ConnectionHandler(Map parent) {
        this.parent = parent;
        onOnlineListeners = new ArrayList<OnOnlineListener>();
        online = checkForActiveNetwork();
    }

    /**
     * Checks if connected on active network.
     *
     * @return java.lang.Boolean
     */
    private boolean checkForActiveNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) parent.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Facade for onReceive.
     *
     * @return java.lang.Boolean
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Checks if connected, alerts all listeners.
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager mgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        online = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() ||
                 mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();

        if (online) {
            for (OnOnlineListener on : onOnlineListeners) {
                on.phoneOnline();
            }
        }
    }

    public static void registerOnOnlineListener(OnOnlineListener onOnlineListener) {
        onOnlineListeners.add(onOnlineListener);
    }

    public static void unRegisterOnOnlineListener(OnOnlineListener onOnlineListener) {
        onOnlineListeners.remove(onOnlineListener);
    }
}
