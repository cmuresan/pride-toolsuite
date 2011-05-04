package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.GelFreeIdentification;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.TwoDimIdentification;
import uk.ac.ebi.pride.gui.task.Task;

import java.util.Arrays;
import java.util.Collection;

/**
 * Retrieve all the Identifications from DataAccessController
 * Running as a background task
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 16:09:12
 */
public class RetrieveIdentificationTask extends Task<Void, Identification> {
    /** the size of each read iteration, for example: return every 100 spectra */
    private static final int CACHE_SIZE = 100;
    /** Data access controller */
    private DataAccessController controller = null;

    public RetrieveIdentificationTask(DataAccessController controller) {
        this.controller = controller;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // Two dimensional identification
        readAndPublish(TwoDimIdentification.class);
        // Gel Free identification
        readAndPublish(GelFreeIdentification.class);

        return null;
    }

    /**
     * Read Identifications from data source, and publish these Identifications
     * @param cl
     * @throws Exception
     */
    private <T extends Identification> void readAndPublish(Class<T> cl) throws Exception {
        // get a list of ids
        boolean isTwoDim = cl.equals(TwoDimIdentification.class);
        Collection<Comparable> ids = isTwoDim ? controller.getTwoDimIdentIds() : controller.getGelFreeIdentIds();
        // iterate over all the ids
        Identification[] cachedIdent = new Identification[CACHE_SIZE];
        int count = 0;
        if (ids != null) {
            for(Comparable id : ids) {
                Identification ident = isTwoDim ? controller.getTwoDimIdentById(id) : controller.getGelFreeIdentById(id);
                cachedIdent[count] = ident;
                count++;
                if ((count % CACHE_SIZE) == 0) {
                    this.publish(cachedIdent);
                    cachedIdent = new Identification[CACHE_SIZE];
                    count = 0;
                }
            }

            // finish unfinished business here
            if (count > 0) {
                Identification[] subArr = Arrays.copyOfRange(cachedIdent, 0, count -1);
                this.publish(subArr);
            }
        }
    }

    @Override
    protected void finished() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void succeed(Void results) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void cancelled() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void interrupted(InterruptedException iex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
