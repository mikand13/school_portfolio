package no.woact.mikand.ticTacToe.controllers.activities.menu;

import android.annotation.TargetApi;
import android.test.suitebuilder.annotation.SmallTest;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;
import no.woact.mikand.ticTacToe.R;

/**
 * Created by Anders Mikkelsen on 09.02.15.
 *
 * Simple TestClass for the Menu Class.
 *
 * @author anders
 * @version 09.02.15
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MenuTest extends ActivityInstrumentationTestCase2<Menu> {
    private Menu testMenuActivity;

    /**
     * Initializes the class.
     */
    public MenuTest() {
        super(Menu.class);
    }

    /**
     * Sets up a testactivity.
     *
     * @throws Exception Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        testMenuActivity = getActivity();

        testPreConditionsMenu();
    }

    /**
     * Verifies that activity is availible.
     *
     * @throws Exception Exception
     */
    private void testPreConditionsMenu() throws Exception {
        assertNotNull("testMenuActivity is null", testMenuActivity);
    }

    /**
     * Verfies correctness of TicTacToeLogo
     *
     * @throws Exception Exception
     */
    @SmallTest
    public void testMenuActivity_testTicTacToeLogo() throws Exception {
        final String expected =
                testMenuActivity.getString(R.string.ticTacToeLogo);
        final String actual =
                ((TextView) testMenuActivity.findViewById(
                        R.id.menuTicTacToeLogo)).getText().toString();
        assertEquals(expected, actual);
    }

    /**
     * Clicks the play button and verifies transition to the playerSelect
     * fragment.
     *
     * @throws Exception Exception.
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    @SmallTest
    public void testMenuActivity_testPlayButton() throws Exception {
        final Button playButton = (Button) getActivity().findViewById(
                R.id.menuPlayButton);

        assertNotNull(playButton.getParent());

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playButton.performClick();
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        assertNull(playButton.getParent());
    }
}