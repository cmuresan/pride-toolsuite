package uk.ac.ebi.pride.gui.component.quant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.QuantitativeSample;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.table.TableFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 15/08/2011
 * Time: 11:34
 */
public class QuantSamplePane extends DataAccessControllerPane {

    private static final Logger logger = LoggerFactory.getLogger(QuantSamplePane.class);

    private JTable sampleDetailTable;

    public QuantSamplePane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    /**
     * Add the rest of components
     */
    @Override
    protected void addComponents() {
        // create identification table
        QuantitativeSample sample = null;
        try {
            sample = controller.getQuantSample();
            sampleDetailTable = TableFactory.createQuantSampleTable(sample);
        } catch (DataAccessException e) {
            logger.error("Fail to get quantitative sample");
        }

        // createAttributedSequence header panel
        JPanel headerPanel = buildHeaderPane();
        this.add(headerPanel, BorderLayout.NORTH);


        // add identification table to scroll pane
        JScrollPane scrollPane = new JScrollPane(sampleDetailTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * This builds the top panel to display, it includes
     *
     * @return JPanel  header panel
     */
    private JPanel buildHeaderPane() {
        // add meta data panel
        JPanel metaDataPanel = buildMetaDataPane();
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(metaDataPanel, BorderLayout.WEST);

        return titlePanel;
    }

    /**
     * Build meta data pane, this panel displays the identification type, search engine and search database
     *
     * @return JPanel   meta data pane
     */
    private JPanel buildMetaDataPane() {
        // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        // protein table label
        JLabel tableLabel = new JLabel("<html><b>Sample</b></html>");
        metaDataPanel.add(tableLabel);

        return metaDataPanel;
    }
}
