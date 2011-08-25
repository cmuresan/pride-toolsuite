package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * User: rwang
 * Date: 12-Apr-2010
 * Time: 15:39:59
 */
public class SpectrumTableModel extends ProgressiveListTableModel<Void, Tuple<TableContentType, List<Object>>> {

    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("#", "Row Number"),
        SPECTRUM_ID_COLUMN("Spectrum ID", "Spectrum ID"),
        MZ_LEVEL_COLUMN("MS level", "MS Level"),
        IDENTIFIED_COLUMN("Identified", "Peptide Identified"),
        PRECURSOR_CHARGE_COLUMN("Precursor Charge", "Precursor Charge"),
        PRECURSOR_MZ_COLUMN("Precursor m/z", "Precursor m/z"),
        PRECURSOR_INTENSITY_COLUMN("Precursor Intensity", "Precursor Intensity"),
        SUM_OF_INTENSITY_COLUMN("Sum of Intensity", "Sum of Intensity"),
        NUMBER_OF_PEAKS_COLUMN("# Peaks", "Number of Peaks");

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

    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(Tuple<TableContentType, List<Object>> newData) {
        // check the content type
        TableContentType type = newData.getKey();
        int rowCnt = this.getRowCount();
        if (TableContentType.SPECTRUM.equals(type)) {
            List<Object> content = new ArrayList<Object>();
            content.add(this.getRowCount() + 1);
            content.addAll(newData.getValue());
            addRow(content);
            fireTableRowsInserted(rowCnt, rowCnt);
        }
    }
}
