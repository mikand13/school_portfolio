package no.wsact.mikand.pg4600.assignment2.MapApp.utils.coordinates;

import com.google.android.gms.maps.model.LatLng;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp.utils.coordinates
 *
 * This class handles conversion of coordinates.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
public class Converter {
    /**
     * This code was gathered, converted and slightly modified to Java from this location:
     * http://stackoverflow.com/questions/2689836/converting-utm-wsg84-coordinates-to-latitude-and-longitude
     *
     * Entered by user 'playful' at May 21 '13 01:39
     *
     * @param utmX double
     * @param utmY double
     * @param utmZone java.lang.String
     */
    @SuppressWarnings("SameParameterValue")
    public static LatLng utmToLatLon(double utmX, double utmY, String utmZone) {
        boolean isNorthHemisphere = true;

        double zone = Integer.parseInt(utmZone.substring(0, utmZone.length() - 1));
        double c_sa = 6378137.000000;
        double c_sb = 6356752.314245;
        double e2 = Math.pow((Math.pow(c_sa, 2) - Math.pow(c_sb, 2)), 0.5)/c_sb;
        double e2cuadrada = Math.pow(e2, 2);
        double c = Math.pow(c_sa, 2) / c_sb;
        double x = utmX - 500000;
        //noinspection ConstantConditions
        double y = isNorthHemisphere ? utmY : utmY - 10000000;

        double s = ((zone * 6.0) - 183.0);
        double lat = y / (c_sa * 0.9996);
        double v = (c / Math.pow(1 + (e2cuadrada * Math.pow(Math.cos(lat), 2)), 0.5)) * 0.9996;
        double a = x / v;
        double a1 = Math.sin(2 * lat);
        double a2 = a1 * Math.pow((Math.cos(lat)), 2);
        double j2 = lat + (a1 / 2.0);
        double j4 = ((3 * j2) + a2) / 4.0;
        double j6 = ((5 * j4) + Math.pow(a2 * (Math.cos(lat)), 2)) / 3.0;
        double alfa = (3.0 / 4.0) * e2cuadrada;
        double beta = (5.0 / 3.0) * Math.pow(alfa, 2);
        double gama = (35.0 / 27.0) * Math.pow(alfa, 3);
        double bm = 0.9996 * c * (lat - alfa * j2 + beta * j4 - gama * j6);
        double b = (y - bm) / v;
        double epsi = ((e2cuadrada * Math.pow(a, 2)) / 2.0) * Math.pow((Math.cos(lat)), 2);
        double eps = a * (1 - (epsi / 3.0));
        double nab = (b * (1 - epsi)) + lat;
        double senoheps = (Math.exp(eps) - Math.exp(-eps)) / 2.0;
        double delt  = Math.atan(senoheps / (Math.cos(nab)));
        double tao = Math.atan(Math.cos(delt) * Math.tan(nab));

        return new LatLng((((lat + (1 + e2cuadrada * Math.pow(Math.cos(lat), 2) - (3.0 / 2.0) *
                e2cuadrada * Math.sin(lat) * Math.cos(lat) * (tao - lat)) * (tao - lat))) / Math.PI)
                * 180, ( delt / Math.PI) * 180 + s);
    }
}
