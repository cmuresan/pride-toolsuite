package uk.ac.ebi.pride.gui.component.mzgraph;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.FragmentIon;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.utils.CvTermReference;
import uk.ac.ebi.pride.mzgraph.chart.data.FragmentLoss;
import uk.ac.ebi.pride.mzgraph.chart.data.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.IonAnnotationInfo;
import uk.ac.ebi.pride.mzgraph.chart.data.IonType;
import uk.ac.ebi.pride.mzgraph.chart.graph.ChromatogramPanel;
import uk.ac.ebi.pride.mzgraph.chart.graph.MzGraphConstants;
import uk.ac.ebi.pride.mzgraph.chart.graph.SpectrumPanel;
import uk.ac.ebi.pride.mzgraph.gui.filter.CheckBoxFilterPanel;
import uk.ac.ebi.pride.mzgraph.gui.util.ActionCascadePanel;
import uk.ac.ebi.pride.mzgraph.gui.util.SideToolBarPanel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 14:53:52
 */
public class MzGraphViewPane extends JPanel implements PropertyChangeListener {
    private MzGraphPaneModel dataModel = null;
    private SideToolBarPanel sideToolBarPanel = null;
    private SpectrumPanel spectrumPanel = null;
    private ChromatogramPanel chromaPanel = null;

    public MzGraphViewPane() {
        setMainPane();
        setModel(new MzGraphPaneModel());
        addComponents();
    }

    private void setMainPane() {
        this.setLayout(new BorderLayout());
        // this is required for the splitpane work at the right size
        this.setMinimumSize(new Dimension(200, 400));
    }

    public void setModel(MzGraphPaneModel paneModel) {
        MzGraphPaneModel oldModel = dataModel;
        if (oldModel != null) {
            oldModel.removePropertyChangeListener(this);
        }
        dataModel = paneModel;
        dataModel.addPropertyChangeListener(this);
    }

    private void addComponents() {
        spectrumPanel = new SpectrumPanel();
        chromaPanel = new ChromatogramPanel();
        sideToolBarPanel = new SideToolBarPanel();

        // create graph property pane
        buildStandardToolbar();
        // populate
        populatePaneWithData();

        this.add(sideToolBarPanel, BorderLayout.CENTER);

    }

    private void populatePaneWithData() {
        MzGraph mzGraph = dataModel.getMzGraph();
        if (mzGraph != null) {
            if (mzGraph instanceof Spectrum) {
                buildSpectrumPane((Spectrum) mzGraph);
            } else if (mzGraph instanceof Chromatogram) {
                buildChromatogramPane((Chromatogram) mzGraph);
            }
        }
    }

    private void buildSpectrumPane(Spectrum spectrum) {
        JComponent centralComp = sideToolBarPanel.getCentralComponent();
        spectrumPanel.clearAllDataAndAnnotations();
        // set peaks
        spectrumPanel.setBaseDatasetset(spectrum.getMzBinaryDataArray().getDoubleArray(), spectrum.getIntensityBinaryDataArray().getDoubleArray());
        // set ion annotations
        java.util.List<FragmentIon> ions = spectrum.getFragmentIons();
        if (ions != null) {
            for (FragmentIon ion : ions) {
                // get the ion type
                IonAnnotation ionAnnotation = getIonAnnotation(ion);
                spectrumPanel.addFragmentIon(ionAnnotation);
            }
        }
        if (centralComp == null || centralComp instanceof ChromatogramPanel) {
            // build the tool bar.
            buildSpectrumToolbar();
            // set the new central component.
            sideToolBarPanel.setCentralComponent(spectrumPanel);
        }
    }

    private void buildChromatogramPane(Chromatogram chroma) {
        JComponent centralComp = sideToolBarPanel.getCentralComponent();
        chromaPanel.clearAllDataAndAnnotations();
        chromaPanel.setBaseDatasetset(chroma.getTimeArray().getDoubleArray(), chroma.getIntensityArray().getDoubleArray());
        if (centralComp == null || centralComp instanceof SpectrumPanel) {
            // remove all the spectrum related command
            destroySpectrumToolBar();
            sideToolBarPanel.setCentralComponent(chromaPanel);
        }
    }


    private void destroySpectrumToolBar() {
        // filter
        sideToolBarPanel.removeActionFromSideBar(ToolbarCommand.FILTER.getActionCommand());
        // ion
        sideToolBarPanel.removeActionFromSideBar(ToolbarCommand.ION.getActionCommand());
        // mass diff
        sideToolBarPanel.removeActionFromSideBar(ToolbarCommand.MASS_DIFF.getActionCommand());
        // amino acid annotation
        sideToolBarPanel.removeActionFromSideBar(ToolbarCommand.AMINO_ACID.getActionCommand());
    }

    private void buildStandardToolbar() {
        // property
        MzGraphPropertyPane propPane = new MzGraphPropertyPane();
        this.addPropertyChangeListener(propPane);
        sideToolBarPanel.addComponentToSideBar(null, ToolbarCommand.PROPERTY.getLabel(),
                ToolbarCommand.PROPERTY.getTooltip(), ToolbarCommand.PROPERTY.getActionCommand(), propPane);
        // grid
        sideToolBarPanel.addCommandToSideBar(null, ToolbarCommand.GRID.getLabel(),
                ToolbarCommand.GRID.getTooltip(), ToolbarCommand.GRID.getActionCommand(), true);
        // save
        sideToolBarPanel.addCommandToSideBar(null, ToolbarCommand.SAVE.getLabel(),
                ToolbarCommand.SAVE.getTooltip(), ToolbarCommand.SAVE.getActionCommand(), false);
        // print
        sideToolBarPanel.addCommandToSideBar(null, ToolbarCommand.PRINT.getLabel(),
                ToolbarCommand.PRINT.getTooltip(), ToolbarCommand.PRINT.getActionCommand(), false);
        // add a separator
        sideToolBarPanel.addSeparator();
    }

