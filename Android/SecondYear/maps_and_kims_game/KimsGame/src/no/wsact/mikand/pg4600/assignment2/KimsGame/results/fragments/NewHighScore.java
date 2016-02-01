package no.wsact.mikand.pg4600.assignment2.KimsGame.results.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import no.wsact.mikand.pg4600.assignment2.KimsGame.R;
import no.wsact.mikand.pg4600.assignment2.KimsGame.results.Results;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.Result;

import java.sql.Timestamp;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.results
 *
 * This class defines the fragment that shows a dialog for adding a highscore.
 *
 * @author Anders Mikkelsen
 * @version 14.05.15
 */
public class NewHighScore extends Fragment implements View.OnClickListener {
    private Results results;

    private EditText name;
    private Button storeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.newhighscore, container, false);
        results = (Results) getActivity();

        initializeDisplay(v);

        return v;
    }

    /**
     * Sets all views and displays rounds beaten.
     *
     * @param v android.view.View
     */
    private void initializeDisplay(View v) {
        int roundsCompleted = results.getPrefs().getInt(results.getString(R.string.round), 0);

        name = (EditText) v.findViewById(R.id.newHighScoreNameEditText);

        String roundText =
                results.getString(R.string.youDefeatedRound) + (roundsCompleted - 1) + "!";
        ((TextView) v.findViewById(R.id.roundsDefeatedTextView)).setText(roundText);

        storeButton = (Button) v.findViewById(R.id.storeButton);
        storeButton.setOnClickListener(this);
    }

    /**
     * Handles store button.
     *
     * @param v android.view.View
     */
    @Override
    public void onClick(View v) {

        if (v.equals(storeButton)) {
            if (TextUtils.isEmpty(name.getText().toString())) {
                Toast.makeText(results,
                        results.getString(R.string.nameCannotBeEmpty), Toast.LENGTH_LONG).show();
            } else {
                results.applyNewHighScore(new Result(
                        0, name.getText().toString(),
                        results.getPrefs().getInt(results.getString(R.string.round), 0) - 1,
                        results.getPrefs().getInt(results.getString(R.string.points), 0),
                        new Timestamp(System.currentTimeMillis())));
            }
        }
    }
}