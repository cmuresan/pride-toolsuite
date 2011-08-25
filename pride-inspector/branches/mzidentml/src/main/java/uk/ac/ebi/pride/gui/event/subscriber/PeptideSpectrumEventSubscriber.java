package uk.ac.ebi.pride.gui.event.subscriber;

import org.bushe.swing.event.EventSubscriber;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.container.PeptideEvent;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveSpectrumTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

/**
 * Subscribes to spectrum event
 *
 * User: rwang
 * Date: 13/06/11
 * Time: 09:31
 */
public class PeptideSpectrumEventSubscriber implements EventSubscriber<PeptideEvent> {
    private DataAccessController controller;
    private TaskListener taskListener;
    private PrideInspectorContext appContext;

    public PeptideSpectrumEventSubscriber(DataAccessController controller, TaskListener taskListener) {
        this.controller = controller;
        this.taskListener = taskListener;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void onEvent(PeptideEvent event) {
        Comparable peptideId = event.getPeptideId();
        Comparable protId = event.getIdentificationId();

        Task newTask = new RetrieveSpectrumTask(controller, protId, peptideId);
        newTask.addTaskListener(taskListener);
        newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
        // add task listeners
        appContext.addTask(newTask);
    }
}