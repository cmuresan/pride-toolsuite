package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskManager;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Feb-2010
 * Time: 11:18:41
 */
public class TaskMonitorPanel extends StatusBarPanel {
    private final Map<Task, TaskProgressBar> componentMap;

    public TaskMonitorPanel() {
        super();
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.setToolTipText("Click to show/hide background tasks window");
        this.componentMap = new HashMap<Task, TaskProgressBar>();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String eventName = evt.getPropertyName();
        if (TaskManager.ADD_TASK_PROP.equals(eventName)) {
            List<Task> newTasks = (List<Task>) evt.getNewValue();
            // get the newest task
            Task newTask = newTasks.get(newTasks.size() - 1);
            // 1. create a new progress bar
            TaskProgressBar progBar = new TaskProgressBar(newTask);
            // display the newest task
            this.add(progBar);
            this.revalidate();
            this.repaint();
            // register the mapping
            this.componentMap.put(newTask, progBar);

        } else if (TaskManager.REMOVE_TASK_PROP.equals(eventName)) {
            List<Task> oldTasks = (List<Task>) evt.getOldValue();
            List<Task> newTasks = (List<Task>) evt.getNewValue();
            oldTasks.removeAll(newTasks);
            for(Task task : oldTasks) {
                TaskProgressBar progBar = componentMap.get(task);
                this.remove(progBar);
            }
            this.revalidate();
            this.repaint();
        }
    }
}
