package uk.ac.ebi.pride.gui.component.mzgraph;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.BinaryDataArray;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.SideToolBarPanel;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.action.impl.OpenHelpAction;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.gui.event.container.PeptideEvent;
import uk.ac.ebi.pride.gui.event.container.SpectrumEvent;
import uk.ac.ebi.pride.gui.event.subscriber.PeptideSpectrumEventSubscriber;
import uk.ac.ebi.pride.gui.event.subscriber.SpectrumEventSubscriber;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.utils.AnnotationUtils;
import uk.ac.ebi.pride.mol.PTModification;
import uk.ac.ebi.pride.mzgraph.SpectrumBrowser;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

/**
 * Panel to display spectrum
 * <p/>
 * User: rwang
 * Date: 10/06/11
 * Time: 14:42
 */
public class SpectrumViewPane extends DataAccessControllerPane<Spectrum, Void> implements EventBusSubscribable{
    private static final Logger logger = LoggerFactory.getLogger(SpectrumViewPane.class);
    /**
     * In memory spectrum browser
     */
    private SpectrumBrowser spectrumBrowser;
    /**
     * True indicates it is the first spectrum to be visualized
     */
    private boolean isFirstSpectrum;
    /**
     * Whether to show the side panel by default
     */
    private boolean showSidePanel;
    /**
     * Subscribe to peptide event
     */
    private PeptideSpectrumEventSubscriber spectrumSubscriber;
    private SpectrumEventSubscriber spectrumSelectSubscriber;

    public SpectrumViewPane(DataAccessController controller, boolean showSidePanel) {
        super(controller);
        this.showSidePanel = showSidePanel;
    }

    @Override
    protected void setupMainPane() {
        // setup the main pane
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected void addComponents() {
        isFirstSpectrum = true;
        spectrumBrowser = new SpectrumBrowser();

        // meta data pane
        MzGraphPropertyPane propPane = new MzGraphPropertyPane();
        propPane.setPreferredSize(new Dimension(200, 200));
        this.addPropertyChangeListener(propPane);

        // add spectrum metadata display pane
        Icon propertyIcon = GUIUtilities.loadIcon(appContext.getProperty("property.small.icon"));
        String propertyDesc = appContext.getProperty("property.title");
        String propertyTooltip = appContext.getProperty("property.tooltip");
        spectrumBrowser.getSidePane().addComponent(propertyIcon, null, propertyTooltip, propertyDesc, propPane);

        // add spectrum help pane
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        String helpTooltip = appContext.getProperty("help.tooltip");
        PrideAction helpAction = new OpenHelpAction(null, helpIcon);
        helpAction.putValue(Action.SHORT_DESCRIPTION, helpTooltip);
        AbstractButton button = (AbstractButton) spectrumBrowser.getSidePane().addAction(helpAction, false);
        CSH.setHelpIDString(button, "help.mzgraph.spectra");
        button.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));

        this.add(spectrumBrowser, BorderLayout.CENTER);
    }

    /**
     * Subscribe to local event bus
     */
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        spectrumSubscriber = new PeptideSpectrumEventSubscriber(controller, this);
        spectrumSelectSubscriber = new SpectrumEventSubscriber(controller, this);

        // subscribeToEventBus
        eventBus.subscribe(PeptideEvent.class, spectrumSubscriber);
        eventBus.subscribe(SpectrumEvent.class, spectrumSelectSubscriber);
    }

    @Override
    public void succeed(TaskEvent<Spectrum> spectrumTaskEvent) {
        Spectrum spectrum = spectrumTaskEvent.getValue();
        BinaryDataArray mzBinary = spectrum == null ? null : spectrum.getMzBinaryDataArray();
        BinaryDataArray intentBinary = spectrum == null ? null : spectrum.getIntensityBinaryDataArray();
        if (mzBinary != null && intentBinary != null) {
            spectrumBrowser.setPeaks(mzBinary.getDoubleArray(), intentBinary.getDoubleArray());
            // set source name
            if (controller.getType().equals(DataAccessController.Type.XML_FILE)) {
                spectrumBrowser.setSource(((File) controller.getSource()).getName());
            } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
                spectrumBrowser.setSource("Pride Experiment " + controller.getForegroundExperimentAcc());
            }
            // set id
            spectrumBrowser.setId(spectrum.getId());
            Peptide peptide = spectrum.getPeptide();
            if (peptide != null) {
                int peptideLength = peptide.getSequenceLength();
                Map<Integer, java.util.List<PTModification>> modifications = AnnotationUtils.createModificationMap(peptide.getModifications(), peptideLength);
                spectrumBrowser.setAminoAcidAnnotationParameters(peptide.getSequenceLength(), modifications);
                java.util.List<IonAnnotation> ions = AnnotationUtils.convertToIonAnnotations(peptide.getFragmentIons());
                spectrumBrowser.addFragmentIons(ions);
                if (showSidePanel && !ions.isEmpty()) {
                    spectrumBrowser.enableAnnotationControl(true);
                    if (isFirstSpectrum) {
                        spectrumBrowser.setAnnotationControlVisible(true);
                    }
                }
            } else {
                if (isFirstSpectrum && showSidePanel) {
                    SideToolBarPanel sidePane = spectrumBrowser.getSidePane();
                    String actionCmd = appContext.getProperty("property.title");
                    if (!sidePane.isToggled(actionCmd)) {
                        sidePane.invokeAction(actionCmd);
                    }
                }
            }
            isFirstSpectrum = false;
        }
        this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, "", spectrum);
    }
}
