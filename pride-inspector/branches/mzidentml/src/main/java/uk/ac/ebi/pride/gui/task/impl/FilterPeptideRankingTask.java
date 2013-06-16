package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.gui.component.table.model.PeptideTreeTableModel;
import uk.ac.ebi.pride.gui.task.Task;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class FilterPeptideRankingTask extends Task<Void, Void> {

    private PeptideTreeTableModel peptideTreeTableModel;
    private int rankingThreshold;

    public FilterPeptideRankingTask(PeptideTreeTableModel peptideTreeTableModel, int rankingThreshold) {
        this.peptideTreeTableModel = peptideTreeTableModel;
        this.rankingThreshold = rankingThreshold;
    }

    @Override
    protected Void doInBackground() throws Exception {
        peptideTreeTableModel.setRankingThreshold(rankingThreshold);
        return null;
    }

    @Override
    protected void finished() {
    }

    @Override
    protected void succeed(Void results) {
    }

    @Override
    protected void cancelled() {
    }

    @Override
    protected void interrupted(InterruptedException iex) {
    }
}
