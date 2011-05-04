package uk.ac.ebi.pride.gui.component.ident;

import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.gui.component.OpenFileDialog;
import uk.ac.ebi.pride.gui.component.table.listener.HyperlinkCellMouseListener;
import uk.ac.ebi.pride.gui.component.table.model.PTMTableModel;
import uk.ac.ebi.pride.gui.component.table.renderer.HyperLinkCellRenderer;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 11:10:51
 */
public class PTMDialog extends JDialog implements ActionListener {
    private final Peptide peptide;
    private JTable peptideTable;

    private final static String EXPORT_ACTION = "Export";
    private final static String CLOSE_ACTION = "Close";

    private final static String PSI_MOD_HYPERLINK_PREFIX = "http://www.ebi.ac.uk/ontology-lookup/?termId=";

    public PTMDialog(Frame owner, Peptide peptide) {
        super(owner);
        this.peptide = peptide;
        this.setTitle("PTM");
        this.setMinimumSize(new Dimension(500, 300));
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addComponents();
    }

    private void addComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        // modification label
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("<html><p>");
        strBuffer.append("<font size='3'><b>Peptide</b>:");
        strBuffer.append(peptide.getSequence());
        strBuffer.append("</font><br></p></html>");

        JLabel label = new JLabel(strBuffer.toString());
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(labelPanel, BorderLayout.NORTH);

        // modification table
        peptideTable = new JTable();
        PTMTableModel tableModel = new PTMTableModel();
        tableModel.addData(peptide);
        peptideTable.setModel(tableModel);
        peptideTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peptideTable.setFillsViewportHeight(true);
        // add hyper link click listener
        int modAccColumnNumber = tableModel.getColumnIndex(PTMTableModel.TableHeader.PTM_ACCESSION.getHeader());
        DesktopContext context = uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();
        String urlPrefix = propMgr.getProperty("ptm.url.prefix");
        String urlEndfix = propMgr.getProperty("ptm.url.endfix");
        peptideTable.addMouseListener(new HyperlinkCellMouseListener(peptideTable, modAccColumnNumber, urlPrefix, urlEndfix));
        // ptm accession hyperlink
        TableColumn peptideColumn = peptideTable.getColumn(PTMTableModel.TableHeader.PTM_ACCESSION.getHeader());
        peptideColumn.setCellRenderer(new HyperLinkCellRenderer(PSI_MOD_HYPERLINK_PREFIX, ""));
        JScrollPane scrollPane = new JScrollPane(peptideTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        // button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Export");
        exportButton.setActionCommand(EXPORT_ACTION);
        exportButton.addActionListener(this);
        JButton closeButton = new JButton("Close");
        closeButton.setActionCommand(CLOSE_ACTION);
        closeButton.addActionListener(this);
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        this.setContentPane(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (EXPORT_ACTION.equals(command)) {
            OpenFileDialog ofd = new OpenFileDialog(System.getProperty("user.dir"), "Select Path Save To");
            ofd.setFileSelectionMode(JFileChooser.FILES_ONLY);
            ofd.setMultiSelectionEnabled(false);
            int result = ofd.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File path = ofd.getSelectedFile();
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(new FileWriter(path));
                    TableModel tableModel = peptideTable.getModel();
                    int rowCnt = tableModel.getRowCount();
                    int colCnt = tableModel.getColumnCount();
                    for (int i = 0; i < rowCnt; i++) {
                        for (int j = 0; j < colCnt; j++) {
                            Object val = tableModel.getValueAt(i, j);
                            writer.print(val !=  null ? val.toString() : "");
                            if (j < colCnt -1) {
                                writer.print("\t");
                            }
                        }
                        writer.println();
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        } else if (CLOSE_ACTION.equals(command)) {
            this.dispose();
        }
    }
}
