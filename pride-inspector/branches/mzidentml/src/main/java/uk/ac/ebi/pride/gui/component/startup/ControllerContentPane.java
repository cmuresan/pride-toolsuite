package uk.ac.ebi.pride.gui.component.startup;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.PrideInspectorLoadingPanel;
import uk.ac.ebi.pride.gui.component.chart.ChartTabPane;
import uk.ac.ebi.pride.gui.component.metadata.MetaDataTabPane;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.gui.component.peptide.PeptideTabPane;
import uk.ac.ebi.pride.gui.component.protein.ProteinTabPane;
import uk.ac.ebi.pride.gui.component.quant.QuantTabPane;
import uk.ac.ebi.pride.gui.component.report.SummaryReportMessage;
import uk.ac.ebi.pride.gui.component.table.model.ProgressiveListTableModel;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.event.SummaryReportEvent;
import uk.ac.ebi.pride.gui.task.impl.ScanExperimentTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * DataContentDisplayPane is the main pane to insert all the view tabs.
 * <p/>
 * User: rwang
 * Date: 29-Jul-2010
 * Time: 14:07:59
 */
public class ControllerContentPane extends DataAccessControllerPane {
    private static final Logger logger = LoggerFactory.getLogger(ControllerContentPane.class);

    private JTabbedPane contentTabPane;
    private MetaDataTabPane metaDataTabPane;
    private MzDataTabPane mzDataTab;
    private ProteinTabPane proteinTabPane;
    private PeptideTabPane peptideTabPane;
    private QuantTabPane quantTabPane;
    private ChartTabPane chartTabPane;

    private int metaDataTabIndex;
    private int mzDataTabIndex;
    private int proteinTabIndex;
    private int peptideTabIndex;
    private int quantTabIndex;
    private int chartTabIndex;

    private boolean metaDataTabEnabled;
    private boolean mzDataTabEnabled;
    private boolean proteinTabEnabled;
    private boolean peptideTabEnabled;
    private boolean quantTabEnabled;
    private boolean chartTabEnabled;

    /**
     * This indicates the index for the latest tab
     */
    private int indexCount = 0;

