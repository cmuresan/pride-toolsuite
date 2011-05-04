package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.curation.exception.CurationException;
import uk.ac.ebi.pride.curation.impl.Pride2CuratorImpl;
import uk.ac.ebi.pride.data.io.db.PooledConnectionFactory;
import uk.ac.ebi.pride.gui.component.message.Message;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.task.Task;

import java.sql.Connection;
import java.util.Date;

/**
 * Task to make a range of private pride experiment public
 * <p/>
 * User: rwang
 * Date: 25-Nov-2010
 * Time: 12:06:22
 */
public class MakeExperimentPublicTask extends Task<Message, Message> {

    private int fromAcc, toAcc;
    private Date date;

    public MakeExperimentPublicTask(int fromAcc, int toAcc, Date date) {
        String msg = "Making experiment public: " + fromAcc + "~" + toAcc;
        this.setName(msg);
        this.setDescription(msg);
        this.fromAcc = fromAcc;
        this.toAcc = toAcc;
        this.date = new Date(date.getTime());
    }

    @Override
    protected Message doInBackground() throws Exception {
        // get a new database connection
        Connection connection = PooledConnectionFactory.getConnection();

        try {
            // create a curator
            Pride2CuratorImpl curator = new Pride2CuratorImpl(connection);
            for (int i = fromAcc; i <= toAcc; i++) {
                try {
                    if (!curator.isExperimentPublic(i + "")) {
                        curator.makeExperimentPublic(i + "", date);
                        publish(new Message(MessageType.INFO, "Experiment " + i + " is now public"));
                    } else {
                        publish(new Message(MessageType.INFO, "Experiment " + i + " is already public"));    
                    }
                } catch (CurationException ex) {
                    publish(new Message(MessageType.ERROR, "Failed to make experiment " + i + " public"));
                }
            }
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }

        return new Message(MessageType.INFO, "Update has finished");
    }

    @Override
    protected void finished() {
    }

    @Override
    protected void succeed(Message results) {
    }

    @Override
    protected void cancelled() {
    }

    @Override
    protected void interrupted(InterruptedException iex) {
    }
}
