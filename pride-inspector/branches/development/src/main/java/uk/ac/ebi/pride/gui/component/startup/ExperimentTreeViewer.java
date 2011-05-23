package uk.ac.ebi.pride.gui.component.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.tree.model.ExperimentTreeModel;
import uk.ac.ebi.pride.gui.task.impl.OpenPrideDatabaseTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.util.NumberUtilities;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * ExperimentTreeViewer list all the experiment accessions in a tree view.
 * <p/>
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:42:46
 */
public class ExperimentTreeViewer extends JPanel implements PropertyChangeListener, TreeSelectionListener, ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(ExperimentTreeViewer.class);

    private static final String OPEN_EXPERIMENT = "Open:";
    private DataAccessController controller = null;
    private PrideInspectorContext context;
    private JTree currTree;
    private JTextField expAccField;

    public ExperimentTreeViewer() {
        // store a reference to pride inspector context
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        // set the main display pane
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
        // experiment tree
        currTree = buildExperimentTree();
        // experiment text filed
        JPanel searchPane = buildSearchPane();
        // add the components
        JScrollPane treeScrollPane = new JScrollPane(currTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(treeScrollPane, BorderLayout.CENTER);
        this.add(searchPane, BorderLayout.NORTH);
    }

    /**
     * Build a experiment tree to display all the experiments.
     *
     * @return JTree    experiment tree.
     */
    private JTree buildExperimentTree() {
        JTree tree = new JTree();
        tree.setEditable(false);
        tree.setDragEnabled(false);
        tree.addTreeSelectionListener(this);
        tree.setBackground(Color.white);
        Icon leafIcon = GUIUtilities.loadIcon(context.getProperty("experiment.tree.viewer.node.small.icon"));
        Icon rootIcon = GUIUtilities.loadIcon(context.getProperty("experiment.tree.viewer.root.node.small.icon"));
        ExperimentTreeCellRenderer renderer = new ExperimentTreeCellRenderer(rootIcon, leafIcon);
        tree.setCellRenderer(renderer);
        return tree;
    }

    /**
     * Build the search pane for the experiment tree.
     *
     * @return JPanel   return a panel
     */
    private JPanel buildSearchPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.white);

        // search label
        JLabel label = new JLabel("<html><b>Jump To:</b></html>");

        // text field panel
        JPanel expFieldPane = new JPanel(new BorderLayout());
        expFieldPane.setOpaque(false);
        expFieldPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));

        // text field
        expAccField = new JTextField("experiment accession");
        expAccField.setForeground(Color.gray);
        expAccField.addMouseListener(new MouseHandler());
        expAccField.addKeyListener(new KeyHandler());
        expFieldPane.add(expAccField, BorderLayout.CENTER);

        // button
        Icon searchIcon = GUIUtilities.loadIcon(context.getProperty("experiment.tree.viewer.find.node.small.icon"));
        JButton button = new JButton(searchIcon);
        button.setActionCommand(OPEN_EXPERIMENT);
        button.addActionListener(this);

        panel.add(label, BorderLayout.NORTH);
        panel.add(expFieldPane, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return panel;
    }

    /**
     * This is triggered when a new database access controller has been assigned.
     *
     * @param evt Property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataSourceBrowser.NEW_DATABASE_ACCESS_CONTROLLER.equals(evtName)) {
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

    /**
     * Update the tree view, set a new tree model based on the experiment accessions
     * in the new data access controller
     */
    private void updateComponents() {
        if (controller != null) {
            // new controller
            try {
                Collection<Comparable> expIds = controller.getExperimentAccs();
                if (expIds != null && expIds.size() > 0) {
                    java.util.List<Comparable> newExpIds = new ArrayList<Comparable>(expIds);

                    // remove experiment accessions which are too big to open
                    // todo: this is not good practice
                    java.util.List<Comparable> accs = new ArrayList<Comparable>();

                    String accStr = context.getProperty("large.pride.experiments");
                    if (accStr != null && !"".equals(accStr.trim())) {
                        String[] parts = accStr.split(",");
                        for (String part : parts) {
                            accs.add(part.trim());
                        }
                    }
                    newExpIds.removeAll(accs);

                    TreeModel model = new ExperimentTreeModel(newExpIds);
                    currTree.setModel(model);
                }
            } catch (DataAccessException dex) {
                String msg = "Failed to update the tree view";
                logger.error(msg, dex);
                context.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
            }
        }
    }

    /**
     * This is called whenever there is a node selection
     *
     * @param e tree selection event
     */

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree) e.getSource();
        Comparable node = (Comparable) tree.getLastSelectedPathComponent();
        if (node != null && !ExperimentTreeModel.rootNode.equals(node)) {
            // fire a new task
            OpenPrideDatabaseTask retrieveTask = new OpenPrideDatabaseTask(node);
            retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));
            context.addTask(retrieveTask);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String evtCmd = e.getActionCommand();
        if (OPEN_EXPERIMENT.equals(evtCmd)) {
            openExperiment();
        }
    }

    private static class ExperimentTreeCellRenderer extends DefaultTreeCellRenderer {
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

    /**
     * MouseHandler changes the foreground text color of the search box
     * and clear all the previous text
     */
    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            expAccField.setForeground(Color.black);
            expAccField.setText("");
            expAccField.removeMouseListener(this);
        }
    }

    /**
     * KeyHandler listens to enter key to get experiment accession.
     */
    private class KeyHandler implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
                openExperiment();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    /**
     * Open a specified experiment accession in expAccField
     */
    private void openExperiment() {
        String expAcc = expAccField.getText().trim();
        // check the format
        TreeModel model = currTree.getModel();
        int index = model.getIndexOfChild(model.getRoot(), expAcc);
        if (index >= 0) {
            currTree.clearSelection();
            currTree.setSelectionInterval(index + 1, index + 1);
        } else {
            JOptionPane.showMessageDialog(this, NumberUtilities.isNumber(expAcc) ? "Not public experiment available" : "Wrong experiment accession format", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
