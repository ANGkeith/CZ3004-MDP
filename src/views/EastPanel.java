package views;

import models.Arena;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


import java.awt.*;
import java.awt.event.ActionListener;

import static models.Constants.*;

public class EastPanel extends JPanel{

    private Arena referenceArena;
    private JPanel mainPanel;
    private ButtonArenaPanel buttonArenaPanel;
    private LabelDecoratorPanel labelDecoratorPanel;

    private JPanel buttonPanel;

    private JButton saveButton;
    private JButton clearButton;

    public EastPanel(Arena referenceArena) {
        this.referenceArena = referenceArena;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(null);

        mainPanel.add(javax.swing.Box.createRigidArea(new Dimension(0, 40)));


        buttonArenaPanel = new ButtonArenaPanel(referenceArena);
        buttonArenaPanel.setBorder(new EmptyBorder(-5, 0, 0, 0));
        buttonArenaPanel.setBackground(null);
        buttonArenaPanel.setPreferredSize(new Dimension(ARENA_WIDTH * GRID_SIZE, ARENA_HEIGHT * GRID_SIZE));

        labelDecoratorPanel = new LabelDecoratorPanel(buttonArenaPanel);
        labelDecoratorPanel.setBackground(null);
        mainPanel.add(labelDecoratorPanel);

        buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        saveButton = new JButton("Save Map");
        buttonPanel.add(saveButton);

        buttonPanel.add(javax.swing.Box.createRigidArea(new Dimension(10, 0)));

        clearButton = new JButton("Clear");
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel);
        add(mainPanel);
    }

    public void addSaveBtnListener(ActionListener a) {
        saveButton.addActionListener(a);
    }

    public void addClearBtnListener(ActionListener a) {
        clearButton.addActionListener(a);
    }

    public Arena getReferenceArena() {
        return referenceArena;
    }

    public ButtonArenaPanel getButtonArenaPanel() {
        return buttonArenaPanel;
    }
}
