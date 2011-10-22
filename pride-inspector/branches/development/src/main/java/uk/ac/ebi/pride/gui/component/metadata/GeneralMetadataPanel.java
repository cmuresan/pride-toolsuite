package uk.ac.ebi.pride.gui.component.metadata;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.jdesktop.swingx.table.TableColumnExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.listener.HyperLinkCellMouseClickListener;
import uk.ac.ebi.pride.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.gui.component.table.model.ParamTableModel;
import uk.ac.ebi.pride.gui.component.table.renderer.HyperLinkCellRenderer;
import uk.ac.ebi.pride.term.CvTermReference;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
/*
 * Created by JFormDesigner on Sat Jul 23 08:30:00 BST 2011
 */


/**
 * @author User #2
 */
public class GeneralMetadataPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(GeneralMetadataPanel.class);

    private DataAccessController controller;

    public GeneralMetadataPanel(MetaData metaData, DataAccessController controller) {
        this.controller = controller;
        populateComponents(metaData);
        initComponents();
    }

    /**
     * Create key components and populate them with values
     *
     * @param metaData meta data
     */
    private void populateComponents(MetaData metaData) {
        // get accession
        String accession = metaData.getAccession();
        accessionField = new JTextField();
        if (accession != null) {
            accessionField.setText(accession);
        }

        expTitleField = new JTextField();
        shortLabelField = new JTextField();

        if (metaData instanceof Experiment) {
            // get experiment title
            String expTitle = ((Experiment) metaData).getTitle();
            if (expTitle != null) {
                expTitleField.setText(expTitle);
            }
            // get short label
            String sl = ((Experiment) metaData).getShortLabel();
            if (sl != null) {
                shortLabelField.setText(sl);
            }
        }
        expTitleField.setCaretPosition(0);
        shortLabelField.setCaretPosition(0);

        projectField = new JTextField();
        expDescArea = new JTextPane();
        List<CvParam> cvs = metaData.getCvParams();
        if (cvs != null) {
            for (CvParam cv : cvs) {
                // get project name
                if (CvTermReference.PROJECT_NAME.getAccession().equals(cv.getAccession())) {
                    projectField.setText(cv.getValue());
                } else if (CvTermReference.EXPERIMENT_DESCRIPTION.getAccession().equals(cv.getAccession())) {
                    // get experiment description
                    expDescArea.setText(cv.getValue());
                }
            }
        }
        projectField.setCaretPosition(0);
        expDescArea.setCaretPosition(0);

        // species field
        speciesField = new JTextField();
        String species = "";
        Set<String> speciesAcc = new HashSet<String>();
        String tissues = "";
        Set<String> tissuesAcc = new HashSet<String>();

        List<Sample> samples = metaData.getSamples();
        if (samples != null) {
            for (Sample sample : samples) {
                for (CvParam cvParam : sample.getCvParams()) {
                    String cvAcc = cvParam.getAccession();
                    String name = cvParam.getName();
                    String cvLabel = cvParam.getCvLookupID().toLowerCase();
                    if ("newt".equals(cvLabel)) {
                        if (!speciesAcc.contains(cvAcc)) {
                            species += ("".equals(species) ? "" : ", ") + name;
                            speciesAcc.add(cvAcc);
                        }
                    } else if ("bto".equals(cvLabel)) {
                        if (!tissuesAcc.contains(cvAcc)) {
                            tissues += ("".equals(tissues) ? "" : ", ") + name;
                            tissuesAcc.add(cvAcc);
                        }
                    }
                }
            }
        }

        speciesField.setText(species);
        speciesField.setCaretPosition(0);

        // tissue field
        tissueField = new JTextField();
        tissueField.setText(tissues);
        tissueField.setCaretPosition(0);


        // instrument field
        instrumentField = new JTextField();
        String instrumentStr = "";
        List<InstrumentConfiguration> instruments = metaData.getInstrumentConfigurations();
        for (InstrumentConfiguration instrument : instruments) {
            instrumentStr += instrument.getId();
        }
        instrumentField.setText(instrumentStr);
        instrumentField.setCaretPosition(0);

        // search engine field
        searchEngineField = new JTextField();
        // search database field
        searchDatabaseField = new JTextField();

        Comparable identId = null;
        try {
            Collection<Comparable> identIds = controller.getIdentificationIds();
            identId = CollectionUtils.getElement(identIds, 0);
            Object engine = identId == null ? "Unknown" : controller.getSearchEngine().getOriginalTitle();
            engine = engine == null ? "Unknown" : engine;
            searchEngineField.setText(engine.toString());
            searchEngineField.setCaretPosition(0);
        }catch (DataAccessException e) {
            logger.error("Failed to retrieve search engine", e);
        }

        try {
            Object database = identId == null ? "Unknown" : controller.getSearchDatabase(identId);
            database = database == null ? "Unknown" : database;
            Object version = identId == null ? "" : controller.getSearchDatabaseVersion(identId);
            version = version == null ? "" : version;
            searchDatabaseField.setText(database.toString() + " " + version);
            searchDatabaseField.setCaretPosition(0);
        } catch (DataAccessException e) {
            logger.error("Failed to retrieve search database", e);
        }

        // reference
        if (metaData instanceof Experiment && ((Experiment) metaData).getReferences() != null) {
            List<Reference> references = ((Experiment) metaData).getReferences();
            referenceTable = TableFactory.createReferenceTable(references);
        } else {
            referenceTable = TableFactory.createReferenceTable(new ArrayList<Reference>());
        }

        // contact

        List<ParamGroup> contacts = metaData.getFileDescription().getContacts();
        contactTable = TableFactory.createContactTable(contacts == null ? new ArrayList<ParamGroup>() : contacts);

        // additional params
        ParamGroup paramGroup = new ParamGroup();
        List<CvParam> cvParams = metaData.getCvParams();
        if (cvParams != null) {
            for (CvParam cvParam : cvParams) {
                String acc = cvParam.getAccession();
                // get project name
                if (!CvTermReference.PROJECT_NAME.getAccession().equals(acc) &&
                        !CvTermReference.EXPERIMENT_DESCRIPTION.getAccession().equals(acc)) {
                    paramGroup.addCvParam(cvParam);
                }
            }
        }

        List<UserParam> userParams = metaData.getUserParams();
        if (userParams != null) {
            paramGroup.addUserParams(userParams);
        }

        additionalTable = TableFactory.createParamTable(paramGroup);
        // hyperlink ontology accessions
        String valColumnHeader = ParamTableModel.TableHeader.VALUE.getHeader();
        TableColumnExt accColumn = (TableColumnExt) additionalTable.getColumn(valColumnHeader);
        accColumn.setCellRenderer(new HyperLinkCellRenderer(Pattern.compile("http.*"), true));

        // add mouse motion listener
        additionalTable.addMouseMotionListener(new TableCellMouseMotionListener(additionalTable, valColumnHeader));
        additionalTable.addMouseListener(new HyperLinkCellMouseClickListener(additionalTable, valColumnHeader, null));

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        accessionLabel = new JLabel();
        expTitleLabel = new JLabel();
        shortLabel = new JLabel();
        projectLabel = new JLabel();
        expDescLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        referenceLabel = new JLabel();
        scrollPane2 = new JScrollPane();
        contactLabel = new JLabel();
        scrollPane3 = new JScrollPane();
        additionalLabel = new JLabel();
        scrollPane4 = new JScrollPane();
        speciesLabel = new JLabel();
        tissueLabel = new JLabel();
        instrumentLabel = new JLabel();
        searchEngineLabel = new JLabel();
        searchDatabaseLabel = new JLabel();

        //======== this ========
        setFocusable(false);

        //---- accessionLabel ----
        accessionLabel.setText("Experiment Accession");
        accessionLabel.setFont(accessionLabel.getFont().deriveFont(accessionLabel.getFont().getStyle() | Font.BOLD));
        accessionLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- expTitleLabel ----
        expTitleLabel.setText("Experiment Title");
        expTitleLabel.setFont(expTitleLabel.getFont().deriveFont(expTitleLabel.getFont().getStyle() | Font.BOLD));
        expTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- shortLabel ----
        shortLabel.setText("Experiment Label");
        shortLabel.setFont(shortLabel.getFont().deriveFont(shortLabel.getFont().getStyle() | Font.BOLD));
        shortLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- projectLabel ----
        projectLabel.setText("Project Name");
        projectLabel.setFont(projectLabel.getFont().deriveFont(projectLabel.getFont().getStyle() | Font.BOLD));
        projectLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- expDescLabel ----
        expDescLabel.setText("Experiment Description");
        expDescLabel.setFont(expDescLabel.getFont().deriveFont(expDescLabel.getFont().getStyle() | Font.BOLD));
        expDescLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- accessionField ----
        accessionField.setEditable(false);

        //---- expTitleField ----
        expTitleField.setEditable(false);

        //---- shortLabelField ----
        shortLabelField.setEditable(false);

        //---- projectField ----
        projectField.setEditable(false);

        //======== scrollPane1 ========
        {

            //---- expDescArea ----
            expDescArea.setEditable(false);
            scrollPane1.setViewportView(expDescArea);
        }

        //---- referenceLabel ----
        referenceLabel.setText("Reference");
        referenceLabel.setFont(referenceLabel.getFont().deriveFont(referenceLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane2 ========
        {
            scrollPane2.setPreferredSize(new Dimension(300, 220));

            //---- referenceTable ----
            referenceTable.setPreferredScrollableViewportSize(new Dimension(400, 200));
            scrollPane2.setViewportView(referenceTable);
        }

        //---- contactLabel ----
        contactLabel.setText("Contact");
        contactLabel.setFont(contactLabel.getFont().deriveFont(contactLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane3 ========
        {

            //---- contactTable ----
            contactTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
            scrollPane3.setViewportView(contactTable);
        }

        //---- additionalLabel ----
        additionalLabel.setText("Additional");
        additionalLabel.setFont(additionalLabel.getFont().deriveFont(additionalLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane4 ========
        {

            //---- additionalTable ----
            additionalTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
            scrollPane4.setViewportView(additionalTable);
        }

        //---- speciesLabel ----
        speciesLabel.setText("Species");
        speciesLabel.setFont(speciesLabel.getFont().deriveFont(speciesLabel.getFont().getStyle() | Font.BOLD));
        speciesLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- speciesField ----
        speciesField.setEditable(false);

        //---- tissueLabel ----
        tissueLabel.setText("Tissue");
        tissueLabel.setFont(tissueLabel.getFont().deriveFont(tissueLabel.getFont().getStyle() | Font.BOLD));
        tissueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- tissueField ----
        tissueField.setEditable(false);

        //---- instrumentLabel ----
        instrumentLabel.setText("Instrument");
        instrumentLabel.setFont(instrumentLabel.getFont().deriveFont(instrumentLabel.getFont().getStyle() | Font.BOLD));
        instrumentLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- instrumentField ----
        instrumentField.setEditable(false);

        //---- searchEngineLabel ----
        searchEngineLabel.setText("Search Engine");
        searchEngineLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        searchEngineLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        searchEngineLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        //---- searchEngineField ----
        searchEngineField.setEditable(false);

        //---- searchDatabaseLabel ----
        searchDatabaseLabel.setText("Search Database");
        searchDatabaseLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        searchDatabaseLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        searchDatabaseLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));

        //---- searchDatabaseField ----
        searchDatabaseField.setEditable(false);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup()
                                        .add(layout.createSequentialGroup()
                                                .add(scrollPane4, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                                                .add(layout.createParallelGroup()
                                                        .add(speciesLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                        .add(shortLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                        .add(expTitleLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                        .add(accessionLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                        .add(projectLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                        .add(instrumentLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                        .add(searchEngineLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                        .add(expDescLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(layout.createParallelGroup()
                                                        .add(layout.createSequentialGroup()
                                                                .add(scrollPane1, GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                                                                .addContainerGap())
                                                        .add(layout.createSequentialGroup()
                                                                .add(layout.createParallelGroup()
                                                                        .add(layout.createSequentialGroup()
                                                                                .add(searchEngineField, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
                                                                                .add(18, 18, 18)
                                                                                .add(searchDatabaseLabel, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(LayoutStyle.RELATED)
                                                                                .add(searchDatabaseField, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE))
                                                                        .add(layout.createSequentialGroup()
                                                                                .add(speciesField, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                                                                .add(18, 18, 18)
                                                                                .add(tissueLabel, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(LayoutStyle.RELATED)
                                                                                .add(tissueField, GroupLayout.PREFERRED_SIZE, 233, GroupLayout.PREFERRED_SIZE))
                                                                        .add(expTitleField, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                                                                        .add(accessionField, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                                                        .add(shortLabelField, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                                                        .add(projectField, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE))
                                                                .add(11, 11, 11))
                                                        .add(layout.createSequentialGroup()
                                                                .add(instrumentField, GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                                                                .addContainerGap())))
                                        .add(layout.createSequentialGroup()
                                                .add(additionalLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(625, Short.MAX_VALUE))
                                        .add(layout.createSequentialGroup()
                                                .add(scrollPane3, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .add(layout.createSequentialGroup()
                                                .add(contactLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(625, Short.MAX_VALUE))
                                        .add(layout.createSequentialGroup()
                                                .add(scrollPane2, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .add(layout.createSequentialGroup()
                                                .add(referenceLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(625, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(accessionLabel)
                                        .add(accessionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(expTitleLabel)
                                        .add(expTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(shortLabel)
                                        .add(shortLabelField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(projectLabel)
                                        .add(projectField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(speciesLabel)
                                        .add(speciesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .add(tissueLabel)
                                        .add(tissueField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(17, 17, 17)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(instrumentField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .add(instrumentLabel))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(searchEngineField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .add(searchEngineLabel)
                                        .add(searchDatabaseLabel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
                                        .add(searchDatabaseField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup()
                                        .add(layout.createSequentialGroup()
                                                .add(layout.createParallelGroup()
                                                        .add(layout.createSequentialGroup()
                                                                .add(77, 77, 77)
                                                                .add(referenceLabel))
                                                        .add(scrollPane1, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(scrollPane2, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(contactLabel)
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(scrollPane3, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(additionalLabel)
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(scrollPane4, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                                                .add(4, 4, 4))
                                        .add(layout.createSequentialGroup()
                                                .add(expDescLabel)
                                                .addContainerGap(243, Short.MAX_VALUE))))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel accessionLabel;
    private JLabel expTitleLabel;
    private JLabel shortLabel;
    private JLabel projectLabel;
    private JLabel expDescLabel;
    private JTextField accessionField;
    private JTextField expTitleField;
    private JTextField shortLabelField;
    private JTextField projectField;
    private JScrollPane scrollPane1;
    private JTextPane expDescArea;
    private JLabel referenceLabel;
    private JScrollPane scrollPane2;
    private JTable referenceTable;
    private JLabel contactLabel;
    private JScrollPane scrollPane3;
    private JTable contactTable;
    private JLabel additionalLabel;
    private JScrollPane scrollPane4;
    private JTable additionalTable;
    private JLabel speciesLabel;
    private JTextField speciesField;
    private JLabel tissueLabel;
    private JTextField tissueField;
    private JLabel instrumentLabel;
    private JTextField instrumentField;
    private JLabel searchEngineLabel;
    private JTextField searchEngineField;
    private JLabel searchDatabaseLabel;
    private JTextField searchDatabaseField;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
