package views;

import utils.FileReaderWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


import java.awt.*;
import java.io.IOException;
import java.nio.file.FileSystems;

import static models.Constants.*;

public class EastPanel extends JPanel{

    private JPanel mainPanel;
    private ButtonArenaPanel arena;
    private LabelDecoratorPanel labelDecoratorPanel;

    public EastPanel() {

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(null);

        arena = new ButtonArenaPanel();
        arena.setBorder(new EmptyBorder(-5, 0, 0, 0));
        arena.setBackground(null);
        arena.setPreferredSize(new Dimension(ARENA_WIDTH * GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));

        labelDecoratorPanel = new LabelDecoratorPanel(arena);
        labelDecoratorPanel.setBackground(null);
        mainPanel.add(labelDecoratorPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton loadButton = new JButton("Load Map");
        loadButton.addActionListener(e -> loadMap());
        buttonPanel.add(loadButton);

        buttonPanel.add(javax.swing.Box.createRigidArea(new Dimension(10, 0)));

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clearObstacle());
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel);
        add(mainPanel);
    }

    private void loadMap() {
        try {
            FileReaderWriter fileWriter = new FileReaderWriter(FileSystems.getDefault().getPath(ARENA_DESCRIPTOR_PATH, new String[0]));
            fileWriter.write(arena.userDefinedArena.obstacleToString());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void clearObstacle() {
        for (int r = 0; r < ARENA_HEIGHT; r++) {
            for (int c = 0; c < ARENA_WIDTH; c++) {
                untoggleObstacle(arena.arenaGrids[r][c]);
                arena.userDefinedArena.resetObstacle();
            }
        }
    }

    private void untoggleObstacle(JButton arenaGrids) {
        if (arenaGrids.getBackground() == OBSTACLE_COLOR) {
            arenaGrids.setBackground(MAP_COLOR);
        }
    }
}
