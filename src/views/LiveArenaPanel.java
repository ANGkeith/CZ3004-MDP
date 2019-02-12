package views;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import static models.Constants.*;


public class LiveArenaPanel
        extends JPanel
{
    private JPanel mainPane;
    private JPanel topPane;
    private JPanel bottomPane;
    private JPanel rowPane;
    private ArenaPanel arenaPane;
    private JButton dummyGrid = new JButton();
    private JLabel[] colLabels;
    private JLabel[] rowLabels;

    public LiveArenaPanel()
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
        bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
        bottomPane.setPreferredSize(new Dimension(400, 500));
        bottomPane.setBackground(null);


        rowPane = new JPanel();
        rowPane.setBackground(null);
        rowPane.setLayout(new GridLayout(20, BoxLayout.X_AXIS));
        rowPane.setPreferredSize(new Dimension(25, 500));
        rowLabels = new JLabel[20];
        for (int c = 0; c < 20; c++) {
            addLabel(rowLabels, c, rowPane);
        }


        arenaPane = new ArenaPanel(18, 1, Orientation.N);
        arenaPane.setBackground(null);
        arenaPane.setPreferredSize(new Dimension(375, 500));
        arenaPane.setOpaque(false);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, 0));

        JButton testMovement = new JButton("Test movement");

        buttonPanel.add(testMovement);


        bottomPane.add(rowPane);
        bottomPane.add(arenaPane);
        mainPane.add(topPane);
        mainPane.add(bottomPane);
        mainPane.add(buttonPanel);

        add(mainPane);
    }

    private void addLabel(JLabel[] labels, int index, JPanel jpane)
    {
        labels[index] = new JLabel(Integer.toString(index + 1), 0);
        jpane.add(labels[index]);
    }
}