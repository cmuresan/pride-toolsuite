package uk.ac.ebi.pride.gui.component.startup;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.report.SummaryReportViewer;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * DataSourceBrowser is an aggregate component, it contains three components:
 * 1. DataSourceViewer, which keeps tracking all the opening experiments
 * 2. SummaryReportViewer, which displays the summary report for selected experiment
 * 3. LaunchMenuViewer, which includes a list of buttons to launch start different features, e.g. database search.
 * <p/>
 * User: rwang
 * Date: 19-Aug-2010
 * Time: 11:28:07
 */
public class LeftControlPane extends JPanel {
    /**
     * reference to pride inspector context
     */
    private PrideInspectorContext context;

    public LeftControlPane() {
        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    private void addComponents() {
        // data source viewer
        DataSourceViewer dataSourceViewer = new DataSourceViewer();
        JScrollPane dataSourceScrollPane = new JScrollPane(dataSourceViewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dataSourceScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // get display related details
        Icon dataSourceViewerIcon = GUIUtilities.loadIcon(context.getProperty("data.source.viewer.small.icon"));
        String dataSourceTitle = context.getProperty("data.source.viewer.title");
        String dataSourceToolTip = context.getProperty("data.source.viewer.tooltip");

        // add a tab
        JTabbedPane dataSourceTabPane = new JTabbedPane();
        dataSourceTabPane.setPreferredSize(new Dimension(200, 200));
        dataSourceTabPane.addTab(dataSourceTitle, dataSourceViewerIcon, dataSourceScrollPane, dataSourceToolTip);

        // experiment tree viewer
        SummaryReportViewer summaryReportViewer = new SummaryReportViewer();

        // get display related details
        Icon sumReportViewerIcon = GUIUtilities.loadIcon(context.getProperty("summary.report.viewer.small.icon"));
        String sumReportTitle = context.getProperty("summary.report.viewer.title");
        String sumReportToolTip = context.getProperty("summary.report.viewer.tooltip");

        // add a tab
        JTabbedPane summaryReportTabPane = new JTabbedPane();
        summaryReportTabPane.setPreferredSize(new Dimension(200, 200));
        summaryReportTabPane.addTab(sumReportTitle, sumReportViewerIcon, summaryReportViewer, sumReportToolTip);

        // launch menu viewer
        LaunchMenuViewer launchMenuViewer = new LaunchMenuViewer();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dataSourceTabPane, summaryReportTabPane);
        splitPane.setOneTouchExpandable(false);
        splitPane.setBorder(null);
        splitPane.setDividerSize(0);
        splitPane.setResizeWeight(0.5);
        this.add(splitPane, BorderLayout.CENTER);
        this.add(launchMenuViewer, BorderLayout.SOUTH);
    }
}
