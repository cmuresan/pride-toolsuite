package uk.ac.ebi.pride.gui.component.table.listener;

import uk.ac.ebi.pride.gui.url.HttpUtilities;
import uk.ac.ebi.pride.gui.url.HyperLinkGenerator;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mouse listener for clicking hyper link
 * <p/>
 * User: rwang
 * Date: 10-Sep-2010
 * Time: 17:05:09
 */
public class HyperLinkCellMouseClickListener extends MouseAdapter {

    private JTable table;
    private String columnHeader;
    private HyperLinkGenerator urlGen;
    private Pattern pattern;

    public HyperLinkCellMouseClickListener(JTable table, String columnHeader,
                                           HyperLinkGenerator generator) {
        this(table, columnHeader, generator, null);
    }

    public HyperLinkCellMouseClickListener(JTable table, String columnHeader,
                                           HyperLinkGenerator generator, Pattern pattern) {
        this.table = table;
        this.columnHeader = columnHeader;
        this.urlGen = generator;
        this.pattern = pattern;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
        String header = table.getColumnName(col);
        if (header.equals(columnHeader)) {
            int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
            TableModel tableModel = table.getModel();
            Object val = tableModel.getValueAt(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(col));
            if (val != null) {
                String text = val.toString();
                boolean match = true;
                if (pattern != null) {
                    Matcher m = pattern.matcher(text);
                    match = m.matches();
                }
                if (match) {
                    String url = urlGen  == null ? text : urlGen.generate(text);
                    if (url != null) {
                        HttpUtilities.openURL(url);
                    }
                }
            }
        }
    }
}
