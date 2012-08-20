/*
 * Created by JFormDesigner on Wed Aug 15 13:55:01 BST 2012
 */

package uk.ac.ebi.pride.gui.component.dialog;

import org.jdesktop.swingx.border.*;
import uk.ac.ebi.pride.data.core.SpectraData;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;

import uk.ac.ebi.pride.gui.component.report.ReportList;
import uk.ac.ebi.pride.gui.component.report.ReportListRenderer;
import uk.ac.ebi.pride.gui.component.report.RoundCornerLabel;
import uk.ac.ebi.pride.gui.component.report.SummaryReportMessage;
import uk.ac.ebi.pride.gui.utils.Constants;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author User #2
 */
public class SimpleMsDialog extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMsDialog.class);

    private static final String OPEN_FILE = "Open MS Files for ";

    private PrideInspectorContext context = null;

    DataAccessController controller = null;

    List<File> msFiles = null;

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

        Integer totalSpectras = 0;
        Integer noMiss        = 0;
        try {
            totalSpectras = ((MzIdentMLControllerImpl) controller).getNumberOfSpectra();
        } catch (DataAccessException e) {
            logger.error("Failed to check the file type", e);
        }

        if(controller != null){
            try{
                Map<SpectraData, DataAccessController> spectraDataFileMap = ((MzIdentMLControllerImpl) controller).getSpectraDataMSFiles();
                if(spectraDataFileMap != null && spectraDataFileMap.size()>0){
                    for(SpectraData spectraData: spectraDataFileMap.keySet()){
                        Object[] data = new Object[4];
                        data[0] = spectraData.getId() + " " + ((spectraData.getName() == null)? spectraData.getLocation(): spectraData.getName());
                        data[1] = ((MzIdentMLControllerImpl)controller).getNumberOfSpectrabySpectraData(spectraData);
                        noMiss =+ (Integer)data[1];
                        data[2] = (spectraDataFileMap.get(spectraData) == null)? "" : spectraDataFileMap.get(spectraData).getSource();
                        data[3] = GUIUtilities.loadImageIcon(context.getProperty("delete.mzidentml.ms.icon.small"));
                        ((DefaultTableModel) table1.getModel()).addRow(data);
                    }
                }

                String message = getMessage(spectraDataFileMap, totalSpectras + noMiss);
                SummaryReportMessage.Type type = getMessageType(spectraDataFileMap);


                RoundCornerLabel label= new RoundCornerLabel(getIcon(type), message, getBackgroundPaint(type), getBorderPaint(type));
                label.setPreferredSize(new Dimension(50, DEFAULT_HEIGHT));
                panelMessage.add(label);
            } catch (DataAccessException e1) {
                logger.error("Failed to check the file type", e1);
            }

        }
    }

    public SimpleMsDialog(Dialog owner) {
        super(owner);
        initComponents();
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    private void addNewMsFile(ActionEvent e) {

        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select mzML/mzXML/mzid/PRIDE xml Files", null, true, Constants.MZIDENT_FILE,
                        Constants.MZML_FILE,
                        Constants.XML_FILE,
                        Constants.MZXML_FILE,
                        Constants.MGF_EXT,
                        Constants.MS2_EXT,
                        Constants.PKL_EXT,
                        Constants.DTA_EXT,
                        Constants.GZIPPED_FILE );

        int result = ofd.showDialog(uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getMainComponent(), null);

        java.util.List<File> filesToOpen = new ArrayList<File>();

        // check the selection results from open fiel dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            filesToOpen.addAll(Arrays.asList(ofd.getSelectedFiles()));
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            // remember the path has visited
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
        }

        msFiles = filesToOpen;

        assignMStoSpectraDataSet();

    }

    private void assignMStoSpectraDataSet() {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void cancelbuttonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        scrollPane1 = new JScrollPane();
        table1 = new JTable();
        addButton = new JButton();
        separator1 = new JSeparator();
        button1 = new JButton();
        button2 = new JButton();
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

        //---- button1 ----
        button1.setText("Set");

        //---- button2 ----
        button2.setText("Cancel");
        button2.addActionListener(new ActionListener() {
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
                            .addComponent(button2)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button1, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
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
                        .addComponent(button1)
                        .addComponent(button2))
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

     private SummaryReportMessage.Type getMessageType(Map<SpectraData, DataAccessController> spectraDataMap){
         if(spectraDataMap == null || spectraDataMap.size() < 1){
             return SummaryReportMessage.Type.ERROR;
         }
         return SummaryReportMessage.Type.WARNING;
     }

    private String getMessage(Map<SpectraData, DataAccessController> spectraDataMap, Integer spectrumCount){
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
    private JButton button1;
    private JButton button2;
    private JPanel panelMessage;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
