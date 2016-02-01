package no.wsact.mikand.pg4600.assignment2.KimsGame.utils.fragments;

import android.app.Activity;
import android.app.Fragment;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.utils.fragments
 *
 * This class handles fragment operations.
 *
 * @author Anders Mikkelsen
 * @version 16.05.15
 */
public class FragmentOperations {
    /**
     * Kills fragment
     */
    private static void replaceFragment(Activity activity, int view, Fragment fragment, String tag) {
        activity.getFragmentManager().beginTransaction().replace(view, fragment, tag).commit();
    }

    /**
     * Starts fragment.
     *
     * @param view java.lang.Integer
     * @param fragment android.app.Fragment
     * @param tag java.lang.String
     */
    public static void makeFragment(Activity activity, int view, Fragment fragment, String tag) {
        FragmentOperations.replaceFragment(activity, view, fragment, tag);
    }

    /**
     * Kills fragment
     */
    public static void killFragment(Activity activity, String tag) {
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(tag);

        if (fragment != null) {
            activity.getFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}
