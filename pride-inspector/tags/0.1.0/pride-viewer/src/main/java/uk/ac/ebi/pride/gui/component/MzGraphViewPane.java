package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.jmzml.gui.ChromatogramPanel;
import uk.ac.ebi.jmzml.gui.SpectrumPanel;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.*;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 14:53:52
 */
public class MzGraphViewPane extends JPanel implements PropertyChangeListener {
    private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.8;

    private MzGraph mzGraph = null;
    private JSplitPane splitPane = null;

    public MzGraphViewPane() {
        this(null);
    }

    public MzGraphViewPane(MzGraph mzGraph) {
        this.mzGraph = mzGraph;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        // top component get all the space after resizing
        splitPane.setResizeWeight(SPLIT_PANE_RESIZE_WEIGHT);
        splitPane.setOneTouchExpandable(true);
        splitPane.setVisible(false);
        this.add(splitPane, BorderLayout.CENTER);

        if (mzGraph != null) {
            addComponents();
        }
    }

    /**
     * This method should be called on ETD
     */
    private void addComponents() {
        MzGraphPane graphPane = new MzGraphPane(mzGraph);
        MzGraphMetaDataPane graphMetaDataPane = new MzGraphMetaDataPane(mzGraph);
        splitPane.setTopComponent(graphPane);
        splitPane.setBottomComponent(graphMetaDataPane);
        splitPane.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (DataAccessController.MZGRAPH_TYPE.equals(evt.getPropertyName())) {
            mzGraph = (MzGraph)evt.getNewValue();
            if (SwingUtilities.isEventDispatchThread()) {
                addComponents();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        addComponents();
                    }
                };
                SwingUtilities.invokeLater(eventDispatcher);
            }

        }
    }
}
