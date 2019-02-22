package views;

import controllers.ToggleButtonController;
import models.Arena;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static models.Constants.*;

public class ButtonArenaPanel extends JPanel {
    private JPanel buttonArenaPanel;
    public JButton[][] arenaGrids;
    private ToggleButtonController toggleButtonController;
    private Arena referenceArena;

    public ButtonArenaPanel(Arena referenceArena){
        buttonArenaPanel = new JPanel(new GridLayout(0, ARENA_WIDTH));

        buttonArenaPanel.setBackground(null);
        buttonArenaPanel.setPreferredSize(new Dimension(ARENA_WIDTH * GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));
        arenaGrids = new JButton[ARENA_HEIGHT][ARENA_WIDTH];
        this.referenceArena = referenceArena;


        for (int r = 0; r < ARENA_HEIGHT; r++) {
            for (int c = 0; c < ARENA_WIDTH; c++) {
                arenaGrids[r][c] = new JButton();
                arenaGrids[r][c].setToolTipText((r) +", " + (c));

                // Show tool tips immediately
                ToolTipManager.sharedInstance().setInitialDelay(0);

                createArenaGridLine(arenaGrids, r, c);
                if (Arena.isGoalZone(r, c)) {
                    createGoalZone(arenaGrids, r, c);
                } else if (Arena.isStartZone(r, c)) {
                    createStartZone(arenaGrids, r, c);
                } else {
                    if (referenceArena.getGrid(r, c).hasObstacle()) {
                        fillObstacle(arenaGrids, r, c);
                    }
                    toggleButtonController = new ToggleButtonController(arenaGrids[r][c], referenceArena.getGrid(r, c));
                    arenaGrids[r][c].addActionListener(toggleButtonController);
                }
                buttonArenaPanel.add(arenaGrids[r][c]);
            }
        }
        add(buttonArenaPanel);
    }

    private void createArenaGridLine(JButton[][] arenaGrids, int r, int c) {
        if (r == ARENA_HEIGHT/2 - 1) {
            arenaGrids[r][c].setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, ARENA_DIVIDER_LINE_COLOR),
                    BorderFactory.createMatteBorder(1, 1, 0, 1, ARENA_GRID_LINE_COLOR)));
        }
        else if (r == ARENA_HEIGHT/2) {
            arenaGrids[r][c].setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 0, 0, ARENA_DIVIDER_LINE_COLOR),
                    BorderFactory.createMatteBorder(0, 1, 1, 1, ARENA_GRID_LINE_COLOR)));
        }
        else {
            arenaGrids[r][c].setBorder(BorderFactory.createLineBorder(ARENA_GRID_LINE_COLOR));
        }
        arenaGrids[r][c].setBackground(EXPLORED_COLOR);
    }

    private void createGoalZone(JButton[][] arenaGrids, int r, int c) {
        arenaGrids[r][c].setEnabled(false);
        arenaGrids[r][c].setBackground(GOAL_ZONE_COLOR);
        if ((r == ZONE_SIZE/2) && (c == ARENA_WIDTH - 1 - ZONE_SIZE/2)) {
            arenaGrids[r][c].setText("G");
        }
    }

    private void createStartZone(JButton[][] arenaGrids, int r, int c) {
        arenaGrids[r][c].setEnabled(false);
        arenaGrids[r][c].setBackground(START_ZONE_COLOR);
        if ((r == ARENA_HEIGHT - 1 - ZONE_SIZE/2 ) && (c == ZONE_SIZE/2)) {
            arenaGrids[r][c].setText("S");
        }
    }

    private void fillObstacle(JButton[][] arenaGrids, int r, int c) {
        arenaGrids[r][c].setBackground(OBSTACLE_COLOR);
    }

}
