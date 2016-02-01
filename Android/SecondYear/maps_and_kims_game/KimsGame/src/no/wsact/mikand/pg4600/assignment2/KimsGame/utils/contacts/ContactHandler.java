package no.wsact.mikand.pg4600.assignment2.KimsGame.utils.contacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import no.wsact.mikand.pg4600.assignment2.KimsGame.game.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: KimsGame
 * Package: no.wsact.mikand.pg4600.assignment2.KimsGame.utils
 *
 * This class fetches and shuffles contact names.
 *
 * @author Anders Mikkelsen
 * @version 13.05.15
 */
@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
public class ContactHandler {
    private final Uri CONTACT_INFO_URI = ContactsContract.Contacts.CONTENT_URI;
    private final String CONTACT_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private final ContentResolver contentResolver;
    private final ArrayList<String> names;

    /**
     * Uses parent for getting contentResolver and retrieving all contact names.
     *
     * @param parent Game
     */
    public ContactHandler(Game parent) {
        contentResolver = parent.getContentResolver();
        String[] columns = {CONTACT_NAME};
        Cursor cursor = contentResolver.query(CONTACT_INFO_URI, columns, null, null, null);

        names = new ArrayList<String>();
        while (cursor.moveToNext()) {
            names.add(cursor.getString(cursor.getColumnIndex(CONTACT_NAME)));
        }
    }

    /**
     * Gets all names in contact list.
     *
     * @return java.utils.List of java.lang.String
     */
    public List<String> getNames() {
        return getNames(0);
    }

    /**
     * Shuffles namelist and returns a number of names, 0 returns all names.
     *
     * @param numberOfNames java.lang.Integer
     * @return java.utils.List of java.lang.String
     */
    public List<String> getNames(int numberOfNames) {
        Collections.shuffle(names);

        if (numberOfNames == 0) {
            return names;
        } else {
            return names.subList(0, numberOfNames);
        }
    }

    /**
     * Returns a single random contacts name.
     *
     * @return java.lang.String
     */
    public String getRandomContactName() {
        return names.get((int) (Math.random() * names.size()));
    }
}
