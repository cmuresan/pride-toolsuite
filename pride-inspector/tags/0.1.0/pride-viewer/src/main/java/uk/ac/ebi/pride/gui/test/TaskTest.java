package uk.ac.ebi.pride.gui.test;

import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskManager;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Feb-2010
 * Time: 10:12:19
 */
public class TaskTest {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        for (int i = 0; i <= 20; i++)
            manager.addTask(new DummyTask("Harry" + i));
        manager.shutdown();
    }
}
