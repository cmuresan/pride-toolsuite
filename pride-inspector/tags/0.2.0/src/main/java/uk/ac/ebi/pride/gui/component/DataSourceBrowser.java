package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.utils.GUIUtilities;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * DataSourceBrowser is a aggregate component, it contains both DataSourceViewer and ExperimentTreeViewer.
 * It handles primarily the interaction between these two components.
 * <p/>
 * User: rwang
 * Date: 19-Aug-2010
 * Time: 11:28:07
 */
public class DataSourceBrowser extends JPanel implements PropertyChangeListener {
    private JTabbedPane dataSourceTabPane;
    private JTabbedPane expTreeTabPane;
    private DataAccessController controller;

    public DataSourceBrowser() {
        this.setLayout(new BorderLayout());
        addComponents();

    }

    private void addComponents() {
        PrideViewerContext context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        context.getDataAccessMonitor().addPropertyChangeListener(this);
        // get property manager
        PropertyManager propMgr = context.getPropertyManager();
        // data source viewer
        DataSourceViewer dataSourceViewer = new DataSourceViewer();
        JScrollPane dataSourceScrollPane = new JScrollPane(dataSourceViewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dataSourceScrollPane.setBorder(BorderFactory.createEmptyBorder());
        // get display related details
        Icon dataSourceViewerIcon = GUIUtilities.loadIcon(propMgr.getProperty("data.source.viewer.small.icon"));
        String dataSourceTitle = propMgr.getProperty("data.source.viewer.title");
        String dataSourceToolTip = propMgr.getProperty("data.source.viewer.tooltip");
        // add a tab
        dataSourceTabPane = new JTabbedPane();
        dataSourceTabPane.addTab(dataSourceTitle, dataSourceViewerIcon, dataSourceScrollPane, dataSourceToolTip);

        // experiemnt tree viewer
        ExperimentTreeViewer experimentTreeViewer = new ExperimentTreeViewer();
        // get display related details
        Icon expTreeViewerIcon = GUIUtilities.loadIcon(propMgr.getProperty("experiment.tree.viewer.small.icon"));
        String expTreeTitle = propMgr.getProperty("experiment.tree.viewer.title");
        String expTreeToolTip = propMgr.getProperty("experiment.tree.viewer.tooltip");
        // add a tab
        expTreeTabPane = new JTabbedPane();
        expTreeTabPane.addTab(expTreeTitle, expTreeViewerIcon, experimentTreeViewer, expTreeToolTip);

        this.add(dataSourceTabPane, BorderLayout.CENTER);
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
        try {
            this.removeAll();
            if (controller != null) {
                Collection<Comparable> expIds = controller.getExperimentAccs();
                if (expIds == null || expIds.isEmpty()) {
                    this.add(dataSourceTabPane, BorderLayout.CENTER);
                } else {
                    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dataSourceTabPane, expTreeTabPane);
                    splitPane.setOneTouchExpandable(false);
                    splitPane.setDividerSize(0);
                    splitPane.setResizeWeight(0.3);
                    this.add(splitPane, BorderLayout.CENTER);
                }
            }
            this.revalidate();
            this.repaint();
        } catch (DataAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
