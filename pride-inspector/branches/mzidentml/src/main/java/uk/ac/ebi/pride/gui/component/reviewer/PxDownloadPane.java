package uk.ac.ebi.pride.gui.component.reviewer;

import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.GetPxSubmissionDetailTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Download panel for ProteomeXchange, allows users input a user name and password
 *
 * @author Rui Wang
 * @version $Id$
 */
public class PxDownloadPane extends JPanel implements ActionListener,TaskListener<java.util.List<Map<String, String>>, String> {

    private static final String USER_NAME_TITLE = "User Name";
    private static final String PASSWORD_TITLE = "Password";
    private static final String LOG_IN_BUTTON = "Login";

    private static final Dimension TXT_FIELD_SIZE = new Dimension(80, 20);

    private JTextField userField;
    private JPasswordField pwdField;
    private JLabel messageLabel;
    private PxDownloadSelectionPane selectionPanePride;

    /**
     * reference to desktop context
     */
    private PrideInspectorContext context;
    private Component parent;

    public PxDownloadPane(Component parent) {
        this.parent = parent;
        setupMainPane();
    }

    /**
     * Set up the main pane and add all components.
     */
    public void setupMainPane() {
        context = (PrideInspectorContext)uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(createLoginPane(), BorderLayout.NORTH);

        selectionPanePride = new PxDownloadSelectionPane(parent, true);
        selectionPanePride.setPreferredSize(new Dimension(720, 445));
        mainPanel.add(selectionPanePride, BorderLayout.CENTER);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(mainPanel);
    }

    /**
     * Create a log in panel
     *
     * @return
     */
    private JPanel createLoginPane() {
        JPanel loginPane = new JPanel(new BorderLayout());

        // input box panel
        JPanel inputBoxPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // username
        JLabel usrLabel = new JLabel(USER_NAME_TITLE);
        userField = new JTextField();
        userField.setPreferredSize(TXT_FIELD_SIZE);
        inputBoxPane.add(usrLabel);
        inputBoxPane.add(userField);
        // password
        JLabel pwdLabel = new JLabel(PASSWORD_TITLE);
        pwdField = new JPasswordField();
        pwdField.setPreferredSize(TXT_FIELD_SIZE);
        inputBoxPane.add(pwdLabel);
        inputBoxPane.add(pwdField);
        // login button
        JButton loginButton = new JButton(LOG_IN_BUTTON);
        loginButton.setActionCommand(LOG_IN_BUTTON);
        loginButton.addActionListener(this);
        inputBoxPane.add(loginButton);

        messageLabel = new JLabel();
        inputBoxPane.add(messageLabel);

        loginPane.add(inputBoxPane, BorderLayout.CENTER);

        // help button
        JPanel helpPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, "Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.px.download");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        helpPane.add(helpButton);

        loginPane.add(helpPane, BorderLayout.EAST);

        return loginPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String evtCmd = e.getActionCommand();

        if (LOG_IN_BUTTON.equals(evtCmd)) {
            loginButtonPressed();
        }
    }

    private void loginButtonPressed() {
        String currentUserName = userField.getText();
        char[] currentPassWord = pwdField.getPassword();
        GetPxSubmissionDetailTask reviewerTask = new GetPxSubmissionDetailTask(currentUserName, String.valueOf(currentPassWord));
        reviewerTask.setGUIBlocker(new DefaultGUIBlocker(reviewerTask, GUIBlocker.Scope.NONE, null));
        selectionPanePride.setCurrentUserName(currentUserName);
        selectionPanePride.setCurrentPassWord(String.valueOf(currentPassWord));
        reviewerTask.addTaskListener(selectionPanePride);
        reviewerTask.addTaskListener(this);
        reviewerTask.execute();
    }

    @Override
    public void started(TaskEvent<Void> event) {
        // set the login in progress icon
        Icon icon = GUIUtilities.loadIcon(context.getProperty("reviewer.login.in.progress.icon.small"));
        messageLabel.setIcon(icon);
        messageLabel.setText(null);
    }

    @Override
    public void process(TaskEvent<java.util.List<String>> listTaskEvent) {
        java.util.List<String> msgs = listTaskEvent.getValue();
        for (String msg : msgs) {
            if (msg.contains("Warning:")) {
                messageLabel.setForeground(new Color(255, 0, 0, 200));
                // set the login in progress icon
                Icon icon = GUIUtilities.loadIcon(context.getProperty("reviewer.login.error.icon.small"));
                messageLabel.setIcon(icon);
                messageLabel.setText(msg.replace("Warning:", ""));
            } else {
                // set the login in progress icon
                Icon icon = GUIUtilities.loadIcon(context.getProperty("reviewer.login.success.icon.small"));
                messageLabel.setIcon(icon);
                messageLabel.setText(null);
            }
        }
    }

    @Override
    public void succeed(TaskEvent<java.util.List<Map<String, String>>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
