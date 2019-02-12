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
        if (arenaGrid.getBackground() == MAP_COLOR) {
            arenaGrid.setBackground(OBSTACLE_COLOR);
        } else {
            arenaGrid.setBackground(MAP_COLOR);
        }
        grid.toggleObstacle();
    }
}
