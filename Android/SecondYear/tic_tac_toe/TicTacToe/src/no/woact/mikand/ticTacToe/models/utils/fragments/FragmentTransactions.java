package no.woact.mikand.ticTacToe.models.utils.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This is a helperclass for adding and removing fragments.
 *
 * @author anders
 * @version 18.02.15
 */
@SuppressWarnings("SameParameterValue")
public class FragmentTransactions {
    /**
     * Method for adding a fragment to an activity.
     *
     * Standard animation sliding from right to left.
     *  @param activity Activity
     * @param fragment Fragment
     * @param pauseFragment String
     * @param view View
     * @param animation Animation
     */
    public static void addFragment(Activity activity, Fragment fragment,
                                   String pauseFragment,
                                   int view, int animation) {
        FragmentTransaction fragTrans =
                activity.getFragmentManager().beginTransaction();
        fragTrans.setCustomAnimations(animation, animation);

        fragTrans.add(view, fragment, pauseFragment).commit();
    }

    /**
     * Method for removing a fragment to an activity.
     *
     * Standard animation sliding from left to right.
     *
     * @param activity Activity
     * @param fragment Fragment
     * @param animation Animation
     */
    public static void removeFragment(Activity activity, Fragment fragment,
                                      int animation) {
        FragmentTransaction fragTrans =
                activity.getFragmentManager().beginTransaction();
        fragTrans.setCustomAnimations(animation, animation);

        fragTrans.remove(fragment).commit();
    }
}
