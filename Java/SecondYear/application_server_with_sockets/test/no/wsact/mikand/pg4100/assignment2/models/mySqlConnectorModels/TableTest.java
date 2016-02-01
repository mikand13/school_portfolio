package no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.views
 * <p>
 * This is a copy of my test for the same class in PG3100 assignment 2.
 *
 * This also implicitly tests row and column, which are so basic i dont think they warrant their
 * own test class.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
@SuppressWarnings("unchecked")
public class TableTest {
    private Table t;

    /**
     * Creates a table with bogus data for each test.
     *
     * @param <T> T
     * @throws Exception Exception
     */
    @Before
    public <T> void setUp() throws Exception {
        ArrayList<Column> temp = new ArrayList<>();
        temp.add(new Column("ID", "VARCHAR", 100));
        temp.add(new Column("Navn", "VARCHAR", 100));
        temp.add(new Column("Adresse", "VARCHAR", 100));

        ArrayList<T> testArray = new ArrayList<>();
        testArray.add((T) "1");
        testArray.add((T) "Donald Duck");
        testArray.add((T) "Uflaksveien 13");
        testArray.add((T) "60");
        Row donald = new Row(testArray.toArray());

        ArrayList<Row> temp2 = new ArrayList<>();
        temp2.add(donald);
        t = new Table("person", temp, temp2);
    }

    /**
     * Sets t up for gc.
     *
     * @throws Exception Exception
     */
    @SuppressWarnings("ConstantConditions")
    @After
    public void tearDown() throws Exception {
        t = null;
    }

    /**
     * Checks to see if columns are set up according to information provided.
     *
     * @throws Exception Exception
     */
    @Test
    public void testGetColumns() throws Exception {
        List<Column> temp = t.getColumns();

        assertNotNull(temp);
        assertEquals("ID", temp.get(0).getName());
        assertEquals("VARCHAR", temp.get(0).getType());
        assertEquals(100, temp.get(0).getSize());
        assertEquals("Navn", temp.get(1).getName());
        assertEquals("VARCHAR", temp.get(1).getType());
        assertEquals(100, temp.get(1).getSize());
        assertEquals("Adresse", temp.get(2).getName());
        assertEquals("VARCHAR", temp.get(2).getType());
        assertEquals(100, temp.get(2).getSize());
    }

    /**
     * Checks to see if rows are set up according to information provided.
     *
     * @throws Exception Exception
     */
    @Test
    public void testGetRows() throws Exception {
        List<Row> temp = t.getRows();

        assertNotNull(temp);
        assertEquals(temp.get(0).toString(), String.format("%-4s%-20s%-20s%-20s\n",
                "1",
                "Donald Duck",
                "Uflaksveien 13",
                "60"));
    }

    /**
     * Checks to see if toString works as intended to output in a formatted manner.
     *
     * @throws Exception Exception
     */
    @Test
    public void testToString() throws Exception {
        ByteArrayOutputStream showTable = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(showTable);
        PrintStream old = System.out;

        System.setOut(ps);
        System.out.println(t.toString());
        System.out.flush();
        System.setOut(old);

        String[] output = showTable.toString().split("\\s+");
        String[] testOutput = {"", "ID", "Navn", "Adresse",
                "1", "Donald", "Duck", "Uflaksveien", "13", "60"};

        assertArrayEquals(testOutput, output);
    }
}