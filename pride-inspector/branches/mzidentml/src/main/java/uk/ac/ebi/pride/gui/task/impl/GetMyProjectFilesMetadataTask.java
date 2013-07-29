package uk.ac.ebi.pride.gui.task.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.prider.webservice.file.model.FileDetailList;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class GetMyProjectFilesMetadataTask extends Task<FileDetailList, String> {
    private String accession;
    private RestTemplate restTemplate;

    /**
     * Constructor
     *
     * @param userName pride user name
     * @param password pride password
     */
    public GetMyProjectFilesMetadataTask(String userName, char[] password, String projectAccession) {
        this.accession = projectAccession;

        initRestTemplate(userName, password);
    }

    private void initRestTemplate(String userName, char[] password) {
        HttpClient client = new HttpClient();

        if (userName != null && password != null) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, new String(password));
            client.getState().setCredentials(AuthScope.ANY, credentials);
        }

        CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);
        this.restTemplate = new RestTemplate(commons);
    }

    @Override
    protected FileDetailList doInBackground() throws Exception {
        String fileDownloadUrl = getFileDownloadUrl();

        try {
            return restTemplate.getForObject(fileDownloadUrl, FileDetailList.class, accession);
        } catch (RestClientException ex) {
            publish("Failed to retrieve file details for project " + accession);
            throw ex;
        }
    }

    protected String getFileDownloadUrl() {
        DesktopContext context = PrideInspector.getInstance().getDesktopContext();
        return context.getProperty("prider.project.file.metadata.url");
    }

    @Override
    protected void finished() {
    }

    @Override
    protected void succeed(FileDetailList results) {
    }

    @Override
    protected void cancelled() {
    }

    @Override
    protected void interrupted(InterruptedException iex) {
    }
}