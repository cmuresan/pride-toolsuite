package uk.ac.ebi.pride.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.protein.DecoyFilterDialog;
import uk.ac.ebi.pride.gui.component.startup.CentralContentPane;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.component.table.filter.DecoyAccessionFilter;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableModel;
import uk.ac.ebi.pride.gui.component.table.model.QuantProteinTableModel;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Action to show a decoy filter dialog
 * <p/>
 * User: rwang
 * Date: 31/08/2011
 * Time: 11:34
 */
public class DecoyFilterAction extends PrideAction implements PropertyChangeListener {
    /**
     * DataAccessController
     */
    private DataAccessController controller;

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
     * @param controller data access controller
     */
    public DecoyFilterAction(DataAccessController controller) {
        super(Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.title"),
                GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.small.icon")));
        this.controller = controller;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (decoyMenu == null) {
            // create new dialog
            decoyMenu = createPopupMenu();
        }
        Point location = button.getLocation();
        decoyMenu.show(button, (int) location.getX() - 150, (int) location.getY() + button.getHeight());
    }

    /**
     * Create an popup menu for the decoy filter
     *
     * @return JPopupMenu  popup menu
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
            Tuple<DecoyAccessionFilter.Type, String> newValue = (Tuple<DecoyAccessionFilter.Type, String>)evt.getNewValue();
            setRowFilters(newValue.getKey(), newValue.getValue());
            // enable menu items
            nonDecoyMenuItem.setEnabled(true);
            nonDecoyMenuItem.setIcon(GUIUtilities.loadIcon(appContext.getProperty("menu.selection.tick.small.icon")));
            decoyMenuItem.setEnabled(true);
            decoyMenuItem.setIcon(null);
            undoFilterMenuItem.setEnabled(true);
        }
    }

    private void setRowFilters(DecoyAccessionFilter.Type type, String criteria) {
        // get content pane
        ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);
        // protein tab
        JTable table = contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable();
        String protAccColName = ProteinTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        int index = getAccessionColumnIndex(table.getModel(), protAccColName);
        setRowFilter(table, new DecoyAccessionFilter(type, criteria, index, false));
        // peptide tab
        table = contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable();
        protAccColName = PeptideTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        index = getAccessionColumnIndex(table.getModel(), protAccColName);
        setRowFilter(table, new DecoyAccessionFilter(type, criteria, index, false));
        // quant tab
        if (contentPane.isQuantTabEnabled()) {
            table = contentPane.getQuantTabPane().getQuantProteinSelectionPane().getQuantProteinTable();
            protAccColName = QuantProteinTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
            index = getAccessionColumnIndex(table.getModel(), protAccColName);
            setRowFilter(table, new DecoyAccessionFilter(type, criteria, index, false));
        }
    }

    private int getAccessionColumnIndex(TableModel tableModel, String protAccColName) {
        int colCnt = tableModel.getColumnCount();
        for (int i = 0; i < colCnt; i++) {
            if (tableModel.getColumnName(i).equals(protAccColName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Set the row filter
     *
     * @param rowFilter a given row filter
     */
    private void setRowFilter(JTable table, RowFilter rowFilter) {
        // get table model
        TableModel tableModel = table.getModel();
        RowSorter rowSorter = table.getRowSorter();
        if (rowSorter == null || !(rowSorter instanceof TableRowSorter)) {
            rowSorter = new NumberTableRowSorter(tableModel);
            table.setRowSorter(rowSorter);
        }
        ((TableRowSorter) rowSorter).setRowFilter(rowFilter);
    }

    /**
     * Action listener triggered by clicking the setting menu item
     */
    private class SettingActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // show dialog
            if (decoyFilterDialog == null) {
                decoyFilterDialog = new DecoyFilterDialog(Desktop.getInstance().getMainComponent());
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
            // remove the decoy filter
            ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);
            // protein tab
            clearFilter(contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable());

            // peptide tab
            clearFilter(contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable());

            // quantitative tab
            if (contentPane.isQuantTabEnabled()) {
                clearFilter(contentPane.getQuantTabPane().getQuantProteinSelectionPane().getQuantProteinTable());
            }

            // disable non-decoy, decoy and undo filter menu items
            undoFilterMenuItem.setEnabled(false);
            nonDecoyMenuItem.setIcon(null);
            nonDecoyMenuItem.setEnabled(false);
            decoyMenuItem.setIcon(null);
            decoyMenuItem.setEnabled(false);
        }

        private void clearFilter(JTable table) {
            TableRowSorter rowSorter = (TableRowSorter) table.getRowSorter();
            rowSorter.setRowFilter(null);
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
            // remove the decoy filter
            ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);
            // protein tab
            setFilter(contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable());

            // peptide tab
            setFilter(contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable());

            // quantitative tab
            if (contentPane.isQuantTabEnabled()) {
                setFilter(contentPane.getQuantTabPane().getQuantProteinSelectionPane().getQuantProteinTable());
            }

            // set icon
            if (decoyOnly) {
                decoyMenuItem.setIcon(GUIUtilities.loadIcon(appContext.getProperty("menu.selection.tick.small.icon")));
                nonDecoyMenuItem.setIcon(null);
            } else {
                nonDecoyMenuItem.setIcon(GUIUtilities.loadIcon(appContext.getProperty("menu.selection.tick.small.icon")));
                decoyMenuItem.setIcon(null);
            }
        }

        private void setFilter(JTable table) {
            TableRowSorter rowSorter = (TableRowSorter) table.getRowSorter();
            DecoyAccessionFilter oldFilter = (DecoyAccessionFilter) rowSorter.getRowFilter();

            rowSorter.setRowFilter(new DecoyAccessionFilter(oldFilter.getType(), oldFilter.getCriteria(), oldFilter.getAccessionColumnIndex(), decoyOnly));
        }
    }
}

