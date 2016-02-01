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

import java.util.List;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.game.fragments
 *
 * This class defines the memorization fragment.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
@SuppressWarnings("ConstantConditions")
public class MemorizeFragment extends Fragment implements View.OnClickListener {
    private Game game;
    private Button readyButton;

    private List<String> randomWordList;
    private ArrayAdapter<String> wordList;
    private ListView randomWordListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.memorize, container, false);
        game = (Game) getActivity();

        initializeButtons(v);
        prepareMemorizationScreen(v);

        return v;
    }

    private void initializeButtons(View v) {
        readyButton = (Button) v.findViewById(R.id.readyButton);
        readyButton.setOnClickListener(this);
    }

    /**
     * Prepares the screen for user memorization
     * @param v android.view.View
     */
    private void prepareMemorizationScreen(View v) {
        ((TextView) v.findViewById(R.id.roundTextView)).setText(
                game.getString(R.string.roundText) + game.getString(R.string.spacer) +
                        game.getPrefs().getInt(game.getString(R.string.round), 0));

        randomWordList = game.getRandomWordList();
        randomWordListView = (ListView) v.findViewById(R.id.randomWordListView);
        wordList = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                randomWordList);

        randomWordListView.setEnabled(false);
        randomWordListView.setAdapter(wordList);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(readyButton)) {
            hideRandomWord();
            game.guessDone();
        }
    }

    /**
     * Removes a random word from the list.
     */
    private void hideRandomWord() {
        game.getPrefs().edit().putString(
                game.getString(R.string.hiddenWord),
                randomWordList.remove((int) (Math.random() * randomWordList.size()))).commit();
        wordList.notifyDataSetChanged();
        randomWordListView.setSelected(false);
    }
}