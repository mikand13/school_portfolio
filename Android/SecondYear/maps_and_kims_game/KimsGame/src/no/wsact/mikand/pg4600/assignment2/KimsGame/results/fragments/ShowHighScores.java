package no.wsact.mikand.pg4600.assignment2.KimsGame.results.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import no.wsact.mikand.pg4600.assignment2.KimsGame.R;
import no.wsact.mikand.pg4600.assignment2.KimsGame.game.Game;
import no.wsact.mikand.pg4600.assignment2.KimsGame.results.Results;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.Result;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.ResultHandler;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.results
 *
 * This class defines the fragment that displays all highscores.
 *
 * @author Anders Mikkelsen
 * @version 14.05.15
 */
public class ShowHighScores extends Fragment implements View.OnClickListener {
    private Results results;
    private View thisFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.showhighscores, container, false);
        results = (Results) getActivity();

        showGameResult(v);
        prepareHighScoresList(v);
        thisFragment = v;

        v.findViewById(R.id.goAgainButton).setOnClickListener(this);
        v.findViewById(R.id.exitButton).setOnClickListener(this);

        return v;
    }

    /**
     * Builds TextView that shows game result.
     * @param v android.view.View
     */
    private void showGameResult(View v) {
        int round = results.getPrefs().getInt(results.getString(R.string.round), 0);

        if (round > 1) {
            String winText =
                    results.getString(R.string.youConqueredRound) + String.valueOf(round - 1) + "!";
            ((TextView) v.findViewById(R.id.resultTextView)).setText(winText);
        } else {
            String failureText = results.getString(R.string.youFailedMiserably);
            ((TextView) v.findViewById(R.id.resultTextView)).setText(failureText);
        }
    }

    /**
     * Prepares the ListView for display HighScores
     * @param v android.view.View
     */
    private void prepareHighScoresList(View v) {
        ListView highScoresList = (ListView) v.findViewById(R.id.highScoreListView);
        highScoresList.setAdapter(new ArrayAdapter<Result>(
                results, android.R.layout.simple_list_item_1,
                results.getResultHandler().getHighScores(
                        Results.MAX_NUMBER_OF_HIGHSCORES,
                        ResultHandler.HIGHSCORE_POINTS)));
    }

    /**
     * Handles exit and goagain button.
     *
     * @param v android.view.View
     */
    @Override
    public void onClick(View v) {
        if (v.equals(thisFragment.findViewById(R.id.exitButton))) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            results.finish();
        } else if (v.equals(thisFragment.findViewById(R.id.goAgainButton))) {
            Intent intent = new Intent(getActivity(), Game.class);
            startActivity(intent);
            results.finish();
        }
    }
}