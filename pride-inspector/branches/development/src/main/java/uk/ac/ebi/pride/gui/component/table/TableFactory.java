package uk.ac.ebi.pride.gui.component.table;

import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import uk.ac.ebi.pride.business.security.DataAccess;
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
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

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
        ProteinTableModel identTableModel = new ProteinTableModel();
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable table = new DefaultPrideTable(identTableModel, columnModel);

        TableColumnExt proteinIdColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        TableColumnExt proteinNameColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_NAME.getHeader());
        // set protein name width
        proteinNameColumn.setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt seqCoverageColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        seqCoverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        seqCoverageColumn.setVisible(false);

        // isoelectric point column
        TableColumnExt isoelectricColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader());
        isoelectricColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = ProteinTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumn protAcc = table.getColumn(ProteinTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        protAcc.setCellRenderer(new HyperLinkCellRenderer());

        // additional column
        String additionalColHeader = ProteinTableModel.TableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) table.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);

        // add mouse motion listener
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, additionalColHeader));
        table.addMouseListener(new ShowParamsMouseListener(controller, table, additionalColHeader));

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

        PeptideTableModel peptideTableModel = new PeptideTableModel(se);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable table = new DefaultPrideTable(peptideTableModel, columnModel);

        // peptide sequence column renderer
        TableColumnExt peptideColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_PTM_COLUMN.getHeader());
        peptideColumn.setCellRenderer(new PeptideSequenceCellRenderer());

        // delta mass column
        TableColumnExt deltaMassColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.DELTA_MASS_COLUMN.getHeader());
        double minLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
        double maxLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
        DeltaMZRenderer renderer = new DeltaMZRenderer(minLimit, maxLimit);
        deltaMassColumn.setCellRenderer(renderer);

        // peptide sequence present in protein sequence
        TableColumnExt peptideFitColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader());
        peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());
        peptideFitColumn.setVisible(false);

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
        proteinNameColumn.setPreferredWidth(200);

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
        peptideColumn.setPreferredWidth(200);

        // hide spectrum id column
        String spectrumIdHeader = PeptideTableModel.TableHeader.SPECTRUM_ID.getHeader();
        TableColumnExt spectrumIdColumn = (TableColumnExt) table.getColumn(spectrumIdHeader);
        spectrumIdColumn.setVisible(false);

        // additional column
        String additionalColHeader = PeptideTableModel.TableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) table.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);

        // hide pI column
        String pIHeader = PeptideTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader();
        TableColumnExt pICol = (TableColumnExt) table.getColumn(pIHeader);
        pICol.setVisible(false);

        // add mouse motion listener
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, additionalColHeader));
        table.addMouseListener(new ShowParamsMouseListener(controller, table, additionalColHeader));

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

        // hide row number column
        String rowNumHeader = DatabaseSearchTableModel.TableHeader.ROW_NUMBER_COLUMN.getHeader();
        TableColumnExt rowColumn = (TableColumnExt) searchTable.getColumn(rowNumHeader);
        rowColumn.setVisible(false);

        // add cell renderer to view column
        String viewColumnHeader = DatabaseSearchTableModel.TableHeader.VIEW.getHeader();
        TableColumnExt viewColumn = (TableColumnExt) searchTable.getColumn(viewColumnHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("add.experiment.small.icon"));
        viewColumn.setCellRenderer(new IconRenderer(icon));
        viewColumn.setMaxWidth(50);

        // pubmed column
        String pubmedHeader = DatabaseSearchTableModel.TableHeader.PUBMED_ID.getHeader();
        TableColumnExt pubmedColumn = (TableColumnExt) searchTable.getColumn(pubmedHeader);
        Pattern pubmedPattern = Pattern.compile("[\\d,]+");
        pubmedColumn.setCellRenderer(new HyperLinkCellRenderer(pubmedPattern));


        // add mouse motion listener
        searchTable.addMouseMotionListener(new TableCellMouseMotionListener(searchTable, viewColumnHeader, pubmedHeader));
        searchTable.addMouseListener(new OpenExperimentMouseListener(searchTable, viewColumnHeader));
        searchTable.addMouseListener(new HyperLinkCellMouseClickListener(searchTable, pubmedHeader, new PrefixedHyperLinkGenerator(Constants.PUBMED_URL_PERFIX), pubmedPattern));

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
        Pattern pubmedPattern = Pattern.compile("[\\d,]+");
        pubMedColumn.setCellRenderer(new HyperLinkCellRenderer(pubmedPattern));
        pubMedColumn.setMaxWidth(100);

        // doi
        String doiColumnHeader = ReferenceTableModel.TableHeader.DOI.getHeader();
        TableColumnExt doiColumn = (TableColumnExt) referenceTable.getColumn(doiColumnHeader);
        doiColumn.setCellRenderer(new HyperLinkCellRenderer());
        doiColumn.setMaxWidth(100);

        // add mouse motion listener
        referenceTable.addMouseMotionListener(new TableCellMouseMotionListener(referenceTable, pubMedColumnHeader, doiColumnHeader));
        referenceTable.addMouseListener(new HyperLinkCellMouseClickListener(referenceTable, pubMedColumnHeader, new PrefixedHyperLinkGenerator(Constants.PUBMED_URL_PERFIX), pubmedPattern));
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
    public static JTable createQuantProteinTable(DataAccessController controller, TableModel tableModel) {
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        DefaultPrideTable quantProteinTable = new DefaultPrideTable(tableModel, columnModel);
        quantProteinTable.setAutoCreateColumnsFromModel(false);
        // add table model change listener
        tableModel.addTableModelListener(new BarChartColumnListener(quantProteinTable));

        // in case the compare doesn't exist
        List<TableColumn> columns = columnModel.getColumns(true);
        for (TableColumn column : columns) {
            if (column.getHeaderValue().equals(QuantProteinTableModel.TableHeader.COMPARE.getHeader())) {
                column.setMaxWidth(25);
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

        // isoelectric point column
        TableColumnExt isoelectricColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader());
        isoelectricColumn.setVisible(false);

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

        // additional column
        String additionalColHeader = ProteinTableModel.TableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) quantProteinTable.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        // add mouse motion listener
        quantProteinTable.addMouseMotionListener(new TableCellMouseMotionListener(quantProteinTable, additionalColHeader));
        quantProteinTable.addMouseListener(new ShowParamsMouseListener(controller, quantProteinTable, additionalColHeader));


        return quantProteinTable;
    }

    /**
     * Create a table for protein quantitative data
     *
     * @param controller data access controller
     * @return JTable   protein quantitative table
     */
    public static JTable createQuantProteinTable(DataAccessController controller) {
        QuantProteinTableModel tableModel = new QuantProteinTableModel();
        return createQuantProteinTable(controller, tableModel);
    }

    /**
     * Create a table for peptide quantitative data
     *
     * @param se search engine
     * @return JTable  peptide table
     */
    public static JTable createQuantPeptideTable(DataAccessController controller, SearchEngine se) {
        QuantPeptideTableModel tableModel = new QuantPeptideTableModel(se);
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
        peptideColumn.setPreferredWidth(150);

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
        double minLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
        double maxLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
        DeltaMZRenderer renderer = new DeltaMZRenderer(minLimit, maxLimit);
        deltaMassColumn.setCellRenderer(renderer);

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

        // additional column
        String additionalColHeader = ProteinTableModel.TableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) quantPeptideTable.getColumn(additionalColHeader);
        Icon detailIcon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(detailIcon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        // add mouse motion listener
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, additionalColHeader));
        quantPeptideTable.addMouseListener(new ShowParamsMouseListener(controller, quantPeptideTable, additionalColHeader));


        return quantPeptideTable;
    }
}
