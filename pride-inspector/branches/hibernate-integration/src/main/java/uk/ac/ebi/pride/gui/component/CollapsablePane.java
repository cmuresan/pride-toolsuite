package uk.ac.ebi.pride.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 
 * User: rwang
 * Date: 25-May-2010
 * Time: 09:01:15
 */
public class CollapsablePane extends JPanel {
    private String title;
    private Component titleComponent;
    private Component contentComponent;

    public CollapsablePane(String title) {
        this.title = title;
        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        //this.setBackground(Color.lightGray);
        this.setBorder(BorderFactory.createEtchedBorder());
    }

    private void addComponents() {
        // add title label
        titleComponent = createTitleComponent();
        this.add(titleComponent, BorderLayout.NORTH);
    }

    private Component createTitleComponent() {
        JLabel label = new JLabel(title);
        label.setOpaque(true);
        label.setBackground(new Color(30, 30, 100));
        label.setForeground(Color.white);
        Font font = UIManager.getDefaults().getFont("Label.font");
        label.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
        label.addMouseListener(new CollapseListener());
        return label;
    }

    public void setContentComponent(Component component) {
        this.contentComponent = component;
        this.add(contentComponent, BorderLayout.CENTER);
        this.repaint();

    }

    private class CollapseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (contentComponent !=  null) {
                contentComponent.setVisible(!contentComponent.isVisible());
                Component parent = CollapsablePane.this.getParent();
                parent.repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
