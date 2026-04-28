package battleship;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Grid grid = new Grid(10, 10);

        // Boat placement example
        grid.placeAllBoatsRandomly();
        grid.display();

        // Create AI
        AI ai = new AI(10, 10);
        Result lastResult = null;
        Position lastShot = null;

        SwingUtilities.invokeLater(() -> {
            BattleshipUI ui = new BattleshipUI(grid, ai);
            ui.setVisible(true);
        });
    }
}
