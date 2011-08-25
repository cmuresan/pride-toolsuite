package uk.ac.ebi.pride.gui.component.report;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.event.SummaryReportEvent;

import javax.swing.*;

/**
 * List model for report list
 *
 * User: rwang
 * Date: 07/06/11
 * Time: 15:14
 */
public class ReportListModel extends DefaultListModel {

    private DataAccessController source;


    public ReportListModel(DataAccessController source) {
        this.source = source;
        // enable annotation
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = SummaryReportEvent.class)
    public void onSummaryReportEvent(SummaryReportEvent evt) {
        DataAccessController controller = evt.getDataSource();
        if(source == controller) {
            ReportMessage msg = evt.getMessage();
            addElement(msg);
        }
    }
}
