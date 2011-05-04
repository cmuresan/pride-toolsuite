package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessControllerType;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.OpenFileDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: dani
 * Date: 23-Aug-2010
 * Time: 11:38:26
 * To change this template use File | Settings | File Templates.
 */
public class BatchDownloadAction extends PrideAction implements PropertyChangeListener {

    public BatchDownloadAction(String name, Icon icon, String menuLocation) {
        super(name, icon, menuLocation);
        this.setEnabled(false);
        //setAccelerator(java.awt.event.KeyEvent.VK_0, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenFileDialog ofd = new OpenFileDialog(System.getProperty("user.dir"), "Select Path Save To");
        ofd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showSaveDialog(Desktop.getInstance().getMainComponent());
        if (result == JFileChooser.APPROVE_OPTION) {
            File path = ofd.getSelectedFile();
            try {
                DesktopContext context = Desktop.getInstance().getDesktopContext();
                DataAccessController controller = ((PrideViewerContext) context).getForegroundDataAccessController();
                for (Comparable spectrumId : controller.getSpectrumIds()) {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(path + "/" + spectrumId.toString() + ".csv"));
                    Spectrum spectrum = controller.getSpectrumById(spectrumId);
                    //get both arrays
                    double[] mzBinaryArray = spectrum.getMzBinaryDataArray().getDoubleArray();
                    double[] intensityArray = spectrum.getIntensityBinaryDataArray().getDoubleArray();
                    
                    for (int i = 0; i < mzBinaryArray.length; i++) {
                        bw.write(mzBinaryArray[i] + "\t" + intensityArray[i] + "\n");
                    }
                    bw.close();
                }
            }
            catch (DataAccessException e2) {
                e2.printStackTrace();
            }
            catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        DataAccessController controller = ((PrideViewerContext) context).getForegroundDataAccessController();
        if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
            if (DataAccessControllerType.DATABASE.equals(controller.getType())) {
                controller.addPropertyChangeListener(this);
            } else {
                enableBatchAction(controller);
            }
        } else if (DataAccessController.FOREGROUND_EXPERIMENT_CHANGED.equals(evtName)) {
            enableBatchAction(controller);
        }
    }

    private void enableBatchAction(DataAccessController controller) {
        if (controller.hasSpectrum()) {
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }

    }
}


