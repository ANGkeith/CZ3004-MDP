
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

import views.*;


public class App extends JFrame {
    private JPanel contentPane;
    private WestPanel westPanel;
    private EastPanel eastPanel;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    App frame = new App();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public App() {
        setResizable(true);
        setTitle("MDP Simulator");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setSize(1200, 650);

        setLocationRelativeTo(null);
    }





    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);


        westPanel = new WestPanel();
        westPanel.setBackground(null);
        westPanel.setVisible(true);
        contentPane.add(westPanel, "West");

        eastPanel = new EastPanel();
        eastPanel.setBackground(null);
        eastPanel.setVisible(true);
        contentPane.add(eastPanel, "East");

    }
}