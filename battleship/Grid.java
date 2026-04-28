package battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {
    public Cell[][] grid;
    public int rows;
    public int cols;
    public List<Boat> boats;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        this.boats = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.grid[i][j] = new Cell();
            }
        }
    }

    public void display() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!grid[i][j].isShot) {
                    System.out.print("# ");
                }
                else if (grid[i][j].hasBoat) {
                    System.out.print("X ");
                }
                else {
                    System.out.print("O ");
                }
            }
            System.out.println();
        }
    }

    public void placeBoat(Boat boat, Position start, Direction dir) {
        // Create offset based on the direction
        int dx = (dir == Direction.VERTICAL) ? 1 : 0;
        int dy = (dir == Direction.HORIZONTAL) ? 1 : 0;

        // Check if the boat placement is valid
        for (int i = 0; i < boat.length; i++) {
            int x = start.x + i * dx;
            int y = start.y + i * dy;

            // Check if out of bounds
            if (x >= rows || y >= cols) {
                System.out.println("Cannot place this boat: out of bounds");
                return;
            }

            // Check if overlaps with another boat
            if (grid[x][y].hasBoat) {
                System.out.println("Cannot place this boat: another boat found in this position");
                return;
            }
        }
        
        // Placing the boat
        for (int i = 0; i < boat.length; i++) {
            int x = start.x + i * dx;
            int y = start.y + i * dy;

            grid[x][y].hasBoat = true;
            grid[x][y].boat = boat;
        }
        boats.add(boat);
    }

    public Result receiveShot(int x, int y) {
        Cell shotCell = grid[x][y];

        if (shotCell.isShot) {
            return Result.MISS;
        }

        shotCell.shoot();

        if (!shotCell.isHit()) {
            return Result.MISS;
        }

        // Check if destroyed
        if (shotCell.boat.isDestroyed()) {
            return Result.SUNK;
        }

        return Result.HIT;
    }

    public boolean allBoatsDestroyed() {
        for (Boat boat : boats) {
            if (!boat.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public void placeAllBoatsRandomly() {
        Random random = new Random();

        int[] shipSizes = {5, 4, 3, 3, 2};

        for (int size : shipSizes) {
            boolean placed = false;

            while (!placed) {
                int x = random.nextInt(rows);
                int y = random.nextInt(cols);

                Direction dir = random.nextBoolean() 
                    ? Direction.HORIZONTAL 
                    : Direction.VERTICAL;

                if (canPlaceBoat(size, x, y, dir)) {
                    placeBoat(new Boat(size), new Position(x, y), dir);
                    placed = true;
                }
            }
        }
    }

    private boolean canPlaceBoat(int length, int x, int y, Direction dir) {
        int dx = (dir == Direction.VERTICAL) ? 1 : 0;
        int dy = (dir == Direction.HORIZONTAL) ? 1 : 0;

        for (int i = 0; i < length; i++) {
            int nx = x + i * dx;
            int ny = y + i * dy;

            if (nx >= rows || ny >= cols) return false;

            if (grid[nx][ny].hasBoat) return false;
        }

        return true;
    }
}
