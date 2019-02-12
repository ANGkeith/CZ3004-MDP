package views;

import utils.FileReaderWriter;
import controllers.ClearMapController;
import controllers.LoadMapController;
import controllers.ToggleButtonController;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import models.Arena;
import static models.Constants.*;

public class ObstaclePanel extends JPanel implements java.awt.event.ActionListener
{
    private JPanel mainPane;
    private JPanel topPane;
    private JPanel bottomPane;
    private JPanel rowPane;
    private JPanel arenaPane;
    private JButton[][] arenaGrids = new JButton[20][15];
    private ToggleButtonController toggleButtonController;
    private JButton dummyGrid = new JButton();
    private JLabel[] colLabels;
    private JLabel[] rowLabels;
    private Arena userDefinedArena;

    public ObstaclePanel()
    {
        mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.setBackground(null);


        topPane = new JPanel();
        topPane.setBackground(null);
        topPane.setLayout(new GridLayout(0, ARENA_WIDTH + 1));
        topPane.setPreferredSize(new Dimension(400, 25));
        dummyGrid.setVisible(false);
        topPane.add(dummyGrid);
        colLabels = new JLabel[15];
        for (int c = 0; c < 15; c++) {
            addLabel(colLabels, c, topPane);
        }

        bottomPane = new JPanel();
        bottomPane.setBackground(null);
        bottomPane.setLayout(new BoxLayout(bottomPane, 0));
        bottomPane.setPreferredSize(new Dimension(400, 500));


        rowPane = new JPanel();
        rowPane.setBackground(null);
        rowPane.setLayout(new GridLayout(20, 0));
        rowPane.setPreferredSize(new Dimension(25, 500));
        rowLabels = new JLabel[20];
        for (int c = 0; c < 20; c++) {
            addLabel(rowLabels, c, rowPane);
        }


        arenaPane = new JPanel(new GridLayout(0, 15));
        arenaPane.setBackground(null);
        arenaPane.setPreferredSize(new Dimension(375, 500));
        arenaGrids = new JButton[20][15];
        userDefinedArena = new Arena();
        /*
        try {
            FileReaderWriter fileReader = new FileReaderWriter(java.nio.file.FileSystems.getDefault().getPath(ARENA_DESCRIPTOR_PATH, new String[0]));
            userDefinedArena.binStringToArena(fileReader.read());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        */

        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 15; c++) {
                arenaGrids[r][c] = new JButton();
                createArenaGridLine(arenaGrids, r, c);
                if (isGoalZone(r, c)) {
                    createGoalZone(arenaGrids, r, c);
                } else if (isStartZone(r, c)) {
                    createStartZone(arenaGrids, r, c);
                } else {
                    if (userDefinedArena.getGrid(r, c).hasObstacle()) {
                        fillObstacle(arenaGrids, r, c);
                    }

                    toggleButtonController = new ToggleButtonController(arenaGrids[r][c], userDefinedArena.getGrid(r, c));
                    arenaGrids[r][c].addActionListener(toggleButtonController);
                }
                arenaPane.add(arenaGrids[r][c]);
            }
        }


        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.setBorder(new javax.swing.border.EmptyBorder(5, 0, 0, 0));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, 0));

        JButton loadMap = new JButton("Load Map");
        LoadMapController loadMapController = new LoadMapController(userDefinedArena);
        loadMap.addActionListener(loadMapController);

        JButton clearMap = new JButton("Clear");
        ClearMapController clearMapController = new ClearMapController(arenaGrids, userDefinedArena, 20, 15);
        clearMap.addActionListener(clearMapController);

        buttonPanel.add(loadMap);
        buttonPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 0)));
        buttonPanel.add(clearMap);
        bottomPane.add(rowPane);
        bottomPane.add(arenaPane);
        mainPane.add(topPane);
        mainPane.add(bottomPane);
        mainPane.add(buttonPanel);

        add(mainPane);
    }

    private void addLabel(JLabel[] labels, int index, JPanel jpane) {
        labels[index] = new JLabel(Integer.toString(index + 1), 0);
        jpane.add(labels[index]);
    }

    private void createArenaGridLine(JButton[][] arenaGrids, int r, int c) {
        if (r == 9) {
            arenaGrids[r][c].setBorder(new CompoundBorder(

                    BorderFactory.createMatteBorder(0, 0, 2, 0, ARENA_DIVIDER_LINE_COLOR),
                    BorderFactory.createMatteBorder(1, 1, 0, 1, ARENA_GRID_LINE_COLOR)));
        }
        else if (r == 10) {
            arenaGrids[r][c].setBorder(new CompoundBorder(

                    BorderFactory.createMatteBorder(2, 0, 0, 0, ARENA_DIVIDER_LINE_COLOR),
                    BorderFactory.createMatteBorder(0, 1, 1, 1, ARENA_GRID_LINE_COLOR)));
        }
        else {
            arenaGrids[r][c].setBorder(BorderFactory.createLineBorder(ARENA_GRID_LINE_COLOR));
        }
        arenaGrids[r][c].setBackground(MAP_COLOR);
    }

    private void createGoalZone(JButton[][] arenaGrids, int r, int c) {
        arenaGrids[r][c].setEnabled(false);
        arenaGrids[r][c].setBackground(GOAL_ZONE_COLOR);
        if ((r == 1) && (c == 13)) {
            arenaGrids[r][c].setText("G");
        }
    }

    private void createStartZone(JButton[][] arenaGrids, int r, int c) {
        arenaGrids[r][c].setEnabled(false);
        arenaGrids[r][c].setBackground(START_ZONE_COLOR);
        if ((r == 18) && (c == 1)) {
            arenaGrids[r][c].setText("S");
        }
    }

    private void fillObstacle(JButton[][] arenaGrids, int r, int c) {
        arenaGrids[r][c].setBackground(OBSTACLE_COLOR);
    }

    private boolean isGoalZone(int row, int col)
    {
        return (row < 3) && (col > 11);
    }

    private boolean isStartZone(int row, int col)
    {
        return (row > 16) && (col < 3);
    }

    public void actionPerformed(ActionEvent e) {

    }
}
