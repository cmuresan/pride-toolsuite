package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class GetMyAssayFilesMetadataTask extends GetMyProjectFilesMetadataTask {

    public GetMyAssayFilesMetadataTask(String userName, char[] password, String projectAccession) {
        super(userName, password, projectAccession);
    }

    @Override
    protected String getFileDownloadUrl() {
        DesktopContext context = PrideInspector.getInstance().getDesktopContext();
        return context.getProperty("prider.assay.file.metadata.url");
    }
}
