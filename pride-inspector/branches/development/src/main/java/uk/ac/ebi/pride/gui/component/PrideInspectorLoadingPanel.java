package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;

import javax.swing.*;
import java.awt.*;

/**
 * A panel displays pride's logo and showing a loading status
 *
 * User: rwang
 * Date: 14/03/11
 * Time: 16:41
 */
public class PrideInspectorLoadingPanel extends PrideInspectorPanel {

    public PrideInspectorLoadingPanel() {
        initComponents();
    }

    private void initComponents() {
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        ImageIcon loadingIcon = (ImageIcon) GUIUtilities.loadIcon(context.getProperty("loading.large.icon"));
        JLabel label = new JLabel();
        label.setOpaque(false);
        label.setIcon(loadingIcon);
        this.add(label, c);
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
        this.setBackground(Color.white);
    }
}
