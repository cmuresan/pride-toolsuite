package uk.ac.ebi.pride.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.db.DatabaseSearchPane;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

/**
 * Task to open database search pane
 * User: rwang
 * Date: 27/05/11
 * Time: 14:44
 */
public class OpenDatabaseSearchPaneTask extends TaskAdapter<Void, Void>{
    private static final String DEFAULT_TASK_TITLE = "Open database search panel";
    private static final String DEFAULT_TASK_DESCRIPTION = "Open database search panel";

    public OpenDatabaseSearchPaneTask() {
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        DatabaseSearchPane searchPane = context.getDatabaseSearchPane();
        if (searchPane == null) {
            searchPane = new DatabaseSearchPane(null);
            context.setDatabaseSearchPane(searchPane);
        }
        EventBus.publish(new DatabaseSearchEvent(null));
        return null;
    }
}
