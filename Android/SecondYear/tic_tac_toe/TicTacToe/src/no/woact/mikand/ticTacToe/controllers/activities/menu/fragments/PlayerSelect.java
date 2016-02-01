package no.woact.mikand.ticTacToe.controllers.activities.menu.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import no.woact.mikand.ticTacToe.R;
import no.woact.mikand.ticTacToe.controllers.activities.game.Game;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This class defines and controls the playerselection screen which is displayed
 * after the introscreen.
 *
 * @author anders
 * @version 09.02.15
 */
public class PlayerSelect extends Fragment implements View.OnClickListener {
    private EditText pOneTxt;
    private EditText pTwoTxt;
    private TextView pTwoTextView;

    private Switch aiSwitch;
    private Button goButton;

    /**
     * Standard constructor for inflating and fetching private views.
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.playerselect, container, false);

        initializeFragmentPrivates(v);

        return v;
    }

    /**
     * This method gets all necessary views and sets listeners.
     *
     * @param v View
     */
    private void initializeFragmentPrivates(View v) {
        pOneTxt = (EditText) v.findViewById(R.id.playerSelectPlayerOneEditText);
        pTwoTxt = (EditText) v.findViewById(R.id.playerSelectPlayerTwoEditText);
        pTwoTextView =
                (TextView) v.findViewById(R.id.playerSelectPlayerTwoTextView);
        aiSwitch = (Switch) v.findViewById(R.id.playerTwoComputerSwitch);
        goButton = (Button) v.findViewById(R.id.playerSelectGoButton);

        goButton.setOnClickListener(this);
        aiSwitch.setOnClickListener(this);
    }

    /**
     * Controls the button and verifies that both players have a name and that
     * player two is not called "Computer".
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        if (v.equals(goButton)) {
            if (checkUniqueAndNotEmptyNames()) {
                Intent i = new Intent(getActivity(), Game.class);

                i.putExtra("playerOne", pOneTxt.getText().toString());

                if (!aiSwitch.isChecked()) {
                    i.putExtra("playerTwo", pTwoTxt.getText().toString());
                } else {
                    i.putExtra("playerTwo", getActivity().getString(
                            R.string.computerPlayer));
                }

                getActivity().finish();
                startActivity(i);
            }
        }

        if (v.equals(aiSwitch)) {
            toggleAi();
        }
    }

    /**
     * This method verifies that playernames are not equal and are unique, plus
     * several additional checks.
     *
     * @return boolean
     */
    private boolean checkUniqueAndNotEmptyNames() {
        String toastMessage = "";
        String mustHaveName = " " +
                getActivity().getString(R.string.mustHaveName);
        String bothMustHaveName =
                        getActivity().getString(R.string.bothPlayers) +
                                mustHaveName;
        String playerOneMustHaveName =
                        getActivity().getString(R.string.playerOne) +
                                mustHaveName;
        String playerTwoMustHaveName =
                getActivity().getString(R.string.playerTwo) +
                        mustHaveName;

        String playerOneName = pOneTxt.getText().toString();
        String playerTwoName = pTwoTxt.getText().toString();

        if (playerOneName.equals(getActivity().getString(R.string.emptyString))
                && playerTwoName.equals(
                getActivity().getString(R.string.emptyString))) {
            if (aiSwitch.isChecked()) {
                toastMessage = playerOneMustHaveName;
            } else {
                toastMessage = bothMustHaveName;
            }
        } else if (playerOneName.equals(
                getActivity().getString(R.string.emptyString))) {
            if (aiSwitch.isChecked()) {
                if (checkForKeyWords()) {
                    return true;
                } else {
                    toastMessage = playerTwoMustHaveName;
                }
            } else {
                toastMessage = playerOneMustHaveName;
            }
        } else if (playerTwoName.equals(
                getActivity().getString(R.string.emptyString))) {
            if (aiSwitch.isChecked()) {
                if (checkForKeyWords() && !playerOneName.equals(
                        getActivity().getString(R.string.emptyString))) {
                    return true;
                } else {
                    toastMessage = playerOneMustHaveName;
                }
            } else {
                toastMessage = playerTwoMustHaveName;
            }
        } else {
            if (checkForKeyWords()) {
                return true;
            }
        }

        Toast.makeText(getActivity(),
                toastMessage, Toast.LENGTH_SHORT)
                .show();

        return false;
    }

    /**
     * This method verifies no names are registered keywords for the game.
     *
     * @return boolean
     */
    private boolean checkForKeyWords() {
        if (!(pTwoTxt.getText().toString().equals(getActivity().getString(
                R.string.computerPlayer)) ||
                pOneTxt.getText().toString().equals(getActivity().getString(
                        R.string.computerPlayer)))) {
            return true;
        } else {
            Toast.makeText(getActivity(),
                    getActivity().getString(
                            R.string.computerIsAKeyWordForNames),
                    Toast.LENGTH_SHORT)
                    .show();
        }

        return false;
    }

    /**
     * This toggles the removal of player two for computer play.
     */
    private void toggleAi() {
        if (aiSwitch.isChecked()) {
            pTwoTextView.setVisibility(View.GONE);
            pTwoTxt.setVisibility(View.GONE);
        } else {
            pTwoTxt.setText(getActivity().getString(R.string.emptyString));
            pTwoTextView.setVisibility(View.VISIBLE);
            pTwoTxt.setVisibility(View.VISIBLE);
        }
    }
}