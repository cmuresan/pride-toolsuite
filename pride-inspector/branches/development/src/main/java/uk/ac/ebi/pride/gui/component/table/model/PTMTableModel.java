package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;

import java.util.ArrayList;
import java.util.List;

/**
 * PTMTableModel contains all
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 11:27:12
 */
public class PTMTableModel extends ProgressiveListTableModel<Void, Peptide> {
    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("#", "Row Number"),
        PTM_ACCESSION("Accession", "PTM Accession"),
        PTM_NAME("Name", "PTM Name"),
        PTM_LOCATION("Location", "Modified location"),
        PTM_MODIFIED_RESIDUE("Modified Residue", "Modified Residue"),
        PTM_MONO_MASS_DELTA("Monoisotopic Mass Delta", "Monoisotopic Mass Delta"),
        PTM_AVG_MASS_DELTA("Average Mass Delta", "Average Mass Delta"),
        PTM_DATABASE("PTM Database", "PTM Database"),
        PTM_DATABASE_VERSION("PTM Database Version", "PTM Database Version");

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
    public void addData(Peptide peptide) {

        String sequence = peptide.getSequence();
        int seqLength = sequence.length();
        List<Modification> mods = peptide.getModifications();
        if (mods != null) {
            for (Modification mod : mods) {
                List<Object> content = new ArrayList<Object>();
                // row number
                content.add(this.getRowCount() + 1);
                // accession
                content.add(mod.getAccession());
                // name
                content.add(mod.getName());
                // location
                int location = mod.getLocation();
                if (location == 0) {
                    location = 1;
                    content.add("N Terminal");
                } else if (location == seqLength + 1) {
                    content.add("C Terminal");
                } else if (location == -1) {
                    content.add(null);
                } else {
                    content.add(location + "");
                }
                // modified residue
                if (sequence.length() >= location && location > 0) {
                    content.add(sequence.charAt(location - 1));
                } else {
                    content.add(null);
                }
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
