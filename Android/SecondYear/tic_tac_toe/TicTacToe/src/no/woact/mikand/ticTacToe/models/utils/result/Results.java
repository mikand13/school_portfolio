package no.woact.mikand.ticTacToe.models.utils.result;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import no.woact.mikand.ticTacToe.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * Helperclass for creating a Resultlist.
 *
 * @author anders
 * @version 18.02.15
 */
public class Results {
    public static final String RESULT_LIST = "TicTacToeResults";
    public static final String SET_NAME = "results";
    private static final Locale LOCALE = Locale.US;

    /**
     * Overloaded method for generating a resultList for a Fragment.
     *
     * @param resultsList ListView
     * @param f Fragment
     * @param v View
     */
    public static void createResultList(ListView resultsList, Fragment f,
                                        View v) {
        ArrayAdapter<String> results = getAdapter(v.getContext());
        resultsList.setAdapter(results);

        Set<String> resultsAsSet = getResults(f.getActivity());

        if (resultsAsSet != null) {
            results.addAll(resultsAsSet);
        } else {
            results.add(f.getActivity().getString(R.string.noResults));
        }

        sortResults(results, f.getActivity());
    }

    /**
     * Overloaded method for generating a resultList for an Activity.
     *
     * @param resultsList ListView
     * @param activity Activity
     *
     * @return ArrayAdapter String
     */
    public static ArrayAdapter<String> createResultList(ListView resultsList,
                                                        Activity activity) {
        ArrayAdapter<String> results = getAdapter(activity);
        resultsList.setAdapter(results);

        Set<String> resultsAsSet = getResults(activity);

        if (resultsAsSet != null) {
            results.addAll(resultsAsSet);
        }

        sortResults(results, activity);

        return results;
    }

    /**
     * This method sorts the arrayadapter before return.
     *
     * @param list ArrayAdapter String
     */
    private static void sortResults(ArrayAdapter<String> list, Context contxt) {
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                DateFormat dateFormat = new SimpleDateFormat(
                        contxt.getResources().getString(R.string.dateFormat),
                        LOCALE);

                Date lhsDate = null, rhsDate = null;
                try {
                    lhsDate = dateFormat.parse(lhs);
                    rhsDate = dateFormat.parse(rhs);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (lhsDate != null && rhsDate != null) {
                    return lhsDate.compareTo(rhsDate);
                } else {
                    return 0;
                }
            }
        });
    }

    /**
     * Overloaded method for getting an adapter for a context.
     *
     * @param context Context
     * @return ArrayAdapter T
     */
    private static <T> ArrayAdapter<T> getAdapter(Context context) {
        ArrayList<T> resultsAsStrings = new ArrayList<>();
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                resultsAsStrings);
    }

    /**
     * This method returns a set of strings from SharedPreferences to build
     * the resultlist.
     *
     * @param activity Activity
     * @return Set String
     */
    private static Set<String> getResults(Activity activity) {
        SharedPreferences ticTacToePrefs =
                activity.getSharedPreferences(RESULT_LIST, 0);
        return ticTacToePrefs.getStringSet(SET_NAME, null);
    }
}
