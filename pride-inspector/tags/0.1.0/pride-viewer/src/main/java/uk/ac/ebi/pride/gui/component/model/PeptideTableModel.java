package uk.ac.ebi.pride.gui.component.model;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.Spectrum;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

        PEPTIDE_ID_COLUMN ("Peptide ID", String.class),
        PEPTIDE_SEQUENCE_COLUMN ("Sequence", String.class),
        PEPTIDE_SEQUENCE_LENGTH_COLUMN ("Sequence Length", Integer.class),
        SEQUENCE_START_COLUMN ("Start", Integer.class),
        SEQUENCE_END_COLUMN ("End", Integer.class),
        SPECTRUM_REFERENCE_COLUMN("Spectrum Reference", Double.class);

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
            // Peptide ID
            content.add(peptide.getId());
            // Sequence
            content.add(peptide.getSequence());
            // Sequence
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
            this.addRow(content);
        }
    }
}
