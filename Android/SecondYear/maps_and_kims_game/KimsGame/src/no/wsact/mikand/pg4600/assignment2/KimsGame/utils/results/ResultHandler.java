package no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import no.wsact.mikand.pg4600.assignment2.KimsGame.R;
import no.wsact.mikand.pg4600.assignment2.KimsGame.results.Results;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.db.OnResultDBReadyListener;
import no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.db.ResultDBHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results
 *
 * This class is SQLite Adapter for the Results Database.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public class ResultHandler {
    private final Results parent;

    // DB CONFIG //
    private static final String DATABASE_NAME = "KimsGameDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "high_scores";

    public static final String HIGHSCORE_ID = "id";
    public static final String HIGHSCORE_NAME = "name";
    public static final String HIGHSCORE_ROUNDS = "rounds";
    public static final String HIGHSCORE_POINTS = "points";
    public static final String HIGHSCORE_TIMESTAMP = "created_at";
    // DB CONFIG //

    private SQLiteDatabase sqLiteDatabase;

    public ResultHandler(Results parent) {
        this.parent = parent;
    }

    public ResultHandler open(OnResultDBReadyListener onResultDBReadyListener) {
        new DBFetcher(onResultDBReadyListener).execute();
        return this;
    }

    /**
     * Gets all highscores stored.
     *
     * @param orderBy java.lang.String
     * @return List of Result
     */
    public List<Result> getHighScores(String orderBy) {
        return getHighScores(0, orderBy);
    }

    /**
     * Gets a number of highscores.
     *
     * @param numberOfHighScores java.lang.Integer
     * @param orderBy java.lang.String
     * @return List of Result
     */
    public List<Result> getHighScores(int numberOfHighScores, String orderBy) {
        List<Result> allHighScores = buildHighScoresList(
                sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, desc(orderBy)));

        if (numberOfHighScores == 0) {
            return allHighScores;
        } else {
            if (allHighScores.size() < numberOfHighScores) {
                return allHighScores;
            }

            return allHighScores.subList(0, numberOfHighScores);
        }
    }

    /**
     * Sets param to descending order.
     *
     * @param orderBy java.lang.String
     * @return java.lang.String
     */
    private String desc(String orderBy) {
        return orderBy + " DESC";
    }

    /**
     * Checks wether a result is a new highscore.
     *
     * @param result Result
     * @return java.lang.Boolean
     */
    public boolean checkForNewHighScore(Result result) {
        List<Result> highScores = getHighScores(Results.MAX_NUMBER_OF_HIGHSCORES, HIGHSCORE_ROUNDS);

        if (highScores.size() < Results.MAX_NUMBER_OF_HIGHSCORES) {
            return true;
        } else {
            for (Result r : highScores) {
                if (r.getRoundsCompleted() < result.getRoundsCompleted()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Creates a List of Result from the database.
     *
     * @param cursor android.database.Cursor
     * @return List of Result
     */
    private List<Result> buildHighScoresList(Cursor cursor) {
        ArrayList<Result> highScores = new ArrayList<Result>();

        while (cursor.moveToNext()) {
            highScores.add(new Result(cursor.getInt(cursor.getColumnIndex(HIGHSCORE_ID)),
                            cursor.getString(cursor.getColumnIndex(HIGHSCORE_NAME)),
                            cursor.getInt(cursor.getColumnIndex(HIGHSCORE_ROUNDS)),
                            cursor.getInt(cursor.getColumnIndex(HIGHSCORE_POINTS)),
                            Timestamp.valueOf(
                                    cursor.getString(cursor.getColumnIndex(HIGHSCORE_TIMESTAMP)))));
        }

        return highScores;
    }

    /**
     * Adds a Result to the database.
     *
     * @param highScore Result
     * @return java.lang.Boolean
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean addHighScore(Result highScore) {
        try {
            sqLiteDatabase.insertOrThrow(
                    TABLE_NAME, null, buildContentValuesForHighScore(highScore));

            trimTable();
            Log.d(parent.getString(R.string.resultHandler), parent.getString(R.string.resultAdded));
        } catch (SQLException sqe) {
            return false;
        }

        return true;
    }

    /**
     * Reduces table size to max highscore.
     */
    private void trimTable() {
        List<Result> results = getHighScores(HIGHSCORE_ROUNDS);

        if (results.size() > Results.MAX_NUMBER_OF_HIGHSCORES) {
            Iterator<Result> it = results.listIterator(Results.MAX_NUMBER_OF_HIGHSCORES);

            while (it.hasNext()) {
                Result result = it.next();

                sqLiteDatabase.delete(
                        TABLE_NAME,
                        parent.getString(R.string.idWhereClause),
                        new String[] {String.valueOf(result.get_id_())});
                Log.d(parent.getString(R.string.resultHandler),
                        parent.getString(R.string.removed) + result);
            }
        }
    }

    /**
     * Creates a ContentValues to match a Result.
     *
     * @param highScore Result
     * @return android.content.ContentValues
     */
    private ContentValues buildContentValuesForHighScore(Result highScore) {
        ContentValues row = new ContentValues();
        row.put(HIGHSCORE_NAME, highScore.getName());
        row.put(HIGHSCORE_ROUNDS, highScore.getRoundsCompleted());
        row.put(HIGHSCORE_POINTS, highScore.getPoints());
        row.put(HIGHSCORE_TIMESTAMP, highScore.getTimestamp().toString());

        return row;
    }

    private class DBFetcher extends AsyncTask<Void, Void, Void> {
        private final OnResultDBReadyListener onResultDBReadyListener;

        public DBFetcher(OnResultDBReadyListener onResultDBReadyListener) {
            this.onResultDBReadyListener = onResultDBReadyListener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ResultDBHelper resultDBHelper =
                    new ResultDBHelper(parent, DATABASE_NAME, null, DATABASE_VERSION);
            sqLiteDatabase = resultDBHelper.getWritableDatabase();
            onResultDBReadyListener.onResultDbReady();

            return null;
        }
    }
}
