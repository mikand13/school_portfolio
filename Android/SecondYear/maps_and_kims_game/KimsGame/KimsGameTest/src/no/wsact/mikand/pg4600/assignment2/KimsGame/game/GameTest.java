package no.wsact.mikand.pg4600.assignment2.KimsGame.game;

import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import no.wsact.mikand.pg4600.assignment2.KimsGame.game.fragments.MemorizeFragment;
import no.wsact.mikand.pg4600.assignment2.KimsGame.R;
import no.wsact.mikand.pg4600.assignment2.KimsGame.results.Results;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.game (tests)
 *
 * This class shows a few simple tests, to show I can implement testing. Didnt see it mentioned as
 * part of the assignment.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class GameTest extends ActivityInstrumentationTestCase2<Game> {
    private Game app;

    public GameTest() {
        super("no.wsact.mikand.pg4600.assignment2.KimsGame", Game.class);
    }

    @Override
    public void setUp() throws Exception {
        app = getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        app.finish();
    }

    public void testOnCreate() throws Exception {
        assertNotNull("MemorizeScreen should be operational!",
                app.getFragmentManager().findFragmentByTag(
                        app.getString(R.string.memorizerFragmentTag)));
    }

    @SuppressWarnings("ConstantConditions")
    public void testPrepareGuessScreen() throws Exception {
        app.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MemorizeFragment memorizeFragment = (MemorizeFragment)
                        app.getFragmentManager().findFragmentByTag(
                                app.getString(R.string.memorizerFragmentTag));

                Button button = (Button) memorizeFragment.getView().findViewById(R.id.readyButton);
                assertNotNull(button);
                assertTrue(button.hasOnClickListeners());
                button.performClick();
            }
        });
        Thread.sleep(2000);

        assertNull("Memory fragment should be null",
                app.getFragmentManager().findFragmentByTag(
                app.getString(R.string.memorizerFragmentTag)));
        assertNotNull("GuessScreen should be operational!",
                app.getFragmentManager().findFragmentByTag(
                app.getString(R.string.guessFragmentTag)));
    }

    public void testLoose() throws Exception {
        Instrumentation.ActivityMonitor activityMonitor =
                getInstrumentation().addMonitor(Results.class.getName(), null, true);
        app.loose();

        Thread.sleep(2000);

        assertEquals(1, activityMonitor.getHits());
        assertTrue(app.isFinishing() || app.isDestroyed());
    }

    public void testWin() throws Exception {
        app.win();

        Thread.sleep(2000);

        assertNull("Memory fragment should be operational!",
                app.getFragmentManager().findFragmentByTag(
                app.getString(R.string.guessFragmentTag)));
        assertNotNull("GuessScreen should be null!",
                app.getFragmentManager().findFragmentByTag(
                app.getString(R.string.memorizerFragmentTag)));
    }
}
