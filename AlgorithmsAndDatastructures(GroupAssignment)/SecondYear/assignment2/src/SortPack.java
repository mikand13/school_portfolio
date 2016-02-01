/**
 * @author Anders Mikkelsen - mikand13@student.westerdals.no
 * @author Espen Rønning - ronesp13@student.westerdals.no
 */
public class SortPack {
    private final int compares;
    private final double time;

    public SortPack(int compares, double time) {
        this.compares = compares;
        this.time = time;
    }

    public int getCompares() {
        return compares;
    }

    public double getTime() {
        return time;
    }
}