package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;

import java.net.HttpURLConnection;
import java.util.*;

/**
 * Get the metadata of a proteomexchange submission
 *
 * @author Rui Wang
 * @version $Id$
 */
public class GetPxSubmissionDetailTask extends AbstractConnectPrideTask {
    private static final Logger logger = LoggerFactory.getLogger(GetPrideExperimentDetailTask.class);

    private String user;
    private String password;
    private Set<Comparable> accessions;

    public GetPxSubmissionDetailTask(String user, String password) {
        this(user, password, null);
    }

    public GetPxSubmissionDetailTask(String user, String password, Collection<Comparable> accessions) {
        this.user = user;
        this.password = password;
        if (accessions != null) {
            this.accessions = new HashSet<Comparable>();
            this.accessions.addAll(accessions);
        }

        String msg = "Download ProteomeXchange Submission";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {

        // create a http connection
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        HttpURLConnection connection = connect(context.getProperty("px.submission.download.url"));

        List<Map<String, String>> metadata = null;

        try {
            if (connection != null) {
                // login for meta data
                initMetaDataDownload(connection, accessions, user, password);

                // download experiment meta data
                metadata = downloadMetaData(connection);

                // this is important for cancelling
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            }
        } catch (InterruptedException ex) {
            logger.warn("Download session has been cancelled");
        }

        return metadata;
    }
}