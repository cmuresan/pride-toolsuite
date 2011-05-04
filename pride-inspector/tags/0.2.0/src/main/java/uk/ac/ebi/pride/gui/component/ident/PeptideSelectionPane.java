package uk.ac.ebi.pride.gui.component.ident;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.table.AlterRowColorTable;
import uk.ac.ebi.pride.gui.component.table.listener.PeptideCellMouseListener;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.component.table.renderer.PeptideSequenceCellRenderer;
import uk.ac.ebi.pride.gui.component.table.renderer.RowNumberRenderer;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.impl.RetrieveEntryTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 11:26:45
 */
public class PeptideSelectionPane extends DataAccessControllerPane<Spectrum, Void> {

    private AlterRowColorTable pepTable;
    private Identification currentIdent;

    public PeptideSelectionPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
    }

    @Override
    protected void addComponents() {
        // create identification table
        pepTable = new AlterRowColorTable();
        PeptideTableModel pepTableModel = new PeptideTableModel();
        pepTable.setModel(pepTableModel);
        pepTable.setRowSorter(new NumberTableRowSorter(pepTableModel));
        pepTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pepTable.getSelectionModel().addListSelectionListener(new PeptideSelectionListener(pepTable));
        pepTable.setFillsViewportHeight(true);
        //pepTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // peptide sequence column renderer
        TableColumn peptideColumn = pepTable.getColumn(PeptideTableModel.TableHeader.PEPTIDE_PTM_COLUMN.getHeader());
        peptideColumn.setCellRenderer(new PeptideSequenceCellRenderer());
        // row number column renderer
        TableColumn rowNumColumn = pepTable.getColumn(PeptideTableModel.TableHeader.ROW_NUMBER_COLUMN.getHeader());
        int rowColumnNum = rowNumColumn.getModelIndex();
        rowNumColumn.setCellRenderer(new RowNumberRenderer());
        pepTable.getColumnModel().getColumn(rowColumnNum).setMaxWidth(40);
        pepTable.setOmitColumn(rowColumnNum);
        // prevent dragging of column
        pepTable.getTableHeader().setReorderingAllowed(false);
        // add mouse listener
        PeptideCellMouseListener peptideMouseListener = new PeptideCellMouseListener(pepTable, peptideColumn.getModelIndex());
        pepTable.addMouseListener(peptideMouseListener);
        // remove border
        pepTable.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollPane = new JScrollPane(pepTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        // add the component
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void succeed(TaskEvent<Spectrum> spectrumTaskEvent) {
        int rowNum = pepTable.getSelectedRow();
        Peptide peptide = currentIdent.getPeptide(rowNum);
        Spectrum spectrum = spectrumTaskEvent.getValue();
        spectrum.setPeptide(peptide);
        this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, null, spectrumTaskEvent.getValue());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if (DataAccessController.IDENTIFICATION_TYPE.equals(evt.getPropertyName())) {
            Object val = evt.getNewValue();
            // clear peptide table,
            PeptideTableModel tableModel = (PeptideTableModel) pepTable.getModel();
            tableModel.removeAllRows();
            if (val == null) {
                // clear peaks list
                this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, "", null);
            } else {
                currentIdent = (Identification) evt.getNewValue();

                tableModel.addData(currentIdent);
                tableModel.fireTableDataChanged();
                // single selection mode, only the second one is used
                pepTable.getSelectionModel().clearSelection();
                pepTable.getSelectionModel().setSelectionInterval(0, 0);
            }
        }
    }

    private class PeptideSelectionListener implements ListSelectionListener {
        private final JTable table;

        private PeptideSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                if (rowNum >= 0) {
                    // get table model
                    PeptideTableModel tableModel = (PeptideTableModel) table.getModel();
                    // get spectrum reference column
                    int columnNum = tableModel.getColumnIndex(PeptideTableModel.TableHeader.SPECTRUM_REFERENCE_COLUMN.getHeader());
                    // get spectrum id
                    Comparable specId = (Comparable) table.getValueAt(rowNum, columnNum);
                    if (specId != null && !"-1".equals(specId)) {
                        Task newTask = new RetrieveEntryTask(controller, Spectrum.class, specId);
                        newTask.addTaskListener(PeptideSelectionPane.this);
                        newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                        // add task listeners
                        uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
                    }
                }
            }
        }
    }
}
