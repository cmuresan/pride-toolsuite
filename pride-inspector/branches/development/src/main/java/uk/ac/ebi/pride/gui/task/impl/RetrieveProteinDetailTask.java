package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.sequence.Protein;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.utils.ProteinDetailFetcher;
import uk.ac.ebi.pride.util.InternetChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Retrieve protein name for a given set of proteins
 * <p/>
 * User: rwang
 * Date: 16-Sep-2010
 * Time: 15:53:16
 */
public class RetrieveProteinDetailTask extends TaskAdapter<Void, Tuple<TableContentType, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveProteinDetailTask.class);

    private static final String DEFAULT_TASK_NAME = "Downloading protein names";

    private static final String DEFAULT_TASK_DESC = "Downloading protein names using web services";

    private static final String UNKNOWN_PROTEIN_NAME = "Not Available";

    /**
     * a collection of protein accessions to be retrieved
     */
    private Collection<String> proteinAccessions;

    /**
     * whether it has internet
     */
    private boolean hasInternet;

    /**
     * Reference to pride inspector context
     */
    private PrideInspectorContext context;

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

        this.proteinAccessions = proteinAccs;

        // check whether the internet is available
        this.hasInternet = InternetChecker.check();

        // store a reference to desktop context
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (!proteinAccessions.isEmpty()) {
            // display loading for all the rows first
            for (String acc : proteinAccessions) {
                Protein tempProteinDetails = new Protein(acc);
                tempProteinDetails.setName("Loading...");
                publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_DETAILS, tempProteinDetails));
            }

            // fetcher will query web service to get protein name
            ProteinDetailFetcher fetcher = new ProteinDetailFetcher();

            try {
                // iterate over each protein
                for (String acc : proteinAccessions) {
                    if (acc == null) {
                        continue;
                    }
                    // get protein name
                    Protein protDetails = context.getProteinDetails(acc);
                    if (protDetails == null && hasInternet) {
                        try {
                            protDetails = fetcher.getProteinDetails(acc);
                        } catch (Exception e) {
                            // ignored
                        }

                        // store the checked protein name
                        if (protDetails != null) {
                            context.addProteinDetails(protDetails);
                        }
                    }

                    // publish the result
                    if (protDetails == null) {
                        protDetails = new Protein(acc);
                        protDetails.setName(UNKNOWN_PROTEIN_NAME);
                    }

                    publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_DETAILS, protDetails));

                    // this is important for cancelling
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Protein name download has been cancelled");
            }
        }

        return null;
    }
}
