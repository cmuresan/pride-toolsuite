package uk.ac.ebi.pride.gui.component.report;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.desktop.*;
import uk.ac.ebi.pride.gui.event.ForegroundDataSourceEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Summary report viewer displays all the warnings, message related to a single database access controller
 *
 * User: rwang
 * Date: 25/05/11
 * Time: 12:24
 */
public class SummaryReportViewer extends JPanel{
    private PrideInspectorContext context;
    private ReportList container;
    private DataAccessController currentController;

    public SummaryReportViewer() {
        // enable annotation
        AnnotationProcessor.process(this);

        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    private void addComponents() {
        // create scroll pane
        JScrollPane scrollPane = new JScrollPane(null,
                                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        container = new ReportList();
        // cell renderer
        ReportListRenderer renderer = new ReportListRenderer();
        container.setCellRenderer(renderer);

        container.setBackground(Color.white);
        scrollPane.setViewportView(container);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @EventSubscriber (eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        DataAccessController controller = (DataAccessController)evt.getNewForegroundDataSource();
        if (controller != currentController) {
            ListModel model = context.getSummaryReportModel(controller);
            container.setModel(model);
            currentController = controller;
        }
    }
}
