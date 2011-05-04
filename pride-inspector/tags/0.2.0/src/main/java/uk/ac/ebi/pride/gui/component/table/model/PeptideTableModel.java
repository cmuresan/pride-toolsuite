package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.gui.component.AnnotationUtil;
import uk.ac.ebi.pride.mzgraph.chart.data.residue.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:15
 */
public class PeptideTableModel extends ProgressiveUpdateTableModel<Void, Identification> {

    /** table column title */
    public enum TableHeader {
        ROW_NUMBER_COLUMN ("", Integer.class),
        PEPTIDE_PTM_COLUMN ("Peptide Sequence", Peptide.class),
        PEPTIDE_PTM_NUMBER_COLUMN ("Number of PTMs", Integer.class),
        PEPTIDE_SEQUENCE_LENGTH_COLUMN ("Sequence Length", Integer.class),
        PEPTIDE_THEORETICAL_MASS ("Theoretical Peptide Mass", Double.class),
        PRECURSOR_MASS ("Precursor Mass", Double.class),
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
    public void initializeTableModel() {
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
            // add row number
            content.add(this.getRowCount() + 1);
            // peptide sequence
            content.add(peptide);
            // peptide ptms
            int ptmCnt = 0;
            List<Modification> mods = peptide.getModifications();
            if (mods != null) {
                ptmCnt = mods.size();
            }
            content.add(ptmCnt);
            // Sequence length
            content.add(peptide.getSequenceLength());
            // theoretical mass
            uk.ac.ebi.pride.mzgraph.chart.data.residue.Peptide p = AnnotationUtil.getPeptideFromString(peptide.getSequence());
            List<uk.ac.ebi.pride.mzgraph.chart.data.residue.Modification> ms = AnnotationUtil.convertModifications(mods);
            double mass = ResidueHelper.calculatePeptideMonoMass(p, ms, NeutralLoss.WATER_LOSS.getMonoMass());
            content.add(mass);
            // precursor mass
            Spectrum spectrum = peptide.getSpectrum();
            if (spectrum != null) {
                int charge = spectrum.getPrecursorCharge();
                content.add(spectrum.getPrecursorMz()*charge - charge* NuclearParticle.PROTON.getMonoMass());
            } else {
                content.add(null);
            }
            // Start
            content.add(peptide.getStart());
            // End
            content.add(peptide.getEnd());
            // Spectrum reference
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
