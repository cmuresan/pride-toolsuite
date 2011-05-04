package uk.ac.ebi.pride.gui.component;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 20-Apr-2010
 * Time: 14:04:15
 */
public class VersionPanel extends StatusBarPanel {
    private String defaultVersion = null;

    public VersionPanel(String defaultVersion) {
        super(20, true);
        this.defaultVersion = defaultVersion;
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.setToolTipText(defaultVersion);
        addComponents();
    }

    private void addComponents() {
        this.removeAll();
        this.setToolTipText(defaultVersion);
        JLabel versionLabel = new JLabel(defaultVersion);
        this.add(versionLabel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //ToDo: monitoring version change
    }
}
