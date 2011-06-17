package uk.ac.ebi.pride.gui.component.curation;

import uk.ac.ebi.pride.gui.task.impl.CreateReviewerTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog to create a reviewer account
 *
 * User: rwang
 * Date: 25-Nov-2010
 * Time: 14:32:01
 */
public class CreateReviewerDialog extends ExperimentUpdateDialog{
    public static final String DIALOG_TITLE = "Creating Reviewer Account";

    public CreateReviewerDialog(Frame owner) {
        super(owner, DIALOG_TITLE);
    }

    @Override
    protected JPanel createAdditionalPanel() {
        return null;
    }

    @Override
    public void doUpdate() {
        // get accession range
        int fromAccInt = Integer.parseInt(getFromAccession());
        int toAccInt = Integer.parseInt(getToAccession());

        // createAttributedSequence a new update task
        CreateReviewerTask newTask = new CreateReviewerTask(fromAccInt, toAccInt);
        newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
        // add task listeners
        newTask.addTaskListener(this);
        // run the task
        uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().addTask(newTask);
    }
}
