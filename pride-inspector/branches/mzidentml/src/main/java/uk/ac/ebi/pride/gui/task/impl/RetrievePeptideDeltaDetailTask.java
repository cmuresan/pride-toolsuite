package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.gui.component.sequence.PeptideAnnotation;
import uk.ac.ebi.pride.gui.component.sequence.PeptideFitState;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.mol.MoleculeUtilities;
import uk.ac.ebi.pride.tools.protein_details_fetcher.ProteinDetailFetcher;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;
import uk.ac.ebi.pride.util.NumberUtilities;

import javax.swing.*;
import java.util.*;

/**
 * This class Retrieve the Details of Delta Mass for Peptide Column.
 * User: yperez
 * Date: 5/23/13
 * Time: 3:10 PM
 */

public class RetrievePeptideDeltaDetailTask extends TaskAdapter<Void, Tuple<TableContentType, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(RetrievePeptideDeltaDetailTask.class);

    private static final String DEFAULT_TASK_NAME = "Retrieve Peptide Delta details";

    private static final String DEFAULT_TASK_DESC = "Retrieve Peptide Delta Details";

    /**
     * The number of proteins for each batch download
     */
    private static final int MAX_BATCH_DOWNLOAD_SIZE = 10;

    /**
     * data access controller
     */
    private DataAccessController controller;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public RetrievePeptideDeltaDetailTask(DataAccessController controller) {

        // set name and description
        this.setName(DEFAULT_TASK_NAME);
        this.setDescription(DEFAULT_TASK_DESC);

        this.controller = controller;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // protein identification id
        Collection<Comparable> protIdentIds = controller.getProteinIds();


        //protein identification id and accession buffer
        Map<Comparable, String> accBuffer = new LinkedHashMap<Comparable, String>();

        // protein map
        Map<String, Protein> proteins = new HashMap<String, Protein>();

        Map<Tuple<Comparable, Comparable>, Double> peptideFits = new HashMap<Tuple<Comparable, Comparable>, Double>();

        // iterate over each protein
        for (Comparable protIdentId : protIdentIds) {
            Collection<Comparable> peptideIdentIds = controller.getPeptideIds(protIdentId);
            for (Comparable peptideId : peptideIdentIds) {
                Double delta = computeDeltaMz(peptideId, protIdentId);
                peptideFits.put(new Tuple<Comparable, Comparable>(protIdentId, peptideId), delta);
            }
        }
        publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE_DELTA, peptideFits));
        return null;
    }

    private Double computeDeltaMz(Comparable peptideId, Comparable identId) {
        Double deltaMass = null;

        List<Modification> mods = new ArrayList<Modification>(controller.getPTMs(identId, peptideId));
        String sequence = controller.getPeptideSequence(identId, peptideId);

        Integer charge = controller.getPeptidePrecursorCharge(identId, peptideId);
        Comparable specId = controller.getPeptideSpectrumId(identId, peptideId);

        if (charge == null && specId != null) {
            charge = controller.getSpectrumPrecursorCharge(specId);
            if (charge == null || charge == 0) {
                charge = null;
            }
        }

        if (specId != null) {
            double mz = controller.getSpectrumPrecursorMz(specId);
            List<Double> ptmMasses = new ArrayList<Double>();
            for (Modification mod : mods) {
                List<Double> monoMasses = mod.getMonoisotopicMassDelta();
                if (monoMasses != null && !monoMasses.isEmpty()) {
                    ptmMasses.add(monoMasses.get(0));
                }
            }
            deltaMass = MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);
        }
        return deltaMass;
    }

}
