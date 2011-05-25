package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.component.SharedLabels;
import uk.ac.ebi.pride.gui.component.table.TableRowDataRetriever;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Sep-2010
 * Time: 17:21:07
 */
public class ExportIdentificationDescTask extends AbstractDataAccessTask<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(ExportIdentificationDescTask.class);
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Identification Descriptions";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Identification Descriptions";


    /**
     * output File
     */
    private String outputFilePath;

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller     DataAccessController
     * @param outputFilePath file path to output the result.
     */
    public ExportIdentificationDescTask(DataAccessController controller, String outputFilePath) {
        super(controller);
        this.outputFilePath = outputFilePath;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void runDataAccess() throws Exception {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(new File(outputFilePath)));
            writer.println("Submitted Protein Accession" + SharedLabels.TAB + "Mapped Protein Accession" + SharedLabels.TAB + "Protein Name" + SharedLabels.TAB +
                            "Score" + SharedLabels.TAB + "Threshold" + SharedLabels.TAB + "Number of peptides" + SharedLabels.TAB +
                            "Number of distinct peptides" + SharedLabels.TAB + "Number of PTMs");
            Collection<Comparable> identIds = controller.getIdentificationIds();
            for (Comparable identId : identIds) {
                // a row of data
                List<Object> content = TableRowDataRetriever.getIdentificationTableRow(controller, identId);

                // output the result
                // identification id is ignored
                for (int i = 0; i < content.size() - 1; i++) {
                    Object entry = content.get(i);
                    writer.print(entry == null ? "" : entry.toString());
                    writer.print(SharedLabels.TAB);
                }

                // line break
                writer.print(SharedLabels.LINE_SEPARATOR);

                // this is important for cancelling
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            }
            writer.flush();
        } catch (DataAccessException e2) {
            String msg = "Failed to retrieve data from data source";
            logger.error(msg, e2);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } catch (IOException e1) {
            String msg = "Failed to write data to the output file, please check you have the right permission";
            logger.error(msg, e1);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } catch (InterruptedException e3) {
            logger.warn("Exporting identification description has been interrupted");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}
