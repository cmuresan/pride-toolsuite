package uk.ac.ebi.pride.gui.action.impl;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.List;

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
            if (ProteinTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(column.getHeaderValue()) ||
                    ProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader().equals(column.getHeaderValue()) ||
                    ProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(column.getHeaderValue())) {
                ((TableColumnExt) column).setVisible(true);
            }
        }
    }
}
