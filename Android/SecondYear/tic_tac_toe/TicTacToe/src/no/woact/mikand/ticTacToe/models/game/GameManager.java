package no.woact.mikand.ticTacToe.models.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import no.woact.mikand.ticTacToe.R;
import no.woact.mikand.ticTacToe.controllers.activities.result.Result;
import no.woact.mikand.ticTacToe.models.utils.gameManager.GameAi;
import no.woact.mikand.ticTacToe.models.utils.gameManager.SelectionContainer;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This class controls the actual flow of the game and is initialized from the
 * Game class.
 *
 * @author anders
 * @version 09.02.15
 */
public class GameManager implements View.OnClickListener {
    private final Activity gameActivity;

    private final TextView playerOne;
    private final TextView playerTwo;

    private final ImageContainer[][] gameBoard;
    private final int gameBoardCapacity;
    private int gameBoardPiecesSelected;

    private ImageContainer selection;
    private int selectionX, selectionY;

    private Button playerDoneButton;

    private int currentPlayer;
    private String result;

    private GameAi computerPlayer;

    /**
     * This constructor receives the initialized views its need from Game and
     * does some finally prepratory initialization before it begins.
     *
     * @param gameActivity Activity
     * @param gameBoard ImageContainer[][]
     * @param playerOne TextView
     * @param playerTwo TextView
     */
    public GameManager(Activity gameActivity, Bundle inState,
                       ImageContainer[][] gameBoard,
                       TextView playerOne, TextView playerTwo) {
        this.gameActivity = gameActivity;

        this.gameBoard = gameBoard;
        int gameBoardSizeY = gameBoard.length;
        int gameBoardSizeX = gameBoard[0].length;
        ImageContainer centerPiece =
                gameBoard[gameBoardSizeY / 2][gameBoardSizeX / 2];
        gameBoardCapacity = gameBoardSizeX * gameBoardSizeY;

        if (inState != null) {
            gameBoardPiecesSelected = inState.getInt("gameTurn");
        } else {
            gameBoardPiecesSelected = 0;
        }

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;

        currentPlayer = 1;

        initializeGameBoard();
        initializeButton();

        if (playerTwo.getText().toString().equals(
                gameActivity.getString(R.string.computerPlayer))) {
            computerPlayer = new GameAi(gameActivity,
                                        gameBoard,
                                        gameBoardSizeY, gameBoardSizeX,
                                        centerPiece);
        }
    }

    /**
     * Sets the OnClickListener for all Squares to this GameManagerinstance.
     */
    private void initializeGameBoard() {
        for (ImageContainer[] image : gameBoard) {
            for (ImageContainer anImage : image) {
                anImage.getImage().setOnClickListener(this);
            }
        }
    }

    /**
     * This method fins and sets the playerDoneButton with listeners and
     * visibilty.
     */
    private void initializeButton() {
        playerDoneButton =
                (Button) gameActivity.findViewById(R.id.gamePlayerDoneButton);
        playerDoneButton.setOnClickListener(this);
        playerDoneButton.setVisibility(View.GONE);
    }

    /**
     * This method receives clickevent on all squares on the gameboard. It will
     * then set the approriate .png for that square according to the current
     * player. So you can select the correct one before pressing done.
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.gamePlayerDoneButton) {
            gameBoardPiecesSelected++;

            if (checkForWinner()) {
                selection = null;
                selectionX = 0;
                selectionY = 0;

                goToResultScreen();
            } else {
                selection = null;
                selectionX = 0;
                selectionY = 0;

                currentPlayer = currentPlayer == 1 ? 2 : 1;

                switch (currentPlayer) {
                    case 1:
                        swapPlayerTurn(playerOne, playerTwo);
                        break;
                    case 2:
                        swapPlayerTurn(playerTwo, playerOne);

                        if (playerTwo.getText().toString()
                                .equals(gameActivity.getString(
                                        R.string.computerPlayer))) {
                            SelectionContainer selectionContainer =
                                   computerPlayer.doComputerMove(currentPlayer);

                            selection = selectionContainer.getSelection();
                            selectionY = selectionContainer.getSelectionY();
                            selectionX = selectionContainer.getSelectionX();

                            gameBoardPiecesSelected++;

                            if (checkForWinner()) {
                                goToResultScreen();
                            } else {
                                currentPlayer = currentPlayer == 1 ? 2 : 1;
                                swapPlayerTurn(playerOne, playerTwo);
                            }
                        }
                        break;
                }
            }
        } else {
            showCurrentSelection(v);
        }
    }

    /**
     * Sets visual representation of playerSwitch.
     *
     * @param first TextView
     * @param second TextView
     */
    private void swapPlayerTurn(TextView first, TextView second) {
        first.setTextColor(gameActivity.getResources()
                .getColor(R.color.green));

        second.setTextColor(gameActivity.getResources()
                .getColor(R.color.white));

        playerDoneButton.setVisibility(View.GONE);

        selection = null;
        selectionX = 0;
        selectionY = 0;
    }

