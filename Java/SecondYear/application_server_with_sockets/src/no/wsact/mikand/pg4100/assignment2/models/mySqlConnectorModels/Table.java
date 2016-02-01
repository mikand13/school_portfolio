package no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels;

import java.util.List;

/**
 * This is a class i devised for an assignment in PG3100, its been modified slightly.
 * <p>
 * Container for a Table from the db.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
@SuppressWarnings("UnusedDeclaration")
public class Table {
    private final String tableName;
    private List<Row> rows;
    private List<Column> columns;

    /**
     * This constructor takes a tablename, a list of columns and a list of rows and builds a table
     * object.
     *
     * @param tableName java.lang.String
     * @param columns   java.utils.List
     * @param rows      java.utils.List
     */
    public Table(String tableName, List<Column> columns, List<Row> rows) {
        this.tableName = tableName;
        setColumns(columns);
        setRows(rows);
    }

    public String getTableName() {
        return tableName;
    }

    public List<Row> getRows() {
        return rows;
    }

    void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public List<Column> getColumns() {
        return columns;
    }

    void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        return !(tableName != null
                ? !tableName.equals(table.tableName)
                : table.tableName != null);
    }

    /**
     * I decided to implement the printing of the table in its formatted form in this toString.
     * So you dont have to handle the table in an application. You get exactly what you want, a
     * table.
     *
     * @return String
     */
    @Override
    public String toString() {
        if (rows.size() > 0) {
            StringBuilder table = new StringBuilder();

            table.append(String.format("\n%-4s", columns.get(0)));

            for (int i = 1; i < columns.size(); i++) {
                table.append(String.format("%-20s", columns.get(i)));
            }

            table.append("\n\n");

            rows.forEach(table::append);

            return table.toString();
        } else {
            return "\nThe table is empty!\n";
        }
    }
}
