package uk.ac.ebi.pride.gui.event.subscriber;

import org.bushe.swing.event.EventSubscriber;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.TaskUtil;
import uk.ac.ebi.pride.gui.task.impl.RetrieveSpectrumTask;

/**
 * Subscribes to spectrum event
 *
 * User: rwang
 * Date: 13/06/11
 * Time: 09:31
 */
public class PeptideSpectrumEventSubscriber implements EventSubscriber<PSMEvent> {

    private DataAccessController controller;
    private TaskListener taskListener;
    private PrideInspectorContext appContext;

    public PeptideSpectrumEventSubscriber(DataAccessController controller, TaskListener taskListener) {
        this.controller = controller;
        this.taskListener = taskListener;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void onEvent(PSMEvent event) {
        Comparable peptideId = event.getPeptideId();
        Comparable protId = event.getIdentificationId();

        Task newTask = new RetrieveSpectrumTask(controller, protId, peptideId);
        newTask.addTaskListener(taskListener);
        TaskUtil.startBackgroundTask(newTask, controller);
    }
}