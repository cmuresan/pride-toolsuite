package uk.ac.ebi.pride.gui.component.report;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 25/05/11
 * Time: 12:24
 * To change this template use File | Settings | File Templates.
 */
public class SummaryReportViewer extends JPanel{

    public SummaryReportViewer() {
        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
    }

    private void addComponents() {
        // create scroll pane
        JScrollPane scrollPane = new JScrollPane(null,
                                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        JPanel container = new JPanel();
        container.setBackground(Color.white);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(container);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void addReportMessageItem(ReportMessageItem item) {

    }
}
