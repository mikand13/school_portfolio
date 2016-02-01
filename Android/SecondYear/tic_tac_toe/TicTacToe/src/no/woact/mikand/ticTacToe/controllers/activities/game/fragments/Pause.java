package no.woact.mikand.ticTacToe.controllers.activities.game.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import no.woact.mikand.ticTacToe.R;
import no.woact.mikand.ticTacToe.models.utils.result.Results;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This class Generates the pausefragment that is accessible ingame.
 *
 * @author anders
 * @version 09.02.15
 */
public class Pause extends Fragment {
    /**
     * Standard constructor for infalting and initializing the resultlist
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.pause, container, false);
        initializeResultList(v);

        return v;
    }

    /**
     * Finds the resultlist view and fills it.
     *
     * @param v View
     */
    private void initializeResultList(View v) {
        ListView resultsList
                = (ListView) v.findViewById(R.id.pauseResultList);

        Results.createResultList(resultsList, this, v);

        resultsList.setOnTouchListener((View.OnTouchListener) getActivity());
    }
}