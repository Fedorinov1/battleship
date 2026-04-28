package battleship;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class AI {
    public int nbTurns;
    public Mode mode;
    public Deque<Position> targetQueue;

    private List<Position> hits = new ArrayList<>();
    private Direction targetDirection = null;
    private final int[] ships = {2,3,3,4,5};

    public AI(int rows, int cols) {
        this.mode = Mode.HUNT;
        this.nbTurns = 0;
        this.targetQueue = new LinkedList<>();
    }

    Position playTurn(Grid grid, Result lastResult, Position lastShot) {
        nbTurns++;

        if (lastResult == Result.SUNK) {
            targetQueue.clear();
            mode = Mode.HUNT;
            hits.clear();
            targetDirection = null;
        } 
        else if (lastResult == Result.HIT) {
            mode = Mode.TARGET;

            hits.add(lastShot);

            if (hits.size() >= 2 && targetDirection == null) {
                Position p1 = hits.get(0);
                Position p2 = hits.get(1);

                if (p1.x == p2.x) {
                    targetDirection = Direction.HORIZONTAL;
                } else if (p1.y == p2.y) {
                    targetDirection = Direction.VERTICAL;
                }
            }

            if (targetDirection == null) {
                int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

                for (int[] d : dirs) {
                    int x = lastShot.x + d[0];
                    int y = lastShot.y + d[1];

                    if (x >= 0 && x < grid.rows && y >= 0 && y < grid.cols) {
                        Position p = new Position(x, y);
                        if (!grid.grid[x][y].isShot && !targetQueue.contains(p)) {
                            targetQueue.add(p);
                        }
                    }
                }
            } 
            else {
                if (targetDirection == Direction.HORIZONTAL) {
                    int minY = hits.stream().mapToInt(p -> p.y).min().getAsInt();
                    int maxY = hits.stream().mapToInt(p -> p.y).max().getAsInt();

                    int y1 = minY - 1;
                    int y2 = maxY + 1;

                    if (y1 >= 0) {
                        Position p = new Position(hits.get(0).x, y1);
                        if (!grid.grid[p.x][p.y].isShot && !targetQueue.contains(p)) {
                            targetQueue.add(p);
                        }
                    }

                    if (y2 < grid.cols) {
                        Position p = new Position(hits.get(0).x, y2);
                        if (!grid.grid[p.x][p.y].isShot && !targetQueue.contains(p)) {
                            targetQueue.add(p);
                        }
                    }
                } 
                else {
                    int minX = hits.stream().mapToInt(p -> p.x).min().getAsInt();
                    int maxX = hits.stream().mapToInt(p -> p.x).max().getAsInt();

                    int x1 = minX - 1;
                    int x2 = maxX + 1;

                    if (x1 >= 0) {
                        Position p = new Position(x1, hits.get(0).y);
                        if (!grid.grid[p.x][p.y].isShot && !targetQueue.contains(p)) {
                            targetQueue.add(p);
                        }
                    }

                    if (x2 < grid.rows) {
                        Position p = new Position(x2, hits.get(0).y);
                        if (!grid.grid[p.x][p.y].isShot && !targetQueue.contains(p)) {
                            targetQueue.add(p);
                        }
                    }
                }
            }
        }

        if (mode == Mode.TARGET) {
            while (!targetQueue.isEmpty()) {
                Position next = targetQueue.pollLast();

                if (!grid.grid[next.x][next.y].isShot) {
                    return next;
                }
            }

            mode = Mode.HUNT;
        }

        if (mode == Mode.HUNT) {
            return getBestShot(grid);
        }

        return null;
    }

    Position getBestShot(Grid grid) {
        int bestScore = -1;
        Position best = null;

        for (int i = 0; i < grid.rows; i++) {
            for (int j = 0; j < grid.cols; j++) {

                if (grid.grid[i][j].isShot) continue;

                int score = 0;

                for (int size : ships) {

                    for (int k = 0; k <= grid.cols - size; k++) {
                        boolean valid = true;
                        boolean containsHit = false;

                        for (int t = 0; t < size; t++) {
                            if (grid.grid[i][k + t].isShot && !grid.grid[i][k + t].isHit()) {
                                valid = false;
                                break;
                            }
                            if (grid.grid[i][k + t].isHit()) {
                                containsHit = true;
                            }
                        }

                        if (!valid) continue;
                        if (!hits.isEmpty() && !containsHit) continue;

                        if (j >= k && j < k + size) {
                            score++;
                        }
                    }

                    for (int k = 0; k <= grid.rows - size; k++) {
                        boolean valid = true;
                        boolean containsHit = false;

                        for (int t = 0; t < size; t++) {
                            if (grid.grid[k + t][j].isShot && !grid.grid[k + t][j].isHit()) {
                                valid = false;
                                break;
                            }
                            if (grid.grid[k + t][j].isHit()) {
                                containsHit = true;
                            }
                        }

                        if (!valid) continue;
                        if (!hits.isEmpty() && !containsHit) continue;

                        if (i >= k && i < k + size) {
                            score++;
                        }
                    }
                }

                if (score > bestScore) {
                    bestScore = score;
                    best = new Position(i, j);
                }
            }
        }

        return best;
    }
}