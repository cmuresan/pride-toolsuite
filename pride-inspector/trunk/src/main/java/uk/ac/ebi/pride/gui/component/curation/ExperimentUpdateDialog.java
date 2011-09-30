package uk.ac.ebi.pride.gui.component.curation;

import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.component.message.Message;
import uk.ac.ebi.pride.gui.component.message.MessageBoard;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.util.NumberUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog to make experiments public.
 * <p/>
 * User: rwang
 * Date: 23-Nov-2010
 * Time: 16:20:16
 */
public abstract class ExperimentUpdateDialog extends JDialog implements ActionListener, TaskListener<Message, Message> {
    private static final String UPDATE_ACTION = "UPDATE";
    private static final String CLEAR_ALL = "Clear All";
    private static final String CLOSE_ACTION = "CLOSE";
    /**
     * Message board for displaying all the results
     */
    private MessageBoard messageBoard;
    /**
     * Text field to capture the starting accession
     */
    private JTextField fromTextField;
    /**
     * Text field to capture the end accession
     */
    private JTextField toTextField;

    public ExperimentUpdateDialog(Frame owner, String title) {
        super(owner, title);

        // layout
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(570, 400));

        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - getWidth()) / 2, (d.height - getHeight()) / 2);

        // container panel
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel topPanel = new JPanel(new BorderLayout());
        // add accession panel
        JPanel accPanel = createAccessionPanel();
        topPanel.add(accPanel, BorderLayout.NORTH);

        // add additional panel
        JPanel additionalPanel = createAdditionalPanel();
        if (additionalPanel != null) {
            topPanel.add(additionalPanel, BorderLayout.CENTER);
        }

        container.add(topPanel, BorderLayout.NORTH);

        // add message board
        JPanel msgPanel = createMessageBoard();
        container.add(msgPanel, BorderLayout.CENTER);

        // add button panel
        JPanel buttonPanel = createButtonPanel();
        container.add(buttonPanel, BorderLayout.SOUTH);

        this.add(container, BorderLayout.CENTER);
    }

    /**
     * Add a panel to input accession numbers
     *
     * @return JPanel  accession panel
     */
    private JPanel createAccessionPanel() {
        // main panel
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Accessions"));

        // add labels and text fields
        // from
        JLabel fromLabel = new JLabel("From:");
        panel.add(fromLabel);
        panel.add(Box.createRigidArea(new Dimension(5, 5)));

        fromTextField = new JTextField();
        fromTextField.setPreferredSize(new Dimension(80, 20));
        panel.add(fromTextField);
        panel.add(Box.createRigidArea(new Dimension(20, 5)));

        // to
        JLabel toLabel = new JLabel("To:");
        panel.add(toLabel);
        panel.add(Box.createRigidArea(new Dimension(5, 5)));

        toTextField = new JTextField();
        toTextField.setPreferredSize(new Dimension(80, 20));
        panel.add(toTextField);
        panel.add(Box.createRigidArea(new Dimension(5, 5)));

        // add update button
        JButton updateButton = new JButton("Update");
        updateButton.setActionCommand(UPDATE_ACTION);
        updateButton.addActionListener(this);
        panel.add(updateButton);

        return panel;
    }

    /**
     * Override this method to add a additional panel between the accession range panel and message board.
     *
     * @return  JPanel  additional panel
     */
    protected abstract JPanel createAdditionalPanel();

    /**
     * Create a message board panel
     *
     * @return JPanel  message panel
     */
    private JPanel createMessageBoard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Messages"));

        messageBoard = new MessageBoard();
        messageBoard.setBackground(Color.white);
        JScrollPane scrollPane = new JScrollPane(messageBoard, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
        // set vertical scroll bar's speed
        vScrollBar.setUnitIncrement(50);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Add a panel which contains buttons
     *
     * @return JPanel  button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // add update button
        JButton updateButton = new JButton("Clear All");
        updateButton.setActionCommand(CLEAR_ALL);
        updateButton.addActionListener(this);
        panel.add(updateButton);

        // add close button
        JButton closeButton = new JButton("Close");
        closeButton.setActionCommand(CLOSE_ACTION);
        closeButton.addActionListener(this);
        panel.add(closeButton);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String eventCmd = e.getActionCommand();

        if (UPDATE_ACTION.equals(eventCmd)) {
            if (isAccessionValid()) {
                doUpdate();
            }
        } else if (CLEAR_ALL.equals(eventCmd)) {
            messageBoard.removeAllMessages();
        } else if (CLOSE_ACTION.equals(eventCmd)) {
            this.dispose();
        }
    }

    /**
     * Check whether the accession range is a valid.
     *
     * @return  boolean true means valid
     */
    private boolean isAccessionValid() {
        boolean result = false;

        // get accession range
        String fromAcc = getFromAccession();
        String toAcc = getToAccession();

        // check whether accessions are positive integers
        if (NumberUtilities.isNonNegativeInteger(fromAcc) && NumberUtilities.isNonNegativeInteger(toAcc)) {
            int fromAccInt = Integer.parseInt(fromAcc);
            int toAccInt = Integer.parseInt(toAcc);

            if (fromAccInt > toAccInt) {
                GUIUtilities.error(this, "Please ensure the starting accession is smaller or equal to the end accession", "Accession Range Error");
            } else {
                result = true;
            }
        } else {
            // display a error pop-up window
            GUIUtilities.error(this, "Please ensure the accession numbers are non-negative integers.", "Accession Format Error");
        }

        return result;
    }

    /**
     * Show a message to message board
     *
     * @param type    message type
     * @param message message body
     */
    public void showMessage(MessageType type, String message) {
        messageBoard.showMessage(new Message(type, message));
    }

    /**
     * Show a message to message board
     *
     * @param msg message object
     */
    public void showMessage(Message msg) {
        messageBoard.showMessage(msg);
    }

    /**
     * Remove all previous messages
     */
    public void removeAllMessages() {
        messageBoard.removeAllMessages();
    }

    /**
     * Get starting accession
     *
     * @return String  starting accession
     */
    public String getFromAccession() {
        return fromTextField.getText().trim();
    }

    /**
     * Get end accession.
     *
     * @return String  end accession
     */
    public String getToAccession() {
        return toTextField.getText().trim();
    }

    /**
     * Overwrite this method to provide action
     * for clicking on the update button.
     */
    public abstract void doUpdate();


    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<java.util.List<Message>> listTaskEvent) {
        java.util.List<Message> msgs = listTaskEvent.getValue();
        for (Message msg : msgs) {
            this.showMessage(msg.getType(), msg.getMessage());
        }
    }

    @Override
    public void succeed(TaskEvent<Message> stringTaskEvent) {
        this.showMessage(stringTaskEvent.getValue());
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
