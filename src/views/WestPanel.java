package views;

import models.Arena;
import models.MyRobot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static models.Constants.*;

public class WestPanel extends JPanel{

    private JPanel mainPanel;
    public ArenaPanel arenaPanel;
    private LabelDecoratorPanel labelDecoratorPanel;
    private JButton testMovement;
    private JPanel buttonPanel;

    private MyRobot myRobot;
    private Arena arena;

    public WestPanel(MyRobot myRobot, Arena arena) {
        this.myRobot = myRobot;
        this.arena = arena;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(null);

        arenaPanel = new ArenaPanel(myRobot, arena);
        arenaPanel.setBackground(null);
        arenaPanel.setPreferredSize(new Dimension(ARENA_WIDTH * GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));
        arenaPanel.setOpaque(false);

        labelDecoratorPanel = new LabelDecoratorPanel(arenaPanel);
        labelDecoratorPanel.setBackground(null);
        mainPanel.add(labelDecoratorPanel);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        testMovement = new JButton("Test movement");
        buttonPanel.add(testMovement);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    public void addTestMovementListener(ActionListener a) {
        testMovement.addActionListener(a);
    }
}
