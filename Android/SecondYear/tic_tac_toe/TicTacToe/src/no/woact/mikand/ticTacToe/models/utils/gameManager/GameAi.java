package no.woact.mikand.ticTacToe.models.utils.gameManager;

import android.app.Activity;
import android.util.Log;
import no.woact.mikand.ticTacToe.R;
import no.woact.mikand.ticTacToe.models.game.ImageContainer;

import java.util.ArrayList;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * Helperclass for controlling an AI.
 *
 * @author anders
 * @version 18.02.15
 */
public class GameAi {
    private final Activity gameActivity;

    private final ImageContainer[][] gameBoard;
    private final int gameBoardSizeY;
    private final int gameBoardSizeX;
    private int currentPlayer;
    private final ImageContainer centerPiece;

    private SelectionContainer selection;

    /**
     * This constructor initializes the GameAi based on the GameManagers input.
     *
     * @param gameActivity Activity
     * @param gameBoard ImageContainer[][]
     * @param gameBoardSizeY int
     * @param gameBoardSizeX int
     * @param centerPiece ImageContainer
     */
    public GameAi(Activity gameActivity,
                  ImageContainer[][] gameBoard,
                  int gameBoardSizeY, int gameBoardSizeX,
                  ImageContainer centerPiece) {
        this.gameActivity = gameActivity;
        this.gameBoard = gameBoard;
        this.gameBoardSizeY = gameBoardSizeY;
        this.gameBoardSizeX = gameBoardSizeX;

        this.centerPiece = centerPiece;
    }

    /**
     * This method activates the AI for a move.
     *
     * @return SelectionContainer
     */
    public SelectionContainer doComputerMove(int currentPlayer) {
        this.currentPlayer = currentPlayer;

        if (centerPiece.getRepresentation().equals(gameActivity.getString(
                R.string.emptySquare))) {
            doSelection(gameBoardSizeY / 2, gameBoardSizeX / 2);

            return selection;
        }

        if (isComputerWinMovePossible()) return selection;
        if (isBlockMovePossible()) return selection;
        if (placeOppositeCorner()) return selection;
        if (doubleOppositeCorners()) return selection;
        if (availibleCorner()) return selection;
        if (availibleEdges()) return selection;

        doRandomMove(); // hail mary if all else fails

        return selection;
    }

    /**
     * Prevent double oppositefork.
     *
     * @return boolean
     */
    private boolean doubleOppositeCorners() {
        boolean doSide = false;

        if (gameBoard[0][0].getRepresentation()
                .equals(gameActivity.getString(R.string.playerOneSymbol)) &&
                gameBoard[gameBoardSizeY - 1][gameBoardSizeX - 1]
                        .getRepresentation().equals(
                        gameActivity.getString(R.string.playerOneSymbol)) ||
                gameBoard[0][gameBoardSizeY - 1].getRepresentation().equals(
                        gameActivity.getString(R.string.playerOneSymbol)) &&
                        gameBoard[gameBoardSizeY - 1][0].getRepresentation()
                                .equals(
                                        gameActivity.getString(
                                                R.string.playerOneSymbol))) {
            doSide = true;
        }

        return doSide && availibleEdges();
    }

    /**
     * This method checks for any availible edges and picks one at random.
     *
     * @return boolean
     */
    private boolean availibleEdges() {
        ArrayList<SelectionContainer> availibleEdges = new ArrayList<>();

        if (checkAvailibleCorner(gameBoardSizeY / 2, 0))
            availibleEdges.add(new SelectionContainer(
                    gameBoard[gameBoardSizeY / 2][0],
                    gameBoardSizeY / 2, 0));
        if (checkAvailibleCorner(gameBoardSizeY - 1, gameBoardSizeX / 2))
            availibleEdges.add(new SelectionContainer(
                    gameBoard[gameBoardSizeY - 1][gameBoardSizeX / 2],
                    gameBoardSizeY - 1, gameBoardSizeX / 2));
        if (checkAvailibleCorner(0, gameBoardSizeX / 2))
            availibleEdges.add(new SelectionContainer(
                    gameBoard[0][gameBoardSizeX / 2], 0,
                    gameBoardSizeX / 2));
        if (checkAvailibleCorner(gameBoardSizeY / 2, gameBoardSizeX / 2))
            availibleEdges
                    .add(new SelectionContainer(
                            gameBoard[gameBoardSizeY / 2]
                                    [gameBoardSizeX / 2],
                            gameBoardSizeY / 2, gameBoardSizeX / 2));

        if (availibleEdges.size() > 0) {
            SelectionContainer selectee =
                    availibleEdges
                            .get((int) (Math.random()
                                    * availibleEdges.size()));

            doSelection(selectee.getSelectionY(), selectee.getSelectionX());

            return true;
        }

        return false;
    }