    /**
     * Build tool bar for spectrum panel
     */
    private void buildSpectrumToolbar() {
        // filters
        CheckBoxFilterPanel fragPanel = new CheckBoxFilterPanel(MzGraphConstants.DEFAULT_FRAGMENT_ION_NAME,
                                                                spectrumPanel.getFragmentIonVisibilities());
//        CheckBoxFilterPanel aminoPanel = new CheckBoxFilterPanel(MzGraphConstants.DEFAULT_AMINO_ACID_NAME,
//                                                                spectrumPanel.getAminoAcidVisibilities());
        ActionCascadePanel acPanel = new ActionCascadePanel();
        acPanel.add(fragPanel);
//        acPanel.add(aminoPanel);

        sideToolBarPanel.addComponentToSideBar(null, ToolbarCommand.FILTER.getLabel(),
                ToolbarCommand.FILTER.getTooltip(), ToolbarCommand.FILTER.getActionCommand(), acPanel);
        // ion visibility
        sideToolBarPanel.addCommandToSideBar(null, ToolbarCommand.ION.getLabel(),
                ToolbarCommand.ION.getTooltip(), ToolbarCommand.ION.getActionCommand(), true);
        // mass difference
        sideToolBarPanel.addCommandToSideBar(null, ToolbarCommand.MASS_DIFF.getLabel(),
                ToolbarCommand.MASS_DIFF.getTooltip(), ToolbarCommand.MASS_DIFF.getActionCommand(), true);
        // amino acid annotation
//        sideToolBarPanel.addCommandToSideBar(null, ToolbarCommand.AMINO_ACID.getLabel(),
//                ToolbarCommand.AMINO_ACID.getTooltip(), ToolbarCommand.AMINO_ACID.getActionCommand(), true);
    }

    private IonAnnotation getIonAnnotation(FragmentIon ion) {
        CvTermReference ionCvTerm = CvTermReference.getCvRefByAccession(ion.getIonTypeAccession());
        IonType ionType = IonType.getIonType(ionCvTerm.getAccession());
        if (ionType.equals(IonType.NON_IDENTIFIED_ION)) {
            // iterate over all the accessions.
            Collection<String> parentAccs = ionCvTerm.getParentAccessions();
            for (String parentAcc : parentAccs) {
                ionType = IonType.getIonType(parentAcc);
                if (ionType.equals(IonType.NON_IDENTIFIED_ION)) {
                    break;
                }
            }
        }
        // get the fragment loss
        FragmentLoss fragLoss = FragmentLoss.getFragmentLoss(ion.getIonType());
        IonAnnotationInfo ionInfo1 = new IonAnnotationInfo(ion.getCharge(), ionType, ion.getLocation(), fragLoss);
        java.util.List<IonAnnotationInfo> ionInfo1List = new LinkedList<IonAnnotationInfo>();
        ionInfo1List.add(ionInfo1);
        IonAnnotation ionAnnotation = new IonAnnotation(ion.getMz(), ion.getIntensity(), ionInfo1List);

        return ionAnnotation;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (DataAccessController.MZGRAPH_TYPE.equals(evt.getPropertyName())) {
            Object newVal = evt.getNewValue();
            if (newVal != null) {
                this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, null, newVal);
                dataModel.setMzGraph((MzGraph) evt.getNewValue());

            }
        } else if (MzGraphPaneModel.NEW_MZ_GRAPH_PROP.equals(evt.getPropertyName())) {
            if (SwingUtilities.isEventDispatchThread()) {
                populatePaneWithData();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        populatePaneWithData();
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }
    }


    private enum ToolbarCommand {
        PROPERTY("Properties", "Show properties", "Properties"),
        GRID("Grid", "Show grid", SpectrumPanel.GRID_LINE_COMMAND),
        SAVE("Save", "Save as", SpectrumPanel.SAVE_AS),
        PRINT("Print", "Print", SpectrumPanel.PRINT_COMMAND),
        FILTER("Filters", "Show filters", "Filter"),
        ION("Ions", "Change ion visibility", SpectrumPanel.HIDE_FRAGMENT_ION),
        MASS_DIFF("MassDiff", "Enable select mass differences", SpectrumPanel.HIDE_MASS_DIFF),
        AMINO_ACID("Amino Acid", "Show amino aicd annotations", SpectrumPanel.HIDE_AMINO_ACID);

        private String label;
        private String tooltip;
        private String actionCommand;

        private ToolbarCommand(String label, String tooltip, String actionCommand) {
            this.label = label;
            this.tooltip = tooltip;
            this.actionCommand = actionCommand;
        }

        public String getLabel() {
            return label;
        }

        public String getTooltip() {
            return tooltip;
        }

        public String getActionCommand() {
            return actionCommand;
        }
    }
}
