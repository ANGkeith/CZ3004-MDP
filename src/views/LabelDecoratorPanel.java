package views;

import javax.swing.*;
import java.awt.*;

import static models.Constants.*;

public class LabelDecoratorPanel extends JPanel {
    private JPanel mainPane;
    private JPanel colPane;
    private JPanel bottomPane;
    private JPanel rowPane;
    private JButton dummyGrid = new JButton();
    private JLabel colLabel;
    private JLabel rowLabel;

    public LabelDecoratorPanel(JPanel arenaBoard) {
        mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.setBackground(null);


        colPane = new JPanel();
        colPane.setBackground(null);
        colPane.setLayout(new GridLayout(0, ARENA_WIDTH + 1));
        colPane.setPreferredSize(new Dimension((ARENA_WIDTH + 1) * GRID_SIZE, GRID_SIZE));
        dummyGrid.setVisible(false);
        colPane.add(dummyGrid);
        for (int c = 0; c < ARENA_WIDTH; c++) {
            colLabel = generateLabel(c);
            colPane.add(colLabel);
        }
        mainPane.add(colPane);

        bottomPane = new JPanel();
        bottomPane.setBackground(null);
        bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
        bottomPane.setPreferredSize(new Dimension((ARENA_WIDTH + 1) * GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));


        rowPane = new JPanel();
        rowPane.setBackground(null);
        rowPane.setLayout(new GridLayout(ARENA_HEIGHT, 1));
        rowPane.setPreferredSize(new Dimension(GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));
        for (int c = 0; c < ARENA_HEIGHT; c++) {
            rowLabel = generateLabel(c);
            rowPane.add(rowLabel);
        }
        bottomPane.add(rowPane);

        bottomPane.add(arenaBoard);

        mainPane.add(bottomPane);
        add(mainPane);
    }

    private JLabel generateLabel(int index) {
        JLabel label = new JLabel(Integer.toString(index), SwingConstants.HORIZONTAL);
        label.setForeground(ARENA_LABEL_COLOR);
        return label;

    }

}
