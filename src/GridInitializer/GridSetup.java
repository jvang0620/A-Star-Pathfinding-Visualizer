package src.GridInitializer; // Package

import src.Validators.*;

import java.util.*; // Import the entire java.util package
import java.util.concurrent.TimeUnit; // Import the TimeUnit class

public class GridSetup {

    /**
     * Class representing a single node in the grid
     */
    static class Node {
        // Coordinates of the node
        int x, y;
        // Values used in the A* algorithm
        double f, g, h;
        // Parent node in the path
        Node parent;

        // Constructor to initialize the node with coordinates (x, y)
        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // 2D array representing the grid
    static int[][] grid;

    // Number of rows and columns in the grid
    static int numRows = 15;
    static int numCols = 15;

    // Random number generator for generating random obstacles
    static Random random = new Random();

    public static void initializer() {
        // Generate the grid with random obstacles
        generateGrid();
        // Display the grid
        displayGrid();

        Scanner scanner = new Scanner(System.in);
        boolean isLooping = true;

        while (isLooping) {
            // Prompt user to input starting node
            System.out.println("\nEnter starting node (x y): ");
            int startX = CoordinateInputValidator.readCoordinate(scanner, "x");
            int startY = CoordinateInputValidator.readCoordinate(scanner, "y");
            Node startNode = new Node(startX, startY);

            // Prompt user to input goal node
            System.out.println("\nEnter goal node (x y): ");
            int goalX = CoordinateInputValidator.readCoordinate(scanner, "x");
            int goalY = CoordinateInputValidator.readCoordinate(scanner, "y");
            Node goalNode = new Node(goalX, goalY);

            // Find the path from the starting node to the goal node using the A* algorithm
            List<Node> path = findPath(startNode, goalNode);

            // If a path is found, print the coordinates of each node in the path
            if (path != null) {
                System.out.println("\nPath found:");

                for (Node node : path) {
                    System.out.print("[" + node.x + ", " + node.y + "] ");
                }

                // Blank space
                System.out.println();

                // Display the grid with the agent moving along the path
                displayAgentsPath(path, startNode, goalNode);

            } else {
                // If no path is found, print a message
                System.out.println("No path could be found.");
            }

            // Ask if the user wants to continue
            System.out.println("\nDo you want to continue?");
            System.out.println("Enter 'y' for yes and 'n' for no:");
            String input = scanner.next();

            // Validate user input
            while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
                System.out.println("Invalid entry. Please enter 'y' for yes and 'n' for no:");
                input = scanner.next();
            }

            // If user enters "n" (no), exit the loop
            if (input.equalsIgnoreCase("n")) {
                System.out.println("\nHope you enjoyed the program.\nSee you next time!");
                // Set variable to 'false' to exit infinite loop
                isLooping = false;

            }
            // If user enters "y" (yes), continue the program
            else {
                // Consume the previous input
                scanner.nextLine();

                // Ask if the user wants to generate a new board
                System.out.println("\nDo you want to generate a new board?");
                System.out.println("Enter 'y' for yes and 'n' for no:");
                String newBoardInput = scanner.next();

                // Validate user input for generating a new board
                while (!newBoardInput.equalsIgnoreCase("y") && !newBoardInput.equalsIgnoreCase("n")) {
                    System.out.println("Invalid entry. Please enter 'y' for yes and 'n' for no:");
                    newBoardInput = scanner.next();
                }

                // Generate a new board if user chooses 'y'
                if (newBoardInput.equalsIgnoreCase("y")) {
                    // Generate a new grid
                    generateGrid();
                    // Display the grid again for the user
                    displayGrid();
                } else {
                    // Display the grid again for the user
                    displayGrid();
                }
                // Consume the previous input
                scanner.nextLine();
            }
        }
        // Close the scanner after the loop
        scanner.close();
    }

