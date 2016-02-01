package no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels;

/**
 * This is a class i devised for an assignment in PG3100, its been modified slightly.
 * <p>
 * It is a representation of a column in a db table.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
@SuppressWarnings("UnusedDeclaration")
public class Column {
    private String name = "";
    private String type = "";
    private int size = 0;

    public Column(String name, String type, int size) {
        setName(name);
        setType(type);
        setSize(size);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    private void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return name;
    }
}
