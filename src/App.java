
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;

import views.*;


public class App extends JFrame {
    private JPanel contentPane;
    private ObstaclePanel obstaclePane;
    private LiveArenaPanel liveArenaPane;

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
        setDefaultCloseOperation(3);
        initComponents();
        setSize(1200, 600);

        setLocationRelativeTo(null);
    }





    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBackground(Color.DARK_GRAY);
        setContentPane(contentPane);

        obstaclePane = new ObstaclePanel();
        obstaclePane.setVisible(true);
        contentPane.add(obstaclePane, "East");

        liveArenaPane = new LiveArenaPanel();
        liveArenaPane.setVisible(true);
        contentPane.add(liveArenaPane, "West");
    }
}