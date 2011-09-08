package uk.ac.ebi.pride.gui.action.impl;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.table.model.AbstractProteinTableModel;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableModel;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveProteinDetailTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.util.InternetChecker;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Retrieve protein details for protein table
 * <p/>
 * User: rwang
 * Date: 09-Oct-2010
 * Time: 18:02:21
 */
public class RetrieveExtraProteinDetailAction extends ExtraProteinDetailAction {

    /**
     * Constructor
     *
     * @param table      protein table
     * @param controller data access controller
     */
    public RetrieveExtraProteinDetailAction(JTable table,
                                            DataAccessController controller) {
        super(table, controller);
    }

    /**
     * Set hidden columns visible
     * such as: protein name and protein sequence coverage
     */
    protected void setColumnVisible() {
        TableColumnModelExt showHideColModel = (TableColumnModelExt) getTable().getColumnModel();
        List<TableColumn> columns = showHideColModel.getColumns(true);
        for (TableColumn column : columns) {
            Object header = column.getHeaderValue();
            if (ProteinTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(header) ||
                    ProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader().equals(header) ||
                    ProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(header)) {
                ((TableColumnExt) column).setVisible(true);
            }
        }
    }
}