    /**
     * This method checks for any availible corners and picks one at random,
     * unless there is a cornerfork.
     *
     * @return boolean
     */
    private boolean availibleCorner() {
        if (checkForAdjacentSquareFork()) return true;

        ArrayList<SelectionContainer> availibleCorners = new ArrayList<>();

        if (checkAvailibleCorner(0, 0))
            availibleCorners.add(new SelectionContainer(gameBoard[0][0], 0, 0));
        if (checkAvailibleCorner(gameBoardSizeY - 1, 0))
            availibleCorners.add(new SelectionContainer(
                    gameBoard[gameBoardSizeY - 1][0], gameBoardSizeY - 1, 0));
        if (checkAvailibleCorner(0, gameBoardSizeX - 1))
            availibleCorners.add(new SelectionContainer(
                    gameBoard[0][gameBoardSizeX - 1], 0, gameBoardSizeX - 1));
        if (checkAvailibleCorner(gameBoardSizeY - 1, gameBoardSizeX - 1))
            availibleCorners
                    .add(new SelectionContainer(
                            gameBoard[gameBoardSizeY - 1][gameBoardSizeX - 1],
                            gameBoardSizeY -1, gameBoardSizeX - 1));

        if (availibleCorners.size() > 0) {
            SelectionContainer selectee =
                    availibleCorners
                            .get((int) (Math.random()
                                    * availibleCorners.size()));

            doSelection(selectee.getSelectionY(), selectee.getSelectionX());

            return true;
        }

        return false;
    }

    /**
     * Checks the board for the adjacentSquareFork.
     *
     * @return boolean
     */
    private boolean checkForAdjacentSquareFork() {
        if (checkSquareRepresentationForSymbol(
                gameBoardSizeY / 2, gameBoardSizeX / 2,
                R.string.playerTwoSymbol)) {
            if (checkSquareRepresentationForSymbol(
                    0, gameBoardSizeX / 2, R.string.playerOneSymbol) &&
                    checkSquareRepresentationForSymbol(
                            gameBoardSizeY / 2, 0, R.string.playerOneSymbol)) {
                if (checkSquareRepresentationForSymbol(
                        0, 0, R.string.emptySquare)) {
                    doSelection(0, 0);

                    return true;
                }
            } else if (
                    checkSquareRepresentationForSymbol(
                            gameBoardSizeY - 1, gameBoardSizeX / 2,
                            R.string.playerOneSymbol) &&
                            checkSquareRepresentationForSymbol(
                                    gameBoardSizeY / 2, 0,
                                    R.string.playerOneSymbol)) {
                if (checkSquareRepresentationForSymbol(
                        0, 0, R.string.emptySquare)) {
                    doSelection(gameBoardSizeY - 1, 0);

                    return true;
                }
            } else if (
                    checkSquareRepresentationForSymbol(
                            0, gameBoardSizeX / 2,
                            R.string.playerOneSymbol) &&
                            checkSquareRepresentationForSymbol(
                                    gameBoardSizeY / 2, gameBoardSizeX - 1,
                                    R.string.playerOneSymbol)) {
                if (checkSquareRepresentationForSymbol(
                        0, 0, R.string.emptySquare)) {
                    doSelection(0, gameBoardSizeX - 1);

                    return true;
                }
            } else if (
                    checkSquareRepresentationForSymbol(
                            gameBoardSizeY - 1, gameBoardSizeX / 2,
                            R.string.playerOneSymbol) &&
                            checkSquareRepresentationForSymbol(
                                    gameBoardSizeY / 2, gameBoardSizeX - 1,
                                    R.string.playerOneSymbol)) {
                if (checkSquareRepresentationForSymbol(
                        0, 0, R.string.emptySquare)) {
                    doSelection(gameBoardSizeY - 1, gameBoardSizeX - 1);

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a square has a specific representation.
     *
     * @param y int
     * @param x int
     * @param symbol int
     *
     * @return boolean
     */
    private boolean checkSquareRepresentationForSymbol(int y, int x, int symbol) {
        return gameBoard[y][x]
                .getRepresentation().equals(
                gameActivity.getString(symbol));
    }

    /**
     * This method checks if a square defined by y and x is availible.
     *
     * @param y int
     * @param x int
     * @return boolean
     */
    private boolean checkAvailibleCorner(int y, int x) {
        return checkSquareRepresentationForSymbol(y, x, R.string.emptySquare);
    }

    /**
     * This method checks if a player has placed a piece in a corner and will
     * counter it.
     *
     * @return boolean
     */
    private boolean placeOppositeCorner() {
        String oneSymbol = gameActivity.getString(R.string.playerOneSymbol);
        String emptySymbol = gameActivity.getString(R.string.emptySquare);

        if (checkOpposables(0, 0, gameBoardSizeY - 1, gameBoardSizeX - 1,
                oneSymbol, emptySymbol)) {
            doSelection(gameBoardSizeY - 1, gameBoardSizeX - 1);
            return true;
        } else if (checkOpposables(0, 0, gameBoardSizeY - 1, gameBoardSizeX - 1,
                emptySymbol, oneSymbol)) {
            doSelection(0, 0);
            return true;
        } else if (checkOpposables(gameBoardSizeY - 1, 0, 0, gameBoardSizeX - 1,
                oneSymbol, emptySymbol)) {
            doSelection(0, gameBoardSizeX - 1);
            return true;
        } else if (checkOpposables(gameBoardSizeY - 1, 0, 0, gameBoardSizeX - 1,
                emptySymbol, oneSymbol)) {
            doSelection(gameBoardSizeY - 1, 0);
            return true;
        }

        return false;
    }

    /**
     * Checks opposable corners.
     *
     * @param y int
     * @param x int
     * @param oppY int
     * @param oppX int
     * @param firstSym String
     * @param secondSym String
     * @return boolean
     */
    @SuppressWarnings("SameParameterValue")
    private boolean checkOpposables(int y, int x, int oppY, int oppX,
                                    String firstSym, String secondSym) {
        return gameBoard[y][x].getRepresentation().equals(firstSym) &&
                gameBoard[oppY][oppX].getRepresentation().equals(secondSym);

    }

    /**
     * Do a random computermove if all other options are unavailible.
     */
    private void doRandomMove() {
        // check if center availible
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j].getRepresentation().equals(
                        gameActivity.getString(R.string.emptySquare))) {
                    doSelection(i, j);

                    return;
                }
            }
        }
    }

