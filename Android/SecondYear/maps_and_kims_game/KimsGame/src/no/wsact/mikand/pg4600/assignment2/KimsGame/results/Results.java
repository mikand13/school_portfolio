package no.wsact.mikand.pg4600.assignment2.KimsGame.results;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import no.wsact.mikand.pg4600.assignment2.KimsGame.R;
import no.wsact.mikand.pg4600.assignment2.KimsGame.results.fragments.NewHighScore;
import no.wsact.mikand.pg4600.assignment2.KimsGame.results.fragments.ShowHighScores;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.fragments.FragmentOperations;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.db.OnResultDBReadyListener;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.Result;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.ResultHandler;

import java.sql.Timestamp;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame
 *
 * This class controls the fragments for adding a highscore and displaying them.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
public class Results extends Activity implements OnResultDBReadyListener {
    private SharedPreferences sharedPreferences;
    public static final int MAX_NUMBER_OF_HIGHSCORES = 7;

    private ResultHandler resultHandler;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name), 0);
        sharedPreferences.edit().putBoolean(getString(R.string.gameRunning), false).apply();

        // lock to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        resultHandler = new ResultHandler(this).open(this);
    }

    public ResultHandler getResultHandler() {
        return resultHandler;
    }

    /**
     * Adds a Result to the Database and switches to ShowHighScores fragment.
     *
     * @param result Result
     */
    public void applyNewHighScore(Result result) {
        resultHandler.addHighScore(result);

        showHighScoreFragment();
        killNewHighScoreFragment();
    }

    /**
     * Makes newhighscore fragment.
     */
    private void showNewHighScoreFragment() {
        FragmentOperations.makeFragment(
                this, R.id.resultsMainView, new NewHighScore(),
                getString(R.string.newHighScoreFragmentTag));
    }

    /**
     * Makes highscoresfragment.
     */
    private void showHighScoreFragment() {
        FragmentOperations.makeFragment(
                this, R.id.resultsMainView, new ShowHighScores(),
                getString(R.string.showHighScoresFragmentTag));
    }

    /**
     * Removes newhighscore fragment.
     */
    private void killNewHighScoreFragment() {
        FragmentOperations.killFragment(this, getString(R.string.newHighScoreFragmentTag));
    }

    @Override
    public void onResultDbReady() {
        Result result = new Result(0, "", (
                sharedPreferences.getInt(getString(R.string.round), 0) - 1),
                sharedPreferences.getInt(getString(R.string.points), 0),
                new Timestamp(System.currentTimeMillis()));

        if (result.getPoints() > 0 && resultHandler.checkForNewHighScore(result)) {
            showNewHighScoreFragment();
        } else {
            showHighScoreFragment();
        }
    }

    public SharedPreferences getPrefs() {
        return sharedPreferences;
    }

    /**
     * Hides keyboard when navigation away from edittext.
     * Gathered from here: http://stackoverflow.com/questions/13693880/hide-android-virtual-keyboard
     *
     * @param view android.view.View
     */
    public void hideSoftKeyboard(View view) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}