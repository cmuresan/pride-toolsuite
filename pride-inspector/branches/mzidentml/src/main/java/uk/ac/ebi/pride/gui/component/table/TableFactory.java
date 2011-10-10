package uk.ac.ebi.pride.gui.component.table;

import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.component.table.listener.*;
import uk.ac.ebi.pride.gui.component.table.model.*;
import uk.ac.ebi.pride.gui.component.table.renderer.*;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.url.*;
import uk.ac.ebi.pride.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.util.Collection;
import java.util.List;

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
     * @param controller data access controller
     * @return JTable   identification table
     */
    public static JTable createIdentificationTable(DataAccessController controller) {
        ProteinTableModel identTableModel = new ProteinTableModel(controller);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable table = new DefaultPrideTable(identTableModel, columnModel);

        TableColumnExt proteinIdColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        TableColumnExt proteinNameColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_NAME.getHeader());
        // set protein name width
        int protNameColumnNum = proteinNameColumn.getModelIndex();
        columnModel.getColumn(protNameColumnNum).setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt seqCoverageColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        seqCoverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        seqCoverageColumn.setVisible(false);

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
     * @param se         search engine
     * @param controller data access controller
     * @return JTable   peptide table
     */
    public static JTable createPeptideTable(SearchEngine se, DataAccessController controller) {

        PeptideTableModel peptideTableModel = new PeptideTableModel(se, controller);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable table = new DefaultPrideTable(peptideTableModel, columnModel);

        // peptide sequence column renderer
        TableColumnExt peptideColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_PTM_COLUMN.getHeader());
        peptideColumn.setCellRenderer(new PeptideSequenceCellRenderer());

        // delta mass column
        TableColumnExt deltaMassColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.DELTA_MASS_COLUMN.getHeader());
        BarChartRenderer renderer = new BarChartRenderer(4, -4, 0);
        renderer.setWarningVisible(true);
        deltaMassColumn.setCellRenderer(renderer);
        // set width
        int deltaMassColumnNum = deltaMassColumn.getModelIndex();
        columnModel.getColumn(deltaMassColumnNum).setPreferredWidth(150);

        // peptide sequence present in protein sequence
        TableColumnExt peptideFitColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader());
        peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());

        // hide modified peptide sequence
        TableColumnExt peptideSeqColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_PTM_MASS_COLUMN.getHeader());
        peptideSeqColumn.setVisible(false);

        // hide protein id column
        TableColumnExt proteinIdColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide peptide id column
        TableColumnExt peptideIdColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_ID.getHeader());
        peptideIdColumn.setVisible(false);

        // set protein name column width
        TableColumnExt proteinNameColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PROTEIN_NAME.getHeader());
        int protNameColumnNum = proteinNameColumn.getModelIndex();
        columnModel.getColumn(protNameColumnNum).setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt coverageColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        coverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        coverageColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = PeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumnExt protAcc = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        protAcc.setCellRenderer(new HyperLinkCellRenderer());

        // set peptide column width
        int rowColumnNum = peptideColumn.getModelIndex();
        columnModel.getColumn(rowColumnNum).setPreferredWidth(200);

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
        TableColumnExt ptmColumn = (TableColumnExt) table.getColumn(PTMTableModel.TableHeader.PTM_ACCESSION.getHeader());
        ptmColumn.setCellRenderer(new HyperLinkCellRenderer());

        return table;
    }

    /**
     * Build a table to display database search summaries.
     *
     * @return JTable  database search table
     */
    public static JTable createDatabaseSearchTable() {
        DatabaseSearchTableModel tableModel = new DatabaseSearchTableModel();
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable searchTable = new DefaultPrideTable(tableModel, columnModel);
        searchTable.setAutoCreateColumnsFromModel(false);
        // add table model change listener
        tableModel.addTableModelListener(new DynamicColumnListener(searchTable));

        // add cell renderer to view column
        String viewColumnHeader = DatabaseSearchTableModel.TableHeader.VIEW.getHeader();
        TableColumnExt viewColumn = (TableColumnExt) searchTable.getColumn(viewColumnHeader);
        viewColumn.setCellRenderer(new ButtonRenderer(Constants.VIEW));
        int viewColumnNum = viewColumn.getModelIndex();
        columnModel.getColumn(viewColumnNum).setMaxWidth(50);


        // add mouse motion listener
        searchTable.addMouseMotionListener(new TableCellMouseMotionListener(searchTable, viewColumnHeader));
        searchTable.addMouseListener(new OpenExperimentMouseListener(searchTable, viewColumnHeader));

        return searchTable;
    }

    /**
     * Create a table to show a list of references
     *
     * @param references a list of input references
     * @return JTable  reference table
     */
    public static JTable createReferenceTable(Collection<Reference> references) {
        ReferenceTableModel tableModel = new ReferenceTableModel(references);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable referenceTable = new DefaultPrideTable(tableModel, columnModel);

        // pubmed
        String pubMedColumnHeader = ReferenceTableModel.TableHeader.PUBMED.getHeader();
        TableColumnExt pubMedColumn = (TableColumnExt) referenceTable.getColumn(pubMedColumnHeader);
        pubMedColumn.setCellRenderer(new HyperLinkCellRenderer());
        int pubMedColumnNum = pubMedColumn.getModelIndex();
        columnModel.getColumn(pubMedColumnNum).setMaxWidth(100);

        // doi
        String doiColumnHeader = ReferenceTableModel.TableHeader.DOI.getHeader();
        TableColumnExt doiColumn = (TableColumnExt) referenceTable.getColumn(doiColumnHeader);
        doiColumn.setCellRenderer(new HyperLinkCellRenderer());
        int doiColumnNum = doiColumn.getModelIndex();
        columnModel.getColumn(doiColumnNum).setMaxWidth(100);

        // add mouse motion listener
        referenceTable.addMouseMotionListener(new TableCellMouseMotionListener(referenceTable, pubMedColumnHeader, doiColumnHeader));
        referenceTable.addMouseListener(new HyperLinkCellMouseClickListener(referenceTable, pubMedColumnHeader, new PrefixedHyperLinkGenerator(Constants.PUBMED_URL_PERFIX)));
        referenceTable.addMouseListener(new HyperLinkCellMouseClickListener(referenceTable, doiColumnHeader, new DOIHyperLinkGenerator(Constants.DOI_URL_PREFIX)));

        return referenceTable;
    }

    /**
     * Create a table for showing a list of param groups
     *
     * @param paramGroups given list of param groups
     * @return JTable  param table
     */
    public static JTable createParamTable(List<ParamGroup> paramGroups) {
        ParamTableModel paramTableModel = new ParamTableModel(paramGroups);
        return createParamTable(paramTableModel);
    }

    /**
     * Create a table for showing a ParamGroup
     *
     * @param paramGroup given ParamGroup
     * @return JTable  param table
     */
    public static JTable createParamTable(ParamGroup paramGroup) {
        ParamTableModel paramTableModel = new ParamTableModel(paramGroup);
        return createParamTable(paramTableModel);
    }

    /**
     * Create a table for showing a collection parameters
     *
     * @param parameters a collection of parameters
     * @return JTable  param table
     */
    public static JTable createParamTable(Collection<Parameter> parameters) {
        ParamTableModel paramTableModel = new ParamTableModel(parameters);
        return createParamTable(paramTableModel);
    }


    /**
     * Create a table for showing a ParamTableModel
     *
     * @param paramTableModel given param table model
     * @return JTable  param table
     */
    private static JTable createParamTable(ParamTableModel paramTableModel) {
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable paramTable = new DefaultPrideTable(paramTableModel, columnModel);

        // hyperlink ontology accessions
        String accColumnHeader = ParamTableModel.TableHeader.ACCESSION.getHeader();
        TableColumnExt accColumn = (TableColumnExt) paramTable.getColumn(accColumnHeader);
        accColumn.setCellRenderer(new HyperLinkCellRenderer());

        // add mouse motion listener
        paramTable.addMouseMotionListener(new TableCellMouseMotionListener(paramTable, accColumnHeader));
        paramTable.addMouseListener(new HyperLinkCellMouseClickListener(paramTable, accColumnHeader, new PrefixedHyperLinkGenerator(Constants.OLS_URL_PREFIX)));

        return paramTable;
    }

    /**
     * Create a table for showing contacts
     *
     * @param contacts given list of contacts
     * @return JTable  contact table
     */
    public static JTable createContactTable(Collection<ParamGroup> contacts) {
        ContactTableModel tableModel = new ContactTableModel(contacts);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable contactTable = new DefaultPrideTable(tableModel, columnModel);

        // hyperlink contact emails
        String infoColumnHeader = ContactTableModel.TableHeader.INFORMATION.getHeader();
        TableColumnExt infoColumn = (TableColumnExt) contactTable.getColumn(infoColumnHeader);
        infoColumn.setCellRenderer(new HyperLinkCellRenderer());

        // add mouse motion listener
        contactTable.addMouseMotionListener(new TableCellMouseMotionListener(contactTable, infoColumnHeader));
        contactTable.addMouseListener(new HyperLinkCellMouseClickListener(contactTable, infoColumnHeader, new EmailHyperLinkGenerator()));

        return contactTable;
    }

    /**
     * Create a table for quantitative sample data
     *
     * @param sample quantitative sample
     * @return JTable  Quantitative table
     */
    public static JTable createQuantSampleTable(QuantitativeSample sample) {
        QuantSampleTableModel tableModel = new QuantSampleTableModel(sample);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        return new DefaultPrideTable(tableModel, columnModel);
    }

    /**
     * Create a table for quantitative protein data with a given table model
     *
     * @param tableModel quant protein table model
     * @return JTable  quant protein table
     */
    public static JTable createQuantProteinTable(TableModel tableModel) {
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable quantProteinTable = new DefaultPrideTable(tableModel, columnModel);
        quantProteinTable.setAutoCreateColumnsFromModel(false);
        // add table model change listener
        tableModel.addTableModelListener(new BarChartColumnListener(quantProteinTable));

        // in case the compare doesn't exist
        List<TableColumn> columns = columnModel.getColumns(true);
        for (TableColumn column : columns) {
            if (column.getHeaderValue().equals(QuantProteinTableModel.TableHeader.COMPARE.getHeader())) {
                int rowColumnNum = column.getModelIndex();
                columnModel.getColumn(rowColumnNum).setMaxWidth(25);
            }
        }
        // hide mapped protein accession
        String mappedProtAccHeader = QuantProteinTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader();
        TableColumnExt mappedProtAccColumn = (TableColumnExt) quantProteinTable.getColumn(mappedProtAccHeader);
        mappedProtAccColumn.setCellRenderer(new HyperLinkCellRenderer());
        // add hyper link click listener
        quantProteinTable.addMouseMotionListener(new TableCellMouseMotionListener(quantProteinTable, mappedProtAccHeader));
        quantProteinTable.addMouseListener(new HyperLinkCellMouseClickListener(quantProteinTable, mappedProtAccHeader, new ProteinAccHyperLinkGenerator()));

        TableColumnExt proteinIdColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide the protein name column
        TableColumnExt proteinNameColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt seqCoverageColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        seqCoverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        seqCoverageColumn.setVisible(false);

        // score
        TableColumnExt proteinScoreColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.IDENTIFICATION_SCORE_COLUMN.getHeader());
        proteinScoreColumn.setVisible(false);

        // threshold
        TableColumnExt proteinThresholdColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.IDENTIFICATION_THRESHOLD_COLUMN.getHeader());
        proteinThresholdColumn.setVisible(false);

        // number of peptides
        TableColumnExt numOfPeptideColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.NUMBER_OF_PEPTIDES.getHeader());
        numOfPeptideColumn.setVisible(false);

        // number of unique peptides
        TableColumnExt numOfUniquePeptideColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.NUMBER_OF_UNIQUE_PEPTIDES.getHeader());
        numOfUniquePeptideColumn.setVisible(false);

        // number of ptms
        TableColumnExt numOfPtmColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.NUMBER_OF_PTMS.getHeader());
        numOfPtmColumn.setVisible(false);


        return quantProteinTable;
    }

    /**
     * Create a table for protein quantitative data
     *
     * @return JTable   protein quantitative table
     */
    public static JTable createQuantProteinTable(DataAccessController controller) {
        QuantProteinTableModel tableModel = new QuantProteinTableModel(controller);
        return createQuantProteinTable(tableModel);
    }

    /**
     * Create a table for peptide quantitative data
     *
     * @param se search engine
     * @return JTable  peptide table
     */
    public static JTable createQuantPeptideTable(SearchEngine se, DataAccessController controller) {
        QuantPeptideTableModel tableModel = new QuantPeptideTableModel(se, controller);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable quantPeptideTable = new DefaultPrideTable(tableModel, columnModel);
        quantPeptideTable.setAutoCreateColumnsFromModel(false);
        // add table model change listener
        tableModel.addTableModelListener(new BarChartColumnListener(quantPeptideTable));

        // hide protein accession
        TableColumnExt proteinAccColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
        proteinAccColumn.setVisible(false);

        // hide mapped protein accession
        String mappedProtAccHeader = QuantPeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader();
        TableColumnExt mappedProtAccColumn = (TableColumnExt) quantPeptideTable.getColumn(mappedProtAccHeader);
        mappedProtAccColumn.setCellRenderer(new HyperLinkCellRenderer());
        mappedProtAccColumn.setVisible(false);
        // add hyper link click listener
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, mappedProtAccHeader));
        quantPeptideTable.addMouseListener(new HyperLinkCellMouseClickListener(quantPeptideTable, mappedProtAccHeader, new ProteinAccHyperLinkGenerator()));

        // peptide sequence column renderer
        TableColumnExt peptideColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_PTM_COLUMN.getHeader());
        peptideColumn.setCellRenderer(new PeptideSequenceCellRenderer());
        // set peptide column width
        int rowColumnNum = peptideColumn.getModelIndex();
        columnModel.getColumn(rowColumnNum).setPreferredWidth(150);

        // hide protein name
        TableColumnExt proteinNameColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setVisible(false);

        // hide protein status
        TableColumnExt proteinStatusColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // hide protein sequence coverage
        TableColumnExt proteinSeqCoverageColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        proteinSeqCoverageColumn.setVisible(false);

        // peptide sequence present in protein sequence
        TableColumnExt peptideFitColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader());
        peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());
        peptideFitColumn.setVisible(false);

        // precursor charge column
        TableColumnExt precursorChargeColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PRECURSOR_CHARGE_COLUMN.getHeader());
        precursorChargeColumn.setVisible(false);

        // delta mass column
        TableColumnExt deltaMassColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.DELTA_MASS_COLUMN.getHeader());
        deltaMassColumn.setVisible(false);
        BarChartRenderer renderer = new BarChartRenderer(4, -4, 0);
        renderer.setWarningVisible(true);
        deltaMassColumn.setCellRenderer(renderer);

        // set width
        int deltaMassColumnNum = deltaMassColumn.getModelIndex();
        columnModel.getColumn(deltaMassColumnNum).setPreferredWidth(150);

        // precursor m/z column
        TableColumnExt precursorMzColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PRECURSOR_MZ_COLUMN.getHeader());
        precursorMzColumn.setVisible(false);

        // number of ptms column
        TableColumnExt ptmsColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_PTM_NUMBER_COLUMN.getHeader());
        ptmsColumn.setVisible(false);
        ImageIcon icon = GUIUtilities.loadImageIcon(Desktop.getInstance().getDesktopContext().getProperty("open.ptm.small.icon"));
        ptmsColumn.setCellRenderer(new OpenPTMRenderer(icon));

        // hide modified peptide sequence
        TableColumnExt peptideSeqColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_PTM_MASS_COLUMN.getHeader());
        peptideSeqColumn.setVisible(false);

        // hide ptm summary
        TableColumnExt ptmSummaryColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_PTM_SUMMARY.getHeader());
        ptmSummaryColumn.setVisible(false);

        // hide number of fragment ions
        TableColumnExt fragIonsColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader());
        fragIonsColumn.setVisible(false);

        // hide peptide sequence length
        TableColumnExt seqLengthColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_SEQUENCE_LENGTH_COLUMN.getHeader());
        seqLengthColumn.setVisible(false);

        // hide sequence start
        TableColumnExt sequenceStartColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.SEQUENCE_START_COLUMN.getHeader());
        sequenceStartColumn.setVisible(false);

        // hide sequence end
        TableColumnExt sequenceEndColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.SEQUENCE_END_COLUMN.getHeader());
        sequenceEndColumn.setVisible(false);

        // hide pi point column
        TableColumnExt piColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader());
        piColumn.setVisible(false);

        // hide spectrum id
        TableColumnExt spectrumIdColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.SPECTRUM_ID.getHeader());
        spectrumIdColumn.setVisible(false);

        // hide protein id column
        TableColumnExt proteinIdColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide peptide id column
        TableColumnExt peptideIdColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_ID.getHeader());
        peptideIdColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = QuantPeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader();
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, protAccColumnHeader));
        quantPeptideTable.addMouseListener(new HyperLinkCellMouseClickListener(quantPeptideTable, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));


        return quantPeptideTable;
    }
}
