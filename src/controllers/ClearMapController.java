package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import models.Arena;
import static models.Constants.*;

public class ClearMapController implements ActionListener {
    JButton[][] arenaGrids;
    Arena userDefinedArena;
    int numOfRows;
    int numOfCols;

    public ClearMapController(JButton[][] arenaGrids, Arena userDefinedArena, int numOfRows, int numOfCols) {
        this.arenaGrids = arenaGrids;
        this.userDefinedArena = userDefinedArena;
        this.numOfCols = numOfCols;
        this.numOfRows = numOfRows;
    }

    public void actionPerformed(ActionEvent e) {
        for (int r = 0; r < numOfRows; r++) {
            for (int c = 0; c < numOfCols; c++) {
                untoggleObstacle(arenaGrids[r][c]);
                userDefinedArena.resetObstacle();
            }
        }
    }

    private void untoggleObstacle(JButton arenaGrids) {
        if (arenaGrids.getBackground() == OBSTACLE_COLOR) {
            arenaGrids.setBackground(MAP_COLOR);
        }
    }
}