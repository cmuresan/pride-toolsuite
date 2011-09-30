package uk.ac.ebi.pride.gui.event.subscriber;

import org.bushe.swing.event.EventSubscriber;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.container.SpectrumEvent;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveSpectrumTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

/**
 * Subscribe to spectrum selection event
 *
 * User: rwang
 * Date: 13/06/11
 * Time: 11:44
 */
public class SpectrumEventSubscriber implements EventSubscriber<SpectrumEvent> {
    private DataAccessController controller;
    private TaskListener taskListener;
    private PrideInspectorContext appContext;

    public SpectrumEventSubscriber(DataAccessController controller, TaskListener taskListener) {
        this.controller = controller;
        this.taskListener = taskListener;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void onEvent(SpectrumEvent event) {
        Comparable specturmId = event.getSpectrumId();

        Task newTask = new RetrieveSpectrumTask(controller, specturmId);
        newTask.addTaskListener(taskListener);
        newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
        // add task listeners
        appContext.addTask(newTask);
    }
}