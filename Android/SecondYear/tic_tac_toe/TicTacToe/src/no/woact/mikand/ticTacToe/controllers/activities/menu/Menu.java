package no.woact.mikand.ticTacToe.controllers.activities.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import no.woact.mikand.ticTacToe.R;
import no.woact.mikand.ticTacToe.controllers.activities.menu.fragments.PlayerSelect;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This class defines and initializes the Menu for the game, and is an
 * OnClickListener.
 *
 * @author anders
 * @version 09.02.15
 */
public class Menu extends Activity implements View.OnClickListener {
    /**
     * Defines the layout and calls initializers.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initializeButtons();
    }

    /**
     * This method clears up any issues with rotation.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean("playerSelectActive")) {
            findViewById(R.id.menuTicTacToeLogo).setVisibility(View.GONE);
            findViewById(R.id.menuPlayButton).setVisibility(View.GONE);
        }

        if (savedInstanceState.getBoolean("computerSwitchActive")) {
            findViewById(R.id.playerSelectPlayerTwoEditText)
                    .setVisibility(View.GONE);
            findViewById(R.id.playerSelectPlayerTwoTextView)
                    .setVisibility(View.GONE);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Finds and sets the play button.
     */
    private void initializeButtons() {
        findViewById(R.id.menuPlayButton).setOnClickListener(this);
    }

    /**
     * Handles the play button event.
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        if (v.equals(findViewById(R.id.menuPlayButton))) {
            launchPlayerSelection(v);
        }
    }

    /**
     * This method removes all views and starts the playerSelect fragment. The
     * reason I remove all is that if you press back you are just going to quit
     * the application anyway.
     *
     * @param v View
     */
    private void launchPlayerSelection(View v) {
        ((ViewGroup) v.getParent()).removeAllViews();

        getFragmentManager().beginTransaction()
                .add(R.id.mainView, new PlayerSelect(),
                        getString(R.string.playerSelectFragmentName))
                .commit();
    }

    /**
     * Saves the current state of the menu.
     *
     * @param outState Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (getFragmentManager().findFragmentByTag(getString(
                R.string.playerSelectFragmentName))
                != null) {
            outState.putBoolean("playerSelectActive", true);

            if (((Switch) (findViewById(R.id.playerTwoComputerSwitch))).isChecked())
                outState.putBoolean("computerSwitchActive", true);
        }

        super.onSaveInstanceState(outState);
    }
}
