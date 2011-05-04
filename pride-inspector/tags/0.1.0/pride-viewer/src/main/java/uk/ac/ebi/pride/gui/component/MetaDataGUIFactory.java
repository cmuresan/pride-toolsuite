package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.core.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Mar-2010
 * Time: 15:09:58
 */
public class MetaDataGUIFactory {

    public static JPanel createGeneralPane(MzGraph mz) {
        JPanel resultPane = new JPanel();
        resultPane.setLayout(new BoxLayout(resultPane,BoxLayout.Y_AXIS));
        JPanel generalPane = new JPanel();
        TitledBorder title = BorderFactory.createTitledBorder("General");
        generalPane.setBorder(title);
        generalPane.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        // spectrum id
        String spectrumId = mz.getId();
        addGridBagComponent("Spectrum ID:", spectrumId, generalPane, c, 0);
        // index
        BigInteger index = mz.getIndex();
        addGridBagComponent("Index:", index != null ? index.toString() : "", generalPane, c, 1);
        // default array length
        int arrLength = mz.getDefaultArrayLength();
        addGridBagComponent("Default array length:", arrLength+"", generalPane, c, 2);
        // start time stamp
        Date timeStamp = mz.getStartTimeStamp();
        addGridBagComponent("Start time stamp:", timeStamp!=null? timeStamp.toString():"", generalPane, c, 3);
        resultPane.add(generalPane);

        // source file
        SourceFile sourceFile = mz.getSourceFile();
        if (sourceFile != null) {
            JPanel sourceFilePane = new JPanel();
            title = BorderFactory.createTitledBorder("Source file");
            sourceFilePane.setLayout(new GridBagLayout());
            sourceFilePane.setBorder(title);

            addGridBagComponent("ID:", sourceFile.getId(), sourceFilePane, c, 0);
            addGridBagComponent("Name:", sourceFile.getName(), sourceFilePane, c, 1);
            addGridBagComponent("Path:", sourceFile.getPath(), sourceFilePane, c, 2);

            resultPane.add(sourceFilePane);
        }

        return resultPane;
    }

    public static JPanel createMzGraphDescPane(MzGraph mz) {
        Map<String, String> extraRow = new LinkedHashMap<String, String>();
        extraRow.put("ID", mz.getId());

        BigInteger index = mz.getIndex();
        if (index !=  null) {
            extraRow.put("Index", index.toString());
        }

        extraRow.put("Default array length", mz.getDefaultArrayLength()+"");
        Date timeStamp = mz.getStartTimeStamp();
        if (timeStamp != null) {
            extraRow.put("Start time stamp", timeStamp.toString());
        }
        
        return MetaDataGUIFactory.createComplexParamGroupPane(extraRow, mz);
    }

    public static JPanel createSourceFilePane(SourceFile sourceFile) {
        Map<String, String> extraRowMap = new LinkedHashMap<String, String>();
        extraRowMap.put("ID", sourceFile.getId());
        String name = sourceFile.getName();
        if (name != null) {
            extraRowMap.put("Name", name);
        }
        String path = sourceFile.getPath();
        if (path != null) {
            extraRowMap.put("Path", path);
        }

        return createComplexParamGroupPane(extraRowMap, sourceFile);
    }
    
    public static JPanel createSoftwarePane(Software software) {
        Map<String, String> extraRowMap = new LinkedHashMap<String, String>();
        extraRowMap.put("ID", software.getId());
        String name = software.getName();
        if (name != null) {
            extraRowMap.put("Name", name);
        }
        String version = software.getVersion();
        if (version != null) {
            extraRowMap.put("Version", version);
        }

        return createComplexParamGroupPane(extraRowMap, software);
    }

    public static JPanel createSamplePane(Sample sample) {
        Map<String, String> extraRowMap = new LinkedHashMap<String, String>();
        extraRowMap.put("ID", sample.getId());
        String name = sample.getName();
        if (name != null) {
            extraRowMap.put("Name", name);
        }
                
        return createComplexParamGroupPane(extraRowMap, sample);
    }