    /**
     * Checks to see if computer can block player.
     *
     * @return boolean
     */
    private boolean isBlockMovePossible() {
        int oneSym = R.string.playerOneSymbol;
        String emptySym = gameActivity.getString(R.string.emptySquare);
        boolean diagBlock = true;

        if (centerPiece.getRepresentation().equals(
                gameActivity.getString(oneSym))) {
            if (isDiagWinningMove(0, oneSym)) {
                if (gameBoard[0][0].getRepresentation().equals(emptySym)) {
                    doSelection(0, 0);
                } else if (gameBoard[gameBoardSizeY - 1][gameBoardSizeY - 1]
                        .getRepresentation().equals(emptySym)) {
                    doSelection(gameBoardSizeY - 1, gameBoardSizeY - 1);
                } else {
                    diagBlock = false;
                }
            } else if (isDiagWinningMove(gameBoardSizeY - 1, oneSym)) {
                if (gameBoard[gameBoardSizeY - 1][0].getRepresentation()
                        .equals(emptySym)) {
                    doSelection(gameBoardSizeY - 1, 0);
                } else if (gameBoard[0][gameBoardSizeY - 1].getRepresentation()
                        .equals(emptySym)){
                    doSelection(0, gameBoardSizeY - 1);
                } else {
                    diagBlock = false;
                }
            } else {
                diagBlock = false;
            }
        } else {
            diagBlock = false;
        }

        Log.i("DiagblocK: ", "" + diagBlock);

        return diagBlock || isPlayerWinMovePossible();
    }

