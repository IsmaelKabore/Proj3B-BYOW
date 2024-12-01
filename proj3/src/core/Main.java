package core;

import java.awt.Font;
import java.io.*;
import java.util.Random;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TERenderer;

public class Main {

    public static void main(String[] args) {
        // Display the menu using StdDraw for graphical input
        displayMainMenu();
    }

    /**
     * Displays the main menu using StdDraw.
     */
    private static void displayMainMenu() {
        StdDraw.setCanvasSize(800, 800);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text(0.5, 0.7, "CS61B: The Game");
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 20));
        StdDraw.text(0.5, 0.5, "(N) New Game");
        StdDraw.text(0.5, 0.45, "(L) Load Game");
        StdDraw.text(0.5, 0.4, "(Q) Quit Game");
        StdDraw.show();

        // Handle user input for the menu options
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (c == 'n') {
                    enterSeedScreen(); // Start new game
                } else if (c == 'l') {
                    loadGame(); // Load game
                } else if (c == 'q') {
                    System.exit(0); // Quit the game
                }
            }
        }
    }

    /**
     * Prompts the user to enter a seed for the new game.
     */
    private static void enterSeedScreen() {
        String seed = "";
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(0.5, 0.7, "Enter Seed:");
        StdDraw.show();

        // Handle input for seed
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.isDigit(c)) {
                    seed += c;
                } else if (Character.toLowerCase(c) == 's') {
                    startGame(seed); // Start the game with the given seed
                }
                StdDraw.clear(StdDraw.BLACK);
                StdDraw.text(0.5, 0.7, "Enter Seed:");
                StdDraw.text(0.5, 0.6, seed);
                StdDraw.show();
            }
        }
    }

    /**
     * Starts the game using the given seed.
     * @param seed The seed to use for world generation.
     */
    private static void startGame(String seed) {
        long seedValue = Long.parseLong(seed);
        System.out.println("Starting game with seed: " + seedValue);

        // Initialize game world and start the game loop
        final int WIDTH = 50;
        final int HEIGHT = 30;
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = World.generateRandomWorld(WIDTH, HEIGHT, seedValue);

        // Place avatar on the world
        Position avatarPosition = placeAvatar(world);

        // Game loop: handle user input for avatar movement
        while (true) {
            ter.renderFrame(world); // Render the current world
            moveAvatarWithStdDraw(world, avatarPosition); // Handle avatar movement through StdDraw

            // Check for :Q input to save and quit
            if (StdDraw.hasNextKeyTyped()) {
                char move = StdDraw.nextKeyTyped();
                if (move == ':') {
                    // Wait for the next key to be pressed
                    while (!StdDraw.hasNextKeyTyped()) {
                        // Idle until a key is available
                    }
                    char next = StdDraw.nextKeyTyped();
                    if (next == 'Q' || next == 'q') {
                        saveGame(world, avatarPosition); // Save the game
                        System.exit(0); // Quit the game
                    }
                } else {
                    // Handle other movement logic (e.g., W/A/S/D)
                }
            }

        }
    }

    /**
     * Handles avatar movement using StdDraw for W/A/S/D input.
     * @param world The game world (2D array of TETiles)
     * @param avatarPosition The current position of the avatar
     */
    private static void moveAvatarWithStdDraw(TETile[][] world, Position avatarPosition) {
        if (StdDraw.hasNextKeyTyped()) {
            char move = StdDraw.nextKeyTyped();
            move = Character.toUpperCase(move); // Convert to uppercase for consistency

            // Handle avatar movement based on W/A/S/D input
            switch (move) {
                case 'W':
                    moveAvatar(world, avatarPosition, 0, 1);  // Move up
                    break;
                case 'A':
                    moveAvatar(world, avatarPosition, -1, 0); // Move left
                    break;
                case 'S':
                    moveAvatar(world, avatarPosition, 0, -1); // Move down
                    break;
                case 'D':
                    moveAvatar(world, avatarPosition, 1, 0);  // Move right
                    break;
                case 'Q':
                    System.exit(0); // Quit the game on 'Q'
                    break;
                case ':' :
                    // Wait for the next key to be pressed
                    while (!StdDraw.hasNextKeyTyped()) {
                        // Idle until a key is available
                    }
                    char next = StdDraw.nextKeyTyped();
                    if (next == 'Q') {
                        saveGame(world, avatarPosition);
                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("Invalid input. Use W/A/S/D or Q to quit.");
            }
        }
    }

    /**
     * Moves the avatar on the world map if the destination is a valid floor tile.
     * @param world The game world (2D array of TETiles)
     * @param avatarPosition The current position of the avatar
     * @param dx The x-axis change in position (dx = 1 to move right, dx = -1 to move left)
     * @param dy The y-axis change in position (dy = 1 to move up, dy = -1 to move down)
     */
    public static void moveAvatar(TETile[][] world, Position avatarPosition, int dx, int dy) {
        int newX = avatarPosition.x + dx;
        int newY = avatarPosition.y + dy;

        // Check if the new position is within bounds and is a valid floor
        if (newX >= 0 && newX < world.length && newY >= 0 && newY < world[0].length &&
                world[newX][newY].description().equals("floor")) {
            // Move the avatar to the new position
            world[avatarPosition.x][avatarPosition.y] = new TETile(' ', java.awt.Color.BLACK, java.awt.Color.BLACK, "empty", 0);
            avatarPosition.x = newX;
            avatarPosition.y = newY;
            world[avatarPosition.x][avatarPosition.y] = new TETile('@', java.awt.Color.RED, java.awt.Color.BLACK, "Avatar", 3);
        } else {
            System.out.println("Invalid move. Try again.");
        }
    }

    /**
     * Places the avatar on a valid floor square and returns its position.
     * @param world The game world (2D array of TETiles)
     * @return The position of the avatar in the world.
     */
    public static Position placeAvatar(TETile[][] world) {
        // Find the first valid floor tile (e.g., bottommost-leftmost floor)
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y].description().equals("floor")) {
                    world[x][y] = new TETile('@', java.awt.Color.RED, java.awt.Color.BLACK, "Avatar", 3);
                    return new Position(x, y);  // Return position of the avatar
                }
            }
        }
        return null; // No floor tile found (should not happen if world is generated correctly)
    }

    /**
     * Saves the current game state to a file.
     * @param world The game world (2D array of TETiles)
     * @param avatarPosition The current position of the avatar
     */
    private static void saveGame(TETile[][] world, Position avatarPosition) {
        try {
            String path = "src/core/save.txt"; // Adjust this path as necessary
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));

            // Save world dimensions
            writer.write(world.length + " " + world[0].length);
            writer.newLine();

            // Save the world tiles
            for (int i = 0; i < world.length; i++) {
                for (int j = 0; j < world[i].length; j++) {
                    char tileChar = getTileChar(world[i][j]); // Convert tile to a character
                    writer.write(tileChar);
                }
                writer.newLine();
            }

            // Save avatar position
            writer.write(avatarPosition.x + " " + avatarPosition.y);
            writer.newLine();

            writer.close();
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving the game: " + e.getMessage());
        }
    }

    private static char getTileChar(TETile tile) {
        // Convert each tile type to a specific character representation
        if (tile.description().equals("Avatar")) {
            return '@';
        } else if (tile.description().equals("floor")) {
            return '.';
        } else if (tile.description().equals("wall")) {
            return '#';
        }
        return ' '; // Default character for unknown tiles
    }

    private static void loadGame() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/core/save.txt"));
            String[] dimensions = reader.readLine().split(" ");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            TETile[][] world = new TETile[width][height];
            for (int i = 0; i < width; i++) {
                String line = reader.readLine();
                for (int j = 0; j < height; j++) {
                    char tileChar = line.charAt(j);
                    world[i][j] = getTileFromChar(tileChar); // Restore the tile based on saved character
                }
            }

            String[] avatarPos = reader.readLine().split(" ");
            Position avatarPosition = new Position(Integer.parseInt(avatarPos[0]), Integer.parseInt(avatarPos[1]));

            // Place the avatar in the loaded world
            world[avatarPosition.x][avatarPosition.y] = new TETile('@', java.awt.Color.RED, java.awt.Color.BLACK, "Avatar", 3);

            reader.close();
            System.out.println("Game loaded successfully.");
            startLoadedGame(world, avatarPosition); // Start the game with the loaded state
        } catch (IOException e) {
            System.out.println("Error loading the game: " + e.getMessage());
        }
    }

    private static TETile getTileFromChar(char c) {
        // Convert the character back to a tile object
        if (c == '@') {
            return new TETile('@', java.awt.Color.RED, java.awt.Color.BLACK, "Avatar", 3);
        } else if (c == '.') {
            return new TETile('.', java.awt.Color.LIGHT_GRAY, java.awt.Color.BLACK, "floor", 0);
        } else if (c == '#') {
            return new TETile('#', java.awt.Color.DARK_GRAY, java.awt.Color.BLACK, "wall", 0);
        }
        return new TETile(' ', java.awt.Color.BLACK, java.awt.Color.BLACK, "empty", 0); // Default empty tile
    }

    private static void startLoadedGame(TETile[][] world, Position avatarPosition) {
        final int WIDTH = world.length;
        final int HEIGHT = world[0].length;
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // Set the avatar's position in the world
        world[avatarPosition.x][avatarPosition.y] = new TETile('@', java.awt.Color.RED, java.awt.Color.BLACK, "Avatar", 3);

        // Game loop: handle user input for avatar movement
        while (true) {
            ter.renderFrame(world); // Render the current world
            moveAvatarWithStdDraw(world, avatarPosition); // Handle avatar movement through StdDraw

            // Check for :Q input to save and quit
            if (StdDraw.hasNextKeyTyped()) {
                char move = StdDraw.nextKeyTyped();
                if (move == ':' && StdDraw.hasNextKeyTyped()) {
                    char next = StdDraw.nextKeyTyped();
                    if (next == 'Q' || next == 'q') {
                        saveGame(world, avatarPosition); // Save game and quit
                        System.exit(0);
                    }
                }
            }
        }
    }



    /**
     * Position class to hold the avatar's coordinates.
     */
    private static class Position {
        int x, y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
