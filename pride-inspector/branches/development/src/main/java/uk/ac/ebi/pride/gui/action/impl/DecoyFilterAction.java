package uk.ac.ebi.pride.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.protein.DecoyFilterDialog;
import uk.ac.ebi.pride.gui.component.table.filter.DecoyAccessionFilter;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Action to show a decoy filter dialog
 *
 * User: rwang
 * Date: 31/08/2011
 * Time: 11:34
 */
public class DecoyFilterAction extends PrideAction implements PropertyChangeListener{
    /**
     * JTable where protein name will be displayed
     */
    private JTable table;

    /**
     * The menu to set decoy filter
     */
    private JPopupMenu decoyMenu;

    /**
     * Decoy filter dialog
     */
    private JDialog decoyFilterDialog;

    /**
     * Menu items
     */
    private JMenuItem settingMenuItem;
    private JMenuItem undoFilterMenuItem;
    private JMenuItem nonDecoyMenuItem;
    private JMenuItem decoyMenuItem;

    /**
     * Pride Inspector context
     */
    private PrideInspectorContext appContext;

    /**
     * Constructor
     *
     * @param table protein table
     */
    public DecoyFilterAction(JTable table) {
        super(Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.title"),
                GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.small.icon")));
        this.table = table;
        this.appContext = (PrideInspectorContext)Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (decoyMenu == null) {
            // create new dialog
            decoyMenu = createPopupMenu();
        }
        Point location = button.getLocation();
        decoyMenu.show(button, (int) location.getX() - 100, (int) location.getY() + button.getHeight());
    }

    /**
     * Create an popup menu for the decoy filter
     * @return  JPopupMenu  popup menu
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        // settings
        settingMenuItem = new JMenuItem("Setting");
        settingMenuItem.setIcon(GUIUtilities.loadIcon(appContext.getProperty("menu.setting.small.icon")));
        settingMenuItem.addActionListener(new SettingActionListener());
        menu.add(settingMenuItem);

        // separator
        menu.addSeparator();

        // non-decoy
        nonDecoyMenuItem = new JMenuItem("Non Decoy");
        nonDecoyMenuItem.setEnabled(false);
        nonDecoyMenuItem.addActionListener(new DecoyActionListener(false));
        menu.add(nonDecoyMenuItem);

        // decoy
        decoyMenuItem = new JMenuItem("Decoy");
        decoyMenuItem.setEnabled(false);
        decoyMenuItem.addActionListener(new DecoyActionListener(true));
        menu.add(decoyMenuItem);

        // separator
        menu.addSeparator();

        // undo filter
        undoFilterMenuItem = new JMenuItem("Undo Filter");
        undoFilterMenuItem.setEnabled(false);
        undoFilterMenuItem.addActionListener(new UndoFilterActionListener());
        menu.add(undoFilterMenuItem);

        return menu;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DecoyFilterDialog.NEW_FILTER)) {
            // enable menu items
            nonDecoyMenuItem.setEnabled(true);
            nonDecoyMenuItem.setIcon(GUIUtilities.loadIcon(appContext.getProperty("menu.selection.tick.small.icon")));
            decoyMenuItem.setEnabled(true);
            decoyMenuItem.setIcon(null);
            undoFilterMenuItem.setEnabled(true);
        }
    }

    /**
     * Action listener triggered by clicking the setting menu item
     */
    private class SettingActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // show dialog
            if (decoyFilterDialog == null) {
                decoyFilterDialog = new DecoyFilterDialog(Desktop.getInstance().getMainComponent(), table);
                decoyFilterDialog.addPropertyChangeListener(DecoyFilterAction.this);
            }
            decoyFilterDialog.setVisible(true);
        }
    }

    /**
     * Action listener triggered by clicking the undo filter menu item
     */
    private class UndoFilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // remove the filter
            TableRowSorter rowSorter = (TableRowSorter)table.getRowSorter();
            rowSorter.setRowFilter(null);

            // disable non-decoy, decoy and undo filter menu items
            undoFilterMenuItem.setEnabled(false);
            nonDecoyMenuItem.setIcon(null);
            nonDecoyMenuItem.setEnabled(false);
            decoyMenuItem.setIcon(null);
            decoyMenuItem.setEnabled(false);
        }
    }

    /**
     * Action listener triggered by clicking the decoy menu item
     */
    private class DecoyActionListener implements ActionListener {
        private boolean decoyOnly;

        private DecoyActionListener(boolean decoyOnly) {
            this.decoyOnly = decoyOnly;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TableRowSorter rowSorter = (TableRowSorter)table.getRowSorter();
            DecoyAccessionFilter oldFilter = (DecoyAccessionFilter)rowSorter.getRowFilter();

            rowSorter.setRowFilter(new DecoyAccessionFilter(oldFilter.getType(), oldFilter.getCriteria(), oldFilter.getAccessionColumnIndex(), decoyOnly));

            // set icon
            if (decoyOnly) {
                decoyMenuItem.setIcon(GUIUtilities.loadIcon(appContext.getProperty("menu.selection.tick.small.icon")));
                nonDecoyMenuItem.setIcon(null);
            } else {
                nonDecoyMenuItem.setIcon(GUIUtilities.loadIcon(appContext.getProperty("menu.selection.tick.small.icon")));
                decoyMenuItem.setIcon(null);
            }
        }
    }
}

