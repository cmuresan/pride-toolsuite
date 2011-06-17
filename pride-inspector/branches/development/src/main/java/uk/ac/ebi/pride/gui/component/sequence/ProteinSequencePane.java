package uk.ac.ebi.pride.gui.component.sequence;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.gui.event.container.PeptideEvent;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.impl.RetrieveProteinDetailModel;
import uk.ac.ebi.pride.gui.task.impl.RetrieveSelectedPeptideAnnotation;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.beans.PropertyChangeEvent;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.List;

import static uk.ac.ebi.pride.gui.component.sequence.AttributedSequenceBuilder.*;

/**
 * Panel to visualize protein sequence
 * <p/>
 * User: rwang
 * Date: 08/06/11
 * Time: 11:57
 */
public class ProteinSequencePane extends DataAccessControllerPane<AnnotatedProtein, Void> implements EventBusSubscribable {
    private final static Logger logger = LoggerFactory.getLogger(ProteinSequencePane.class);
    /**
     * property indicates a change of protein model
     */
    private static final String MODEL_PROP = "model";
    /**
     * This contains protein details and service as a data model for the protein sequence pane
     */
    private AnnotatedProtein proteinModel;
    /**
     * Unique protein id
     */
    private Comparable proteinId;
    /**
     * Subscribe peptide event
     */
    private PeptideEventSubscriber peptideEventSubscriber;

    public ProteinSequencePane(DataAccessController controller) {
        super(controller);
        this.addPropertyChangeListener(this);
    }

    @Override
    protected void setupMainPane() {
        this.setBackground(Color.white);
    }

    public Comparable getProteinId() {
        return proteinId;
    }

    public void setProteinId(Comparable proteinId) {
        this.proteinId = proteinId;
    }

    /**
     * Get protein model for this pane
     *
     * @return Protein protein detail object
     */
    public Protein getProteinModel() {
        return proteinModel;
    }

    /**
     * Set a new protein model
     *
     * @param protein protein detail object
     */
    public void setProteinModel(AnnotatedProtein protein) {
        AnnotatedProtein oldProt, newProt;
        synchronized (this) {
            oldProt = this.proteinModel;
            this.proteinModel = protein;
            newProt = protein;

            // property change listener
            if (oldProt != null) {
                oldProt.removePropertyChangeListener(this);
            }

            if (newProt != null) {
                newProt.addPropertyChangeListener(this);
            }
        }
        // notify
        firePropertyChange(MODEL_PROP, oldProt, newProt);
    }

