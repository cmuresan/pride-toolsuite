package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.tree.model.ExperimentTreeModel;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.utils.GUIUtilities;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:42:46
 */
public class ExperimentTreeViewer extends JPanel implements PropertyChangeListener, TreeSelectionListener, ActionListener {

    private static final String GO_TO_EXPERIMENT = "Go to:";
    private DataAccessController controller = null;
    private PrideViewerContext context;
    private JTree currTree;
    private JTextField expField;

    public ExperimentTreeViewer() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        context.getDataAccessMonitor().addPropertyChangeListener(this);
    }

    /**
     * Build a experiment tree to display all the experiments.
     * @return JTree    experiment tree.
     */
    private JTree buildExperimentTree() {
        JTree tree = new JTree();
        tree.setEditable(false);
        tree.setDragEnabled(false);
        tree.setVisible(false);
        tree.addTreeSelectionListener(this);
        tree.setBackground(Color.white);
        // get property manager
        PropertyManager propMgr = context.getPropertyManager();
        Icon leafIcon = GUIUtilities.loadIcon(propMgr.getProperty("experiment.tree.viewer.node.small.icon"));
        Icon rootIcon = GUIUtilities.loadIcon(propMgr.getProperty("experiment.tree.viewer.root.node.small.icon"));
        ExperimentTreeCellRenderer renderer = new ExperimentTreeCellRenderer(rootIcon, leafIcon);
        tree.setCellRenderer(renderer);
        return tree;
    }

    /**
     * Build the search pane for the experiment tree.
     * @return JPanel   return a panel
     */
    private JPanel buildSearchPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(Color.white);
        JLabel label = new JLabel("Go to");
        expField = new JTextField();
        expField.setColumns(10);
        PropertyManager propMgr = context.getPropertyManager();
        Icon searchIcon = GUIUtilities.loadIcon(propMgr.getProperty("experiment.tree.viewer.find.node.small.icon"));
        JButton button = new JButton(searchIcon);
        button.setActionCommand(GO_TO_EXPERIMENT);
        button.addActionListener(this);
        panel.add(label);
        panel.add(expField);
        panel.add(button);
        return panel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
            controller = (DataAccessController) evt.getNewValue();
            if (SwingUtilities.isEventDispatchThread()) {
                updateComponents();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        updateComponents();
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }
    }

    private void updateComponents() {
        if (controller != null) {
            // new controller
            try {
                // get context
                PrideViewerContext context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
                JTree tree = (JTree) context.getExperimentTree(controller);
                if (tree == null) {
                    tree = buildExperimentTree();
                    Collection<Comparable> expIds = controller.getExperimentAccs();
                    if (expIds != null && expIds.size() > 0) {
                        java.util.List<Comparable> newExpIds = new ArrayList<Comparable>(expIds);
                        TreeModel model = new ExperimentTreeModel(newExpIds);
                        tree.setModel(model);
                        tree.setVisible(true);
                        tree.setSelectionInterval(1, 1);
                    }
                    context.addExperimentTree(controller, tree);
                }
                this.removeAll();
                currTree = tree;
                JScrollPane treeScrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                this.add(treeScrollPane, BorderLayout.CENTER);
                this.add(buildSearchPane(), BorderLayout.SOUTH);
            } catch (DataAccessException dex) {
                // ToDo: need to be optimized: Exception
                dex.printStackTrace();
            }
        } else {
            this.removeAll();
        }
        this.getParent().repaint();
    }


    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree) e.getSource();
        String node = (String) tree.getLastSelectedPathComponent();
        if (node != null) {
            try {
                controller.setForegroundExperimentAcc(node);
            } catch (DataAccessException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String evtCmd = e.getActionCommand();
        if (GO_TO_EXPERIMENT.equals(evtCmd)) {
            String expAcc = expField.getText().trim();
            // check the format
            TreeModel model = currTree.getModel();
            int index = model.getIndexOfChild(model.getRoot(), expAcc);
            if ( index >= 0) {
                currTree.setSelectionInterval(index + 1, index + 1);
            } else {
                JOptionPane.showMessageDialog(this, "Wrong experiment accession format", "Warning", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ExperimentTreeCellRenderer extends DefaultTreeCellRenderer {
        private Icon rootIcon;
        private Icon leafIcon;

        public ExperimentTreeCellRenderer(Icon rootIcon, Icon leafIcon) {
            this.rootIcon = rootIcon;
            this.leafIcon = leafIcon;
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (leaf) {
                label.setIcon(leafIcon);
            } else {
                label.setIcon(rootIcon);
            }
            return label;
        }
    }
}
