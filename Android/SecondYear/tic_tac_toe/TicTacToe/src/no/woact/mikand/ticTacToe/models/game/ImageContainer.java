package no.woact.mikand.ticTacToe.models.game;

import android.content.Context;
import android.widget.ImageView;
import no.woact.mikand.ticTacToe.R;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * VO for each square of the game board.
 *
 * @author anders
 * @version 18.02.15
 */
public class ImageContainer {
    private boolean selected;
    private final ImageView image;
    private String representation;

    private final Context context;

    /**
     * Sets the square to unselected, empty .png and a supplied ImageView.
     *
     * @param image ImageView
     */
    public ImageContainer(ImageView image, Context context) {
        this.image = image;
        this.context = context;
        selected = false;
        representation = " ";
    }

    /**
     * This method is called to see if the square can be selected by a player.
     *
     * @return boolean
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * This method sets the .png based on playernumber and selection. Also
     * handles deselection.
     *
     * @param i int
     * @param selected boolean
     */
    public void setSelected(int i, boolean selected) {
        switch (i) {
            case 0:
                image.setImageResource(R.drawable.empty);
                representation = context.getResources().getString(
                        R.string.emptySquare);
                break;
            case 1:
                image.setImageResource(R.drawable.cross);
                representation = context.getResources().getString(
                        R.string.playerOneSymbol);
                break;
            case 2:
                image.setImageResource(R.drawable.circle);
                representation = context.getResources().getString(
                        R.string.playerTwoSymbol);
                break;
            default:
                break;
        }

        this.selected = selected;
    }

    /**
     * Gets the enclosed ImageView.
     *
     * @return ImageView
     */
    public ImageView getImage() {
        return image;
    }

    /**
     * Gets the representation for this square.
     *
     * @return String
     */
    public String getRepresentation() {
        return representation;
    }
}
