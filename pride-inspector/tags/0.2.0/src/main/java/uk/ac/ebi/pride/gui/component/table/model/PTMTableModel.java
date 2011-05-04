package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 11:27:12
 */
public class PTMTableModel extends ProgressiveUpdateTableModel<Void, Peptide> {
    /**
     * table column title
     */
    public enum TableHeader {

        PTM_ACCESSION("Accession", String.class),
        PTM_NAME("Name", String.class),
        PTM_LOCATION("Location", String.class),
        PTM_MODIFIED_RESIDUE("Modified Residue", String.class),
        PTM_MONO_MASS_DELTA("Monoisotopic Mass Delta", Double.class),
        PTM_AVG_MASS_DELTA("Average Mass Delta", Double.class),
        PTM_DATABASE("PTM Database",String.class),
        PTM_DATABASE_VERSION("PTM Database",String.class);

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
    public void addData(Peptide peptide) {
        String sequence = peptide.getSequence();
        int seqLength = sequence.length();
        List<Modification> mods = peptide.getModifications();
        if (mods != null) {
            for (Modification mod : mods) {
                List<Object> content = new ArrayList<Object>();
                // accession
                content.add(mod.getAccession());
                // name
                content.add(mod.getName());
                // location
                int location = mod.getLocation();
                if (location == 0) {
                    location = 1;
                    content.add("N Terminal");
                } else if (location == seqLength) {
                    content.add("C Terminal");
                } else {
                    content.add(location + "");
                }
                // modified residue
                content.add(sequence.charAt(location - 1));
                // mono mass
                List<Double> monoMasses = mod.getMonoMassDeltas();
                if (monoMasses != null && !monoMasses.isEmpty()) {
                    content.add(monoMasses.get(0));
                } else {
                    content.add(null);
                }
                // average mass
                List<Double> avgMasses = mod.getAvgMassDeltas();
                if (avgMasses != null && !avgMasses.isEmpty()) {
                    content.add(avgMasses.get(0));
                } else {
                    content.add(null);
                }
                // ptm database
                content.add(mod.getModDatabase());
                // ptm database version
                content.add(mod.getModDatabaseVersion());
                contents.add(content);
            }
        }
    }
}
