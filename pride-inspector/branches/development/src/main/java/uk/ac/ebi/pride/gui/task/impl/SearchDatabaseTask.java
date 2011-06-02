package uk.ac.ebi.pride.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.gui.search.SearchEntry;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.util.IOUtilities;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * Task to search database entries
 * <p/>
 * User: rwang
 * Date: 31/05/11
 * Time: 15:27
 */
public class SearchDatabaseTask extends TaskAdapter<Void, List<List<Object>>> {
    private SearchEntry entry;

    public SearchDatabaseTask() {
        this(null);
    }

    public SearchDatabaseTask(SearchEntry entry) {
        this.entry = entry;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // start the search
        EventBus.publish(new DatabaseSearchEvent<Void>(DatabaseSearchEvent.Status.START));

        // read database summary file
        readDatabaseSummaryFile();

        // complete the search
        EventBus.publish(new DatabaseSearchEvent<Void>(DatabaseSearchEvent.Status.COMPLETE));
        return null;
    }

    /**
     * Read and filter summary the file
     */
    private void readDatabaseSummaryFile() {
        // to filter
        boolean toFilter = (entry != null && entry.getTerm() != null && !"".equals(entry.getTerm().trim()));

        URL pathURL = IOUtilities.getFullPath(SearchDatabaseTask.class, "config/database.tsv");
        File file = IOUtilities.convertURLToFile(pathURL);
        // input stream of the database file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            // get header

            // get content

        } catch (IOException ex) {

        }
    }
}
