package uk.ac.ebi.pride.gui.component.mzgraph;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.component.AnnotationUtil;
import uk.ac.ebi.pride.gui.desktop.*;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.utils.GUIUtilities;
import uk.ac.ebi.pride.mzgraph.ChromatogramBrowser;
import uk.ac.ebi.pride.mzgraph.SpectrumBrowser;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 14:53:52
 */
public class MzGraphViewPane extends JPanel implements PropertyChangeListener {
    private final SpectrumBrowser spectrumBrowser;
    private final ChromatogramBrowser chromaBrowser;

    public MzGraphViewPane() {
        spectrumBrowser = new SpectrumBrowser();
        MzGraphPropertyPane propPane = new MzGraphPropertyPane();
        this.addPropertyChangeListener(propPane);
        DesktopContext context = uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();
        Icon propertyIcon = GUIUtilities.loadIcon(propMgr.getProperty("property.small.icon"));
        String propertyDesc = propMgr.getProperty("property.title");
        String propertyTooltip = propMgr.getProperty("property.tooltip");
        spectrumBrowser.addComponentToSideBar(propertyIcon, null, propertyTooltip, propertyDesc, propPane);
        chromaBrowser = new ChromatogramBrowser();
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());
        // this is required for the splitpane work at the right size
        this.setMinimumSize(new Dimension(200, 400));

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (DataAccessController.MZGRAPH_TYPE.equals(evt.getPropertyName())) {
            // todo: make it run on EDT
            Object oldVal = evt.getOldValue();
            Object newVal = evt.getNewValue();
            MzGraph mzGraph = (MzGraph) evt.getNewValue();
            this.removeAll();
            if (mzGraph instanceof Spectrum) {
                Spectrum spectrum = (Spectrum) mzGraph;
                spectrumBrowser.setPeaks(spectrum.getMzBinaryDataArray().getDoubleArray(), spectrum.getIntensityBinaryDataArray().getDoubleArray());
                Peptide peptide = spectrum.getPeptide();
                if (peptide != null) {
                    int peptideLength = peptide.getSequenceLength();
                    Map<Integer, java.util.List<Modification>> modifications = AnnotationUtil.createModificationMap(peptide.getModifications(), peptideLength);
                    spectrumBrowser.setAminoAcidAnnotationParameters(peptide.getSequenceLength(), modifications);
                    java.util.List<IonAnnotation> ions = AnnotationUtil.convertToIonAnnotations(peptide.getFragmentIons());
                    spectrumBrowser.addFragmentIons(ions);
                }
                this.add(spectrumBrowser, BorderLayout.CENTER);
            } else if (mzGraph instanceof Chromatogram) {
                Chromatogram chroma = (Chromatogram) mzGraph;
                chromaBrowser.setGraphData(chroma.getTimeArray().getDoubleArray(), chroma.getIntensityArray().getDoubleArray());
                this.add(chromaBrowser, BorderLayout.CENTER);
            }
            this.revalidate();
            this.repaint();
            this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, oldVal, newVal);
        }
    }
}

