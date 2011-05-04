package uk.ac.ebi.pride.gui.component.tree.model;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 14:41:57
 */
public class ExperimentTreeModel implements TreeModel {

    private List<Comparable> experimentIds = null;
    private static final String rootNode = "Public Experiments";

    public ExperimentTreeModel(List<Comparable> expIds) {
        this.experimentIds = expIds;
    }


    @Override
    public Object getRoot() {
        return rootNode;
    }

    @Override
    public Object getChild(Object parent, int index) {
        Object result = null;
        if (parent.equals(rootNode)) {
            result = experimentIds.get(index);
        }
        return result;
    }

    @Override
    public int getChildCount(Object parent) {
        return experimentIds.size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return experimentIds.contains(node);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int result = -1;

        if (parent.equals(rootNode)) {
            result = experimentIds.indexOf(child);
        }

        return result;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }
}
