package uk.ac.ebi.pride.gui.component.mzgraph;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 11-May-2010
 * Time: 12:37:31
 */
public class MzGraphPaneModel extends PropertyChangeHelper implements PropertyChangeListener {
    public static String NEW_MZ_GRAPH_PROP = "mz_graph_pane_mz_graph";
    private MzGraph mzGraph = null;

    public MzGraphPaneModel() {
        this(null);
    }

    public MzGraphPaneModel(MzGraph mzGraph) {
        this.mzGraph = mzGraph;
    }

    public MzGraph getMzGraph() {
        return mzGraph;
    }

    public void setMzGraph(MzGraph mzGraph) {
        MzGraph oldGraph = this.mzGraph;
        this.mzGraph = mzGraph;
        this.firePropertyChange(NEW_MZ_GRAPH_PROP, oldGraph, mzGraph);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (DataAccessController.MZGRAPH_TYPE.equals(evt.getPropertyName())) {
            this.setMzGraph((MzGraph)evt.getNewValue());
        }
    }
}
