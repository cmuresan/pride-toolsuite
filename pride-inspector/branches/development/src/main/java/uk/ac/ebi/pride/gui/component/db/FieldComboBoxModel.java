package uk.ac.ebi.pride.gui.component.db;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 02/06/11
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class FieldComboBoxModel extends DefaultComboBoxModel{

    public FieldComboBoxModel() {
        super();
        // enable annotation
        AnnotationProcessor.process(this);

        this.addElement("Any field");
    }

    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchEvent(DatabaseSearchEvent evt) {
        if (DatabaseSearchEvent.Status.HEADER.equals(evt.getStatus())) {
            List<String> headers = (List<String>)evt.getResult();
            for (String header : headers) {
                this.addElement(header);
            }
        }
    }
}
