package uk.ac.ebi.pride.gui.component.startup;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * DataSourceBrowser is an aggregate component, it contains both DataSourceViewer and ExperimentTreeViewer.
 * It handles primarily the interaction between these two components, also manages the visibilities of the components.
 * <p/>
 * User: rwang
 * Date: 19-Aug-2010
 * Time: 11:28:07
 */
public class DataSourceBrowser extends JPanel implements TaskListener<DataAccessController, Void> {
    /** This action command is fired when there is a new connection to pride database */
    public static final String NEW_DATABASE_ACCESS_CONTROLLER = "new_database_access_controller";

    private JTabbedPane dataSourceTabPane;
    private JTabbedPane expTreeTabPane;

    /** reference to pride inspector context */
    private PrideInspectorContext context;

    public DataSourceBrowser() {
        this.setLayout(new BorderLayout());
        addComponents();
    }

    private void addComponents() {
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();

        // data source viewer
        DataSourceViewer dataSourceViewer = new DataSourceViewer();
        JScrollPane dataSourceScrollPane = new JScrollPane(dataSourceViewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dataSourceScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // get display related details
        Icon dataSourceViewerIcon = GUIUtilities.loadIcon(context.getProperty("data.source.viewer.small.icon"));
        String dataSourceTitle = context.getProperty("data.source.viewer.title");
        String dataSourceToolTip = context.getProperty("data.source.viewer.tooltip");

        // add a tab
        dataSourceTabPane = new JTabbedPane();
        dataSourceTabPane.setPreferredSize(new Dimension(200, 200));
        dataSourceTabPane.addTab(dataSourceTitle, dataSourceViewerIcon, dataSourceScrollPane, dataSourceToolTip);

        // experiment tree viewer
        ExperimentTreeViewer experimentTreeViewer = new ExperimentTreeViewer();

        // get display related details
        Icon expTreeViewerIcon = GUIUtilities.loadIcon(context.getProperty("experiment.tree.viewer.small.icon"));
        String expTreeTitle = context.getProperty("experiment.tree.viewer.title");
        String expTreeToolTip = context.getProperty("experiment.tree.viewer.tooltip");

        // add experiment tree viewer as a property change listener
        this.addPropertyChangeListener(experimentTreeViewer);

        // add a tab
        expTreeTabPane = new JTabbedPane();
        expTreeTabPane.setPreferredSize(new Dimension(200, 200));
        expTreeTabPane.addTab(expTreeTitle, expTreeViewerIcon, experimentTreeViewer, expTreeToolTip);

        this.add(dataSourceTabPane, BorderLayout.CENTER);
    }

    /**
     * This method is notified only when there is successful connection been created to PRIDE database.
     *
     * @param taskEvent task event which contains a reference to data access controller.
     */
    @Override
    public void succeed(TaskEvent<DataAccessController> taskEvent) {
        DataAccessController controller = taskEvent.getValue();
        if (controller != null && DataAccessController.Type.DATABASE.equals(controller.getType())) {
            // set experiment tree viewer tab to visible
            this.removeAll();
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dataSourceTabPane, expTreeTabPane);
            splitPane.setOneTouchExpandable(false);
            splitPane.setDividerSize(5);
            splitPane.setResizeWeight(0.3);
            this.add(splitPane, BorderLayout.CENTER);
            
            // have to repaint here
            this.revalidate();
            this.repaint();

            // set the data source browser pane to visible
            context.setDataSourceBrowserVisible(true);
            // notify experiment tree viewer on the new data base access controller
            this.firePropertyChange(NEW_DATABASE_ACCESS_CONTROLLER, null, controller);
        }
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<List<Void>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
