package no.wsact.mikand.pg4600.assignment2.KimsGame.game;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;
import no.wsact.mikand.pg4600.assignment2.KimsGame.R;
import no.wsact.mikand.pg4600.assignment2.KimsGame.results.Results;
import no.wsact.mikand.pg4600.assignment2.KimsGame.game.fragments.GuessFragment;
import no.wsact.mikand.pg4600.assignment2.KimsGame.game.fragments.MemorizeFragment;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.contacts.ContactHandler;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.fragments.FragmentOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame
 *
 * This class controls the game by switching between the memorization and guessing fragment.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
public class Game extends Activity {
    private SharedPreferences sharedPreferences;
    private ContactHandler contactHandler;
    private List<String> randomWords;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), 0);

        if (!sharedPreferences.getBoolean(getString(R.string.gameRunning), false)) {
            sharedPreferences.edit().putInt(getString(R.string.round), 1).apply();
            sharedPreferences.edit().putInt(getString(R.string.points), 0).apply();
            sharedPreferences.edit().putString(
                    getString(R.string.hiddenWord),
                    getString(R.string.spacer)).apply();
            sharedPreferences.edit().putBoolean(getString(R.string.gameRunning), true).apply();
        }

        // lock to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        contactHandler = new ContactHandler(this);
        randomWords = new ArrayList<String>();

        prepareMemorizeScreen();
    }

    /**
     * Fires up the MemorizeFragment and kills the MemorizeFragment.
     */
    private void prepareMemorizeScreen() {
        killGuessFragment();
        makeMemorizeFragment();
    }

    /**
     * Fires up the GuessFragment and kills the MemorizeFragment.
     */
    private void prepareGuessScreen() {
        killMemorizeFragment();
        makeGuessFragment();
    }

    /**
     * Starts guess after removing a word.
     *
     */
    public void guessDone() {
        prepareGuessScreen();
    }

    /**
     * Makes memorizefragment.
     */
    private void makeMemorizeFragment() {
        FragmentOperations.makeFragment(
                this, R.id.mainView, new MemorizeFragment(),
                getString(R.string.memorizerFragmentTag));
    }

    /**
     * Makes guessfragment.
     */
    private void makeGuessFragment() {
        FragmentOperations.makeFragment(
                this, R.id.mainView, new GuessFragment(),
                getString(R.string.guessFragmentTag));
    }

    /**
     * Removes MemorizeFragment
     */
    private void killMemorizeFragment() {
        FragmentOperations.killFragment(this, getString(R.string.memorizerFragmentTag));
    }

    /**
     * Removes GuessFragment
     */
    private void killGuessFragment() {
        FragmentOperations.killFragment(this, getString(R.string.guessFragmentTag));
    }

    /**
     * Resets upon win.
     */
    public void win() {
        int roundValue = sharedPreferences.getInt(getString(R.string.round), 0);
        sharedPreferences.edit().putInt(getString(R.string.round), roundValue + 1).apply();

        int prevPoints = sharedPreferences.getInt(getString(R.string.points), 0);
        sharedPreferences.edit().putInt(getString(R.string.points),
                prevPoints + (roundValue * 100)).apply();

        randomWords.clear();

        prepareMemorizeScreen();

        Toast.makeText(this, getString(R.string.youWin), Toast.LENGTH_SHORT).show();
    }

    /**
     * On loosing switches to the ShowHighScores fragment.
     */
    public void loose() {
        killMemorizeFragment();
        killGuessFragment();

        Toast.makeText(this, getString(R.string.youLoose), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, Results.class);
        startActivity(intent);
        finish();
    }

    /**
     * Fetches a List of random Strings from contact names.
     *
     * @return List of String
     */
    public List<String> getRandomWordList() {
        if (randomWords.size() == 0) {
            randomWords = contactHandler.getNames(7);
        }

        return randomWords;
    }

    /**
     * Fetches a random word from the database.
     *
     * @return java.lang.String
     */
    public String getRandomWord() {
        return contactHandler.getRandomContactName();
    }

    public SharedPreferences getPrefs() {
        return sharedPreferences;
    }
}
