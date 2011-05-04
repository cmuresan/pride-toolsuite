package uk.ac.ebi.pride.gui.component.curation;

import com.toedter.calendar.JDateChooser;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.task.impl.MakeExperimentPublicTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Dialog for making private pride experiments public.
 * <p/>
 * User: rwang
 * Date: 25-Nov-2010
 * Time: 09:10:26
 */
public class MakeExperimentPublicDialog extends ExperimentUpdateDialog {
    public static final String DIALOG_TITLE = "Making Experiment Public";

    private JDateChooser dateChooser;

    public MakeExperimentPublicDialog(Frame owner) {
        super(owner, DIALOG_TITLE);
    }

    /**
     * create a date panel.
     *
     * @return JPanel date panel
     */
    @Override
    protected JPanel createAdditionalPanel() {
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setBorder(BorderFactory.createTitledBorder("Public Date"));

        // date
        JLabel dayLabel = new JLabel("Date:");
        datePanel.add(dayLabel);
        datePanel.add(Box.createRigidArea(new Dimension(5, 5)));

        // input text field
        dateChooser = new JDateChooser(new Date());
        datePanel.add(dateChooser);

        return datePanel;
    }

    @Override
    public void doUpdate() {
        // get accession range
        int fromAccInt = Integer.parseInt(getFromAccession());
        int toAccInt = Integer.parseInt(getToAccession());

        // build a new update task
        Date date = dateChooser.getDate();
        if (date != null) {
            MakeExperimentPublicTask newTask = new MakeExperimentPublicTask(fromAccInt, toAccInt, dateChooser.getDate());
            newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
            // add task listeners
            newTask.addTaskListener(this);
            // run the task
            uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().addTask(newTask);
        }else {
            GUIUtilities.error(this, "Please select a valid date", "Wrong Date Format");
        }
    }
}
