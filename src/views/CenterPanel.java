package views;

import controllers.SimulatorController;
import models.Arena;
import models.MyRobot;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static models.Constants.*;

public class CenterPanel extends JPanel implements PropertyChangeListener {

    public final String[] statusPrefixedLbls;

    private JLabel orientationLbl;
    private JLabel[] lbls;
    private JTextField[] fields;

    private JComboBox <String> orientationSelection;
    private JButton cancelBtn;
    private JButton modifyBtn;
    private JButton okBtn;
    private JPanel buttonPanel;
    private JButton rpiBtn;
    private JButton explorationBtn;
    private JButton timeLimitedExplorationBtn;
    private JButton coverageLimitedExplorationBtn;
    private JButton fastestPathBtn;
    private JButton restartBtn;
    private JButton mapDescriptorP1;
    private JButton mapDescriptorP2;
    private JLabel title;
    private JLabel[] statusLbls;
    private JPanel mainPanel;
    private JPanel configPanel;
    private JPanel statusPanel;
    private MyRobot myRobot;


    public CenterPanel(MyRobot myRobot) {
        this.myRobot = myRobot;
        myRobot.addPropertyChangeListener(this);

        mainPanel = new JPanel();
        mainPanel.setBackground(null);
        mainPanel.setPreferredSize(new Dimension(300, 700));
        mainPanel.setLayout(new MigLayout("fillx"));

        title = new JLabel("MDP GROUP 2");
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, 40));

        rpiBtn = new JButton("Connect to RPI");
        rpiBtn.setToolTipText("Left Click to connect to RPI");
        explorationBtn = new JButton( "Exploration");
        explorationBtn.setToolTipText("Right Click to Explore Using Basic Right-Hugging Algorithm");
        fastestPathBtn = new JButton("Fastest Path");
        fastestPathBtn.setEnabled(false);
        coverageLimitedExplorationBtn = new JButton("Coverage-Limited Exploration ");
        timeLimitedExplorationBtn = new JButton("Time-Limited Exploration");
        restartBtn = new JButton("Restart");

        // Config Panel
        configPanel = new JPanel(new MigLayout("fillx"));
        configPanel.setBorder(new TitledBorder("Robot's Configuration"));

        orientationLbl = new JLabel("Starting orientation:");
        orientationSelection = new JComboBox<>(orientationList);
        orientationSelection.setSelectedItem(orientationEnumToString(myRobot.getStartOrientation()));

        lbls = new JLabel[6];
        lbls[0] = new JLabel("Starting position:");
        lbls[1] = new JLabel("Time taken to move a grid (s):");
        lbls[2] = new JLabel("Time taken to make a turn (s):");
        lbls[3] = new JLabel("Way point:");
        lbls[4] = new JLabel("Coverage-limit (%):");
        lbls[5] = new JLabel("Exploration time-limit (m : s):");

        fields = new JTextField[6];
        fields[0] = new JTextField((Arena.getActualRowFromRow(myRobot.getStartRow())) + ", " + (myRobot.getStartCol()));
        fields[1] = new JTextField(Double.toString(myRobot.getForwardSpeed()));
        fields[2] = new JTextField(Double.toString(myRobot.getTurningSpeed()));
        fields[3] = new JTextField(Arena.getActualRowFromRow(myRobot.getWayPointRow()) + ", " + myRobot.getWayPointCol());
        fields[4] = new JTextField(Double.toString(myRobot.getExplorationCoverageLimit()));
        fields[5] = new JTextField(myRobot.getExplorationTimeLimitFormatted());

        configPanel.add(orientationLbl);
        orientationSelection.setEnabled(false);
        configPanel.add(orientationSelection, "wrap");

        for (int i = 0; i < lbls.length; i++) {
            fields[i].setEnabled(false);
            configPanel.add(lbls[i]);
            configPanel.add(fields[i], "wrap, growx");
        }


        buttonPanel = new JPanel(new MigLayout("fillx, inset 0"));
        buttonPanel.setBackground(null);
        okBtn = new JButton("Ok");
        okBtn.setEnabled(false);
        cancelBtn = new JButton("Cancel");
        cancelBtn.setEnabled(false);
        modifyBtn = new JButton("Modify");
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(modifyBtn);
        // Config Panel

        // Status Panel
        statusPanel = new JPanel(new MigLayout("fillx"));
        statusPanel.setBorder(new TitledBorder("Status"));

        statusPrefixedLbls = new String[4];
        statusPrefixedLbls[0] = "Time Elapsed (s): ";
        statusPrefixedLbls[1] = "Coverage (%): ";
        statusPrefixedLbls[2] = "Number of Forward: ";
        statusPrefixedLbls[3] = "Number of Turn: ";
        statusLbls = new JLabel[4];

        for (int i = 0; i  < statusLbls.length; i++) {
            statusLbls[i] = new JLabel(statusPrefixedLbls[i] + 0);
            statusPanel.add(statusLbls[i], "wrap");
        }

        mapDescriptorP1 = new JButton("P1");
        mapDescriptorP1.setToolTipText("Copy to system clipboard");
        mapDescriptorP2 = new JButton("P2");
        mapDescriptorP2.setToolTipText("Copy to system clipboard");

        statusPanel.add(mapDescriptorP1, "split2, growx");
        statusPanel.add(mapDescriptorP2, "growx");

        // Status Panel

        mainPanel.add(title, "alignx center, spanx, wrap");
        mainPanel.add(rpiBtn, "gapy 10 0, growx, wrap, alignx center");
        mainPanel.add(explorationBtn, "gapy 20 0, split2, growx, alignx center");
        mainPanel.add(fastestPathBtn, "gapy 10 0, growx, wrap, alignx center");
        mainPanel.add(coverageLimitedExplorationBtn, "gapy 10 0, growx, wrap, alignx center");
        mainPanel.add(timeLimitedExplorationBtn, "gapy 10 0, growx, wrap, alignx center");
        mainPanel.add(restartBtn, "gapy 10 0, growx, wrap, alignx center");

        configPanel.add(buttonPanel, "gapy 10 0, spanx, pushx, alignx right");
        mainPanel.add(configPanel, "pushx, growx, gapy 10 0, wrap");
        mainPanel.add(statusPanel, "pushx, growx, gapy 10 0, wrap");

        add(mainPanel);
    }



    // utils
    public void reinitStatusPanelTxt(){
        for (int i = 0; i  < statusLbls.length; i++) {
            statusLbls[i].setText(statusPrefixedLbls[i] + 0);
        }
        SimulatorController.timeElapsed[0] = 0;
        SimulatorController.numTurn = 0;
        SimulatorController.numFwd = 0;
    }
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

    public void setExplorationAndFastestPathBtns(Boolean isEnable) {
    	rpiBtn.setEnabled(isEnable);
        explorationBtn.setEnabled(isEnable);
        fastestPathBtn.setEnabled(isEnable);
        timeLimitedExplorationBtn.setEnabled(isEnable);
        coverageLimitedExplorationBtn.setEnabled(isEnable);
    }

    // Listener
    public void addRPIBtnListener(ActionListener a) {
        rpiBtn.addActionListener(a);
    }
    public void addRPIRightClickListener(MouseListener m) {
    	rpiBtn.addMouseListener(m);
    }
    public void addExplorationBtnListener(ActionListener a) {
        explorationBtn.addActionListener(a);
    }
    public void addExplorationRightClickListener(MouseListener m) {
        explorationBtn.addMouseListener(m);
    }
    public void addFastestPathBtnListener(ActionListener a) {
        fastestPathBtn.addActionListener(a);
    }
    public void addCoverageLimitedExplorationBtnListener(ActionListener a) {
        coverageLimitedExplorationBtn.addActionListener(a);
    }
    public void addTimeLimitedExplorationBtnListener(ActionListener a) {
        timeLimitedExplorationBtn.addActionListener(a);
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

    public void addMapDescriptorP1Listener(ActionListener a) {
        mapDescriptorP1.addActionListener(a);
    }

    public void addMapDescriptorP2Listener(ActionListener a) {
        mapDescriptorP2.addActionListener(a);
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

    public JButton getRestartBtn() {
        return restartBtn;
    }

    public JButton getFastestPathBtn () {
        return fastestPathBtn;
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

    public JLabel[] getStatusLbls() {
        return statusLbls;
    }

    public JButton getRpiBtn() {
        return rpiBtn;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (MyRobot.WAYPOINT_UPDATE.equals(evt.getPropertyName())) {
            fields[3].setText(Arena.getActualRowFromRow(myRobot.getWayPointRow()) + ", " + myRobot.getWayPointCol());
        }
        if (MyRobot.START_POSITION_UPDATE.equals(evt.getPropertyName())) {
            fields[3].setText(Arena.getActualRowFromRow(myRobot.getWayPointRow()) + ", " + myRobot.getWayPointCol());
            orientationSelection.setSelectedItem(orientationEnumToString(myRobot.getStartOrientation()));
            fields[0].setText(Arena.getActualRowFromRow(myRobot.getStartRow()) + ", " + (myRobot.getStartCol()));
        }
    }
}