    /**
     * This method checks the representation of a square.
     *
     * @param y int
     * @param x int
     * @param symbol int
     * @return boolean
     */
    private boolean checkPieceRep(int y, int x, int symbol) {
        return gameBoard[y][x].getRepresentation().equals(
                gameActivity.getString(symbol));
    }

    /**
     * Makes selection on gameBoard.
     *
     * @param y int
     * @param x int
     */
    private void doSelection(int y, int x) {
        selection = gameBoard[y][x];
        selectionY = y;
        selectionX = x;

        Log.i("Selection:", "Done: " + currentPlayer + " Y: " + y + " X: " + x);

        selection.setSelected(currentPlayer, true);
    }

    /**
     * Checks for player selection and returns a visual representation.
     *
     * @param v View
     */
    private void showCurrentSelection(View v) {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (v.getId() == gameBoard[i][j].getImage().getId()) {
                    Log.i(gameActivity.getString(
                            R.string.LOGcurrentlySelectedImage),
                            "" + v.getId());
                    if (!gameBoard[i][j].isSelected()) {
                        if (selection != null) {
                            selection.setSelected(0, false);
                        }

                        playerDoneButton.setVisibility(View.VISIBLE);

                        doSelection(i, j);
                    }
                }
            }
        }
    }

    /**
     * This method checks all possible variants for winning for both players and
     * returns the result. Only checks the row, column and diagonal that is
     * relevant.
     *
     * @return boolean
     */
    private boolean checkForWinner() {
        int horizontal = 0, vertical = 0;
        int winCriteria = gameBoard.length;
        int playerSym = 0;

        String looser;
        String winner = looser = gameActivity
                .getString(R.string.emptyString);

        // presets winner / looser dependent on player
        switch (currentPlayer) {
            case 1:
                playerSym = R.string.playerOneSymbol;
                winner = playerOne.getText().toString();
                looser = playerTwo.getText().toString();
                break;
            case 2:
                playerSym = R.string.playerTwoSymbol;
                winner = playerTwo.getText().toString();
                looser = playerOne.getText().toString();
                break;
        }

        // check horizontal/vertical/diagonal victory
        int diagonalUp = 0, diagonalDown = 0;

        for (int i = 0; i < gameBoard[selectionY].length; i++) {
            if (checkPieceRep(selectionY, i, playerSym)) horizontal++;
            if (checkPieceRep(i, selectionX, playerSym)) vertical++;
            if (checkPieceRep(i, i, playerSym)) diagonalDown++;
            if (checkPieceRep(gameBoard.length - 1 - i, i, playerSym))
                diagonalUp++;
        }

        // checks winner, checks for draw if no winner
        if (horizontal == winCriteria || vertical == winCriteria ||
                (diagonalUp == winCriteria || diagonalDown == winCriteria)) {
            result = buildResultString(winner, looser,
                    gameActivity.getString(R.string.winText));

            return true;
        } else if (gameBoardPiecesSelected == gameBoardCapacity) {
            result = buildResultString(winner, looser,
                    gameActivity.getString(R.string.drawText));

            return true;
        }

        return false;
    }

    /**
     * Helpermethod for building resultString for checkForWinner()
     *
     * @param first String
     * @param second String
     * @param res String
     * @return String
     */
    private String buildResultString(String first, String second, String res) {
        return first + res + second + "!";
    }

    /**
     * This method builds an intent with playernames and results and fires up
     * the Result Activity.
     */
    private void goToResultScreen() {
        Intent i = new Intent(gameActivity, Result.class);

        i.putExtra("playerOne", playerOne.getText());
        i.putExtra("playerTwo", playerTwo.getText());

        Time now = new Time();
        now.setToNow();

        i.putExtra("results", now.format(
                gameActivity.getString(R.string.dateFormatEncode))
                + "\n" + result);

        gameActivity.finish();
        gameActivity.startActivity(i);
    }

    /**
     * Returns a representation of the game turn.
     *
     * @return int
     */
    public int getGameBoardPiecesSelected() {
        return gameBoardPiecesSelected;
    }

    /**
     * Returns the current selection in the game.
     *
     * @return SelectionContainer
     */
    public SelectionContainer getSelectionContainer() {
        return new SelectionContainer(selection, selectionX, selectionY);
    }

    /**
     * This allows the Game to apply a selection after rotation.
     *
     * @param selection SelectionContainer
     */
    public void setSelectionContainer(SelectionContainer selection) {
        this.selection = selection.getSelection();
        this.selectionY = selection.getSelectionY();
        this.selectionX = selection.getSelectionX();
    }

    /**
     * Returns the player that controls the turn.
     *
     * @return int
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the current player for reconstructions.
     *
     * @param currentPlayer int
     */
    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
