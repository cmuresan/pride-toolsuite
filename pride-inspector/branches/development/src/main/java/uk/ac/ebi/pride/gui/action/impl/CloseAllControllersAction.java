package uk.ac.ebi.pride.gui.action.impl;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.ForegroundDataSourceEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Close all existing data access controller
 * <p/>
 * User: rwang
 * Date: 11-Oct-2010
 * Time: 13:55:43
 */
public class CloseAllControllersAction extends PrideAction {

    public CloseAllControllersAction(String name, Icon icon) {
        super(name, icon);
        // enable annotation
        AnnotationProcessor.process(this);

        // register this action as property listener to database access monitor

        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        for (DataAccessController controller : context.getControllers()) {
            context.removeDataAccessController(controller, true);
        }
    }

    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        DataAccessController foregroundController = (DataAccessController) evt.getNewForegroundDataSource();
        this.setEnabled(foregroundController != null);
    }
}