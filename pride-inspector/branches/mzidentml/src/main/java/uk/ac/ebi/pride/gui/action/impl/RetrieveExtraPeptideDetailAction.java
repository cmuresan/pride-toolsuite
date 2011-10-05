package uk.ac.ebi.pride.gui.action.impl;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.List;

/**
 * Retrieve additional peptide details
 * <p/>
 * User: rwang
 * Date: 23/06/11
 * Time: 17:02
 */
public class RetrieveExtraPeptideDetailAction extends ExtraProteinDetailAction {


    /**
     * Constructor
     *
     * @param table      protein table
     * @param controller data access controller
     */
    public RetrieveExtraPeptideDetailAction(JTable table,
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
            if (PeptideTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(column.getHeaderValue()) ||
                    PeptideTableModel.TableHeader.PROTEIN_STATUS.getHeader().equals(column.getHeaderValue()) ||
                    PeptideTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(column.getHeaderValue()) ||
                    PeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader().equals(column.getHeaderValue())) {
                ((TableColumnExt) column).setVisible(true);
            }
        }
    }
}
