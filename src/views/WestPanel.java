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

    public WestPanel(MyRobot myRobot) {
        this.myRobot = myRobot;
        this.arena = myRobot.getArena();

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(null);

        mainPanel.add(javax.swing.Box.createRigidArea(new Dimension(0, 40)));

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
        testMovement.setToolTipText("Activate arrow key movement");
        buttonPanel.add(testMovement);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    public void addTestMovementListener(ActionListener a) {
        testMovement.addActionListener(a);
    }
}
