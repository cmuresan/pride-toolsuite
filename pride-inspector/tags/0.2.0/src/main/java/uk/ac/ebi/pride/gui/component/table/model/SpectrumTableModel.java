package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.core.Spectrum;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * User: rwang
 * Date: 12-Apr-2010
 * Time: 15:39:59
 */
public class SpectrumTableModel extends ProgressiveUpdateTableModel<Void, Spectrum> {

    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("", Integer.class),
        SPECTRUM_ID_COLUMN("Spectrum ID", Comparable.class),
        MZ_LEVEL_COLUMN("MS level", Integer.class),
        PRECURSOR_CHARGE_COLUMN("Precursor Charge", Integer.class),
        PRECURSOR_MZ_COLUMN("Precursor m/z", Double.class),
        PRECURSOR_INTENSITY_COLUMN("Precursor Intensity", Double.class),
        SUM_OF_INTENSITY_COLUMN("Sum of Intensity", Double.class),
        NUMBER_OF_PEAKS_COLUMN("Number of peaks", Integer.class);

        private final String header;
        private final Class headerClass;

        private TableHeader(String header, Class classType) {
            this.header = header;
            this.headerClass = classType;
        }

        public String getHeader() {
            return header;
        }

        public Class getHeaderClass() {
            return headerClass;
        }
    }

    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getHeaderClass());
        }
    }

    @Override
    public void addData(Spectrum newData) {
        List<Object> content = new ArrayList<Object>();
        content.add(this.getRowCount() + 1);
        // spectrum id
        content.add(newData.getId());
        //ms level
        content.add(newData.getMsLevel());
        // precursor charge
        content.add(newData.getPrecursorCharge());
        DecimalFormat formatter = new DecimalFormat("#.####");
        // precursor m/z
        content.add(formatter.format(newData.getPrecursorMz()));
        // precursor intensity
        content.add(formatter.format(newData.getPrecursorIntensity()));
        // sum of intensity
        content.add(formatter.format(newData.getSumOfIntensity()));
        // Number of peaks
        content.add(newData.getNumberOfPeaks());

        this.addRow(content);
    }
}
