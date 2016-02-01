package no.wsact.mikand.pg4600.assignment2.KimsGame.game.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import no.wsact.mikand.pg4600.assignment2.KimsGame.R;
import no.wsact.mikand.pg4600.assignment2.KimsGame.game.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.game.fragments
 *
 * This class defines the guessing fragment.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
@SuppressWarnings("FieldCanBeLocal")
public class GuessFragment extends Fragment implements View.OnClickListener {
    private Game game;
    private final int GUESS_BUTTONS = 3;

    private Button randomWordButtonOne;
    private Button randomWordButtonTwo;
    private Button randomWordButtonThree;

    private List<String> randomWordList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.guess, container, false);
        game = (Game) getActivity();

        prepareGuessScreen(v);
        initializeButtons(v);

        return v;
    }

    /**
     * Finds all buttons and initializes them for random guess.
     *
     * @param v android.view.View
     */
    private void initializeButtons(View v) {
        randomWordButtonOne = (Button) v.findViewById(R.id.randomWordButtonOne);
        randomWordButtonTwo = (Button) v.findViewById(R.id.randomWordButtonTwo);
        randomWordButtonThree = (Button) v.findViewById(R.id.randomWordButtonThree);

        String[] randomWords = createRandomWordGuessWords();
        randomWordButtonOne.setText(randomWords[0]);
        randomWordButtonTwo.setText(randomWords[1]);
        randomWordButtonThree.setText(randomWords[2]);

        randomWordButtonOne.setOnClickListener(this);
        randomWordButtonTwo.setOnClickListener(this);
        randomWordButtonThree.setOnClickListener(this);
    }

    /**
     * Sets the three guess buttons with two random and one hidden word.
     *
     * @return String[]
     */
    private String[] createRandomWordGuessWords() {
        ArrayList<String> randomGuessWords = new ArrayList<String>();
        int hiddenWordPlacement = (int) (Math.random() * GUESS_BUTTONS) + 1;

        for (int i = 0; i < GUESS_BUTTONS; i++) {
            if (i + 1 == hiddenWordPlacement) {
                randomGuessWords.add(game.getPrefs().getString(
                        game.getString(R.string.hiddenWord), game.getString(R.string.spacer)));
            } else {
                String word;
                do {
                    word = game.getRandomWord();
                } while (randomWordList.contains(word));

                randomGuessWords.add(word);
            }
        }

        return randomGuessWords.toArray(new String[GUESS_BUTTONS]);
    }

    /**
     * Prepares the screen for user guess.
     * @param v android.view.View
     */
    private void prepareGuessScreen(View v) {
        ((TextView) v.findViewById(R.id.roundView)).setText(
                game.getString(R.string.roundText) + game.getString(R.string.spacer) +
                        game.getPrefs().getInt(game.getString(R.string.round), 0));

        randomWordList = game.getRandomWordList();
        Collections.shuffle(randomWordList);

        ListView randomWordListView = (ListView) v.findViewById(R.id.randomWordListViewGuess);
        ArrayAdapter<String> wordList = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                randomWordList);

        randomWordListView.setEnabled(false);
        randomWordListView.setAdapter(wordList);
    }

    /**
     * Checks if the winner button was pressed.
     *
     * @param button android.widget.Button
     */
    private void checkForWin(Button button) {
        if (button.getText().equals(game.getPrefs().getString(
                game.getString(R.string.hiddenWord),
                game.getString(R.string.spacer)))) {
            game.win();
        } else {
            game.loose();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(randomWordButtonOne) ||
                v.equals(randomWordButtonTwo) ||
                v.equals(randomWordButtonThree)) {
            checkForWin((Button) v);
        }
    }
}