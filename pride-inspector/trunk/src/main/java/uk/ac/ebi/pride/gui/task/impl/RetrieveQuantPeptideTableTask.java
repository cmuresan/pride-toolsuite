package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.table.TableDataRetriever;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Task to retrieve all the peptide related quantitative information per protein identification
 * User: rwang
 * Date: 18/08/2011
 * Time: 11:09
 */
public class RetrieveQuantPeptideTableTask extends TaskAdapter<Void, Tuple<TableContentType, List<Object>>> {

    private static final String DEFAULT_TASK_NAME = "Updating Peptide Table";

    private static final String DEFAULT_TASK_DESC = "Updating Peptide Table";

    private DataAccessController controller;
    private Comparable identId;
    private int referenceSampleIndex;

    public RetrieveQuantPeptideTableTask(DataAccessController controller, Comparable identId, int referenceSampleIndex) {
        this.setName(DEFAULT_TASK_NAME);
        this.setDescription(DEFAULT_TASK_DESC);
        this.controller = controller;
        this.identId = identId;
        this.referenceSampleIndex = referenceSampleIndex;
    }

    @Override
    protected Void doInBackground() throws Exception {

        // get new headers
        // protein quantitative table header
        List<Object> peptideQuantHeaders = TableDataRetriever.getPeptideQuantTableHeaders(controller, referenceSampleIndex);
        publish(new Tuple<TableContentType, List<Object>>(TableContentType.PEPTIDE_QUANTITATION_HEADER, peptideQuantHeaders));

        // get all the peptide ids
        Collection<Comparable> peptideIds = controller.getPeptideIds(identId);
        for (Comparable peptideId : peptideIds) {
            // get each row
            List<Object> allQuantContent = new ArrayList<Object>();
            // get and publish protein related details
            List<Object> peptideContent = TableDataRetriever.getPeptideTableRow(controller, identId, peptideId);
            allQuantContent.addAll(peptideContent);
            // get and publish quantitative data
            List<Object> peptideQuantContent = TableDataRetriever.getPeptideQuantTableRow(controller, identId, peptideId, referenceSampleIndex);
            allQuantContent.addAll(peptideQuantContent);

            publish(new Tuple<TableContentType, List<Object>>(TableContentType.PEPTIDE_QUANTITATION, allQuantContent));
        }

        // check for interruption
        checkInterruption();

        return null;
    }

    private void checkInterruption() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}