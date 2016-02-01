package no.woact.mikand.ticTacToe.models.utils.gameManager;

import no.woact.mikand.ticTacToe.models.game.ImageContainer;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * This is a VO used for interaction between the AI and GameManager.
 *
 * @author anders
 * @version 18.02.15
 */
public class SelectionContainer {
    private final ImageContainer selection;
    private final int selectionX;
    private final int selectionY;

    /**
     * This standard Constructor finalizes the SelectionContainer.
     *
     * @param selection ImageContainer
     * @param selectionX int
     * @param selectionY int
     */
    public SelectionContainer(ImageContainer selection,
                              int selectionX, int selectionY) {
        this.selection = selection;
        this.selectionX = selectionX;
        this.selectionY = selectionY;
    }

    /**
     * Getter for ImageContainer.
     *
     * @return ImageContainer
     */
    public ImageContainer getSelection() {
        return selection;
    }

    /**
     * Getter for X.
     *
     * @return int
     */
    public int getSelectionX() {
        return selectionX;
    }

    /**
     * Getter for Y.
     *
     * @return int
     */
    public int getSelectionY() {
        return selectionY;
    }
}
