package uk.ac.ebi.pride.gui.component.model;

import uk.ac.ebi.pride.data.utils.CollectionUtils;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 14:41:57
 */
public class ExperimentTreeModel implements TreeModel {

    private Collection<String> experimentIds = null;
    private static final String rootNode = "Experiments";

    public ExperimentTreeModel(Collection<String> expIds) {
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
            result = CollectionUtils.getElement(experimentIds, index);
        }
        return result;
    }

    @Override
    public int getChildCount(Object parent) {
        return experimentIds.size();  //To change body of implemented methods use File | Settings | File Templates.
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
            result = CollectionUtils.getIndex(experimentIds, (String)child);
        }

        return result;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