    public static JPanel createInstrumentPane(Instrument instrument) {
        JPanel instrumentPane = new JPanel();
        instrumentPane.setLayout(new BoxLayout(instrumentPane, BoxLayout.Y_AXIS));
        Border border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.white);
        // general info
        Map<String, String> extraRow = new LinkedHashMap<String, String>();
        extraRow.put("ID", instrument.getId());
        JPanel genPane = createComplexParamGroupPane(extraRow, instrument);
        genPane.setBorder(border);
        instrumentPane.add(genPane);
        // source
        java.util.List<ParamGroup> sources = instrument.getSource();
        if (sources != null && !sources.isEmpty()) {
            createMetaDataSubPane("Source", createParamGroupPanelList(sources), border);
        }
        // analyzer
        java.util.List<ParamGroup> analyzers = instrument.getAnalyzerList();
        if (analyzers != null && !analyzers.isEmpty()) {
            createMetaDataSubPane("Analyzer", createParamGroupPanelList(analyzers), border);
        }
        // dectector
        java.util.List<ParamGroup> dectectors = instrument.getAnalyzerList();
        if (dectectors != null && !dectectors.isEmpty()) {
            createMetaDataSubPane("Dectector", createParamGroupPanelList(dectectors), border);
        }
        // software
        Software software = instrument.getSoftware();
        if (software !=  null) {
            instrumentPane.add(createMetaDataSubPane("Software", createSoftwarePane(software), border));
        }
        // scansetting
        // ToDo: implementation for scansettings

