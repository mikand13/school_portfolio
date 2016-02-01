package no.woact.mikand.ticTacToe.controllers.activities.result;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import no.woact.mikand.ticTacToe.R;
import no.woact.mikand.ticTacToe.controllers.activities.game.Game;
import no.woact.mikand.ticTacToe.controllers.activities.menu.Menu;
import no.woact.mikand.ticTacToe.models.utils.result.Results;

import java.util.*;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This class defines and initializes the Resultscreen and handles all reset
 * options from the three buttons.
 *
 * @author anders
 * @version 09.02.15
 */
public class Result extends Activity implements View.OnClickListener{
    private ListView resultsList;

    /**
     * Standard Constructor that calls initializers.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        initializeButtons();
        initViews();
        initAdapters();
    }

    /**
     * Finds and sets buttons for the resultscreen
     */
    private void initializeButtons() {
        findViewById(R.id.resultGoAgainButton).setOnClickListener(this);
        findViewById(R.id.resultsNewGameButton).setOnClickListener(this);
        findViewById(R.id.resultExitGameButton).setOnClickListener(this);
    }

    /**
     * Finds and sets the resultlist for the resultscreen
     */
    private void initViews() {
        resultsList = (ListView) findViewById(R.id.resultList);
    }

    /**
     * Sets the adapter for the resultlist
     */
    private void initAdapters() {
        Results.createResultList(resultsList, this)
                .add(getIntent().getExtras().getString("results"));
    }

    /**
     * Controls the three buttons on the bottom of the screen.
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resultGoAgainButton:
                Intent i = new Intent(this, Game.class);

                Bundle extras = getIntent().getExtras();
                i.putExtra("playerOne", "" + extras.get("playerOne"));
                i.putExtra("playerTwo", "" + extras.get("playerTwo"));

                startActivity(i);
                break;
            case R.id.resultsNewGameButton:
                startActivity(new Intent(this, Menu.class));
                break;
            case R.id.resultExitGameButton:
                Toast.makeText(this, getString(R.string.exitText),
                        Toast.LENGTH_SHORT).show();
                System.exit(0);
                break;
        }

        finish();
    }

    /**
     * This ensures the resultlist is saved in SharedPreferences. I chose to use
     * apply(); instead of commit(); to make sure it happens immediatly.
     */
    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences ticTacToePrefs =
                getSharedPreferences(Results.RESULT_LIST, 0);
        SharedPreferences.Editor editor = ticTacToePrefs.edit();

        Set<String> results = new HashSet<>();
        for (int i = 0; i < resultsList.getAdapter().getCount(); i++) {
            results.add("" + resultsList.getAdapter().getItem(i));
        }

        editor.putStringSet(Results.SET_NAME, results);

        editor.apply();
    }
}