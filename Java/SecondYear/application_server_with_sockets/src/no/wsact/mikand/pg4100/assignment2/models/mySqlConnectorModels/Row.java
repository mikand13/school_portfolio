package no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels;

/**
 * This is a class i devised for an assignment in PG3100, its been modified slightly.
 * <p>
 * It is a representation of a row in a db table.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class Row<T> {
    private T[] row;

    public Row(T[] rowElements) {
        setRow(rowElements);
    }

    public T[] getRow() {
        return row;
    }

    void setRow(T[] row) {
        this.row = row;
    }

    @Override
    public String toString() {
        StringBuilder completeRow = new StringBuilder();

        completeRow.append(String.format("%-4s", getRow()[0]));

        for (int i = 1; i < row.length; i++) {
            completeRow.append(String.format("%-20s", getRow()[i]));
        }

        completeRow.append("\n");

        return completeRow.toString();
    }
}
