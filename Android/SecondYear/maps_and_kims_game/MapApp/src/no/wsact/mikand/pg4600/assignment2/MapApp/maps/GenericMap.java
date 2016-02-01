package no.wsact.mikand.pg4600.assignment2.MapApp.maps;

import android.graphics.Point;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import no.wsact.mikand.pg4600.assignment2.MapApp.R;
import no.wsact.mikand.pg4600.assignment2.MapApp.utils.location.LocationHandler;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp.maps
 *
 * This class defines basic functionality for a map with an optional progressbar. I made it this way
 * so I can expand with other map fragments.
 *
 * @author Anders Mikkelsen
 * @version 12.05.15
 */
public abstract class GenericMap extends MapFragment implements OnMapReadyCallback {
    LocationHandler locationHandler;

    /**
     * Gets the map and sets size of progressbar if availible.
     */
    public void initializeMap() {
        getMapAsync(this);

        if (getActivity().findViewById(R.id.progressBar) != null) {
            setProgressBarWidthToHalfScreenWidth();
        }
    }

    /**
     * Sets width of progressbar to half that of screen size.
     */
    private void setProgressBarWidthToHalfScreenWidth() {
        Point display = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(display);

        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        layoutParams.width = display.x / 2;
        progressBar.setLayoutParams(layoutParams);
    }

    /**
     * This callback is called when the map is ready after initialization. In this callback i enable
     * my location for the map and set my locationhandler as the source.
     *
     * @param googleMap com.google.android.gms.maps.GoogleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setLocationSource(locationHandler);
    }

    /**
     * This method creates a simple Marker for a map.
     *
     * @param latitude java.lang.Double
     * @param longitude java.lang.Double
     * @param name java.lang.String
     * @param description java.lang.String
     * @return com.google.android.gms.maps.model.MarkerOptions;
     */
    MarkerOptions makeMapMarker(double latitude, double longitude,
                                String name, String description) {
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(name)
                .snippet(getActivity().getString(R.string.stop) + description +
                    String.format("%s%.1f", getActivity().getString(R.string.atLat), latitude) +
                    String.format("%s%.1f", getActivity().getString(R.string.commaLng), longitude));
    }
}
