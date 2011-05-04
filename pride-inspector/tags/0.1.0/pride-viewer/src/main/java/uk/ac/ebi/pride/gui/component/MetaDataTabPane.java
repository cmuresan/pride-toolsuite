package uk.ac.ebi.pride.gui.component;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 05-Mar-2010
 * Time: 15:12:07
 */
public class MetaDataTabPane extends JPanel implements PropertyChangeListener {
    private static final Logger logger = Logger.getLogger(MetaDataTabPane.class.getName());
    private static final String PANE_TITLE = "General";
    private DataAccessController controller = null;
    private JScrollPane innerPane = null;

    public MetaDataTabPane(DataAccessController controller) {
        this.setLayout(new BorderLayout());
        this.setName(PANE_TITLE);
        this.controller = controller;
        buildGUI();
    }

    private void buildGUI() {
        if (innerPane != null) {
            this.remove(innerPane);
        }

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 200));
        contentPane.setBackground(Color.white);
        try {
            if (controller.isExperimentFriendly()) {
                contentPane.add(buildExperimentMetaDataPane());
            } else if (controller.isSpectrumFriendly()) {
                contentPane.add(buildMzMLMetaDataPane());
            } else if (controller.isIdentificationFriendly()) {
                contentPane.add(buildIdentifcationMetaDataPane());
            }
        } catch(DataAccessException dae) {
            String msg = String.format("%s failed on : %s", this, dae);
            logger.log(Level.ERROR, msg, dae);
        }

        innerPane = new JScrollPane(contentPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(innerPane, BorderLayout.CENTER);
    }

    private JPanel buildIdentifcationMetaDataPane() {
        //ToDo: need implementation
        return null;
    }

    private JPanel buildMzMLMetaDataPane() throws DataAccessException {
        MzML mzML = controller.getMzMLMetaData();
        JPanel metaPane = new JPanel();
        metaPane.setOpaque(false);
        metaPane.setLayout(new BoxLayout(metaPane, BoxLayout.Y_AXIS));
        
        // file content
        ParamGroup fileContent = mzML.getFileContent();
        if (fileContent != null) {
            JPanel fileContentPane = MetaDataGUIFactory.createHeaderPane("File Content",
                                            MetaDataGUIFactory.createParamGroupPane(fileContent));
            metaPane.add(fileContentPane);
        }
        // contacts
        java.util.List<ParamGroup> contacts = mzML.getContacts();
        if (contacts != null && !contacts.isEmpty()) {
            JPanel contactPane = MetaDataGUIFactory.createHeaderPane("Contact");
            for(ParamGroup contact : contacts) {
                contactPane.add(MetaDataGUIFactory.createParamGroupPane(contact));
            }
            metaPane.add(contactPane);
        }
        
        // list of samples
        java.util.List<Sample> samples = mzML.getSamples();
        if (samples !=  null && !samples.isEmpty()) {
            JPanel samplePane = MetaDataGUIFactory.createHeaderPane("Sample");
            for(Sample sample : samples) {
                samplePane.add(MetaDataGUIFactory.createSamplePane(sample));
            }
            metaPane.add(samplePane);
        }
        
        // list of instruments
        java.util.List<Instrument> instruments = mzML.getInstruments();
        if (instruments != null && !instruments.isEmpty()) {
            JPanel instrumentPane = MetaDataGUIFactory.createHeaderPane("Instrument");
            for(Instrument instrument : instruments) {
                JPanel contentPane = MetaDataGUIFactory.createInstrumentPane(instrument);
                instrumentPane.add(contentPane);
            }
            metaPane.add(instrumentPane);
        }
        
        // list of data processings
        java.util.List<DataProcessing> dataProcessings = mzML.getDataProcessing();
        if (dataProcessings != null && !dataProcessings.isEmpty()) {
            JPanel dataProcessingPane = MetaDataGUIFactory.createHeaderPane("Data Processing");
            for(DataProcessing dataProcessing : dataProcessings) {
                JPanel contentPane = MetaDataGUIFactory.createDataProcessingPane(dataProcessing);
                dataProcessingPane.add(contentPane);
            }
            metaPane.add(dataProcessingPane);
        }

        return metaPane;
    }

    private JPanel buildExperimentMetaDataPane() {
        JPanel panel = new JPanel();
        return panel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
       String evtName = evt.getPropertyName();
       if (DataAccessController.FOREGROUND_EXPERIMENT_CHANGED.equals(evtName)) {
           buildGUI();
       }
    }
}