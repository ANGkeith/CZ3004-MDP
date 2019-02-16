package views;

import models.MyRobot;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import static models.Constants.*;

public class CenterPanel extends JPanel {


    private JLabel orientationLbl;
    private JLabel[] lbls;
    private JTextField[] fields;

    private JComboBox <String> orientationSelection;
    private JButton cancelBtn;
    private JButton modifyBtn;
    private JButton okBtn;
    private JPanel buttonPanel;
    private JButton explorationBtn;
    private JButton fastestPathBtn;
    private JButton restartBtn;
    private JLabel title;
    private JPanel mainPanel;
    private JPanel configPanel;


    public CenterPanel(MyRobot myRobot) {
        mainPanel = new JPanel();
        mainPanel.setBackground(null);
        mainPanel.setPreferredSize(new Dimension(300, 650));
        mainPanel.setLayout(new MigLayout("fillx"));

        title = new JLabel("MDP GROUP 2");
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 30));

        explorationBtn = new JButton("Start Exploration ");
        fastestPathBtn = new JButton("Start Fastest Path");
        restartBtn = new JButton("Restart");

        configPanel = new JPanel(new MigLayout("fillx"));
        configPanel.setBorder(new TitledBorder("Robot's Configuration"));

        lbls = new JLabel[4];
        lbls[0] = new JLabel("Starting position:");
        lbls[1] = new JLabel("Forward Speed (s):");
        lbls[2] = new JLabel("90 degree turn Speed (s):");
        lbls[3] = new JLabel("Enter way point:");

        fields = new JTextField[4];
        fields[0] = new JTextField((myRobot.getCurRow()+1) + ", " + (myRobot.getCurCol()+1));
        fields[1] = new JTextField();
        fields[2] = new JTextField();
        fields[3] = new JTextField();

        orientationLbl = new JLabel("Starting orientation:");
        orientationSelection = new JComboBox<>(orientationList);
        orientationSelection.setSelectedItem(orientationEnumToString(myRobot.getCurOrientation()));

        configPanel.add(orientationLbl);
        orientationSelection.setEnabled(false);
        configPanel.add(orientationSelection, "wrap");

        for (int i = 0; i < lbls.length; i++) {
            fields[i].setEnabled(false);
            configPanel.add(lbls[i]);
            configPanel.add(fields[i], "wrap, growx");
        }


        buttonPanel = new JPanel(new MigLayout("fillx, inset 0"));
        okBtn = new JButton("Ok");
        okBtn.setEnabled(false);
        cancelBtn = new JButton("Cancel");
        cancelBtn.setEnabled(false);
        modifyBtn = new JButton("Modify");
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(modifyBtn);

        mainPanel.add(title, "alignx center, spanx, wrap");

        configPanel.add(buttonPanel, "gapy 10 0, spanx, pushx, alignx right");
        mainPanel.add(configPanel, "pushx, growx, gapy 10 0, wrap");

        mainPanel.add(explorationBtn, "gapy 50 0, wrap, alignx center");
        mainPanel.add(fastestPathBtn, "gapy 10 0, wrap, alignx center");
        mainPanel.add(restartBtn, "gapy 10 0, wrap, alignx center");

        add(mainPanel);
    }



    // utils
    public String orientationEnumToString(Orientation o) {
        if (o == Orientation.N) {
            return "North";
        } else if (o == Orientation.E) {
            return "East";
        } else if (o == Orientation.S) {
            return "South";
        }
        return "West";
    }

    // Listener
    public void addExplorationBtnListener(ActionListener a) {
        explorationBtn.addActionListener(a);
    }
    public void addFastestPathBtnListener(ActionListener a) {
        fastestPathBtn.addActionListener(a);
    }
    public void addOkBtnListener(ActionListener a) {
        okBtn.addActionListener(a);
    }

    public void addCancelBtnListener(ActionListener a) {
        cancelBtn.addActionListener(a);
    }

    public void addModifyBtnListener(ActionListener a) {
        modifyBtn.addActionListener(a);
    }

    public void addRestartBtnListener(ActionListener a) {
        restartBtn.addActionListener(a);
    }


    // getter & setters
    public JComboBox<String> getOrientationSelection() {
        return orientationSelection;
    }

    public JButton getCancelBtn() {
        return cancelBtn;
    }

    public JButton getModifyBtn() {
        return modifyBtn;
    }

    public JButton getOkBtn() {
        return okBtn;
    }

    public JLabel[] getLbls() {
        return lbls;
    }

    public JTextField[] getFields() {
        return fields;
    }
}
