package uk.ac.ebi.pride.test;

import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 02-Aug-2010
 * Time: 08:53:30
 */
public abstract class TestTableModel<T> extends DefaultTableModel implements TaskListener<Void, T> {
    @Override
    public void process(TaskEvent<List<T>> listTaskEvent) {
        final List<T> newDataList = listTaskEvent.getValue();
        final int rowCnt = this.getRowCount();
        for (T newData : newDataList) {
            addData(newData);
        }
        //fireTableRowsInserted(rowCnt, rowCnt + newDataList.size() - 1);
        fireTableRowsInserted(rowCnt, rowCnt);
    }

    protected abstract void addData(T data);

    @Override
    public void finished(TaskEvent<Void> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void succeed(TaskEvent<Void> voidTaskEvent) {
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
