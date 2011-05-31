package uk.ac.ebi.pride.gui.component.table;

import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import uk.ac.ebi.pride.data.core.SearchEngine;
import uk.ac.ebi.pride.gui.component.table.listener.HyperLinkCellMouseClickListener;
import uk.ac.ebi.pride.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.gui.component.table.model.*;
import uk.ac.ebi.pride.gui.component.table.renderer.HyperLinkCellRenderer;
import uk.ac.ebi.pride.gui.component.table.renderer.PeptideSequenceCellRenderer;
import uk.ac.ebi.pride.gui.url.PTMHyperLinkGenerator;
import uk.ac.ebi.pride.gui.url.ProteinAccHyperLinkGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 * TableFactory can be used to different type of tables.
 * <p/>
 * User: rwang
 * Date: 11-Sep-2010
 * Time: 13:39:00
 */
public class TableFactory {
    /**
     * Build a table to display spectrum related details
     *
     * @return JTable   spectrum table
     */
    public static JTable createSpectrumTable() {
        return new DefaultPrideTable(new SpectrumTableModel(), new DefaultTableColumnModelExt());
    }

    /**
     * Build a table to display chromatogram related details.
     *
     * @return JTable   chromatogram table
     */
    public static JTable createChromatogramTable() {
        return new DefaultPrideTable(new ChromatogramTableModel(), new DefaultTableColumnModelExt());
    }

    /**
     * Build a table to display identification related details.
     *
     * @return JTable   identification table
     */
    public static JTable createIdentificationTable() {
        ProteinTableModel identTableModel = new ProteinTableModel();
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable table = new DefaultPrideTable(identTableModel, columnModel);

        TableColumnExt proteinIdColumn = (TableColumnExt)table.getColumn(ProteinTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        TableColumnExt proteinNameColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_NAME.getHeader());
        // set protein name width
        int protNameColumnNum = proteinNameColumn.getModelIndex();
        table.getColumnModel().getColumn(protNameColumnNum).setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = ProteinTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumn protAcc = table.getColumn(ProteinTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        protAcc.setCellRenderer(new HyperLinkCellRenderer());

        return table;
    }

    /**
     * Build a table to display peptide related details.
     *
     * @param se    search engine
     * @param ptmIcon   whether to display open ptm dialog icon
     * @return JTable   peptide table
     */
    public static JTable createPeptideTable(SearchEngine se, boolean ptmIcon) {

        PeptideTableModel peptideTableModel = new PeptideTableModel(se);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable table = new DefaultPrideTable(peptideTableModel, columnModel);

        // peptide sequence column renderer
        TableColumn peptideColumn = table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_PTM_COLUMN.getHeader());
        peptideColumn.setCellRenderer(new PeptideSequenceCellRenderer(ptmIcon));

        // hide modified peptide sequence
        TableColumnExt peptideSeqColumn = (TableColumnExt)table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_PTM_MASS_COLUMN.getHeader());
        peptideSeqColumn.setVisible(false);

        // hide protein id column
        TableColumnExt proteinIdColumn = (TableColumnExt)table.getColumn(PeptideTableModel.TableHeader.IDENTIFICATION_ID_COLUMN.getHeader());
        proteinIdColumn.setVisible(false);

        // hide peptide id column
        TableColumnExt peptideIdColumn = (TableColumnExt)table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_ID_COLUMN.getHeader());
        peptideIdColumn.setVisible(false);

        // set protein name column width
        TableColumnExt proteinNameColumn = (TableColumnExt)table.getColumn(PeptideTableModel.TableHeader.PROTEIN_NAME.getHeader());
        int protNameColumnNum = proteinNameColumn.getModelIndex();
        table.getColumnModel().getColumn(protNameColumnNum).setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = PeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumnExt protAcc = (TableColumnExt)table.getColumn(PeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        protAcc.setCellRenderer(new HyperLinkCellRenderer());

        // set peptide column width
        int rowColumnNum = peptideColumn.getModelIndex();
        table.getColumnModel().getColumn(rowColumnNum).setPreferredWidth(200);


        return table;
    }

    /**
     * Build a table to display PTM related details.
     *
     * @return JTable   ptm table
     */
    public static JTable createPTMTable() {
        PTMTableModel tableModel = new PTMTableModel();
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable table = new DefaultPrideTable(tableModel, columnModel);

        // add hyper link click listener
        String modAccColumnHeader = PTMTableModel.TableHeader.PTM_ACCESSION.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, modAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, modAccColumnHeader, new PTMHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumnExt ptmColumn = (TableColumnExt)table.getColumn(PTMTableModel.TableHeader.PTM_ACCESSION.getHeader());
        ptmColumn.setCellRenderer(new HyperLinkCellRenderer());

        return table;
    }
}
