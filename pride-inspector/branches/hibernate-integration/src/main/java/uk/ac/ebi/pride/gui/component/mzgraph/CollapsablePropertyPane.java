package uk.ac.ebi.pride.gui.component.mzgraph;

import uk.ac.ebi.pride.data.core.Parameter;
import uk.ac.ebi.pride.data.utils.CollectionUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 23-Apr-2010
 * Time: 09:59:43
 */
public class CollapsablePropertyPane extends JPanel {
    private String title = null;
    private Collection<Parameter> params = null;
    private Component titleComponent = null;
    private Component propComponent = null;

    public CollapsablePropertyPane(String title, Collection<Parameter> params) {
        this.title = title;
        this.params = params;
        configureMainPanel();
        initialize();       
    }

    private void initialize() {
        // add title label
        titleComponent = createTitleComponent();
        this.add(titleComponent, BorderLayout.NORTH);

        // add property table
        propComponent = createPropertyComponent();
        this.add(propComponent, BorderLayout.CENTER);
    }

    private void configureMainPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.red);
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

    private Component createPropertyComponent() {
        JTable table =  new JTable(new PropertyTableModel(params));
        table.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        return table;
    }

    private class PropertyTableModel extends AbstractTableModel {

        public Collection<Parameter> params = null;

        private PropertyTableModel(Collection<Parameter> params) {
            this.params = params;
        }

        @Override
        public int getRowCount() {
            return params.size();
        }

        @Override
        public int getColumnCount() {
            // only two columns
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object result = null;
            Parameter param = CollectionUtils.getElement(params, rowIndex);
            if (columnIndex == 0) {
                result = param.getName();
            } else if (columnIndex == 1) {
                result = param.getValue();
            }
            return result;
        }
    }

    private class CollapseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            propComponent.setVisible(!propComponent.isVisible());
            Component parent = CollapsablePropertyPane.this.getParent();
            parent.repaint();
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
