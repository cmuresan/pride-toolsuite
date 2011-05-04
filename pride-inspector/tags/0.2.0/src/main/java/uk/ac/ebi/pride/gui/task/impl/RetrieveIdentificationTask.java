package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

import java.util.Arrays;
import java.util.Collection;

/**
 * Retrieve all the Identifications from DataAccessController
 * Running as a background task
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 16:09:12
 */
public class RetrieveIdentificationTask extends TaskAdapter<Void, Identification> {
    /**
     * the size of each read iteration, for example: return every 100 spectra
     */
    private static final int DEFAULT_CACHE_SIZE = 100;
    /**
     * Data access controller
     */
    private DataAccessController controller = null;
    private int offset;
    private int start;
    private boolean isOneIter = false;


    public RetrieveIdentificationTask(DataAccessController controller) {
        this.controller = controller;
        String title = "Loading Identifications";
        this.setName(title);
        this.setDescription(title);
        this.offset = DEFAULT_CACHE_SIZE;
    }

    public RetrieveIdentificationTask(DataAccessController controller, int start, int max_size) {
        this.controller = controller;
        String title = "Loading Identifications";
        this.setName(title);
        this.setDescription(title);
        this.offset = max_size;
        this.start = start;
        this.isOneIter = true;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // Two dimensional identification
        readAndPublish();
        return null;
    }

    /**
     * Read Identifications from data source, and publish these Identifications
     *
     * @throws Exception
     */
    private <T extends Identification> void readAndPublish() throws Exception {
        // get a list of ids
        Collection<Identification> identifications = controller.getIdentificationsByIndex(start, offset);

        // iterate over all the ids
        Identification[] cachedIdent = new Identification[offset];
        int count = 0;
        if (identifications != null) {
            for (Identification ident : identifications) {
                cachedIdent[count] = ident;
                count++;
                if ((count % offset) == 0) {
                    this.publish(cachedIdent);
                    cachedIdent = new Identification[offset];
                    count = 0;
                    if (isOneIter) {
                        break;
                    }
                }
            }

            // finish unfinished business here
            if (count > 0) {
                Identification[] subArr = Arrays.copyOfRange(cachedIdent, 0, count);
                this.publish(subArr);
            }
        }
    }
}
