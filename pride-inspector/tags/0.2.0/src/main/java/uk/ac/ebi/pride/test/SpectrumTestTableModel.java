package uk.ac.ebi.pride.test;

import uk.ac.ebi.pride.data.core.Spectrum;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 02-Aug-2010
 * Time: 08:56:30
 */
public class SpectrumTestTableModel extends TestTableModel<Spectrum>{

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

    public SpectrumTestTableModel() {
        TableHeader[] headers = TableHeader.values();
        for(TableHeader header : headers) {
            this.addColumn(header.getHeader());
        }
    }

    @Override
    protected void addData(Spectrum data) {
        Object[] content = new Object[3];
        // spectrum id
        content[0] = data.getId();
        // Number of peaks
        content[1] = data.getNumberOfPeaks();
        // ToDo: m/z level
        // m/z range start

        this.addRow(content);
    }
}
