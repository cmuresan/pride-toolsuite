package uk.ac.ebi.pride.gui.desktop;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.task.TaskManager;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

/**
 * Todo: make this singleton for simple retrial
 * User: rwang
 * Date: 21-Jan-2010
 * Time: 11:25:45
 */
public class DesktopContext extends PropertyChangeHelper {
    private static final Logger logger = Logger.getLogger(DesktopContext.class.getName());
    private Desktop desktop = null;
    private final TaskManager taskManager;
    private final PropertyManager propertyManager;

    protected DesktopContext() {
        taskManager = new TaskManager();
        propertyManager = new PropertyManager();
    }

    public final Desktop getDesktop() {
        return desktop;
    }

    public final void setDesktop(Desktop desktop) {
        if (this.desktop != null) {
            throw new IllegalStateException("Desktop has already been launched");
        }
        this.desktop = desktop;
    }

    public final TaskManager getTaskManager() {
        return taskManager;
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }
}
