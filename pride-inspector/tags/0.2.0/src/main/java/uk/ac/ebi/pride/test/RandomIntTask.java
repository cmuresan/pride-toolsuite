package uk.ac.ebi.pride.test;

import uk.ac.ebi.pride.gui.task.Task;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Aug-2010
 * Time: 10:31:06
 */
public class RandomIntTask extends Task<Void, String> {
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

    @Override
    protected Void doInBackground() throws Exception {
        int i = 0;
        int loop = 100;
        for (int j = 0; j < loop; j++) {
            i++;
            publish(i + "");
            Thread.sleep(5000);
        }
        return null;
    }
}
