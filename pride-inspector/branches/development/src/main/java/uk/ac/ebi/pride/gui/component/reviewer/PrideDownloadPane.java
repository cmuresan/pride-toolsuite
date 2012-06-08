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
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 09:43:33
 */
public class PrideDownloadPane extends JDialog implements ActionListener {

    private static final String TITLE = "Download PRIDE Experiment";
    private static final String MSG_TITLE = "Message";
    private static final String USER_NAME_TITLE = "User Name";
    private static final String PASSWORD_TITLE = "Password";
    private static final String LOG_IN_BUTTON = "Login";

    private static final Dimension MAIN_PANE_SIZE = new Dimension(650, 600);
    private static final Dimension LOGIN_PANE_SIZE = new Dimension(650, 40);
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

    public PrideDownloadPane(Frame owner) {
        super(owner, TITLE);
        setupMainPane();
    }

    /**
     * Set up the main pane and add all components.
     */
    public void setupMainPane() {
        context = (PrideInspectorContext)uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(createLoginPane());
        mainPanel.add(createMsgPane());

        selectionPanePride = new PrideDownloadSelectionPane(this, true);
        mainPanel.add(selectionPanePride);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(mainPanel);
        this.setMinimumSize(MAIN_PANE_SIZE);
        // set icon
        ImageIcon dialogIcon = (ImageIcon) GUIUtilities.loadIcon(context.getProperty("reviewer.download.icon.small"));
        this.setIconImage(dialogIcon.getImage());
        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - getWidth())/2, (d.height - getHeight())/2);
    }

    /**
     * Create a log in panel
     *
     * @return
     */
    private JPanel createLoginPane() {
        JPanel loginPane = new JPanel(new BorderLayout());
//        loginPane.setBorder(BorderFactory.createTitledBorder(LOGIN_TITLE));
        loginPane.setMaximumSize(LOGIN_PANE_SIZE);

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
        CSH.setHelpIDString(helpButton, "help.download");
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
