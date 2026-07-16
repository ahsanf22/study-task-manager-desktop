package it.unifi.ast.studytaskmanager;

import javax.swing.SwingUtilities;

import it.unifi.ast.studytaskmanager.gui.MainFrame;
import it.unifi.ast.studytaskmanager.gui.StudyTaskManagerPanel;

public final class StudyTaskManagerApp {

    public static final String APPLICATION_NAME = "Study Task Manager Desktop";

    private StudyTaskManagerApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudyTaskManagerApp::showApplication);
    }

    static void showApplication() {
        MainFrame frame = new MainFrame(new StudyTaskManagerPanel());
        frame.setVisible(true);
    }
}