    /**
     * Finds the shortest path from the starting node to the goal node using the A*
     * algorithm.
     * 
     * @param start The starting node.
     * @param goal  The goal node.
     * @return The list of nodes representing the path from start to goal, or null
     *         if no path is found.
     */
    static List<Node> findPath(Node start, Node goal) {
        // Check if the starting or goal nodes are obstacles
        if (grid[start.x][start.y] == 1 || grid[goal.x][goal.y] == 1) {
            System.out.println("\nInvalid input! Starting or goal node is an obstacle.");
            System.out.println("Select coordinates that ARE NOT obstacles (X).");
            return null;
        }

        // Initialize open set, closed set, and add start node to open set
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<Node> closedSet = new HashSet<>();
        openSet.add(start);

        // Loop until open set is empty
        while (!openSet.isEmpty()) {

            // Get the node with the lowest f score from open set
            Node current = openSet.poll();

            // If goal reached, reconstruct and return the path
            if (current.x == goal.x && current.y == goal.y) {
                return reconstructPath(current);
            }

            // Add current node to closed set
            closedSet.add(current);

            // Iterate through neighbors of current node
            for (Node neighbor : getNeighbors(current)) {
                // Skip if neighbor is in closed set or is an obstacle
                if (closedSet.contains(neighbor)) {
                    // Skip evaludating this neigbor node any further
                    continue;
                }

                // Calculate tentative g score for neighbor
                double tentativeGScore = current.g + 1; // Assuming uniform cost for each move

                // Update neighbor's information if it's not already in open set
                // or has a better g score
                if (!openSet.contains(neighbor) || tentativeGScore < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeGScore;
                    neighbor.h = calculateHeuristic(neighbor, goal);
                    neighbor.f = neighbor.g + neighbor.h;

                    // Add neighbor to open set if it's not already present
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    } else {
                        // Update neighbor's f-score in the open set
                        openSet.remove(neighbor); // Remove the neighbor
                        openSet.add(neighbor); // Add it back with updated f-score
                    }
                }
            }
        }
        return null;
    }

    /**
     * Reconstructs the path from the given node to the starting node.
     * 
     * @param current The current node to start the reconstruction from.
     * @return The list of nodes representing the reconstructed path.
     */
    static List<Node> reconstructPath(Node current) {
        // Initialize list to store the path
        List<Node> path = new ArrayList<>();

        // Traverse backwards from current node until reaching the starting node
        while (current != null) {
            // Add current node to the path
            path.add(current);
            // Move to the parent node
            current = current.parent;
        }

        // Reverse the path to get it in correct order
        Collections.reverse(path);
        return path;
    }

    /**
     * Calculates the Manhattan distance heuristic between two nodes.
     * 
     * @param a The first node.
     * @param b The second node.
     * @return The Manhattan distance between the two nodes.
     */
    static double calculateHeuristic(Node a, Node b) {
        // Manhattan distance calculation: |x1 - x2| + |y1 - y2|
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * Retrieves the neighboring nodes of a given node.
     * 
     * @param node The node for which neighbors are to be retrieved.
     * @return A list of neighboring nodes.
     */
    static List<Node> getNeighbors(Node node) {
        // Initialize list to store neighboring nodes
        List<Node> neighbors = new ArrayList<>();

        // Define directions (right, left, down, up, diagonal)
        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

        // Iterate through directions to find neighboring nodes
        for (int[] dir : directions) {
            // Calculate the new coordinates for a neighboring node based on the current
            // node's coordinates and the specified direction
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];

            // Check if the neighbor is within the grid bounds and is traversable
            if (GridValidator.isValid(newX, newY, numRows, numCols) && grid[newX][newY] == 0) {
                // Add valid neighbor nodes
                neighbors.add(new Node(newX, newY));
            }
        }
        return neighbors;
    }

    /**
     * Generates a grid with random obstacles.
     */
    static void generateGrid() {
        grid = new int[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                // Assign a value of 1 (obstacle) with a probability of 50% (0.5)
                grid[i][j] = random.nextDouble() < 0.5 ? 1 : 0;
            }
        }
    }

    /**
     * Displays the grid with row and column numbers.
     * Zeros (0) are replaced by "-", X's are the obstacles.
     */
    static void displayGrid() {

        // Display legend for coordinates
        System.out.println("\nLegend for coordinates:");
        System.out.println("  x-coordinate (col) V");
        System.out.println("  y-coordinate (row) ->\n");

        // Display column numbers above the grid
        System.out.print("    ");
        for (int i = 0; i < numCols; i++) {
            System.out.printf("%2d ", i);
        }

        // Horizontal line
        System.out.println("\n  --" + "-".repeat(numCols * 3));

        // Display Grid with row numbers
        for (int i = 0; i < numRows; i++) {
            System.out.printf("%2d |", i); // Row number
            for (int j = 0; j < numCols; j++) {
                System.out.print(grid[i][j] == 1 ? " X " : " - "); // Mark obstacles as 'X'
            }
            System.out.println();
        }
    }

    /**
     * Display the grid with the agent moving along the path.
     * 
     * @param path      The list of nodes representing the path to be displayed.
     * @param startNode The starting node of the path.
     * @param endNode   The ending node of the path.
     */
    static void displayAgentsPath(List<Node> path, Node startNode, Node endNode) {
        // Copy the original grid to avoid modifying it
        char[][] displayGrid = new char[numRows][numCols];

        // Print message
        System.out.println("\nProgram displaying agent's movement from start to goal: \n");
        System.out.println("    S = Starting Node");
        System.out.println("    G = Goal Node\n");

        // Display column numbers above the grid
        System.out.print("    ");
        for (int i = 0; i < numCols; i++) {
            System.out.printf("%3d", i);
        }

        // Horizontal line
        System.out.println("\n  " + "-".repeat(numCols * 3 + 4));

        // Initialize the display grid with obstacles and empty spaces
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                displayGrid[i][j] = grid[i][j] == 1 ? 'X' : '-';
            }
        }

        // Mark the path on the display grid with '+'
        for (Node node : path) {
            displayGrid[node.x][node.y] = '@';
        }

        // Mark the starting point on the display grid with 'S'
        displayGrid[startNode.x][startNode.y] = 'S';

        // Mark the ending point on the display grid with 'E'
        displayGrid[endNode.x][endNode.y] = 'G';

        // Display the grid with numbers, obstacles, starting point, ending point, and
        // agent's path with delay
        try {
            for (int i = 0; i < numRows; i++) {
                // Row number
                System.out.printf("%2d |", i);
                for (int j = 0; j < numCols; j++) {
                    System.out.print(" " + displayGrid[i][j] + " ");
                }
                // Blank line
                System.out.println();

                // (500 milliseconds in this case)
                TimeUnit.MILLISECONDS.sleep(500);
            }
        }
        // Catch InterruptedException if the thread is interrupted while sleeping and
        // restore the interrupted status of the thread
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}