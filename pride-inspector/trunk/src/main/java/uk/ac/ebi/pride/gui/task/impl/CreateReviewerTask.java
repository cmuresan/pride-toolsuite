package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.curation.exception.CurationException;
import uk.ac.ebi.pride.curation.impl.Pride2CuratorImpl;
import uk.ac.ebi.pride.curation.model.Reviewer;
import uk.ac.ebi.pride.data.io.db.PooledConnectionFactory;
import uk.ac.ebi.pride.gui.component.message.Message;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.task.Task;

import java.sql.Connection;

/**
 * Task to create a reviewer account
 * <p/>
 * User: rwang
 * Date: 25-Nov-2010
 * Time: 14:44:50
 */
public class CreateReviewerTask extends Task<Message, Message> {
    private int fromAcc, toAcc;

    public CreateReviewerTask(int fromAcc, int toAcc) {
        String msg = "Making experiment public: " + fromAcc + "~" + toAcc;
        this.setName(msg);
        this.setDescription(msg);
        this.fromAcc = fromAcc;
        this.toAcc = toAcc;
    }

    @Override
    protected Message doInBackground() throws Exception {
        // get a new database connection
        Connection connection = PooledConnectionFactory.getConnection();

        //Reviewer
        Reviewer reviewer = null;

        try {
            // create a curator
            Pride2CuratorImpl curator = new Pride2CuratorImpl(connection);
            String[] arr = new String[toAcc - fromAcc + 1];
            int index = 0;
            for (int i = fromAcc; i <= toAcc; i++) {
                arr[index] = i + "";
                index++;
            }

            if (verifyAccessions(curator, arr)) {
                reviewer = curator.createReviewerAccount(arr);
            } else {
                publish(new Message(MessageType.ERROR, "Accession range contains invalid experiment accessions"));
            }
        } catch (CurationException ex) {
            publish(new Message(MessageType.ERROR, "Failed to create a reviewer account for " + fromAcc + "~" + toAcc));
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

        return new Message(reviewer == null ? MessageType.ERROR : MessageType.INFO, reviewer == null ? ("Failed to create a reviewer account for " + fromAcc + "~" + toAcc) : getOuputMessage(reviewer));
    }

    /**
     * Return false if one accession does not exist.
     *
     * @param curator   Pride2CuratorImpl
     * @param accs an array of accessions
     * @return boolean true means all accessions do exist.
     * @throws uk.ac.ebi.pride.curation.exception.CurationException error while checking the existence of accessions
     */
    private boolean verifyAccessions(Pride2CuratorImpl curator, String[] accs) throws CurationException {
        for (String acc : accs) {
            if (!curator.hasExperiment(acc)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Construct a output message from a given reviewer
     *
     * @param reviewer reviewer object
     * @return String  output message
     */
    private String getOuputMessage(Reviewer reviewer) {
        StringBuilder msg = new StringBuilder();
        msg.append("<html><b>New reviewer account created for ");
        msg.append(fromAcc);
        msg.append("~");
        msg.append(toAcc);
        msg.append("</b><br>");
        msg.append("User Name: ");
        msg.append(reviewer.getUserName());
        msg.append("<br>");
        msg.append("Password: ");
        msg.append(reviewer.getPassword());
        msg.append("<br></html>");
        return msg.toString();
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
