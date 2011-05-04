package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Mar-2010
 * Time: 11:58:32
 */
public class MzGraphMetaDataPane extends JPanel {

    private JTabbedPane tabPane = null;
    private MzGraph mzGraph = null;

    public MzGraphMetaDataPane() {
        this(null);
    }

    public MzGraphMetaDataPane(MzGraph mzGraph) {
        this.mzGraph = mzGraph;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        tabPane = new JTabbedPane();
        
        if (mzGraph != null) {
            if (mzGraph instanceof Spectrum) {
                initializeMzGraphPane(mzGraph);
            } else {
                initializeMzGraphPane(mzGraph);
            }
        }

        this.add(tabPane, BorderLayout.CENTER);
    }

    private void initializeMzGraphPane(MzGraph mz) {
        // build general tab
        JPanel generalPane = createGeneralDescPane(mz);
        JScrollPane genScrollPane = new JScrollPane(generalPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tabPane.add("General", genScrollPane);
        
        // build experiment settings tab
        JPanel settingsPane = createExpSettingsPane(mz);
        if (settingsPane != null) {
            JScrollPane settingScrollPane = new JScrollPane(settingsPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tabPane.add("Experiment Settings", settingScrollPane);
        }
        
        // build precursor and product
        JPanel resultsPane = createExpResultsPane(mz);
        if (resultsPane != null) {
            JScrollPane settingScrollPane = new JScrollPane(resultsPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tabPane.add("Experiment Results", settingScrollPane);
        }
    }

    private JPanel createGeneralDescPane(MzGraph mz) {
        JPanel generalPane = new JPanel();
        generalPane.setBackground(Color.white);
        generalPane.setLayout(new BoxLayout(generalPane, BoxLayout.X_AXIS));
        generalPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel spectrumDescPane = MetaDataGUIFactory.createHeaderPane("General",
                                    MetaDataGUIFactory.createMzGraphDescPane(mz));
        generalPane.add(spectrumDescPane);

        SourceFile sourceFile = mz.getSourceFile();
        if (sourceFile != null) {
            JPanel sourcePane = MetaDataGUIFactory.createHeaderPane("Source File",
                                    MetaDataGUIFactory.createSourceFilePane(sourceFile));
            generalPane.add(sourcePane);
        }

        return generalPane;
    }

    private JPanel createExpSettingsPane(MzGraph mz) {
        JPanel settingsPane = null;

        Sample sample = mz.getSample();
        Instrument instrument = mz.getInstrument();
        DataProcessing dataProcessing = mz.getDataProcessing();
        if (instrument != null || dataProcessing != null || sample != null) {
            settingsPane = new JPanel();
            settingsPane.setBackground(Color.white);
            settingsPane.setLayout(new BoxLayout(settingsPane, BoxLayout.X_AXIS));
            settingsPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            if (sample !=  null) {
                JPanel samplePane = MetaDataGUIFactory.createHeaderPane("Sample",
                                        MetaDataGUIFactory.createSamplePane(sample));
                settingsPane.add(samplePane);
            }

            if (instrument != null) {
                JPanel instrumentPane = MetaDataGUIFactory.createHeaderPane("Instrument",
                                            MetaDataGUIFactory.createInstrumentPane(instrument));
                settingsPane.add(instrumentPane);
            }

            if (dataProcessing != null) {
                JPanel dataProcessingPane = MetaDataGUIFactory.createHeaderPane("Data Processing",
                                                MetaDataGUIFactory.createDataProcessingPane(dataProcessing));
                settingsPane.add(dataProcessingPane);
            }
        }

        return settingsPane;
    }

    private JPanel createExpResultsPane(MzGraph mz) {
        JPanel resultPane =  null;

        ScanList scanList = null;
        if (mz instanceof Spectrum) {
            scanList = ((Spectrum)mz).getScanList();
        }
        java.util.List<Precursor> precursors = mz.getPrecursors();
        java.util.List<Product> products = mz.getProducts();
        if (scanList != null || (precursors != null && !precursors.isEmpty())
                || (products != null && !products.isEmpty())) {
            resultPane = new JPanel();
            resultPane.setBackground(Color.white);
            resultPane.setLayout(new BoxLayout(resultPane, BoxLayout.X_AXIS));
            resultPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            if (scanList != null) {
                JPanel scanListPane = MetaDataGUIFactory.createHeaderPane("Scan List",
                                        MetaDataGUIFactory.createScanListPane(scanList));
                resultPane.add(scanListPane);
            }

            if (precursors != null && !precursors.isEmpty()) {
                JPanel precursorPane = MetaDataGUIFactory.createHeaderPane("Precursor");
                for(Precursor precursor : precursors) {
                    precursorPane.add(MetaDataGUIFactory.createPrecursorPane(precursor));
                }
                resultPane.add(precursorPane);
            }

            if (products != null && !products.isEmpty()) {
                JPanel productPane = MetaDataGUIFactory.createHeaderPane("Product");
                for(Product product : products) {
                    productPane.add(MetaDataGUIFactory.createProductPane(product));
                }
                resultPane.add(productPane);
            }

        }

        return resultPane;
    }
}
