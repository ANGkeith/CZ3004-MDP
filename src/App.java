
import java.awt.*;
import javax.swing.*;

import views.*;
import static models.Constants.*;

public class App extends JFrame {
    private JPanel contentPane;
    private CenterPanel centerPanel;
    private EastPanel eastPanel;
    private WestPanel westPanel;

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
        setResizable(false);
        setTitle("MDP Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        setSize(APP_WIDTH, APP_HEIGHT);
        pack();
        setLocationRelativeTo(null);

    }





    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        centerPanel = new CenterPanel();
        centerPanel.setBackground(null);
        centerPanel.setVisible(true);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        westPanel = new WestPanel();
        westPanel.setBackground(null);
        westPanel.setVisible(true);
        contentPane.add(westPanel, BorderLayout.WEST);

        eastPanel = new EastPanel();
        eastPanel.setBackground(null);
        eastPanel.setVisible(true);
        contentPane.add(eastPanel, BorderLayout.EAST);

    }
}