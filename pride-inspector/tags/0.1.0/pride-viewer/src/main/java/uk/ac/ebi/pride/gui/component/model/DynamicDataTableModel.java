package uk.ac.ebi.pride.gui.component.model;

import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 
 * User: rwang
 * Date: 01-Apr-2010
 * Time: 13:26:41
 */
public abstract class DynamicDataTableModel<T> extends UpdateTableModel<T> implements TaskListener<Void, T> {

    @Override
    public void process(TaskEvent<List<T>> listTaskEvent) {
        List<T> newDataList = listTaskEvent.getValue();
        for(T newData : newDataList) {
            addData(newData);
        }
        fireTableDataChanged();
    }

    @Override
    public void finished(TaskEvent<Void> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void succeed(TaskEvent<Void> vTaskEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
