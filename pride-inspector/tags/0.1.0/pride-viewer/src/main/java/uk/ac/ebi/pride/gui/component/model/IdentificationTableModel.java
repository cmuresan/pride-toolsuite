package uk.ac.ebi.pride.gui.component.model;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.TwoDimIdentification;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:04
 */
public class IdentificationTableModel extends DynamicDataTableModel<Identification> {


    /** table column title */
    public enum TableHeader {
        
        PROTEIN_ACCESSION_COLUMN ("Protein Accession", String.class),
        PROTEIN_ACCESSION_VERSION_COLUMN ("Protein Accession Version", String.class),
        IDENTIFICATION_TYPE_COLUMN ("Type", String.class),
        IDENTIFICATION_SCORE_COLUMN ("Score", Double.class),
        IDENTIFICATION_THRESHOLD_COLUMN("Threshold", Double.class),
        SEQUENCE_COVERAGE_COLUMN ("Sequence Coverage", Double.class),
        SEARCH_DATABASE_COLUMN ("Search Database", String.class),
        SEARCH_DATABASE_VERSION_COLUMN ("Search Database Version", String.class);

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
        List<Object> content = new ArrayList<Object>();

        // Protein Accession
        content.add(newData.getAccession());
        // Protein Accession Version
        content.add(newData.getAccessionVersion());
        // Type
        content.add((newData instanceof TwoDimIdentification) ?
                        DataAccessController.TWO_DIM_IDENTIFICATION_TYPE :
                        DataAccessController.GEL_FREE_IDENTIFICATION_TYPE);
        // Score
        content.add(newData.getScore());
        // Threshold
        content.add(newData.getThreshold());
        // Sequence Coverage
        content.add(newData.getSequenceConverage());
        // Search Database
        content.add(newData.getSearchDatabase());
        // Search Database Version
        content.add(newData.getSearchDatabaseVerison());
        
        contents.add(content);
    }
}
