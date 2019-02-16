package views;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import static models.Constants.*;

public class CenterPanel extends JPanel {


    private JLabel[] lbls;
    private JTextField[] fields;
    private JLabel orientationLbl;
    private JComboBox <String> orientationSelection;
    private JButton cancelBtn;
    private JButton modifyBtn;
    private JButton okBtn;

    public CenterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(null);
        mainPanel.setPreferredSize(new Dimension(300, 650));
        mainPanel.setLayout(new MigLayout("fillx"));

        JLabel title = new JLabel("MDP GROUP 2");
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 30));

        JButton explorationBtn = new JButton("Start Exploration ");
        JButton fastestPathBtn = new JButton("Start Fastest Path");
        JButton restartBtn = new JButton("Restart");



        JPanel configPanel = new JPanel(new MigLayout("fillx"));
        configPanel.setBorder(new TitledBorder("Robot's Configuration"));

        lbls = new JLabel[4];
        lbls[0] = new JLabel("Starting position:");
        lbls[1] = new JLabel("Forward Speed (s):");
        lbls[2] = new JLabel("90 degree turn Speed (s):");
        lbls[3] = new JLabel("Enter way point:");

        fields = new JTextField[4];
        fields[0] = new JTextField();
        fields[1] = new JTextField();
        fields[2] = new JTextField();
        fields[3] = new JTextField();

        orientationLbl = new JLabel("Starting orientation:");

        orientationSelection = new JComboBox<>(orientationList);

        for (int i = 0; i < lbls.length; i++) {
            fields[i].setEnabled(false);
            if (i == 1) {
                configPanel.add(orientationLbl);
                orientationSelection.setEnabled(false);
                configPanel.add(orientationSelection, "wrap");
            }
            configPanel.add(lbls[i]);
            configPanel.add(fields[i], "wrap, growx");
        }


        JPanel buttonPanel = new JPanel(new MigLayout("fillx, inset 0"));
        okBtn = new JButton("Ok");
        okBtn.setEnabled(false);
        okBtn.addActionListener(e -> saveConfigurations());
        cancelBtn = new JButton("Cancel");
        cancelBtn.setEnabled(false);
        cancelBtn.addActionListener(e -> disableConfigurations());
        modifyBtn = new JButton("Modify");
        modifyBtn.addActionListener(e -> enableConfigurations());
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(modifyBtn);

        mainPanel.add(title, "alignx center, spanx, wrap");

        mainPanel.add(explorationBtn, "gapy 50 0, wrap, alignx center");
        mainPanel.add(fastestPathBtn, "gapy 10 0, wrap, alignx center");
        mainPanel.add(restartBtn, "gapy 10 0, wrap, alignx center");

        configPanel.add(buttonPanel, "gapy 10 0, spanx, pushx, alignx right");
        mainPanel.add(configPanel, "pushx, growx, gapy 50 0, wrap");

        add(mainPanel);
    }


    private void enableConfigurations() {
        for (int i = 0; i < lbls.length; i++) {
            fields[i].setEnabled(true);
        }
        okBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
        modifyBtn.setEnabled(false);
        orientationSelection.setEnabled(true);
    }

    private void disableConfigurations() {
        for (int i = 0; i < lbls.length; i++) {
            fields[i].setEnabled(false);
        }
        okBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        modifyBtn.setEnabled(true);
        orientationSelection.setEnabled(false);
    }

    private void saveConfigurations() {
        disableConfigurations();
        // TODO set model
    }
}
