package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import static models.Constants.*;
import models.Grid;

public class ToggleButtonController implements ActionListener
{
    JButton arenaGrid;
    Grid grid;

    public ToggleButtonController(JButton arenaGrid, Grid grid)
    {
        this.arenaGrid = arenaGrid;
        this.grid = grid;
    }

    public void actionPerformed(ActionEvent e) {
        toggleButtonColor(arenaGrid);
    }

    private void toggleButtonColor(JButton arenaGrid) {
        if (arenaGrid.getBackground() == EXPLORED_COLOR) {
            arenaGrid.setBackground(OBSTACLE_COLOR);
        } else if (arenaGrid.getBackground() == GOAL_ZONE_COLOR || arenaGrid.getBackground() == START_ZONE_COLOR) {
            arenaGrid.setBackground(OBSTACLE_COLOR);
        } else {
            arenaGrid.setBackground(EXPLORED_COLOR);
        }
        grid.toggleObstacle();
    }
}
