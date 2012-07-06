package uk.ac.ebi.pride.gui.component.reviewer;

import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.task.impl.GetPrideExperimentDetailTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Download panel for PRIDE experiments
 *
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 09:43:33
 */
public class PrideDownloadPane extends JPanel implements ActionListener {

    private static final String MSG_TITLE = "Message";
    private static final String USER_NAME_TITLE = "User Name";
    private static final String PASSWORD_TITLE = "Password";
    private static final String LOG_IN_BUTTON = "Login";

    private static final Dimension TXT_FIELD_SIZE = new Dimension(80, 20);
    private static final Dimension MSG_PANE_SIZE = new Dimension(650, 100);

    private JTextField userField;
    private JPasswordField pwdField;
    private MessageLabel msgLabel;
    private PrideDownloadSelectionPane selectionPanePride;

    /**
     * reference to desktop context
     */
    private PrideInspectorContext context;
    private Component parent;

    public PrideDownloadPane(Component parent) {
        this.parent = parent;
        setupMainPane();
    }

    /**
     * Set up the main pane and add all components.
     */
    public void setupMainPane() {
        context = (PrideInspectorContext)uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(createLoginPane(), BorderLayout.NORTH);
        northPanel.add(createMsgPane(), BorderLayout.CENTER);
        mainPanel.add(northPanel, BorderLayout.NORTH);


        selectionPanePride = new PrideDownloadSelectionPane(parent, true);
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

        loginPane.add(inputBoxPane, BorderLayout.CENTER);

        // help button
        JPanel helpPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, "Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.pride.download");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        helpPane.add(helpButton);

        loginPane.add(helpPane, BorderLayout.EAST);

        return loginPane;
    }

    private JPanel createMsgPane() {
        JPanel msgPane = new JPanel();
        msgPane.setLayout(new BorderLayout());
        msgPane.setBorder(BorderFactory.createTitledBorder(MSG_TITLE));
        msgPane.setMaximumSize(MSG_PANE_SIZE);
        msgPane.setVisible(false);
        msgLabel = new MessageLabel(msgPane);
        msgLabel.setOpaque(false);
        msgPane.add(msgLabel);
        return msgPane;
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
        GetPrideExperimentDetailTask reviewerTask = new GetPrideExperimentDetailTask(currentUserName, String.valueOf(currentPassWord));
        reviewerTask.setGUIBlocker(new DefaultGUIBlocker(reviewerTask, GUIBlocker.Scope.NONE, null));
        selectionPanePride.setCurrentUserName(currentUserName);
        selectionPanePride.setCurrentPassWord(String.valueOf(currentPassWord));
        reviewerTask.addTaskListener(selectionPanePride);
        reviewerTask.addTaskListener(msgLabel);
        reviewerTask.execute();
    }
}
