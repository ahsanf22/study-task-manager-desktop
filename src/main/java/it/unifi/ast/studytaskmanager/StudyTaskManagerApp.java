package it.unifi.ast.studytaskmanager;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import it.unifi.ast.studytaskmanager.config.ApplicationConfiguration;
import it.unifi.ast.studytaskmanager.gui.MainFrame;

public final class StudyTaskManagerApp {

    public static final String APPLICATION_NAME = "Study Task Manager Desktop";

    private StudyTaskManagerApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudyTaskManagerApp::showApplication);
    }

    static void showApplication() {
        try {
            ApplicationConfiguration configuration = ApplicationConfiguration.create();
            MainFrame frame = configuration.createMainFrame();

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent event) {
                    configuration.close();
                }
            });

            frame.setVisible(true);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not start application: " + exception.getMessage(),
                    APPLICATION_NAME,
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
