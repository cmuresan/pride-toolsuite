package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.gui.component.sequence.PeptideAnnotation;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;

import java.util.*;

/**
 * Retrieve protein sequence coverages for protein identifications
 * <p/>
 * User: rwang
 * Date: 23/06/11
 * Time: 11:36
 */
public class RetrieveSequenceCoverageTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, Object>> {
    private List<Comparable> identIds;

    public RetrieveSequenceCoverageTask(List<Comparable> identIds, DataAccessController controller) {
        super(controller);
        this.identIds = new ArrayList<Comparable>(identIds);
    }

    @Override
    protected Void retrieve() throws Exception {
        Map<Comparable, Double> coverageMap = new HashMap<Comparable, Double>();

        for (Comparable identId : identIds) {
            PrideInspectorCacheManager cacheManager = PrideInspectorCacheManager.getInstance();
            Double coverage = cacheManager.getSequenceCoverage(controller.getUid(), identId);
            if (coverage == null) {
                // get mapped protein accession
                String accession = controller.getProteinAccession(identId);
                String accessionVersion = controller.getProteinAccessionVersion(identId);
                String database = controller.getSearchDatabase(identId);

                // Mapped Protein Accession
                AccessionResolver resolver = new AccessionResolver(accession, accessionVersion, database);
                String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;

                // get protein details
                Protein protein = PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
                if (protein != null) {
                    AnnotatedProtein annotatedProtein = new AnnotatedProtein(protein);
                    Collection<Comparable> peptideIds = controller.getPeptideIds(identId);
                    for (Comparable peptideId : peptideIds) {
                        PeptideAnnotation peptide = new PeptideAnnotation();
                        peptide.setSequence(controller.getPeptideSequence(identId, peptideId));
                        peptide.setStart(controller.getPeptideSequenceStart(identId, peptideId));
                        peptide.setEnd(controller.getPeptideSequenceEnd(identId, peptideId));
                        annotatedProtein.addAnnotation(peptide);
                    }
                    coverage = annotatedProtein.getSequenceCoverage();
                    coverageMap.put(identId, coverage);
                    // cache the sequence coverage
                    PrideInspectorCacheManager.getInstance().addSequenceCoverage(controller.getUid(), identId, coverage);
                }
            } else {
                coverageMap.put(identId, coverage);
            }
        }

        // publish the results
        publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_SEQUENCE_COVERAGE, coverageMap));

        return null;
    }
}
