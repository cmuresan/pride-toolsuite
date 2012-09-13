/*
 * Created by JFormDesigner on Wed Aug 15 13:55:01 BST 2012
 */

package uk.ac.ebi.pride.gui.component.mzidentml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.core.SpectraData;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.gui.component.peptide.PeptideTabPane;
import uk.ac.ebi.pride.gui.component.report.ReportList;
import uk.ac.ebi.pride.gui.component.report.RoundCornerLabel;
import uk.ac.ebi.pride.gui.component.report.SummaryReportMessage;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author User #2
 */
public class SimpleMsDialog extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMsDialog.class);

    private static final String OPEN_FILE = "Open MS Files for ";

    private PrideInspectorContext context = null;

    DataAccessController controller = null;

    Map<SpectraData, File> msFileMap = null;

    private ReportList container;

    private static final int DEFAULT_HEIGHT = 30;
    private static final int START_ALPHA = 100;
    private static final int STOP_ALPHA = 150;
    private static final String ERROR_MESSAGE = "No Supported Spectra Data Files for this mzIdentml";
    private static final String WARNING = "Missing Spectra Information";
    private static final String TOTAL_SPECTRUMS = "All Spectrums Found";


    public SimpleMsDialog(Frame owner, DataAccessController controller) {
        super(owner);
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        this.controller = controller;
        initComponents();
        customInitComponents();
    }

    public SimpleMsDialog(Dialog owner) {
        super(owner);
        initComponents();
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
    }


    /**
     * This method initialize the custom components in the MsDialog
     *
     */
    private void customInitComponents() {

        table1.setModel(new DefaultTableModel(
                new Object[][] {
                        },
                new String[] {
                        "Spectra File Source", "No. Spectras", "MS File", "Remove"
                }
        ) {
            Class<?>[] columnTypes = new Class<?>[] {
                    String.class, Double.class, String.class, ImageIcon.class
            };
            boolean[] columnEditable = new boolean[] {
                    false,false,false,false
            };
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        });

        try {
            msFileMap = ((MzIdentMLControllerImpl) controller).getSpectraDataMSFiles();

        } catch (DataAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        updateMSFileList(msFileMap);
    }

    private void addNewMsFile(ActionEvent e) {

        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select mzML/mzXML/mzid/PRIDE xml Files", null, true, Constants.MGF_FILE);

        int result = ofd.showDialog(this, null);

        java.util.List<File> filesToOpen = new ArrayList<File>();

        // check the selection results from open fiel dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            filesToOpen.addAll(Arrays.asList(ofd.getSelectedFiles()));
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            // remember the path has visited
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
        }
        addMStoSpectraDataSet(filesToOpen);

    }

    private void addMStoSpectraDataSet(List<File> files) {
        try {
            msFileMap = ((MzIdentMLControllerImpl) controller).checkMScontrollers(files);
        } catch (DataAccessException e1) {
            logger.error("Failed to check the files as controllers", e1);
        }
        updateMSFileList(msFileMap);
    }

    private void updateMSFileList(Map<SpectraData, File> spectraDataFileMap) {
        if(spectraDataFileMap != null){
            clearTalbeMsFiles();
            int totalSpectras   = 0;
            int noMissSpectrums = 0;
            for(SpectraData spectraData: spectraDataFileMap.keySet()){
                String msFileName = (spectraDataFileMap.get(spectraData) == null)? "" : spectraDataFileMap.get(spectraData).getAbsolutePath();
                int countFile = fillTableRow(spectraData,msFileName);
                totalSpectras =+ countFile;
                noMissSpectrums =+ ((spectraDataFileMap.get(spectraData) == null)?0:countFile);
            }
            String message = getMessage(spectraDataFileMap, totalSpectras - noMissSpectrums);
            SummaryReportMessage.Type type = getMessageType(spectraDataFileMap,totalSpectras-noMissSpectrums);
            updateMessage(type,message);
            updateButtonStatus(type);
        }
    }

    private void clearTalbeMsFiles() {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        for( int i = model.getRowCount() - 1; i >= 0; i-- ) {
            model.removeRow(i);
        }
    }

    private int fillTableRow(SpectraData spectraData, String msFileName){
        Object[] data = new Object[4];
        data[0] = spectraData.getId() + " " + ((spectraData.getName() == null)? "" : ": " + spectraData.getName());
        data[1] = ((MzIdentMLControllerImpl)controller).getNumberOfSpectrabySpectraData(spectraData);
        data[2] = msFileName;
        data[3] = GUIUtilities.loadImageIcon(context.getProperty("delete.mzidentml.ms.icon.small"));
        ((DefaultTableModel) table1.getModel()).addRow(data);
        return (Integer)data[1];
    }

    private void updateMessage(SummaryReportMessage.Type type, String message){
        if(panelMessage.getComponentCount() > 0) panelMessage.removeAll();
        RoundCornerLabel label = new RoundCornerLabel(getIcon(type), message, getBackgroundPaint(type), getBorderPaint(type));
        label.setPreferredSize(new Dimension(50, DEFAULT_HEIGHT));
        panelMessage.add(label);
        panelMessage.revalidate();
    }

    private void updateButtonStatus(SummaryReportMessage.Type type){
        if(type == SummaryReportMessage.Type.ERROR){
            addButton.setEnabled(false);
            setButton.setEnabled(false);
        }else if(type == SummaryReportMessage.Type.SUCCESS){
            addButton.setEnabled(false);
        }
    }

    private void cancelbuttonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void setMSFilesActionPerformed(ActionEvent e) {
        try {
            ((MzIdentMLControllerImpl)controller).addMSController(msFileMap);
            ControllerContentPane contentPane = (ControllerContentPane) context.getDataContentPane(controller);

            //Update the Spectrum Tab
            MzDataTabPane mzDataPane = contentPane.getMzDataTab();
            int index = contentPane.getMzDataTabIndex();
            mzDataPane = new MzDataTabPane(controller, contentPane);
            contentPane.removeTab(index);
            contentPane.setMzDataTab(mzDataPane);
            contentPane.insertTab(mzDataPane.getTitle(), mzDataPane.getIcon(), mzDataPane, mzDataPane.getTitle(), index);
            mzDataPane.populate();

            //Update the Peptide and Protein Tabs
            PeptideTabPane peptideContentPane = contentPane.getPeptideTabPane();
            peptideContentPane.getVizTabPane().addSpectrumViewPane();





            contentPane.populate();
        } catch (DataAccessException e1) {
            logger.error("Failed to check the files as controllers", e1);
        }
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        addButton = new JButton();
        separator1 = new JSeparator();
        setButton = new JButton();
        cancelButton = new JButton();
        panelMessage = new JPanel();

        //======== this ========
        setTitle("Open Ms files for");
        Container contentPane = getContentPane();

        //======== scrollPane1 ========
        {

            //---- table1 ----
            table1.setModel(new DefaultTableModel(
                new Object[][] {
                    {null, null, null, null},
                },
                new String[] {
                    "Spectra File Source", "No. Spectras", "MS File", "Remove"
                }
            ));
            {
                TableColumnModel cm = table1.getColumnModel();
                cm.getColumn(0).setPreferredWidth(130);
                cm.getColumn(1).setPreferredWidth(85);
                cm.getColumn(2).setPreferredWidth(55);
                cm.getColumn(3).setPreferredWidth(55);
            }
            scrollPane1.setViewportView(table1);
        }

        //---- addButton ----
        addButton.setText("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewMsFile(e);
            }
        });

        //---- setButton ----
        setButton.setText("Set");
        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setMSFilesActionPerformed(e);
            }
        });

        //---- cancelButton ----
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelbuttonActionPerformed(e);
            }
        });

        //======== panelMessage ========
        {
            panelMessage.setLayout(new BorderLayout());
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(addButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(panelMessage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(0, 354, Short.MAX_VALUE)
                            .addComponent(cancelButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(setButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
                        .addComponent(separator1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(addButton)
                        .addComponent(panelMessage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(setButton)
                        .addComponent(cancelButton))
                    .addGap(13, 13, 13))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    /**
     * Get the icon of the message according to the type
     *
     * @param type  message type
     * @return  Icon    message icon
     *
     **/

     private Icon getIcon(SummaryReportMessage.Type type) {
         switch(type) {
             case SUCCESS:
                 return GUIUtilities.loadIcon(context.getProperty("report.item.success.icon.small"));
             case ERROR:
                 return GUIUtilities.loadIcon(context.getProperty("report.item.error.icon.small"));
             case WARNING:
                 return GUIUtilities.loadIcon(context.getProperty("report.item.warning.icon.small"));
             case INFO:
                 return GUIUtilities.loadIcon(context.getProperty("report.item.plain.icon.small"));
             default:
                 return GUIUtilities.loadIcon(context.getProperty("report.item.plain.icon.small"));
         }
     }

     /**
      * Get the paint for the message background
      * @param type  message type
      * @return  Paint   background
      **/

     private Paint getBackgroundPaint(SummaryReportMessage.Type type) {
         switch(type) {
             case SUCCESS:
                 return new GradientPaint(0, 0, new Color(40, 175, 99, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(40, 175, 99, STOP_ALPHA), true);
             case ERROR:
                 return new GradientPaint(0, 0, new Color(215, 39, 41, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(215, 39, 41, STOP_ALPHA), true);
             case WARNING:
                 return new GradientPaint(0, 0, new Color(251, 182, 1, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(251, 182, 1, STOP_ALPHA), true);
             case INFO:
                 return new GradientPaint(0, 0, new Color(27, 106, 165, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(27, 106, 165, STOP_ALPHA), true);
             default:
                 return new GradientPaint(0, 0, new Color(27, 106, 165, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(27, 106, 165, STOP_ALPHA), true);
         }
     }

     /**
      * Get the paint for the message border
      *
      * @param type  message type
      * @return  Paint   border color
     */

     private Paint getBorderPaint(SummaryReportMessage.Type type) {
         switch(type) {
             case SUCCESS:
                 return new Color(40, 175, 99);
             case ERROR:
                 return new Color(215, 39, 41);
             case WARNING:
                 return new Color(251, 182, 1);
             case INFO:
                 return new Color(27, 106, 165);
             default:
                 return new Color(27, 106, 165);
         }
     }

     private SummaryReportMessage.Type getMessageType(Map<SpectraData, File> spectraDataMap, int total){
         if(spectraDataMap == null || spectraDataMap.size() < 1){
             return SummaryReportMessage.Type.ERROR;
         }else if(total > 0){
             return SummaryReportMessage.Type.WARNING;
         }else if(total == 0){
             return SummaryReportMessage.Type.SUCCESS;
         }
         return SummaryReportMessage.Type.INFO;
     }

    private String getMessage(Map<SpectraData, File> spectraDataMap, Integer spectrumCount){
        if(spectraDataMap == null || spectraDataMap.size() < 1){
            return SimpleMsDialog.ERROR_MESSAGE;
        }else if(spectrumCount == 0){
            return SimpleMsDialog.TOTAL_SPECTRUMS;
        }else if(spectrumCount > 0){
            return SimpleMsDialog.WARNING + " [" + spectrumCount +"]";
        }
        return SimpleMsDialog.ERROR_MESSAGE;

    }



    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JTable table1;
    private JButton addButton;
    private JSeparator separator1;
    private JButton setButton;
    private JButton cancelButton;
    private JPanel panelMessage;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