    /**
     * Used by computer to check horizontal/vertical block.
     *
     * @return boolean
     */
    private boolean isPlayerWinMovePossible() {
        int horizontal = 0, vertical = 0;
        int winCriteria = gameBoard.length - 1;

        // check horizontal/vertical victory
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j].getRepresentation().equals(
                        gameActivity.getString(R.string.emptySquare))) {
                    int l;
                    for (l = 0; l < gameBoard[i].length; l++) {
                        if (checkPieceRep(i, l, R.string.playerOneSymbol))
                            horizontal++;
                        if (checkPieceRep(l, j, R.string.playerOneSymbol))
                            vertical++;
                    }

                    if (horizontal == winCriteria || vertical == winCriteria) {
                        doSelection(i, j);
                        return true;
                    }

                    horizontal = vertical = 0;
                }
            }
        }

        Log.i("WinningValues: ", "Hor: " + horizontal + " Ver: " + vertical);
        return false;
    }

    /**
     * Calculate if a winmove is possible for computer.
     *
     * @return boolean
     */
    private boolean isComputerWinMovePossible() {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j].getRepresentation().equals(
                        gameActivity.getString(R.string.emptySquare))) {
                    if (isWinningComputerMove(i, j)) {
                        doSelection(i, j);

                        Log.i("Winmove detected: ", "Y: " + i + " X: " + j);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * This method checks if a computermove is a winning move.
     *
     * @param y int
     * @param x int
     * @return boolean
     */
    private boolean isWinningComputerMove(int y, int x) {
        int horizontal = 0, vertical = 0;
        int winCriteria = gameBoard.length - 1;

        // check horizontal/vertical/diagonal victory
        int diagonalUp = 0, diagonalDown = 0;

        for (int i = 0; i < gameBoard[y].length; i++) {
            if (checkPieceRep(y, i, R.string.playerTwoSymbol)) horizontal++;
            if (checkPieceRep(i, x, R.string.playerTwoSymbol)) vertical++;
            if (checkPieceRep(i, i, R.string.playerTwoSymbol)) diagonalDown++;
            if (checkPieceRep(gameBoard.length - 1 - i, i,
                    R.string.playerTwoSymbol))
                diagonalUp++;
        }

        Log.i("WinningValues: ", "Hor: " + horizontal + " Ver: " + vertical +
                " diagDown: " + diagonalDown + " diagUp: " + diagonalUp);

        if (diagonalDown == winCriteria) {
            if (!((y == 0 && x == 0) ||
                    (y == gameBoardSizeY - 1 && x == gameBoardSizeX - 1))) {
                diagonalDown = 0;
            } else {
                if (!(checkSquareRepresentationForSymbol(
                        0, 0, R.string.emptySquare) ||
                        checkSquareRepresentationForSymbol(
                                gameBoardSizeY - 1, gameBoardSizeX - 1,
                                R.string.emptySquare))) {
                    diagonalDown = 0;
                }
            }
        } else if (diagonalUp == winCriteria) {
            if (!((y == gameBoardSizeY - 1 && x == 0) ||
                    (y == 0 && x == gameBoardSizeX - 1))) {
                diagonalUp = 0;
            } else {
                if (!(checkSquareRepresentationForSymbol(gameBoardSizeY - 1, 0,
                        R.string.emptySquare) ||
                        checkSquareRepresentationForSymbol(
                                0, gameBoardSizeX - 1, R.string.emptySquare))) {
                    diagonalUp = 0;
                }
            }
        }

        Log.i("WinningValues: ", "Hor: " + horizontal + " Ver: " + vertical +
                " diagDown: " + diagonalDown + " diagUp: " + diagonalUp);

        return horizontal == winCriteria || vertical == winCriteria ||
                diagonalDown == winCriteria || diagonalUp == winCriteria;
    }

    /**
     * This method checks if a diagonal playermove is a winning move.
     *
     * @param y int
     * @return boolean
     */
    private boolean isDiagWinningMove(int y, int symbol) {
        int winCriteria = gameBoard.length - 1;

        // check diagonal victory
        int diagonalUp = 0, diagonalDown = 0;

        if (y == 0) {
            for (int i = 0; i < gameBoard[y].length; i++) {
                if (checkPieceRep(i, i, symbol)) diagonalDown++;
            }
        } else if (y == gameBoard.length - 1) {
            for (int i = 0; i < gameBoard[y].length; i++) {
                if (checkPieceRep(gameBoard.length - 1 - i, i, symbol))
                    diagonalUp++;
            }
        }

        return diagonalDown == winCriteria || diagonalUp == winCriteria;
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
        selection = new SelectionContainer(gameBoard[y][x], x, y);

        Log.i("Selection:", "Done: " + currentPlayer + " Y: " + y + " X: " + x);

        selection.getSelection().setSelected(currentPlayer, true);
    }
}
