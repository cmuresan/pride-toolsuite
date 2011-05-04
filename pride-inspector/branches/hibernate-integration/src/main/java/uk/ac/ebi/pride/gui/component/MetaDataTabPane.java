package uk.ac.ebi.pride.gui.component;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * MetaDataTabPane displays all the meta data shared across the data source/experiment.
 * It listens to the following property change event:
 *
 * 1. Foreground experiment change event, it should update itself.
 *
 * User: rwang
 * Date: 05-Mar-2010
 * Time: 15:12:07
 */
public class MetaDataTabPane extends DataAccessControllerPane {
    private static final Logger logger = Logger.getLogger(MetaDataTabPane.class.getName());
    private static final String PANE_TITLE = "General";
    private static final String GENERAL = "General";
    private static final String FILE_CONTENET = "File Content";
    private static final String SOURCE_FILE = "Source File";
    private static final String CONTACT = "Contact";
    private static final String SAMPLE = "Sample";
    private static final String SOFTWARE = "Software";
    private static final String SCAN_SETTING = "Scan Setting";
    private static final String INSTRUMENT_CONFIG = "Instrument Configration";
    private static final String DATA_PROCESSING = "Data Processing";
    private static final String PROTOCOL = "Protocol";
    private static final String REFERENCE = "Reference";

    private JPanel metaDataContainer;

    public MetaDataTabPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setName(PANE_TITLE);
    }

    protected void addComponents() {
        // remove all existing components.
        this.removeAll();

        JPanel contentPane = new JPanel();
        //contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 200));
        contentPane.setBackground(Color.white);
        try {
            buildMetaDataPane();
            contentPane.add(metaDataContainer, BorderLayout.CENTER);
        } catch(DataAccessException dae) {
            String msg = String.format("%s failed on : %s", this, dae);
            logger.log(Level.ERROR, msg, dae);
        }

        JScrollPane innerPane = new JScrollPane(contentPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(innerPane, BorderLayout.CENTER);
    }

    @Override
    protected void updatePropertyChange() {
        addComponents();
    }

    private void buildMetaDataPane() throws DataAccessException {
        MetaData metaData = controller.getMetaData();

        // init container
        createContainer();

        // general info
        addGeneralContentPane(metaData);

        // samples
        addSamples(metaData);

        // protocol
        addProtocol(metaData);

        // references
        addReferences(metaData);

        // instruments
        addInstrumentConfigurations(metaData);

        // scan settings
        addScanSettings(metaData);

        // data processings
        addDataProcessings(metaData);

        // softwares
        addSoftwares(metaData);
        
        // file content
        addFileDescription(metaData);

    }

    private void createContainer() {
        metaDataContainer = new JPanel();
        metaDataContainer.setOpaque(false);
        metaDataContainer.setLayout(new BoxLayout(metaDataContainer, BoxLayout.Y_AXIS));
    }

    private void addGeneralContentPane(MetaData metaData) {
        Collection<Parameter> generalContent = MetaDataHelper.getGeneralContent(metaData);
        addCollapsablePane(GENERAL, generalContent);
    }

    private void addFileDescription(MetaData metaData) {
        FileDescription fileDesc = metaData.getFileDescription();
        if (fileDesc != null) {
            // file content
            addFileContent(fileDesc);
            // source Files
            addSourceFiles(fileDesc);
            // contacts
            addContacts(fileDesc);
        }
    }

    private void addFileContent(FileDescription fileDesc) {
        ParamGroup fileContent = fileDesc.getFileContent();
        if (fileContent != null) {
            Collection<Parameter> params = MetaDataHelper.getParamGroup(fileContent);
            addCollapsablePane(FILE_CONTENET, params);
        }
    }

    private void addSourceFiles(FileDescription fileDesc) {
        java.util.List<SourceFile> sourceFiles = fileDesc.getSourceFiles();
        if (sourceFiles != null) {
            for (SourceFile sourceFile : sourceFiles) {
                Collection<Parameter> params = MetaDataHelper.getSourceFile(sourceFile);
                addCollapsablePane(SOURCE_FILE, params);
            }
        }

    }

    private void addContacts(FileDescription fileDesc) {
        java.util.List<ParamGroup> contacts = fileDesc.getContacts();
        if (contacts != null) {
            for (ParamGroup contact : contacts) {
                Collection<Parameter> params = MetaDataHelper.getParamGroup(contact);
                addCollapsablePane(CONTACT, params);
            }
        }
    }

    private void addSamples(MetaData metaData) {
        java.util.List<Sample> samples = metaData.getSamples();

        if (samples != null) {
            for (Sample sample : samples) {
                Collection<Parameter> params = MetaDataHelper.getSample(sample);
                addCollapsablePane(SAMPLE, params);
            }
        }
    }

    private void addSoftwares(MetaData metaData) {
        java.util.List<Software> softwares = metaData.getSoftwares();

        if (softwares != null) {
            for (Software software : softwares) {
                Collection<Parameter> params  = MetaDataHelper.getSoftware(software);
                addCollapsablePane(SOFTWARE, params);
            }
        }
    }

    private void addScanSettings(MetaData metaData) {
        java.util.List<ScanSetting> scanSettings = metaData.getScanSettings();

        if (scanSettings != null) {
            for (ScanSetting scanSetting : scanSettings) {
                Collection<Parameter> params = MetaDataHelper.getScanSetting(scanSetting);
                addCollapsablePane(SCAN_SETTING, params);
            }
        }
    }

    private void addInstrumentConfigurations(MetaData metaData) {
        java.util.List<InstrumentConfiguration> instruments = metaData.getInstrumentConfigurations();

        if (instruments != null) {
            for (InstrumentConfiguration instrument : instruments) {
                Collection<Parameter> params = MetaDataHelper.getInstrumentConfiguration(instrument);
                addCollapsablePane(INSTRUMENT_CONFIG, params);
            }
        }
    }

    private void addDataProcessings(MetaData metaData) {
        java.util.List<DataProcessing> dataProcs = metaData.getDataProcessings();

        if (dataProcs != null) {
            for (DataProcessing dataProc : dataProcs) {
                Collection<Parameter> params = MetaDataHelper.getDataProcessing(dataProc);
                addCollapsablePane(DATA_PROCESSING, params);
            }
        }
    }

    private void addProtocol(MetaData metaData) {
        if (metaData instanceof Experiment) {
            Protocol protocol = ((Experiment) metaData).getProtocol();

            if (protocol !=  null) {
                Collection<Parameter> params = MetaDataHelper.getProtocol(protocol);
                addCollapsablePane(PROTOCOL, params);
            }
        }
    }

    private void addReferences(MetaData metaData) {
        if (metaData instanceof Experiment) {
            java.util.List<Reference> references = ((Experiment) metaData).getReferences();

            if (references != null) {
                for (Reference reference : references) {
                    Collection<Parameter> params = MetaDataHelper.getReference(reference);
                    addCollapsablePane(REFERENCE, params);
                }
            }
        }
    }

    private void addCollapsablePane(String title, Collection<Parameter> params) {
        CollapsablePane cPane = new CollapsablePane(title);
        JPanel contentPane = new MetaDataViewer(params);
        contentPane.setOpaque(false);
        cPane.setContentComponent(contentPane);
        metaDataContainer.add(cPane);
        metaDataContainer.add(Box.createRigidArea(new Dimension(0, 10)));
    }
}