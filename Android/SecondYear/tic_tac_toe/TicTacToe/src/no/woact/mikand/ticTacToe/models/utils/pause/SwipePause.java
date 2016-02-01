package no.woact.mikand.ticTacToe.models.utils.pause;

import android.app.Activity;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import no.woact.mikand.ticTacToe.R;
import no.woact.mikand.ticTacToe.controllers.activities.game.fragments.Pause;
import no.woact.mikand.ticTacToe.models.utils.fragments.FragmentTransactions;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This class controls the pause screen ingame.
 *
 * @author anders
 * @version 09.02.15
 */
public class SwipePause extends GestureDetector.SimpleOnGestureListener {
    @SuppressWarnings("FieldCanBeLocal")
    private final int MIN_SWIPE = 100;

    private static boolean gamePaused;
    private static boolean doneButtonVisible;
    private final Activity parent;
    private final ViewGroup mainGameView;
    private static Pause pauseFragment;

    /**
     * This constructor sets the game to unpaused by default, sets an activity
     * as parent as supplied by the caller and generates a pauseFragment.
     *
     * @param parent Activity
     */
    public SwipePause (Activity parent) {
        this.parent = parent;
        mainGameView = (ViewGroup) parent.findViewById(R.id.mainGameWindow);
    }

    /**
     * This method is called whenever a swipe is detected in Game.
     * It then verifies the direction and wether the game is paused or not and
     * proceeds accordingly.
     *
     * @param evt1 MotionEvent
     * @param evt2 MotionEvent
     * @param velocityX float
     * @param velocityY float
     * @return boolean
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onFling(MotionEvent evt1, MotionEvent evt2,
                           float velocityX, float velocityY) {
        if (((evt2.getX() - evt1.getX()) < 0 - MIN_SWIPE) && !gamePaused) {
            doneButtonVisible = parent.findViewById(R.id.gamePlayerDoneButton)
                                .getVisibility() == View.VISIBLE;

            addPauseFragment();

            return true;
        } else if (((evt1.getX() - evt2.getX()) < MIN_SWIPE) && gamePaused) {
            gamePaused = false;

            invertVisibility(View.VISIBLE);

            if (!doneButtonVisible) {
                parent.findViewById(R.id.gamePlayerDoneButton)
                        .setVisibility(View.GONE);
            }

            FragmentTransactions.removeFragment(
                    parent, pauseFragment, R.animator.slide_right_exit);

            Log.i("Fragment removed: ", "" + pauseFragment.getTag());

            return true;
        }

        return false;
    }

    /**
     * Regenerates the pausewindow correctly on a rotation.
     */
    public void regeneratePauseFragment() {
        if (parent.getFragmentManager().findFragmentByTag(
                parent.getString(R.string.pauseFragmentname)) == null) {
            addPauseFragment();
        }
    }

    /**
     * Adds the pausefragment.
     */
    private void addPauseFragment() {
        if (pauseFragment == null) {
            pauseFragment = new Pause();
        }

        gamePaused = true;

        invertVisibility(View.GONE);

        FragmentTransactions.addFragment(parent, pauseFragment,
                parent.getString(R.string.pauseFragmentname),
                R.id.mainGameWindow, R.animator.slide_right_enter);

        Log.i("Fragment added: ", "" + pauseFragment.getTag());
    }

    /**
     * This method accepts a visibility int and sets it to the entire activity
     * view.
     *
     * @param visibility int
     */
    private void invertVisibility(int visibility) {
        TransitionManager.beginDelayedTransition(mainGameView, new Fade());

        parent.findViewById(R.id.playerOneNameGame).setVisibility(visibility);
        parent.findViewById(R.id.playerTwoNameGame).setVisibility(visibility);
        parent.findViewById(R.id.gamePlayerDoneButton)
                .setVisibility(visibility);
        parent.findViewById(R.id.gamePauseText).setVisibility(visibility);
        parent.findViewById(R.id.gameGameBoard).setVisibility(visibility);

        Log.i("Fading to: ", "" + visibility);
    }

    /**
     * Returns pauseState of game.
     *
     * @return boolean
     */
    public boolean isGamePaused() {
        return gamePaused;
    }
}