    public ControllerContentPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        this.setLayout(new BorderLayout());
    }

    @Override
    protected void addComponents() {
        Collection<DataAccessController.ContentCategory> categories = controller.getContentCategories();

        if (!categories.isEmpty()) {
            // create all the tabs
            createTabbedPane();
            // set tab visibility
            setTabVisibility();
            this.add(contentTabPane, BorderLayout.CENTER);

            // retrieve identification data
            retrieveIdentificationData();
        } else {
            JPanel panel = new PrideInspectorLoadingPanel();
            this.setBorder(BorderFactory.createLineBorder(Color.lightGray));
            this.add(panel, BorderLayout.CENTER);
        }
    }

    /**
     * Set the visibility of each tab
     */
    private void setTabVisibility() {
        try {
            Collection<DataAccessController.ContentCategory> categories = controller.getContentCategories();

            metaDataTabEnabled = true;


            if (!categories.isEmpty()) {

                if (categories.contains(DataAccessController.ContentCategory.SPECTRUM)
                        || categories.contains(DataAccessController.ContentCategory.CHROMATOGRAM)) {

                    boolean hasSpectrum = controller.hasSpectrum();
                    boolean hasChromatogram = controller.hasChromatogram();

                    contentTabPane.setEnabledAt(mzDataTabIndex, hasSpectrum || hasChromatogram);

                    mzDataTabEnabled = hasSpectrum || hasChromatogram;

                    // check spectrum
                    if (categories.contains(DataAccessController.ContentCategory.SPECTRUM)) {
                        if (hasSpectrum) {
                            EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.SUCCESS, "Spectra found", "This data source contains spectra")));
                        } else {
                            EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.ERROR, "Spectra not found", "This data source does not contain spectra")));
                        }
                    }

                    // check chromatogram
                    if (categories.contains(DataAccessController.ContentCategory.CHROMATOGRAM)) {
                        if (hasChromatogram) {
                            EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.SUCCESS, "Chromatograms found", "This data source contains Chromatograms")));
                        } else {
                            EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.ERROR, "Chromatograms not found", "This data source does not contain Chromatograms")));
                        }
                    }
                }

                if (categories.contains(DataAccessController.ContentCategory.PROTEIN)) {
                    proteinTabEnabled = controller.hasProtein();
                    contentTabPane.setEnabledAt(proteinTabIndex, proteinTabEnabled);
                    if (proteinTabEnabled) {
                        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.SUCCESS, "Proteins found", "This data source contains protein identifications")));
                    } else {
                        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.ERROR, "Proteins not found", "This data source does not contain protein identifications")));
                    }
                }

                if (categories.contains(DataAccessController.ContentCategory.PEPTIDE)) {
                    peptideTabEnabled = controller.hasPeptide();
                    contentTabPane.setEnabledAt(peptideTabIndex, peptideTabEnabled);
                    if (peptideTabEnabled) {
                        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.SUCCESS, "Peptides found", "This data source contains peptides")));
                    } else {
                        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.ERROR, "Peptides not found", "This data source does not contain peptides")));
                    }
                }

                if (categories.contains(DataAccessController.ContentCategory.QUANTITATION)) {
                    quantTabEnabled = controller.hasQuantData();
                    contentTabPane.setEnabledAt(quantTabIndex, quantTabEnabled);
                    if (quantTabEnabled) {
                        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.SUCCESS, "Quantifications found", "This data source contains quantitative data")));
                    }
                }

                if (categories.contains(DataAccessController.ContentCategory.SPECTRUM)
                        || categories.contains(DataAccessController.ContentCategory.PROTEIN)) {
                    chartTabEnabled = controller.hasSpectrum() || controller.hasProtein();
                    contentTabPane.setEnabledAt(chartTabIndex, chartTabEnabled);
                }

                contentTabPane.setSelectedIndex(metaDataTabIndex);
            }
        } catch (DataAccessException ex) {
            logger.error("Failed to set visibility of data content tabs", ex);
        }
    }

    /**
     * Insert each tab
     * This function create and insert new Tabs
     */
    private void createTabbedPane() {
        contentTabPane = new JTabbedPane();
        Collection<DataAccessController.ContentCategory> categories = controller.getContentCategories();

        // the type of data contains experiment data
        if (!categories.isEmpty()) {


            if (controller.hasMetaDataInformation()) {
                metaDataTabPane = new MetaDataTabPane(controller, this);
                metaDataTabIndex = indexCount++;
                contentTabPane.insertTab(metaDataTabPane.getTitle(), metaDataTabPane.getIcon(), metaDataTabPane, metaDataTabPane.getTitle(), metaDataTabIndex);
                metaDataTabPane.populate();
            }


            // identification data tab
            if (categories.contains(DataAccessController.ContentCategory.PROTEIN)) {
                proteinTabPane = new ProteinTabPane(controller, this);
                proteinTabIndex = indexCount++;
                contentTabPane.insertTab(proteinTabPane.getTitle(), proteinTabPane.getIcon(), proteinTabPane, proteinTabPane.getTitle(), proteinTabIndex);
            }

            // peptide data tab
            if (categories.contains(DataAccessController.ContentCategory.PEPTIDE)) {
                peptideTabPane = new PeptideTabPane(controller, this);
                peptideTabIndex = indexCount++;
                contentTabPane.insertTab(peptideTabPane.getTitle(), peptideTabPane.getIcon(), peptideTabPane, peptideTabPane.getTitle(), peptideTabIndex);
                peptideTabPane.populate();
            }

            // spectra data tab
            if (categories.contains(DataAccessController.ContentCategory.SPECTRUM)) {
                mzDataTab = new MzDataTabPane(controller, this);
                mzDataTabIndex = indexCount++;
                contentTabPane.insertTab(mzDataTab.getTitle(), mzDataTab.getIcon(), mzDataTab, mzDataTab.getTitle(), mzDataTabIndex);
                mzDataTab.populate();
            }

            // quant data tab
            if (categories.contains(DataAccessController.ContentCategory.QUANTITATION)) {
                quantTabPane = new QuantTabPane(controller, this);
                quantTabIndex = indexCount++;
                contentTabPane.insertTab(quantTabPane.getTitle(), quantTabPane.getIcon(), quantTabPane, quantTabPane.getTitle(), quantTabIndex);
                quantTabPane.populate();
            }

            // chart tab
            /*if (categories.contains(DataAccessController.ContentCategory.SPECTRUM) || categories.contains(DataAccessController.ContentCategory.PROTEIN)) {
                chartTabPane = new ChartTabPane(controller, this);
                chartTabIndex = indexCount++;
                contentTabPane.insertTab(chartTabPane.getTitle(), chartTabPane.getIcon(), chartTabPane, chartTabPane.getTitle(), chartTabIndex);
                chartTabPane.populate();
            }*/
        }
    }

    /**
     * Use a background task to populate identification tab's
     * and peptide tab's table with content
     */
    @SuppressWarnings("unchecked")
    private void retrieveIdentificationData() {

        Collection<DataAccessController.ContentCategory> categories = controller.getContentCategories();
        if (categories.contains(DataAccessController.ContentCategory.PROTEIN) &&
                categories.contains(DataAccessController.ContentCategory.PEPTIDE)) {
            DesktopContext context = (uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext());
            ScanExperimentTask retrieveTask = new ScanExperimentTask(controller);

            // register protein tab as a task listener
            retrieveTask.addTaskListener(proteinTabPane);

            // register protein table model as a task listener
            JTable identTable = proteinTabPane.getIdentificationPane().getIdentificationTable();
            retrieveTask.addTaskListener((ProgressiveListTableModel) identTable.getModel());

            // register peptide tab as a task listener
            retrieveTask.addTaskListener(peptideTabPane);

            // register peptide table model as a task listener
            JTable peptideTable = peptideTabPane.getPeptidePane().getPeptideTable();
            retrieveTask.addTaskListener((ProgressiveListTableModel) peptideTable.getModel());

            // register quantitative tab as a task listener
            try {
                if (controller.hasQuantData()) {
                    retrieveTask.addTaskListener(quantTabPane);

                    // register quantitative protein table model as a task listener
                    JTable quantProteinTable = quantTabPane.getQuantProteinSelectionPane().getQuantProteinTable();
                    retrieveTask.addTaskListener((ProgressiveListTableModel) quantProteinTable.getModel());
                }
            } catch (DataAccessException e) {
                logger.error("Failed to check the availability of quantitative data", e);
            }

            // start the task
            retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));
            context.addTask(retrieveTask);
        }
    }

    /**
     * Set the title of the tab
     *
     * @param index index of the tab
     * @param title title of the tab
     */
    public void setTabTitle(int index, String title) {
        if (index >= 0 && index < contentTabPane.getTabCount()) {
            contentTabPane.setTitleAt(index, title);
        }
    }

    /**
     * Set data content pane's tab icon
     *
     * @param index index of the tab
     * @param icon  icon of the tab
     */
    public synchronized void setTabIcon(int index, Icon icon) {
        if (index >= 0 && index < contentTabPane.getTabCount()) {
            contentTabPane.setIconAt(index, icon);
        }
    }

    /**
     * Return meta data tab pane
     *
     * @return MetaDataTabPane meta data tab pane
     */
    public MetaDataTabPane getMetaDataTabPane() {
        return metaDataTabPane;
    }

    /**
     * Return mzdata tab pane
     *
     * @return MzDataTabPane    mzdata tab pane
     */

    public void setMzDataTab(MzDataTabPane mzDataTabPane) {
        mzDataTab = mzDataTabPane;
    }


    /**
     * Return identification tab pane
     *
     * @return IdentTabPane ident tab pane
     */
    public ProteinTabPane getProteinTabPane() {
        return proteinTabPane;
    }

    /**
     * Return peptide tab pane
     *
     * @return PeptideTabPane   peptide tab pane
     */
    public PeptideTabPane getPeptideTabPane() {
        return peptideTabPane;
    }

    /**
     * Retrun quantitative tab pane
     *
     * @return QuantTabPane    quantitative tab pane
     */
    public QuantTabPane getQuantTabPane() {
        return quantTabPane;
    }

    /**
     * Tab index of the metadata tab
     *
     * @return int tab index
     */
    public int getMetaDataTabIndex() {
        return metaDataTabIndex;
    }

    /**
     * Tab index of the spectrum tab
     *
     * @return int tab index
     */
    public int getMzDataTabIndex() {
        return mzDataTabIndex;
    }

    /**
     * Tab index of the spectrum tab
     *
     * @return int tab index
     */
    public void setMzDataTabIndex(int index) {
        mzDataTabIndex = index;
    }


    /**
     * Tab index of the protein tab
     *
     * @return int tab index
     */
    public int getProteinTabIndex() {
        return proteinTabIndex;
    }

    /**
     * Tab index of the peptide tab
     *
     * @return int tab index
     */
    public int getPeptideTabIndex() {
        return peptideTabIndex;
    }

    /**
     * Tab index of the quantitative tab
     *
     * @return
     */
    public int getQuantTabIndex() {
        return quantTabIndex;
    }

    /**
     * Tab index of the chart tab
     *
     * @return int tab index
     */
    public int getChartTabIndex() {
        return chartTabIndex;
    }

    /**
     * Whether meta data tab is enabled
     *
     * @return boolean true means enabled
     */
    public boolean isMetaDataTabEnabled() {
        return metaDataTabEnabled;
    }

    /**
     * Whether mz data tab is enabled
     *
     * @return boolean true means enabled
     */
    public boolean isMzDataTabEnabled() {
        return mzDataTabEnabled;
    }

    /**
     * Whether protein tab is enabled
     *
     * @return boolean true means enabled
     */
    public boolean isProteinTabEnabled() {
        return proteinTabEnabled;
    }

    /**
     * Whether peptide tab is enabled
     *
     * @return boolean true means enabled
     */
    public boolean isPeptideTabEnabled() {
        return peptideTabEnabled;
    }

    /**
     * Whether quantitative data tab is enabled
     *
     * @return boolean true means enabled
     */
    public boolean isQuantTabEnabled() {
        return quantTabEnabled;
    }

    /**
     * Whether chart tab is enabled
     *
     * @return boolean true means enabled
     */
    public boolean isChartTabEnabled() {
        return chartTabEnabled;
    }

    public int indexOf(JComponent component) {
        return contentTabPane.indexOfComponent(component);
    }

    public void insertTab(String title, Icon icon, MzDataTabPane mzDataPane, String title1, int index) {
        contentTabPane.insertTab(title, icon, mzDataPane, title1, index);
    }

    public void removeTab(int index) {
        contentTabPane.removeTabAt(index);
    }

}
