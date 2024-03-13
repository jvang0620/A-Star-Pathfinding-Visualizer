package src.Validators; // Package

import java.util.Scanner;

public class CoordinateInputValidator {
    /**
     * Method to read a coordinate (x or y) from the user and validate it.
     * 
     * @param scanner        The Scanner object used for input.
     * @param coordinateName The name of the coordinate being read (e.g., "x" or
     *                       "y").
     * @return The validated coordinate value.
     */
    public static int readCoordinate(Scanner scanner, String coordinateName) {
        int coordinate;

        while (true) {
            // Prompt user to enter coordinnates
            System.out.print("Enter " + coordinateName + " coordinate (0-14): ");
            // Read input as string
            String input = scanner.nextLine();

            try {
                // Parse input as integer
                coordinate = Integer.parseInt(input);

                // Validate input is between 0 and 14
                if (coordinate >= 0 && coordinate <= 14) {
                    // Exit the loop if the coordinate is valid
                    break;
                } else {
                    // If not in range, print message to user
                    System.out.println("Invalid coordinate. Please enter a value between 0 and 14.\n");
                }
            } catch (NumberFormatException e) {
                // Handle the case where input cannot be parsed as an integer
                System.out.println("Invalid input! Please enter a valid integer value.\n");
            }
        }
        return coordinate;
    }
}