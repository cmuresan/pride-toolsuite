package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.gui.component.sequence.PeptideFitState;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Task to check whether peptide sequence fit the given protein sequences
 * <p/>
 * User: rwang
 * Date: 24/06/11
 * Time: 11:01
 */
public class RetrievePeptideFitTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, Map<Tuple<Comparable, Comparable>, Integer>>> {
    private Collection<Comparable> identIds;

    public RetrievePeptideFitTask(Collection<Comparable> identIds, DataAccessController controller) {
        super(controller);
        this.identIds = identIds;
    }

    @Override
    protected Void retrieve() throws Exception {
        Map<Tuple<Comparable, Comparable>, Integer> results = new HashMap<Tuple<Comparable, Comparable>, Integer>();

        for (Comparable identId : identIds) {
            Collection<Comparable> peptideIds = controller.getPeptideIds(identId);
            if (peptideIds != null) {
                for (Comparable peptideId : peptideIds) {
                    Integer state = PrideInspectorCacheManager.getInstance().getPeptideFitState(controller.getUid(), identId, peptideId);
                    if (state == null) {
                        // Original Protein Accession
                        String protAcc = controller.getProteinAccession(identId);
                        String protAccVersion = controller.getProteinAccessionVersion(identId);
                        String database = controller.getSearchDatabase(identId);

                        // Mapped Protein Accession
                        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database);
                        String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;

                        // get protein details
                        Protein protein = PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
                        if (protein != null) {
                            protein = new AnnotatedProtein(protein);
                        }

                        // get peptide sequence
                        String sequence = controller.getPeptideSequence(identId, peptideId);

                        // start and end position
                        int start = controller.getPeptideSequenceStart(identId, peptideId);
                        int end = controller.getPeptideSequenceEnd(identId, peptideId);

                        // peptide present
                        if (protein == null || sequence == null || protein.getSequenceString() == null) {
                            state = PeptideFitState.UNKNOWN;
                        } else {
                            if (protein.hasSubSequenceString(sequence, start, end)) {
                                state = PeptideFitState.STRICT_FIT;
                            } else if (protein.hasSubSequenceString(sequence)) {
                                state = PeptideFitState.FIT;
                            } else {
                                state = PeptideFitState.NOT_FIT;
                            }
                        }
                        PrideInspectorCacheManager.getInstance().addPeptideFitState(controller.getUid(), identId, peptideId, state);
                    }
                    results.put(new Tuple<Comparable, Comparable>(identId, peptideId), state);
                }
            }
        }

        publish(new Tuple<TableContentType, Map<Tuple<Comparable, Comparable>, Integer>>(TableContentType.PEPTIDE_FIT, results));

        return null;
    }
}
