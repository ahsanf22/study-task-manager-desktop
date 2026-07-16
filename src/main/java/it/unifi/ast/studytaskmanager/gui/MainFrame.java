package it.unifi.ast.studytaskmanager.gui;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainFrame(StudyTaskManagerPanel panel) {
        super("Study Task Manager Desktop");

        setName("mainFrame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel);
        setSize(900, 500);
        setLocationRelativeTo(null);
    }
}
