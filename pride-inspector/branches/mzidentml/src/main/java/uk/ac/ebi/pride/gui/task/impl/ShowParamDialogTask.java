package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.gui.EDTUtils;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.dialog.ParamGroupDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

/**
 * Task to show a dialog which shows all the params in a param group
 *
 * User: rwang
 * Date: 16/09/2011
 * Time: 16:36
 */
public class ShowParamDialogTask extends TaskAdapter<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(ShowParamDialogTask.class);

    private static final String TASK_NAME = "Showing additional parameters";
    private static final String TASK_DESCRIPTION = "Showing additional parameters";


    /**
     * data access controller to pride public instance
     */
    private DataAccessController controller = null;

    /**
     * Protein identification id
     */
    private Comparable protId;

    /**
     * Peptide identification id
     */
    private Comparable peptideId;

    /**
     * Reference to PRIDE context
     */
    PrideInspectorContext context;

    /**
     * Open a connection to pride database
     *
     * @param controller data access controller
     * @param protId     protein identification id
     * @param peptideId  peptide identification id
     */
    public ShowParamDialogTask(DataAccessController controller, Comparable protId, Comparable peptideId) {
        this.setName(TASK_NAME);
        this.setDescription(TASK_DESCRIPTION);

        this.controller = controller;
        this.protId = protId;
        this.peptideId = peptideId;

        context = ((PrideInspectorContext) Desktop.getInstance().getDesktopContext());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Void doInBackground() throws Exception {
        final ParamGroup params;
        final String title;
        if (peptideId == null) {
            Identification protIdent = controller.getIdentificationById(protId);
            title = "Additional Protein Parameters: " + protIdent.getId().toString();
            params = protIdent;
        } else {
            Peptide peptide = controller.getPeptideById(protId, peptideId);
            title = "Additional Peptide Parameters: " + peptide.getSequence();
            params = peptide;
        }

        Runnable code = new Runnable() {

            @Override
            public void run() {
                ParamGroupDialog dialog = new ParamGroupDialog(Desktop.getInstance().getMainComponent(), title, params);
                dialog.setVisible(true);
            }
        };
        EDTUtils.invokeAndWait(code);


        return null;
    }
}
