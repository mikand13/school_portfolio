package no.wsact.mikand.pg4600.assignment2.KimsGame.results;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import no.wsact.mikand.pg4600.assignment2.KimsGame.R;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.results (tests)
 *
 * This class shows a few simple tests, to show I can implement testing. Didnt see it mentioned as
 * part of the assignment.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ResultsTest extends ActivityInstrumentationTestCase2<Results> {
    private Results results;

    public ResultsTest() {
        super("no.wsact.mikand.pg4600.assignment2.KimsGame", Results.class);
    }

    public void setUp() throws Exception {
        results = getActivity();
    }

    public void tearDown() throws Exception {
        results.finish();
    }

    public void testShowNewHighScoresFragment() throws Exception {
        Thread.sleep(2000);

        assertNotNull("NewHighscoresfragment should be operational!",
                results.getFragmentManager().findFragmentByTag(
                results.getString(R.string.newHighScoreFragmentTag)));
    }
}