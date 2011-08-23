package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.tools.protein_details_fetcher.ProteinDetailFetcher;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.*;

/**
 * Retrieve protein name for a given set of proteins
 * <p/>
 * User: rwang
 * Date: 16-Sep-2010
 * Time: 15:53:16
 */
public class RetrieveProteinDetailTask extends TaskAdapter<Void, Tuple<TableContentType, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveProteinDetailTask.class);

    private static final String DEFAULT_TASK_NAME = "Downloading protein details";

    private static final String DEFAULT_TASK_DESC = "Downloading protein details using web services";

    /**
     * a collection of protein accessions to be retrieved
     */
    private Collection<String> proteinAccessions;

    /**
     * Fetcher to download protein details
     */
    private ProteinDetailFetcher fetcher;


    /**
     * Constructor
     *
     * @param proteinAccs a collection of protein accessions
     */
    public RetrieveProteinDetailTask(Collection<String> proteinAccs) {
        if (proteinAccs == null) {
            throw new IllegalArgumentException("Protein accession collection can not be null");
        }

        // set name and description
        this.setName(DEFAULT_TASK_NAME);
        this.setDescription(DEFAULT_TASK_DESC);

        this.proteinAccessions = new HashSet<String>(proteinAccs);

        this.fetcher = new ProteinDetailFetcher();
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (!proteinAccessions.isEmpty()) {
            showLoadingMessages();

            // accession buffer
            Set<String> accBuffer = new HashSet<String>();

            // buffer size
            int maxBufferSize = 20;

            // protein map
            Map<String, Protein> proteins = new HashMap<String, Protein>();

            try {
                // iterate over each protein
                for (String acc : proteinAccessions) {
                    if (acc == null) {
                        continue;
                    }
                    // get existing protein details
                    Protein protDetails = PrideInspectorCacheManager.getInstance().getProteinDetails(acc);
                    if (protDetails != null) {
                        proteins.put(acc, protDetails);
                        continue;
                    }

                    accBuffer.add(acc);
                    if (accBuffer.size() == maxBufferSize) {
                        // fetch and publish protein details
                        fetchAndPublish(accBuffer, proteins);

                        // clear protein map
                        proteins = new HashMap<String, Protein>();
                    }

                    // this is important for cancelling
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }

                if (!accBuffer.isEmpty()) {
                    fetchAndPublish(accBuffer, proteins);
                }
            } catch (InterruptedException e) {
                logger.warn("Protein name download has been cancelled");
            }
        }

        return null;
    }


    /**
     * Show loading messages
     */
    private void showLoadingMessages() {
        // display loading for all the rows first
        Map<String, Protein> tempProteinDetailsMap = new HashMap<String, Protein>();
        for (String acc : proteinAccessions) {
            Protein tempProteinDetails = new Protein(acc);
            tempProteinDetails.setName("Loading...");
            tempProteinDetailsMap.put(acc, tempProteinDetails);
            tempProteinDetails.setStatus(Protein.STATUS.UNKNOWN);
        }
        publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_DETAILS, tempProteinDetailsMap));
    }


    /**
     * Fetch then publish
     *
     * @param accs  protein accessions
     * @param proteins  protein map
     * @throws Exception    exception while fetching the protein
     */
    private void fetchAndPublish(Set<String> accs, Map<String, Protein> proteins) throws Exception {
        // fetch protein details
        Map<String, Protein> results = fetcher.getProteinDetails(accs);
        // add results to cache
        PrideInspectorCacheManager.getInstance().addProteinDetails(results.values());
        // add results to protein map
        proteins.putAll(results);
        // public results
        publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_DETAILS, proteins));
        // clear accession buffer
        accs.clear();
    }
}
