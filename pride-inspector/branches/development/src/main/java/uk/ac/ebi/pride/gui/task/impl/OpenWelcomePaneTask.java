package uk.ac.ebi.pride.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.gui.EDTUtils;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.db.DatabaseSearchPane;
import uk.ac.ebi.pride.gui.component.startup.WelcomePane;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.gui.event.ShowWelcomePaneEvent;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01/06/11
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class OpenWelcomePaneTask extends TaskAdapter<Void, Void> {
    private static final String DEFAULT_TASK_TITLE = "Open welcome panel";
    private static final String DEFAULT_TASK_DESCRIPTION = "Open welcome panel";

    public OpenWelcomePaneTask() {
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        final PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();

        WelcomePane welcomePane = context.getWelcomePane();
        if (welcomePane == null) {
            // create a database search pane
            Runnable code = new Runnable() {
                @Override
                public void run() {
                    context.setWelcomePane(new WelcomePane());
                }
            };

            EDTUtils.invokeAndWait(code);
        }

        EventBus.publish(new ShowWelcomePaneEvent(null));
        return null;
    }
}
