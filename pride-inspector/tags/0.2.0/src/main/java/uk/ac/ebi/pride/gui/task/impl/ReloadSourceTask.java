package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 21-Aug-2010
 * Time: 21:57:07
 */
public class ReloadSourceTask extends TaskAdapter<Void,String> {
    private DataAccessController controller;

    public ReloadSourceTask(DataAccessController controller) {
        this.controller = controller;
        this.setName("Reloading...");
    }

    @Override
    protected Void doInBackground() throws Exception {
        controller.reload();
        return null;
    }
}