    /**
     * Listen to the property change events
     *
     * @param evt property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        if (MODEL_PROP.equals(propName)) {
            revalidate();
            repaint();
        } else if (AnnotatedProtein.PEPTIDE_SELECTION_PROP.equals(propName)) {
            revalidate();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        AnnotatedProtein protein = new AnnotatedProtein("Test");
        protein.setSequenceString("ABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPYABCPY");

        // create a new graphics 2D
        Graphics2D g2 = (Graphics2D) g.create();

        // get formatted protein sequence string
        AttributedString sequence = AttributedSequenceBuilder.build(protein);

        // rendering hits
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (sequence != null) {
            drawProteinSequence(g2, sequence);
        } else {
            drawMissingProteinSequence(g2);
        }

        // dispose graphics 2D
        g2.dispose();
    }

    /**
     * This method is called when a protein sequence needs to be drawn
     *
     * @param g2       graphics 2D
     * @param sequence formatted protein sequence
     */
    private void drawProteinSequence(Graphics2D g2, AttributedString sequence) {
        Container viewport = getParent();
        int width = viewport.getWidth();

        // spacing within the panel
        int margin = 20;
        int yMargin = 20;
        int leftMargin = margin;
        int rightMargin = width - margin;

        // line spacing
        int lineSpacing = 20;

        // AttributedString iterator
        AttributedCharacterIterator sequenceIter = sequence.getIterator();

        // renderer context
        FontRenderContext fontContext = g2.getFontRenderContext();

        // line break measurer
        LineBreakMeasurer measurer = new LineBreakMeasurer(sequenceIter, fontContext);

        // set the initial value of vertical position
        int yPos = yMargin;

        // calculate each protein sequence segment's length
        int proteinSegLength = PROTEIN_SEGMENT_LENGTH + PROTEIN_SEGMENT_LENGTH - 1 + PROTEIN_SEGMENT_GAP.length();

        // index of formatted protein sequence
        int textPosIndex = proteinSegLength;

        while (measurer.getPosition() < sequenceIter.getEndIndex()) {
            float xPos = leftMargin;
            // line contains text already
            boolean lineContainText = false;
            boolean lineComplete = false;

            // stores all the text layout to be drawn and their starting horizontal position
            List<Tuple<TextLayout, Float>> layouts = new ArrayList<Tuple<TextLayout, Float>>();

            while (!lineComplete) {
                float wrappingWidth = rightMargin - xPos;
                TextLayout layout = measurer.nextLayout(wrappingWidth, textPosIndex, lineContainText);

                if (layout != null) {
                    // add an layout entry
                    Tuple<TextLayout, Float> element = new Tuple<TextLayout, Float>(layout, xPos);
                    layouts.add(element);

                    // increment horizontal position
                    xPos += layout.getAdvance();
                } else {
                    // line finished
                    lineComplete = true;
                }

                lineContainText = true;

                // increment text position index
                if (measurer.getPosition() == textPosIndex) {
                    textPosIndex += proteinSegLength;
                }

                // check whether reached the end of sequence
                if (measurer.getPosition() == sequenceIter.getEndIndex()) {
                    lineComplete = true;
                }
            }

            // move to next line
            yPos += lineSpacing;

            // adjust the size of the panel
            if (yPos > getHeight()) {
                setPreferredSize(new Dimension(width, yPos));
                revalidate();
            }

            // draw the line
            for (Tuple<TextLayout, Float> entry : layouts) {
                TextLayout nextLayout = entry.getKey();
                Float nextPosition = entry.getValue();
                nextLayout.draw(g2, nextPosition, yPos);
            }
        }

        // set a margin gap at the bottom of the panel
        setPreferredSize(new Dimension(width, yPos + yMargin));
        revalidate();
    }

    /**
     * This method is called when a protein sequence is missing, a warning messge will be shown
     *
     * @param g2 graphics 2D
     */
    private void drawMissingProteinSequence(Graphics2D g2) {
        // todo: draw warning message
    }

    @Override
    public void subscribeToEventBus() {
        EventService eventBus = ContainerEventServiceFinder.getEventService(this);

        // create subscriber
        peptideEventSubscriber = new PeptideEventSubscriber();

        // subscribe
        eventBus.subscribe(PeptideEvent.class, peptideEventSubscriber);
    }

    @Override
    public void succeed(TaskEvent<AnnotatedProtein> proteinTaskEvent) {
        // set a new protein model
        this.setProteinModel(proteinTaskEvent.getValue());
    }

    /**
     * Subscribe to peptide event
     */
    private class PeptideEventSubscriber implements EventSubscriber<PeptideEvent> {

        @Override
        public void onEvent(PeptideEvent event) {
            Comparable identId = event.getIdentificationId();
            Comparable peptideId = event.getPeptideId();

            logger.debug("Peptide selected: Ident {} - Peptide {}", identId, peptideId);

            Task task;
            if (proteinModel != null && proteinId.equals(identId)) {
                logger.debug("New peptide selection on protein sequence");
                task = new RetrieveSelectedPeptideAnnotation(controller, proteinModel, identId, peptideId);

            } else {
                logger.debug("New protein sequence will be shown");
                task = new RetrieveProteinDetailModel(controller, identId, peptideId);
                task.addTaskListener(ProteinSequencePane.this);
            }

            task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));
            appContext.addTask(task);
        }
    }
}
