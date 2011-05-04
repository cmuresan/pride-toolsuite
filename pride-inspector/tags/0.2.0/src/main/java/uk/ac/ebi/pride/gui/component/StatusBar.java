package uk.ac.ebi.pride.gui.component;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 17-Feb-2010
 * Time: 08:39:10
 */
public class StatusBar extends JPanel {

    private StatusBarPanel[] panels;

    public StatusBar (StatusBarPanel ... panels) {
        super();
        //ToDo: set a border
        this.setBorder(BorderFactory.createEtchedBorder());
        this.panels = panels;
        initialize();
    }

    private void initialize() {
        this.setPreferredSize(new Dimension(35, 35));
        // set layout
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 0, 0, 2);
        this.setLayout(new GridBagLayout());
        for(int i = 0; i < panels.length; i++) {
            constraints.gridx = i;
            
            constraints.ipadx = (panels[i].getPanelWidth() == -1) ? 0 : panels[i].getPanelWidth();

            if (panels[i].isFixedWidth()) {
                constraints.fill = GridBagConstraints.NONE;
                constraints.weightx = 0;
            } else {
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.weightx = 100;
            }
            
            if (i == panels.length - 1)
                constraints.insets = new Insets(0, 0, 0, 0);

            this.add(panels[i], constraints);
        }
    }

    public StatusBarPanel[] getPanels() {
        return panels == null ? null : Arrays.copyOf(panels, panels.length);
    }

    public void setPanels(StatusBarPanel[] panels) {
        this.panels = (panels == null ? null : Arrays.copyOf(panels, panels.length));
    }
}
