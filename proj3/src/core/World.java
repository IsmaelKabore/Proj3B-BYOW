package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private static class Room {
        int x, y, width, height;

        Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        // Returns the center point of the room
        int[] getCenter() {
            return new int[]{x + width / 2, y + height / 2};
        }

        // Checks if a point is along the border of the room
        boolean isOnBorder(int x, int y) {
            return (x == this.x - 1 || x == this.x + this.width) ||
                    (y == this.y - 1 || y == this.y + this.height);
        }
    }

    public static TETile[][] generateRandomWorld(int width, int height, long seed) {
        Random rand = new Random(seed);
        TETile[][] world = new TETile[width][height];
        List<Room> rooms = new ArrayList<>();

        // Fill the world with "NOTHING" tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // Generate random rooms
        for (int i = 0; i < 8; i++) { // Generate 8 random rooms
            int roomWidth = rand.nextInt(5) + 4; // Random width 4-8
            int roomHeight = rand.nextInt(5) + 4; // Random height 4-8
            int startX = rand.nextInt(width - roomWidth - 2) + 1; // Avoid edges
            int startY = rand.nextInt(height - roomHeight - 2) + 1;

            Room room = new Room(startX, startY, roomWidth, roomHeight);
            rooms.add(room);

            // Add the room floor
            for (int x = startX; x < startX + roomWidth; x++) {
                for (int y = startY; y < startY + roomHeight; y++) {
                    world[x][y] = Tileset.FLOOR;
                }
            }

            // Add walls around the room
            for (int x = startX - 1; x <= startX + roomWidth; x++) {
                for (int y = startY - 1; y <= startY + roomHeight; y++) {
                    if (x == startX - 1 || x == startX + roomWidth || y == startY - 1 || y == startY + roomHeight) {
                        if (world[x][y] == Tileset.NOTHING) {
                            world[x][y] = Tileset.WALL;
                        }
                    }
                }
            }
        }

        // Connect rooms with hallways
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room roomA = rooms.get(i);
            Room roomB = rooms.get(i + 1);

            int[] centerA = roomA.getCenter();
            int[] centerB = roomB.getCenter();

            // Create a horizontal hallway
            int xMin = Math.min(centerA[0], centerB[0]);
            int xMax = Math.max(centerA[0], centerB[0]);
            for (int x = xMin; x <= xMax; x++) {
                world[x][centerA[1]] = Tileset.FLOOR;
            }

            // Add walls around the horizontal hallway
            for (int x = xMin - 1; x <= xMax + 1; x++) {
                if (world[x][centerA[1] - 1] == Tileset.NOTHING) {
                    world[x][centerA[1] - 1] = Tileset.WALL;
                }
                if (world[x][centerA[1] + 1] == Tileset.NOTHING) {
                    world[x][centerA[1] + 1] = Tileset.WALL;
                }
            }

            // Create a vertical hallway
            int yMin = Math.min(centerA[1], centerB[1]);
            int yMax = Math.max(centerA[1], centerB[1]);
            for (int y = yMin; y <= yMax; y++) {
                world[centerB[0]][y] = Tileset.FLOOR;
            }

            // Add walls around the vertical hallway
            for (int y = yMin - 1; y <= yMax + 1; y++) {
                if (world[centerB[0] - 1][y] == Tileset.NOTHING) {
                    world[centerB[0] - 1][y] = Tileset.WALL;
                }
                if (world[centerB[0] + 1][y] == Tileset.NOTHING) {
                    world[centerB[0] + 1][y] = Tileset.WALL;
                }
            }

            // Remove walls at hallway-room intersections
            if (roomA.isOnBorder(centerA[0] - 1, centerA[1])) {
                world[centerA[0] - 1][centerA[1]] = Tileset.FLOOR;
            }
            if (roomA.isOnBorder(centerA[0] + 1, centerA[1])) {
                world[centerA[0] + 1][centerA[1]] = Tileset.FLOOR;
            }
            if (roomB.isOnBorder(centerB[0], centerB[1] - 1)) {
                world[centerB[0]][centerB[1] - 1] = Tileset.FLOOR;
            }
            if (roomB.isOnBorder(centerB[0], centerB[1] + 1)) {
                world[centerB[0]][centerB[1] + 1] = Tileset.FLOOR;
            }
        }

        return world;
    }
}
