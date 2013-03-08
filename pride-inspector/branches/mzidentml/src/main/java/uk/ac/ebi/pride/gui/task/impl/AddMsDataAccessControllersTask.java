package uk.ac.ebi.pride.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.core.SpectraData;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.gui.component.peptide.PeptideTabPane;
import uk.ac.ebi.pride.gui.component.report.RemovalReportMessage;
import uk.ac.ebi.pride.gui.component.report.SummaryReportMessage;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.event.SummaryReportEvent;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: yperez
 * Date: 3/4/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddMsDataAccessControllersTask extends TaskAdapter<Void, Map<SpectraData, File>>{
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
            ((MzIdentMLControllerImpl)controller).addMSController(spectraDataFileMap);
            //((MzIdentMLControllerImpl) controller).addMSController(msFileMap);
            ControllerContentPane contentPane = (ControllerContentPane) context.getDataContentPane(controller);

            //Update the Spectrum Tab
            MzDataTabPane mzDataPane;
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

        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