        return instrumentPane;
    }

    public static JPanel createDataProcessingPane(DataProcessing dataProcessing) {
        JPanel dataProcessingPane = new JPanel();
        dataProcessingPane.setLayout(new BoxLayout(dataProcessingPane, BoxLayout.Y_AXIS));
        Border border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.white);

        // id
        Map<String, String> extraRow = new LinkedHashMap<String, String>();
        extraRow.put("ID", dataProcessing.getId());
        JPanel genPane = createComplexParamGroupPane(extraRow, null);
        genPane.setBorder(border);
        dataProcessingPane.add(genPane);
        // list of processing method
        java.util.List<ProcessingMethod> processingMethods = dataProcessing.getProcessingMethods();
        for(ProcessingMethod processingMethod : processingMethods) {
            JPanel paramPane = createParamGroupPane(processingMethod);
            paramPane.setBorder(border);
            dataProcessingPane.add(paramPane);
            Software software = processingMethod.getSoftware();
            if (software !=  null) {
                dataProcessingPane.add(createMetaDataSubPane("Software", createSoftwarePane(software), border));
            }
        }

        return dataProcessingPane;
    }

    public static JPanel createScanListPane(ScanList scanList) {
        JPanel scanListPane = new JPanel();
        scanListPane.setLayout(new BoxLayout(scanListPane, BoxLayout.Y_AXIS));
        Border border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.white);

        // params
        JPanel genPane = createParamGroupPane(scanList);
        genPane.setBorder(border);
        scanListPane.add(genPane);
        // scans
        java.util.List<Scan> scans = scanList.getScans();
        if (scans != null && !scans.isEmpty()) {
            for(Scan scan : scans) {
                JPanel paramPane = createParamGroupPane(scan);
                paramPane.setBorder(border);
                scanListPane.add(paramPane);
                // ToDo: Instrument?
                // ToDo: Source file?
                java.util.List<ParamGroup> scanWindows = scan.getScanWindows();
                if (scanWindows != null && !scanWindows.isEmpty()) {
                    for(ParamGroup scanWindow : scanWindows) {
                        JPanel scanWindowPane = createParamGroupPane(scanWindow);
                        scanListPane.add(createMetaDataSubPane("Scan Window", scanWindowPane, border));
                    }
                }
            }
        }

        return scanListPane;
    }

    public static JPanel createPrecursorPane(Precursor precursor) {
        JPanel precursorPane = new JPanel();
        precursorPane.setLayout(new BoxLayout(precursorPane, BoxLayout.Y_AXIS));
        Border border = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.white);
        
        // general
        Spectrum spectrum = precursor.getSpectrum();
        if (spectrum != null) {
            Map<String, String> extraRow = new LinkedHashMap<String, String>();
            extraRow.put("Spectrum Ref", spectrum.getId());
            JPanel genPane = createComplexParamGroupPane(extraRow, null);
            genPane.setBorder(border);
            precursorPane.add(genPane);
        }
        // isolation window
        ParamGroup isoWindow = precursor.getIsolationWindow();
        if (isoWindow != null) {
            JPanel isoPane = createParamGroupPane(precursor.getIsolationWindow());
            precursorPane.add(createMetaDataSubPane("Isolation Window", isoPane, border));
        }
        // selected ion
        java.util.List<ParamGroup> selIons = precursor.getSelectedIon();
        if (selIons != null && !selIons.isEmpty()) {
            for(ParamGroup selIon : selIons) {
                JPanel selIonPane = createParamGroupPane(selIon);
                precursorPane.add(createMetaDataSubPane("Selected Ion", selIonPane, border));
            }
        }
        // activation
        ParamGroup activation = precursor.getActivation();
        if (activation != null) {
            JPanel actPane = createParamGroupPane(precursor.getActivation());
            precursorPane.add(createMetaDataSubPane("Activation", actPane, border));
        }

        return precursorPane;
    }

    public static JPanel createProductPane(Product product) {
        JPanel productPane = new JPanel();
        productPane.setLayout(new BoxLayout(productPane, BoxLayout.Y_AXIS));

        //isolation window
        JPanel isoPane = createParamGroupPane(product.getIsolationWindow());
        productPane.add(isoPane);
        return productPane;
    }

    public static JPanel createComplexParamGroupPane(Map<String, String> extraRowMap, ParamGroup paramGroup) {
        JPanel complexPane = new JPanel();
        complexPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int rowCount = 0;
        
        for (Map.Entry<String, String> entry : extraRowMap.entrySet()) {
            addGridBagComponents(complexPane, c, rowCount, entry.getKey(), entry.getValue(), null, null, null, null);
            rowCount++;
        }

        if (paramGroup != null) {
            addParamGroupComponents(complexPane, c, rowCount, paramGroup);
        }

        return complexPane;
    }

    public static JPanel createParamGroupPane(ParamGroup paramGroup) {
        JPanel paramGroupPane = new JPanel();
        paramGroupPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        addParamGroupComponents(paramGroupPane, c, 0, paramGroup);
        return paramGroupPane;
    }

    public static void addParamGroupComponents(JPanel panel, GridBagConstraints c, int startingRow, ParamGroup paramGroup) {
        java.util.List<CvParam> cvParams = paramGroup.getCvParams();
        int row = startingRow;
        if (cvParams != null && !cvParams.isEmpty()) {
            for(CvParam cvParam : cvParams) {
                String cv = cvParam.getCvLookupID();
                String acc = cvParam.getAccession();
                String name = cvParam.getName();
                String value = cvParam.getValue();
                String unitName = cvParam.getUnitName();
                addGridBagComponents(panel, c, row, cv, acc, name, value, unitName);
                row ++;
            }
        }
        Collection<UserParam> userParams = paramGroup.getUserParams();
        if (userParams != null && !userParams.isEmpty()) {
            for (UserParam userParam : userParams) {
                String name = userParam.getName();
                String value = userParam.getValue();
                String unitName = userParam.getUnitName();
                addGridBagComponents(panel, c, row, null, null, name, value, unitName);
                row ++;
            }
        }
    }

    public static JPanel createHeaderPane(String header) {
        return createHeaderPane(header, null);
    }

    public static JPanel createHeaderPane(String header, Component ... components) {
        JPanel displayPane = new JPanel();
        displayPane.setOpaque(false);
        displayPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        TitledBorder paneBorder = new TitledBorder(BorderFactory.createMatteBorder(2,0,0,0, Color.black), header);
        displayPane.setBorder(paneBorder);
        if (components != null) {
            for(Component component : components) {
                displayPane.add(component);
            }
        }
        return displayPane;
    }

    private static void addGridBagComponents(JPanel panel, GridBagConstraints c, int row, String ... values) {
        c.gridy = row;
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.LINE_START;
        double w = 1/(double)values.length;
        for(int i = 0; i < values.length; i++) {
            String val = values[i];
            if (val != null) {
                c.gridx = i;
                c.weightx = w;
                JLabel label = new JLabel(values[i]);
                panel.add(label, c);
            }
        }
    }

    private static void addGridBagComponent(String label, String value, JPanel panel, GridBagConstraints c, int row) {
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        JLabel labelComp = new JLabel(label);
        panel.add(labelComp, c);
        c.gridx = 1;
        c.weightx = 0.8;
        c.anchor = GridBagConstraints.LINE_START;
        JTextField textField = new JTextField(value);
        panel.add(textField, c);
    }


    private static java.util.List<JPanel> createParamGroupPanelList(java.util.List<ParamGroup> paramGroupList) {
        java.util.List<JPanel> panelList = new ArrayList<JPanel>();
        for(ParamGroup paramGroup : paramGroupList) {
            JPanel innerPane = createParamGroupPane(paramGroup);
            panelList.add(innerPane);
        }
        return panelList;
    }

    private static JPanel createMetaDataSubPane(String header, JPanel panel, Border border) {
        java.util.List<JPanel> panelList = new ArrayList<JPanel>();
        panelList.add(panel);
        return createMetaDataSubPane(header, panelList, border);
    }

    private static JPanel createMetaDataSubPane(String header, java.util.List<JPanel> panels, Border border) {
        JPanel subPane = new JPanel();
        subPane.setBorder(border);
        subPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 5, 0);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 1; c.weighty = 1;
        // add header
        c.gridx = 0; c.gridy = 0;
        subPane.add(new JLabel(header), c);

        // add panels
        for(int i = 0 ; i <panels.size(); i++) {
            c.gridy = i + 1;
            JPanel panel = panels.get(i);
            if (i != panels.size() - 1) {
                panel.setBorder(border);
            }
            subPane.add(panel, c);
        }

        return subPane;
    }
}
