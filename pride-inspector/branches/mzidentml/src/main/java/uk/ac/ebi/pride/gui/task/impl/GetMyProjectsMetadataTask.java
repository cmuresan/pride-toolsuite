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
import uk.ac.ebi.pride.prider.webservice.project.model.ProjectDetailList;

/**
 * GetPrideUserDetailTask retrieves pride user details using pride web service
 *
 * @author Rui Wang
 * @version $Id$
 */
public class GetMyProjectsMetadataTask extends Task<ProjectDetailList, String> {

    private RestTemplate restTemplate;

    /**
     * Constructor
     *
     * @param userName pride user name
     * @param password pride password
     */
    public GetMyProjectsMetadataTask(String userName, char[] password) {
        HttpClient client = new HttpClient();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, new String(password));
        client.getState().setCredentials(AuthScope.ANY, credentials);
        CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);
        this.restTemplate = new RestTemplate(commons);
    }

    @Override
    protected ProjectDetailList doInBackground() throws Exception {
        DesktopContext context = PrideInspector.getInstance().getDesktopContext();
        String projectMetadataUrl = context.getProperty("prider.my.projects.metadata.url");

        try {
            return restTemplate.getForObject(projectMetadataUrl, ProjectDetailList.class);
        } catch (RestClientException ex) {
            publish("Failed to login, please check your username and password");
            return null;
        }
    }

    @Override
    protected void finished() {
    }

    @Override
    protected void succeed(ProjectDetailList results) {
    }

    @Override
    protected void cancelled() {
    }

    @Override
    protected void interrupted(InterruptedException iex) {
    }
}

