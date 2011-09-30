package uk.ac.ebi.pride.gui.component.reviewer;

import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 14:44:58
 */
public class MessageLabel extends JLabel implements TaskListener<List<Map<String, String>>, String> {
    private JComponent parent;

    public MessageLabel(JComponent owner) {
        this.parent = owner;
    }

    @Override
    public void process(TaskEvent<List<String>> listTaskEvent) {
        List<String> msgs = listTaskEvent.getValue();
        StringBuilder output = new StringBuilder();
        output.append("<html>");
        for (String msg : msgs) {
            output.append(msg);
            output.append("<br>");
        }
        output.append("</html>");
        String outputStr = output.toString();
        this.setText(outputStr);
        if (outputStr.toLowerCase().contains("warning")) {
            this.setForeground(new Color(255, 0, 0, 200));
        } else {
            this.setForeground(new Color(0, 100, 0, 150));
        }
        this.parent.setVisible(true);
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<List<Map<String, String>>> listTaskEvent) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
