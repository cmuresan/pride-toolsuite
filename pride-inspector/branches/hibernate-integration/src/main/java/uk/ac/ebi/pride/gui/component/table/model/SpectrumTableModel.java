package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.core.Spectrum;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * User: rwang
 * Date: 12-Apr-2010
 * Time: 15:39:59
 */
public class SpectrumTableModel extends DynamicDataTableModel<Spectrum> {

   /** table column title */
    public enum TableHeader {

        SPECTRUM_ID_COLUMN ("Spectrum ID", String.class),
        NUMBER_OF_PEAKS_COLUMN ("Number of peaks", Integer.class),
        MZ_RANGE_START_COLUMN ("m/z Range Start", Double.class),
        MZ_RANGE_END_COLUMN ("m/z Range End", Double.class),
        INTENSITY_RANGE_START_COLUMN ("Intensity Range Start", Double.class),
        INTENSITY_RANGE_END_COLUMN ("Intensity Range End", Double.class);

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
    public void initializeTable() {
        TableHeader[] headers = TableHeader.values();
        for(TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getHeaderClass());
        }
    }

    @Override
    public void addData(Spectrum newData) {
        List<Object> content = new ArrayList<Object>();
        // spectrum id
        content.add(newData.getId());
        // Number of peaks
        content.add(newData.getNumberOfPeaks());
        // ToDo: m/z level
        // m/z range start
        content.add(newData.getMzRangeStart());
        // m/z range end
        content.add(newData.getMzRangeEnd());
        // Intensity range start
        content.add(newData.getIntentRangeStart());
        // Intensity range end
        content.add(newData.getIntentRangeEnd());

        this.addRow(content);
    }
}
