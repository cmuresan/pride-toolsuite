package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.core.*;

import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:15
 */
public class PeptideTableModel extends UpdateTableModel<Identification> {

        /** table column title */
    public enum TableHeader {

        PEPTIDE_SEQUENCE_COLUMN ("Sequence", String.class),
        PEPTIDE_PTM_COLUMN ("PTM", Peptide.class),
        PEPTIDE_SEQUENCE_LENGTH_COLUMN ("Sequence Length", Integer.class),
        SEQUENCE_START_COLUMN ("Sequence Start Position", Integer.class),
        SEQUENCE_END_COLUMN ("Sequence Stop Position", Integer.class),
        SPECTRUM_REFERENCE_COLUMN("Spectrum Reference", Double.class),
        NUMBER_OF_FRAGMENT_IONS ("Number Of Fragment Ions", Integer.class);

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
    public void addData(Identification newData) {
        List<Peptide> peptides = newData.getPeptides();
        this.removeAllRows();
        for(Peptide peptide : peptides) {
            List<Object> content = new ArrayList<Object>();
            // Sequence
            content.add(peptide.getSequence());
            // ptm
            content.add(peptide);
            // Sequence length
            content.add(peptide.getSequenceLength());
            // Start
            content.add(peptide.getStart());
            // End
            content.add(peptide.getEnd());
            // Spectrum reference
            Spectrum spectrum = peptide.getSpectrum();
            if (spectrum != null) {
                content.add(spectrum.getId());
            } else {
                content.add(null);
            }
            // number of fragment ions
            int numOfFragIons = 0;
            List<FragmentIon> fragmentIons = peptide.getFragmentIons();
            if (fragmentIons !=  null) {
                numOfFragIons = fragmentIons.size();
            }
            content.add(numOfFragIons);
            this.addRow(content);
        }
    }
}
