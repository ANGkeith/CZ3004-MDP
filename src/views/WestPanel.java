package views;

import javax.swing.*;
import java.awt.*;

import static models.Constants.*;

public class WestPanel extends JPanel{

    private JPanel mainPanel;
    private ArenaPanel arena;
    private LabelDecoratorPanel labelDecoratorPanel;

    public WestPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(null);

        arena = new ArenaPanel(18, 1, Orientation.N);
        arena.setBackground(null);
        arena.setPreferredSize(new Dimension(ARENA_WIDTH * GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));
        arena.setOpaque(false);

        labelDecoratorPanel = new LabelDecoratorPanel(arena);
        labelDecoratorPanel.setBackground(null);
        mainPanel.add(labelDecoratorPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, 0));

        JButton testMovement = new JButton("Test movement");
        testMovement.addActionListener( e -> enableArrowKeyMovement());
        buttonPanel.add(testMovement);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    // Listener Logic
    private void enableArrowKeyMovement() {
        arena.requestFocus();
    }
}
