package src.Validators;

public class GridValidator {
    /**
     * Checks if the given coordinates are within the bounds of the grid.
     * 
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if the coordinates are valid, false otherwise.
     */
    public static boolean isValid(int x, int y, int numRows, int numCols) {
        return x >= 0 && x < numRows && y >= 0 && y < numCols;
    }
}
