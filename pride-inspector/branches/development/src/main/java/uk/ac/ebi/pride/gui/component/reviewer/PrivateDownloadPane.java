package uk.ac.ebi.pride.gui.component.reviewer;

import uk.ac.ebi.pride.gui.PrideInspectorContext;

import javax.swing.*;
import java.awt.*;

/**
 * The main dialog for private download, including both PRIDE and ProteomeXchange download
 *
 * @author Rui Wang
 * @version $Id$
 */
public class PrivateDownloadPane extends JDialog {

    public PrivateDownloadPane(Frame owner) {
        super(owner);
        setupMainPane();
    }

    private void setupMainPane() {
        PrideInspectorContext context = (PrideInspectorContext)uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        this.setTitle(context.getProperty("reviewer.download.title"));
        this.setSize(new Dimension(750, 550));
        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - getWidth())/2, (d.height - getHeight())/2);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(context.getProperty("reviewer.download.px.title"), new PxDownloadPane(this));
        tabbedPane.addTab(context.getProperty("reviewer.download.pride.title"), new PrideDownloadPane(this));

        this.add(tabbedPane);
    }

}
