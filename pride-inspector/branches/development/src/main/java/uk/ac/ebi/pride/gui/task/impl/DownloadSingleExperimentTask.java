package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.EDTUtils;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Download one experiment from PRIDE
 *
 * User: rwang
 * Date: 14/09/2011
 * Time: 14:23
 */
public class DownloadSingleExperimentTask extends AbstractConnectPrideTask {
    private static final Logger logger = LoggerFactory.getLogger(DownloadSingleExperimentTask.class);

    /**
     * PRIDE experiment accession
     */
    private Comparable accession;
    /**
     * Output file
     */
    private File output;
    /**
     * User name
     */
    private String user;
    /**
     * Password
     */
    private String password;

    public DownloadSingleExperimentTask(Comparable accession, File output, String user, String password) {
        this.accession = accession;
        this.output = output;
        this.user = user;
        this.password = password;
        String msg = "Download PRIDE Experiment";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {
        // connect
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        // initialize meta data download
        String url = buildPrideMetaDataDownloadURL(context.getProperty("pride.experiment.download.url"), Arrays.asList(accession), user, password);
        // download meta data
        List<Map<String, String>> metaData = downloadMetaData(url);
        // get file size
        if (metaData != null && metaData.size() == 1) {
            String sizeStr = metaData.get(0).get("Size");
            double size = Double.parseDouble(sizeStr) * 1024 * 1024;
            // init download
            url = buildExperimentDownloadURL(context.getProperty("pride.experiment.download.url"), accession, user, password);
            // download
            downloadFile(url, output, size);
            // show download finished
            showDoneMessage();

        } else {
            logger.error("Failed to retrieve experiment metadata from PRIDE");
        }

        return null;
    }

    private void showDoneMessage() {
        Runnable code = new Runnable() {

            @Override
            public void run() {
                GUIUtilities.message(Desktop.getInstance().getMainComponent(), "DONE: PRIDE Experiment " + accession, "Download Finished");
            }
        };
        EDTUtils.invokeLater(code);
    }
}
