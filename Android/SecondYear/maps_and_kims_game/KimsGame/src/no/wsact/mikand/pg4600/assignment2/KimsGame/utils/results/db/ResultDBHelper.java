package no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import static no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results.ResultHandler.*;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.utils.results
 *
 * This class is a facade for SQLiteOpenHelper.
 *
 * @author Anders Mikkelsen
 * @version 14.05.15
 */
@SuppressWarnings("SameParameterValue")
public class ResultDBHelper extends SQLiteOpenHelper {
    public ResultDBHelper(Context resultHandler, String databaseName, CursorFactory cursorFactory, int databaseVersion) {
        super(resultHandler, databaseName, cursorFactory, databaseVersion);
    }

    /**
     * Creates the sql which defines the table.
     *
     * @return java.lang.String
     */
    private String buildCreationSQL() {
        return "CREATE TABLE " + TABLE_NAME + "(" +
                    HIGHSCORE_ID + " NUMBER PRIMARY KEY, " +
                    HIGHSCORE_NAME + " TEXT NOT NULL, " +
                    HIGHSCORE_ROUNDS + " NUMBER NOT NULL, " +
                    HIGHSCORE_POINTS + " NUMBER NOT NULL, " +
                    HIGHSCORE_TIMESTAMP + " DATE NOT NULL)";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(buildCreationSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
