package uk.ac.ebi.pride.gui.component.table.listener;

import uk.ac.ebi.pride.gui.url.HttpUtilities;
import uk.ac.ebi.pride.gui.url.HyperLinkGenerator;
import uk.ac.ebi.pride.gui.component.table.model.ListTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
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
    private String clickHeader;
    private String linkedHeader;
    private HyperLinkGenerator urlGen;
    private Pattern pattern;

    public HyperLinkCellMouseClickListener(JTable table, String clickHeader,
                                           HyperLinkGenerator generator) {
        this(table, clickHeader, generator, null);
    }

    public HyperLinkCellMouseClickListener(JTable table, String clickHeader,
                                           HyperLinkGenerator generator, Pattern pattern) {
        this(table, clickHeader, clickHeader, generator, pattern);
    }

    public HyperLinkCellMouseClickListener(JTable table, String clickHeader, String linkedHeader,
                                           HyperLinkGenerator generator, Pattern pattern) {
        this.table = table;
        this.clickHeader = clickHeader;
        this.linkedHeader = linkedHeader;
        this.urlGen = generator;
        this.pattern = pattern;
    }

    private int getColumnIndex(String header, TableModel tableModel) {
        int index = -1;

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            if (tableModel.getColumnName(i).equals(header)) {
                index = i;
                break;
            }
        }

        return index;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
        String header = table.getColumnName(col);
        if (header.equals(clickHeader)) {
            int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
            TableModel tableModel = table.getModel();

            Object val = null;
            if (clickHeader.equals(linkedHeader)) {
                val = tableModel.getValueAt(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(col));
            } else {
                val = tableModel.getValueAt(table.convertRowIndexToModel(row), getColumnIndex(linkedHeader, tableModel));
            }

            if (val != null) {
                String text = val.toString();
                Set<String> urlList = new HashSet<String>();

                if (pattern != null) {
                    Matcher m = pattern.matcher(text);

                    while (m.find()) {
                        urlList.add(m.group());
                    }
                }

                for (String url : urlList) {
                    url = urlGen == null ? url : urlGen.generate(url);
                    if (url != null) {
                        HttpUtilities.openURL(url);
                    }
                }
            }
        }
    }
}