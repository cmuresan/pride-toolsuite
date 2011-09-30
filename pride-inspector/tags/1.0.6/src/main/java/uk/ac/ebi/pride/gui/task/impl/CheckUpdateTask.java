package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.utils.UpdateChecker;

/**
 * Check for available update
 * <p/>
 * User: rwang
 * Date: 11-Nov-2010
 * Time: 18:06:51
 */
public class CheckUpdateTask extends TaskAdapter<Boolean, Void> {

    public CheckUpdateTask() {
        String msg = "Checking for update";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return UpdateChecker.hasUpdate();
    }
}
