package no.wsact.mikand.pg4100.assignment2.models;

import no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels.Column;
import no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels.Row;
import no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels.Table;
import no.wsact.mikand.pg4100.assignment2.utils.Log;
import no.wsact.mikand.pg4100.assignment2.utils.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class i devised for an assignment in PG3100, its been modified slightly.
 * <p>
 * This is barebones compared to the original, it simply gets tables from the db.
 * I have also made it autocloseable this time around.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class DatabaseHandler implements AutoCloseable {
    private final MySqlConnectorBokliste db;

    /**
     * Constructor connects to relevant db.
     *
     * @throws SQLException java.sql.SQLException
     */
    public DatabaseHandler() throws SQLException  {
        db = new MySqlConnectorBokliste();
    }

    /**
     * Closes connection
     */
    public void close() throws SQLException {
        try {
            db.getConnection().close();
        } catch (NullPointerException npe) {
            Logger.getInstance().add(new Log("HANDLER NOT CONNECTED"));
        }
    }

    /**
     * Returns a formatted table from the db.
     *
     * @param tableName java.lang.String
     * @param <T>       T
     * @return models.mySqlConnectorModels.Table
     *
     * @throws SQLException java.sql.SQLException
     */
    public <T> Table getTable(String tableName) throws SQLException {
        try (Statement stmt = db.getConnection().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(clean("SELECT * FROM " + tableName + ";"))) {
                int columnAmount = rs.getMetaData().getColumnCount();

                return new Table(tableName,
                        buildTableColumns(columnAmount, rs),
                        buildTableRows(rs, columnAmount));
            }
        } catch (SQLException sqe) {
            if (sqe.getErrorCode() == 1051) {
                Logger.getInstance().add(new Log("Table unavailible..."));
            }

            Logger.getInstance().add(new Log("Database unavailible..."));

            throw sqe;
        } catch (NullPointerException npe) {
            Logger.getInstance().add(new Log("HANDLER NOT CONNECTED"));

            throw npe;
        }
    }

    /**
     * Builds all necessary columns for the Table object collected from db.
     *
     * @param columnAmount Integer
     * @param rs ResultSet
     * @throws SQLException SQLException
     * @return ArrayList ArrayList of Columns
     */
    private ArrayList<Column> buildTableColumns(int columnAmount, ResultSet rs)
            throws SQLException {
        ArrayList<Column> columns = new ArrayList<>();

        for (int i = 1; i <= columnAmount; i++) {
            columns.add(new Column(
                    rs.getMetaData().getColumnName(i),
                    rs.getMetaData().getColumnTypeName(i),
                    rs.getMetaData().getColumnDisplaySize(i)));
        }

        return columns;
    }

    /**
     * Builds the individual rows for the Table object collected from the db.
     *
     * @param rs ResultSet
     * @param columnAmount Integer
     * @param <T> Type
     * @return List of Rows
     * @throws SQLException SQLException
     */
    @SuppressWarnings("unchecked")
    private <T> List<Row> buildTableRows(ResultSet rs, int columnAmount) throws SQLException {
        // builds rows with / for seperator
        ArrayList<Row> rows = new ArrayList<>();

        while (rs.next()) {
            List<T> row = new ArrayList<>();

            for (int i = 1; i <= columnAmount; i++) {
                row.add((T) rs.getObject(i));
            }

            // adds rows by spliting on seperator
            rows.add(new Row(row.toArray()));
        }

        return rows;
    }

    /**
     * This method cleans simple sql injectionattempts.
     *
     * @param input java.lang.String
     * @return java.lang.String
     */
    private String clean(String input) {
        return input.replaceAll("--", "").replaceAll("'", "");
    }

    /**
     * Tells client wether connection is closed.
     *
     * @return boolean
     * @throws SQLException SQLException
     */
    public boolean isClosed() throws SQLException {
        return db.getConnection() == null || db.getConnection().isClosed();
    }
}