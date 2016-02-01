package no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results
 *
 * This class is a VO for a result after a game.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
public class Result {
    private final Integer _id_;
    private final String name;
    private final Integer roundsCompleted;
    private final Integer points;
    private final Timestamp timestamp;

    public Result(Integer id, String name, Integer roundsCompleted, Integer points, Timestamp timestamp) {
        this._id_ = id;
        this.name = name;
        this.roundsCompleted = roundsCompleted;
        this.points = points;
        this.timestamp = timestamp;
    }

    public Integer get_id_() {
        return _id_;
    }

    public String getName() {
        return name;
    }

    public Integer getRoundsCompleted() {
        return roundsCompleted;
    }

    public Integer getPoints() {
        return points;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return name + "\t\tRounds: " + roundsCompleted + " Score: " + points + "\n" +
                new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp);
    }
}
