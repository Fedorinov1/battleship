package battleship;

import javax.swing.*;
import java.awt.*;

public class BattleshipUI extends JFrame {
    private Grid grid;
    private AI ai;
    private Result lastResult = null;
    private Position lastShot = null;

    private JPanel boardPanel;
    private JLabel[][] cellLabels;
    private JLabel statusLabel;
    private JButton nextTurnBtn;
    private JButton autoPlayBtn;
    private JButton restartBtn;
    private Timer autoTimer;

    public BattleshipUI(Grid grid, AI ai) {
        this.grid = grid;
        this.ai = ai;

        setTitle("Bataille Navale  - Mode Chasse et Cible");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 680);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(25, 35, 45));

        initComponents();
        updateBoardColors();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        Font modernFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 18);

        statusLabel = new JLabel("Tours : 0 | Mode : HUNT", SwingConstants.CENTER);
        statusLabel.setFont(titleFont);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(38, 50, 56));
        add(statusLabel, BorderLayout.NORTH);

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(grid.rows, grid.cols, 2, 2));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        boardPanel.setBackground(new Color(25, 35, 45));

        cellLabels = new JLabel[grid.rows][grid.cols];

        for (int y = 0; y < grid.cols; y++) {
            for (int x = 0; x < grid.rows; x++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setOpaque(true);
                cell.setBackground(new Color(30, 144, 255)); // Water
                cell.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 80)));
                cell.setFont(modernFont);
                cellLabels[x][y] = cell;
                boardPanel.add(cell);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 10));
        controlPanel.setBackground(new Color(38, 50, 56));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 18, 10));

        nextTurnBtn = new JButton("Tour Suivant");
        nextTurnBtn.setFont(modernFont);
        nextTurnBtn.setBackground(new Color(76, 175, 80));
        nextTurnBtn.setForeground(Color.WHITE);
        nextTurnBtn.setFocusPainted(false);
        nextTurnBtn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        nextTurnBtn.addActionListener(e -> playOneTurn());

        autoTimer = new Timer(100, e -> playOneTurn());
        autoPlayBtn = new JButton("Jeu Auto");
        autoPlayBtn.setFont(modernFont);
        autoPlayBtn.setBackground(new Color(33, 150, 243));
        autoPlayBtn.setForeground(Color.WHITE);
        autoPlayBtn.setFocusPainted(false);
        autoPlayBtn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        autoPlayBtn.addActionListener(e -> {
            if (autoTimer.isRunning()) {
                autoTimer.stop();
                autoPlayBtn.setText("Jeu Auto");
                nextTurnBtn.setEnabled(true);
            } else {
                autoTimer.start();
                autoPlayBtn.setText("Arrêt Auto");
                nextTurnBtn.setEnabled(false);
            }
        });

        restartBtn = new JButton("Redémarrer");
        restartBtn.setFont(modernFont);
        restartBtn.setBackground(new Color(244, 67, 54));
        restartBtn.setForeground(Color.WHITE);
        restartBtn.setFocusPainted(false);
        restartBtn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        restartBtn.addActionListener(e -> restartGame());

        controlPanel.add(nextTurnBtn);
        controlPanel.add(autoPlayBtn);
        controlPanel.add(restartBtn);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void playOneTurn() {
        if (grid.allBoatsDestroyed()) {
            endGame();
            return;
        }

        Position shot = ai.playTurn(grid, lastResult, lastShot);
        if (shot != null) {
            lastResult = grid.receiveShot(shot.x, shot.y);
            lastShot = shot;
            updateBoardColors();
            statusLabel.setText("Tours : " + ai.nbTurns + " | Mode IA : " + ai.mode + " | Dernier tir : " + lastResult);
        }
        
        if (grid.allBoatsDestroyed()) {
            endGame();
        }
    }

    private void updateBoardColors() {
        for (int y = 0; y < grid.cols; y++) {
            for (int x = 0; x < grid.rows; x++) {
                Cell cell = grid.grid[x][y];
                JLabel label = cellLabels[x][y];

                if (!cell.isShot) {
                    label.setBackground(new Color(30, 144, 255)); // Water
                } else if (!cell.hasBoat) {
                    label.setBackground(new Color(189, 189, 189)); // Miss
                } else if (cell.hasBoat && !cell.boat.isDestroyed()) {
                    label.setBackground(new Color(255, 167, 38)); // Hit
                } else if (cell.hasBoat && cell.boat.isDestroyed()) {
                    label.setBackground(new Color(229, 57, 53)); // Sunk
                }
            }
        }
    }

    private void restartGame() {
        if (autoTimer.isRunning()) {
            autoTimer.stop();
            autoPlayBtn.setText("Jeu Auto");
        }

        grid = new Grid(grid.rows, grid.cols);
        grid.placeAllBoatsRandomly();
        ai = new AI(grid.rows, grid.cols);
        lastResult = null;
        lastShot = null;

        nextTurnBtn.setEnabled(true);
        autoPlayBtn.setEnabled(true);
        statusLabel.setText("Tours : 0 | Mode IA : HUNT");
        updateBoardColors();
    }

    private void endGame() {
        if (autoTimer.isRunning()) autoTimer.stop();
        nextTurnBtn.setEnabled(false);
        autoPlayBtn.setEnabled(false);
        statusLabel.setText("Victoire ! Tous les bateaux détruits en " + ai.nbTurns + " tours.");
        JOptionPane.showMessageDialog(this, "Partie Terminée ! L'IA a gagné en " + ai.nbTurns + " tours.");
    }
}
