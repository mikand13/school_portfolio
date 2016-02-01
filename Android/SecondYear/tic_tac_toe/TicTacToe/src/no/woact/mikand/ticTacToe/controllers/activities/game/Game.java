package no.woact.mikand.ticTacToe.controllers.activities.game;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import no.woact.mikand.ticTacToe.R;
import no.woact.mikand.ticTacToe.models.game.GameManager;
import no.woact.mikand.ticTacToe.models.game.ImageContainer;
import no.woact.mikand.ticTacToe.models.utils.gameManager.SelectionContainer;
import no.woact.mikand.ticTacToe.models.utils.pause.SwipePause;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This class defines the layour for the game, and initializes a GameManager
 * which controls the flow of the game as an OnClickListener. It also works as a
 * OnTouchListener to redirect to pause if swipes are detected.
 *
 * @author anders
 * @version 09.02.15
 */
public class Game extends Activity implements View.OnTouchListener {
    private ImageContainer[][] gameBoard;
    @SuppressWarnings("FieldCanBeLocal")
    private final int GAME_SIZE = 3;

    private TextView playerOne, playerTwo;

    private GestureDetector pauseDetector;
    private SwipePause pauseListener;

    private GameManager gameManager;

    /**
     * Standard constructor for initializing the layout and starting the
     * GameManager.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        initializeGame();
        initializePlayerNames();
        initializeSquares();

        gameManager = new GameManager(this, savedInstanceState,
                gameBoard,
                playerOne, playerTwo);

        initializePauseListener();
    }

    /**
     * This method clears up any issues with rotation.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String gameState = savedInstanceState.getString("gameState");
        char playerOneChar = getString(R.string.playerOneSymbol).charAt(0);
        char playerTwoChar = getString(R.string.playerTwoSymbol).charAt(0);

        Log.i("GameState", gameState);
        Log.i("playerOneChar", "" + playerOneChar);
        Log.i("playerTwoChar", "" + playerTwoChar);

        int rowCounter = 0;
        for (int i = 0; i < gameState.length(); i++) {
            if (i % GAME_SIZE == 0 && i != 0) {
                rowCounter++;
            }

            if (gameState.charAt(i) == playerOneChar) {
                Log.i("PrintingOneChar in: ",
                        "Y: " + rowCounter + " X: " + i % GAME_SIZE);
                gameBoard[rowCounter][i % GAME_SIZE].setSelected(1, true);
            } else if (gameState.charAt(i) == playerTwoChar) {
                Log.i("PrintingTwoChar in: ",
                        "Y: " + rowCounter + " X: " + i % GAME_SIZE);
                gameBoard[rowCounter][i % GAME_SIZE].setSelected(2, true);
            }
        }

        int gameStateAsInt = savedInstanceState.getInt("pauseState");

        playerOne.setVisibility(gameStateAsInt);
        playerTwo.setVisibility(gameStateAsInt);

        findViewById(R.id.gamePlayerDoneButton).setVisibility(gameStateAsInt);
        findViewById(R.id.gamePauseText).setVisibility(gameStateAsInt);
        findViewById(R.id.gameGameBoard).setVisibility(gameStateAsInt);

        if (gameStateAsInt != View.GONE) {
            findViewById(R.id.gamePlayerDoneButton)
                    .setVisibility(savedInstanceState.getInt("doneVisible"));
        } else {
            pauseListener.regeneratePauseFragment();
        }

        if (savedInstanceState.getBoolean("selection")) {
            int y = savedInstanceState.getInt("selectionY");
            int x = savedInstanceState.getInt("selectionX");

            gameManager.setSelectionContainer(
                    new SelectionContainer(gameBoard[y][x], x, y));
        }

        gameManager.setCurrentPlayer(
                savedInstanceState.getInt("currentPlayer"));

        switch (savedInstanceState.getInt("currentPlayer")) {
            case 1:
                playerOne.setTextColor(getResources().getColor(R.color.green));
                playerTwo.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                playerOne.setTextColor(getResources().getColor(R.color.white));
                playerTwo.setTextColor(getResources().getColor(R.color.green));
                break;
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Sets the playernames and sets color green for the first player.
     */
    private void initializeGame() {
        playerOne = ((TextView) findViewById(R.id.playerOneNameGame));
        playerTwo = ((TextView) findViewById(R.id.playerTwoNameGame));

        playerOne.setTextColor(getResources().getColor(R.color.green));
    }

    /**
     * Gets the playernames from the Intent.
     */
    private void initializePlayerNames() {
        playerOne.setText(getIntent().getExtras().getString("playerOne"));
        playerTwo.setText(getIntent().getExtras().getString("playerTwo"));
    }

    /**
     * Builds the gameboard with ImageViews
     */
    private void initializeSquares() {
        gameBoard = new ImageContainer[GAME_SIZE][GAME_SIZE];

        int rIdCounter = R.id.upperLeftCenter;
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                gameBoard[i][j] = new ImageContainer(
                        (ImageView) findViewById(rIdCounter++), this);
            }
        }
    }

    /**
     * Makes a GestureDetector and tells all views in the activity to listen to
     * the OnTouchListener so any swipe will generate a pauseevent.
     */
    private void initializePauseListener() {
        pauseListener = new SwipePause(this);
        pauseDetector = new GestureDetector(this, pauseListener);

        findViewById(android.R.id.content)
                .getRootView().setOnTouchListener(this);

        for (ImageContainer[] image : gameBoard) {
            for (ImageContainer ic : image) {
                ic.getImage().setOnTouchListener(this);
            }
        }
    }

    /**
     * Redirects touchevents to the SwipePause object encapsulated in
     * pauseDetector
     *
     * @param v View
     * @param event MotionEvent
     * @return boolean
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return pauseDetector.onTouchEvent(event);
    }

    /**
     * Saves the gameBoard for any rotates, does not save when game over.
     *
     * @param outState Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String gameState = "";

        for (ImageContainer[] row : gameBoard) {
            for (ImageContainer square : row) {
                gameState += square.getRepresentation();
            }
        }

        if (gameManager.getGameBoardPiecesSelected() < GAME_SIZE * GAME_SIZE) {
            outState.putString("gameState", gameState);
            outState.putInt("gameTurn",
                    gameManager.getGameBoardPiecesSelected());
        }

        if (pauseListener.isGamePaused()) {
            outState.putInt("pauseState", View.GONE);
            getFragmentManager().beginTransaction().remove(
                    getFragmentManager().findFragmentByTag(
                    getString(R.string.pauseFragmentname))).commit();
        } else {
            outState.putInt("pauseState", View.VISIBLE);
        }

        outState.putInt("doneVisible",
                findViewById(R.id.gamePlayerDoneButton).getVisibility());

        if (gameManager.getSelectionContainer().getSelection() != null) {
            outState.putBoolean("selection", true);
            outState.putInt("selectionX", gameManager.getSelectionContainer()
                    .getSelectionX());
            outState.putInt("selectionY", gameManager.getSelectionContainer()
                    .getSelectionY());
        } else {
            outState.putBoolean("selection", false);
        }

        outState.putInt("currentPlayer", gameManager.getCurrentPlayer());

        super.onSaveInstanceState(outState);
    }
}