package uk.ac.ebi.pride.gui.component.metadata;

import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.utils.GUIUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: rwang
 * Date: 25-May-2010
 * Time: 09:01:15
 */
public class CollapsiblePane extends JPanel {
    private final static Color DEFAULT_TITLE_BACKGROUND = new Color(30, 30, 100, 150);
    private final static Color DEFAULT_BORDER_BACKGROUND = new Color(30, 30, 100, 150);
    private final static Dimension DEFAULT_MAX_DIMENSION = new Dimension(1000, 1000);
    private final String title;
    private Component contentComponent;
    private Icon expandIcon;
    private Icon collapseIcon;
    private JLabel iconLabel;

    public CollapsiblePane(String title) {
        this.title = title;
        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(DEFAULT_BORDER_BACKGROUND));
        this.setMaximumSize(DEFAULT_MAX_DIMENSION);
    }

    private void addComponents() {
        // add title label
        JComponent titleComponent = createTitleComponent();
        this.add(titleComponent, BorderLayout.PAGE_START);
    }

    private JComponent createTitleComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(DEFAULT_TITLE_BACKGROUND);
        // text label
        JLabel label = new JLabel(title);
        label.setOpaque(false);
        label.setForeground(Color.white);
        Font font = UIManager.getDefaults().getFont("Label.font");
        label.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
        panel.add(label, BorderLayout.WEST);
        // icons
        PrideViewerContext context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();
        expandIcon = GUIUtilities.loadIcon(propMgr.getProperty("navigation.expand.small.icon"));
        collapseIcon = GUIUtilities.loadIcon(propMgr.getProperty("navigation.collapse.small.icon"));
        iconLabel = new JLabel(expandIcon);
        panel.add(iconLabel, BorderLayout.EAST);
        panel.addMouseListener(new CollapseListener());
        return panel;
    }

    public void setContentComponent(Component component) {
        this.contentComponent = component;
        this.add(contentComponent, BorderLayout.CENTER);
    }

    private class CollapseListener extends MouseAdapter{

        @Override
        public void mouseClicked(MouseEvent e) {
            if (contentComponent != null) {
                boolean vis = !contentComponent.isVisible();
                contentComponent.setVisible(vis);
                if (vis) {
                    iconLabel.setIcon(expandIcon);
                } else {
                    iconLabel.setIcon(collapseIcon);                    
                }
                Component parent = CollapsiblePane.this.getParent();
                parent.repaint();
            }
        }
    }
}
