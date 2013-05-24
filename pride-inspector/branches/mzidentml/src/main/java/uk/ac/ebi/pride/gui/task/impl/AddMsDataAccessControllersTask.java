package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.core.SpectraData;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.gui.component.peptide.PeptideTabPane;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yperez
 * Date: 3/4/13
 * Time: 2:48 PM
 */
public class AddMsDataAccessControllersTask extends TaskAdapter<Void, Map<SpectraData, File>> {

    DataAccessController controller;
    Map<SpectraData, File> spectraDataFileMap;
    private PrideInspectorContext context = null;

    public AddMsDataAccessControllersTask(DataAccessController controller, Map<SpectraData, File> spectraDataMap) {
        this.controller = controller;
        spectraDataFileMap = spectraDataMap;
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            ((MzIdentMLControllerImpl) controller).addMSController(spectraDataFileMap);
            //((MzIdentMLControllerImpl) controller).addMSController(msFileMap);
            ControllerContentPane contentPane = (ControllerContentPane) context.getDataContentPane(controller);

            //Update the Spectrum Tab
            MzDataTabPane mzDataTabPane;
            int index = contentPane.getMzDataTabIndex();
            mzDataTabPane = new MzDataTabPane(controller, contentPane);
            contentPane.removeTab(index);
            contentPane.setMzDataTab(mzDataTabPane);
            contentPane.insertTab(mzDataTabPane.getTitle(), mzDataTabPane.getIcon(), mzDataTabPane, mzDataTabPane.getTitle(), index);
            mzDataTabPane.spectrumChange();
            mzDataTabPane.populate();

            //Update the Peptide and Protein Tabs
            PeptideTabPane peptideContentPane = contentPane.getPeptideTabPane();

            //Add Spectrum View
            peptideContentPane.getVizTabPane().addSpectrumViewPane();
            //Add Spectrum View
            peptideContentPane.getVizTabPane().addFragmentationViewPane();

            peptideContentPane.peptideChange();
            contentPane.populate();

            RetrievePeptideSpectrumDetailTask task = new RetrievePeptideSpectrumDetailTask(controller);
            JTable table = contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable();
            TableModel tableModel = table.getModel();
            task.addTaskListener((TaskListener) tableModel);

            // gui blocker
            task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));

            // add task to task manager without notify
            Desktop.getInstance().getDesktopContext().addTask(task);

        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
