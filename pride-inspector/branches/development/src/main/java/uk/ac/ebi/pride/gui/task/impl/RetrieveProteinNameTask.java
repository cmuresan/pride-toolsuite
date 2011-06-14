package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.sequence.Protein;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.utils.ProteinNameFetcher;
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
public class RetrieveProteinNameTask extends TaskAdapter<Void, Tuple<TableContentType, List<Object>>> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveProteinNameTask.class);

    private static final String DEFAULT_TASK_NAME = "Downloading protein names";

    private static final String DEFAULT_TASK_DESC = "Downloading protein names using web services";

    /**
     * default protein name when the name is unknown
     */
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
    public RetrieveProteinNameTask(Collection<String> proteinAccs) {
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
                Tuple<String, String> proteinName = new Tuple<String, String>(acc, "Loading...");
                List<Object> content = new ArrayList<Object>();
                content.add(proteinName);
                publish(new Tuple<TableContentType, List<Object>>(TableContentType.PROTEIN_NAME, content));
            }

            // fetcher will query web service to get protein name
            ProteinNameFetcher fetcher = new ProteinNameFetcher();

            try {
                // iterate over each protein
                for (String acc : proteinAccessions) {
                    if (acc == null) {
                        continue;
                    }
                    // get protein name
                    String name = context.getProteinName(acc);
                    if (name == null) {
                        name = UNKNOWN_PROTEIN_NAME;
                        if (hasInternet) {
                            try {
                            	Protein p = fetcher.getProteinDetails(acc);
                                name = (p != null) ? p.getName() : UNKNOWN_PROTEIN_NAME;
                            } catch (Exception e) {
                                // ignored
                            }
                        }
                        // store the checked protein name
                        context.addProteinName(acc, name);
                    }

                    // publish the result
                    Tuple<String, String> proteinName = new Tuple<String, String>(acc, name);
                    List<Object> content = new ArrayList<Object>();
                    content.add(proteinName);
                    publish(new Tuple<TableContentType, List<Object>>(TableContentType.PROTEIN_NAME, content));

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
