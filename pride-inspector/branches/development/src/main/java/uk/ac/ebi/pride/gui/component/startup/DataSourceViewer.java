package uk.ac.ebi.pride.gui.component.startup;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.gui.event.ForegroundDataSourceEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.Collection;

/**
 * DataSourceViewer should be monitor the DataAccessControllers in
 * DataAccessMonitor.
 * <p/>
 * User: rwang
 * Date: 26-Feb-2010
 * Time: 10:42:08
 */
public class DataSourceViewer extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceViewer.class);

    /**
     * table to display the data access controllers
     */
    private JTable sourceTable = null;

    /**
     * table model for table which displays the data access controllers.
     */
    private DataAccessTableModel sourceTableModel = null;

    /**
     * a reference to PrideInspectorContext
     */
    private PrideInspectorContext context = null;

    /**
     * Constructor
     */
    public DataSourceViewer() {
        // enable annotation
        AnnotationProcessor.process(this);

        // pride inspector context
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();

        // set up the main pane
        this.setLayout(new BorderLayout());

        // set up the rest of components
        initialize();
    }

    private void initialize() {

        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(new BorderLayout());

        // create data source table with data access model
        sourceTableModel = new DataAccessTableModel();
        sourceTable = new JTable(sourceTableModel);

        // set renderer for data source column
        TableColumn sourceCol = sourceTable.getColumn(TableHeader.DATA_SOURCE_COLUMN.getHeader());
        sourceCol.setCellRenderer(new DataAccessTableCellRenderer());

        // set renderer for close data source column
        TableColumn closeCol = sourceTable.getColumn(TableHeader.CLOSE_COLUMN.getHeader());
        closeCol.setCellRenderer(new CloseDataSourceCellRenderer());

        // set the max width for the close data source column
        closeCol.setMaxWidth(20);

        // listen to any close data source event
        sourceTable.addMouseListener(new CloseDataSourceMouseListener());

        // listen to any row selection
        sourceTable.getSelectionModel().addListSelectionListener(new DataAccessSelectionListener());

        // setup table visual
        sourceTable.setRowSelectionAllowed(true);
        sourceTable.setRowHeight(20);
        sourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourceTable.setFillsViewportHeight(true);
        sourceTable.setTableHeader(null);
        sourceTable.setGridColor(Color.white);
        sourceTable.setBackground(Color.white);

        panel.add(sourceTable);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * This is triggered when data access controller is added/removed
     */
    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForeGroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        // get the new foreground data access controller
        DataAccessController controller = (DataAccessController) evt.getNewForegroundDataSource();

        if (controller != null) {
            // set the data source browser to visible
            // todo: is this the best way
            context.setLeftControlPaneVisible(true);

            // highlight the selected foreground data source
            final int rowNum = sourceTableModel.getRowIndex(controller);

            if (SwingUtilities.isEventDispatchThread()) {
                sourceTable.changeSelection(rowNum, sourceTableModel.getColumnIndex(TableHeader.DATA_SOURCE_COLUMN), false, false);
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        sourceTable.changeSelection(rowNum, sourceTableModel.getColumnIndex(TableHeader.DATA_SOURCE_COLUMN), false, false);
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }

        // update the table with the new entries
        sourceTable.revalidate();
        sourceTable.repaint();
    }

    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchEvent(DatabaseSearchEvent evt) {
        if (SwingUtilities.isEventDispatchThread()) {
            sourceTable.clearSelection();
        } else {
            Runnable eventDispatcher = new Runnable() {
                public void run() {
                    sourceTable.clearSelection();
                }
            };
            EventQueue.invokeLater(eventDispatcher);
        }
    }

    /**
     * table column title
     */
    public enum TableHeader {
        DATA_SOURCE_COLUMN("Data Source", "Data Source"),
        CLOSE_COLUMN("Close", "Close Data Source");

        private final String header;
        private final String toolTip;

        private TableHeader(String header, String tooltip) {
            this.header = header;
            this.toolTip = tooltip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    /**
     * DataAccessTableModel tracks data sources stored in DataAccessMonitor
     * <p/>
     * It uses DataAccessMonitor as a background data model, and it also use
     * TableHeader to define the table headers.
     */
    private class DataAccessTableModel extends AbstractTableModel {

        public String getColumnName(int column) {
            return TableHeader.values()[column].getHeader();
        }

        public int getColumnIndex(TableHeader header) {
            int index = -1;
            TableHeader[] headers = TableHeader.values();
            for (int i = 0; i < headers.length; i++) {
                TableHeader tableHeader = headers[i];
                if (tableHeader.equals(header)) {
                    index = i;
                }
            }
            return index;
        }

        @Override
        public int getRowCount() {
            java.util.List<DataAccessController> controllers = context.getControllers();
            return controllers.size();
        }

        @Override
        public int getColumnCount() {
            return TableHeader.values().length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            // get all data access controllers
            java.util.List<DataAccessController> controllers = context.getControllers();

            // return data access controller if the column is data source column
            return TableHeader.values()[columnIndex].equals(TableHeader.DATA_SOURCE_COLUMN) ? controllers.get(rowIndex).getName() : null;
        }

        public int getRowIndex(Object controller) {
            // get all data access controllers
            java.util.List<DataAccessController> controllers = context.getControllers();
            return controllers.indexOf(controller);
        }
    }

    /**
     * DataAccessTableCellRender draw a icon in front of each data access controller
     * Depending on the type of the data access controller:
     * <p/>
     * For database based controller, it is a database icon
     * <p/>
     * For file based controller, it is a file icon
     */
    private class DataAccessTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // get the original component
            final JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // get the current data access controller
            DataAccessController controller = context.getControllers().get(row);
            // get its content categories
            Collection<DataAccessController.ContentCategory> categories = controller.getContentCategories();

            // get the icon depending on the type of the data access controller
            ImageIcon icon = null;
            DataAccessController.Type type = controller.getType();
            if (DataAccessController.Type.XML_FILE.equals(type)) {
                icon = GUIUtilities.loadImageIcon(context.getProperty(categories.isEmpty() ? "file.source.loading.small.icon" : "file.source.small.icon"));
            } else if (DataAccessController.Type.DATABASE.equals(type)) {
                icon = GUIUtilities.loadImageIcon(context.getProperty(categories.isEmpty() ? "database.source.loading.small.icon" : "database.source.small.icon"));
            }

            // set the icon
            cell.setIcon(icon);

            if (icon != null && icon.getImageObserver() == null) {
                icon.setImageObserver(new CellImageObserver(table));
            }

            return cell;
        }
    }

    private static class CellImageObserver implements ImageObserver {
        private JTable table;

        private CellImageObserver(JTable table) {
            this.table = table;
        }

        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
                //Rectangle rect = table.getCellRect(row, col, false);
                table.revalidate();
                table.repaint();
            }
            return (infoflags & (ABORT | ALLBITS)) == 0;
        }
    }

    /**
     * Draw a red cross for close the data access controller
     */
    private class CloseDataSourceCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // get the original component
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // get the icon
            Icon icon = GUIUtilities.loadIcon(context.getProperty("close.individual.source.enable.icon.small"));

            // set the icon
            cell.setIcon(icon);

            // overwrite the background changing behavior when selected
            cell.setBackground(Color.white);

            // set the component to none focusable
            cell.setFocusable(false);

            return cell;
        }
    }

    /**
     * DataAccessSelectionListener is triggered when a selection has been made on a data source
     */
    private class DataAccessSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            // get row number and column number
            int row = sourceTable.getSelectedRow();
            if (row >= 0) {
                int col = sourceTable.getSelectedColumn();

                // get column name
                String colName = sourceTable.getColumnName(col);
                DataAccessController controller = context.getControllers().get(row);
                if (colName.equals(TableHeader.DATA_SOURCE_COLUMN.getHeader()) && !context.isForegroundDataAccessController(controller)) {
                    // close foreground data access controller
                    logger.debug("Set foreground data access controller: {}", controller.getName());
                    context.setForegroundDataAccessController(controller);
                }
            } else {
                context.setForegroundDataAccessController(null);
            }
        }
    }

    /**
     * CloseDataSourceMouseListener  is triggered when a close action has been made on a data source
     */
    private class CloseDataSourceMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // get row number and column number
            int row = sourceTable.rowAtPoint(new Point(e.getX(), e.getY()));
            int col = sourceTable.columnAtPoint(new Point(e.getX(), e.getY()));

            // get column name
            String colName = sourceTable.getColumnName(col);
            if (colName.equals(TableHeader.CLOSE_COLUMN.getHeader())) {
                // remove the data access controller from data access monitor
                java.util.List<DataAccessController> controllers = context.getControllers();
                if (row >= 0 && row < controllers.size()) {
                    DataAccessController controller = controllers.get(row);
                    context.removeDataAccessController(controller, true);
                }
            }
        }
    }
}
