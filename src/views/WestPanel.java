package views;

import javax.swing.*;
import java.awt.*;

import static models.Constants.*;

public class WestPanel extends JPanel{

    JPanel mainPanel;
    ArenaPanel arena;
    LabelDecoratorPanel labelDecoratorPanel;

    public WestPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(null);

        arena = new ArenaPanel(18, 1, Orientation.N);
        arena.setBackground(null);
        arena.setPreferredSize(new Dimension(ARENA_WIDTH * GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));
        arena.setOpaque(false);

        ButtonArenaPanel a = new ButtonArenaPanel();
        a.setBackground(null);
        a.setPreferredSize(new Dimension(ARENA_WIDTH * GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));

        labelDecoratorPanel = new LabelDecoratorPanel(arena);
        labelDecoratorPanel.setBackground(null);
        mainPanel.add(labelDecoratorPanel);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.MAGENTA);
        //buttonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, 0));

        JButton testMovement = new JButton("Test movement");
        //TODO add in listener
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
