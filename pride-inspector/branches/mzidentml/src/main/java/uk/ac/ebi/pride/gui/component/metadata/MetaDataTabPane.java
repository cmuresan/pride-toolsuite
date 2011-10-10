package uk.ac.ebi.pride.gui.component.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.impl.RetrieveMetaDataTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.EDTUtils;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * MetaDataTabPane displays all the meta data shared across the data source/experiment.
 * It listens to the following property change event:
 * <p/>
 * User: rwang
 * Date: 05-Mar-2010
 * Time: 15:12:07
 */
public class MetaDataTabPane extends DataAccessControllerPane<ExperimentMetaData, Void> implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(MetaDataTabPane.class);

    private static final String GENERAL = "Experiment General";
    private static final String SAMPLE_PROTOCOL = "Sample & Protocol";
    private static final String INSTRUMENT_SOFTWARE = "Instrument & Processing";

    private static final String PANE_TITLE = "Overview";

    private JPanel metaDataTopPanel;
    private JPanel metaDataContainer;
    private JPanel metaDataControlBar;
    private JPanel generalMetadataPanel;
    private JPanel sampleProtocolMetadataPanel;
    private JPanel instrumentProcMetadataPanel;
    private PrideInspectorContext context;

    public MetaDataTabPane(DataAccessController controller, JComponent component) {
        super(controller, component);
    }

    @Override
    protected void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.setTitle(PANE_TITLE);

        // set the final icon
        context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        this.setIcon(GUIUtilities.loadIcon(context.getProperty("general.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(context.getProperty("general.tab.loading.icon.small")));
    }

    @Override
    public void populate() {
        // start retrieving meta data
        retrieveMetaData();
    }

    @Override
    public void started(TaskEvent event) {
        showIcon(getLoadingIcon());
    }

    @Override
    public void succeed(TaskEvent<ExperimentMetaData> metaDataTaskEvent) {
        final ExperimentMetaData metaData = metaDataTaskEvent.getValue();

        Runnable run = new Runnable() {

            @Override
            public void run() {
                // init container
                createContainer();

                // create meta data panels
                createMetaDataPanels(metaData);

                // tool bar
                createTopPanel();

                // add to scroll pane
                JScrollPane scrollPane = new JScrollPane(metaDataContainer,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                MetaDataTabPane.this.add(scrollPane, BorderLayout.CENTER);

                // set vertical scroll bar's speed
                scrollPane.getVerticalScrollBar().setUnitIncrement(100);
            }
        };

        try {
            EDTUtils.invokeAndWait(run);
        } catch (InvocationTargetException e) {
            String msg = "Failed to create meta data panels";
            logger.error(msg, e);

        } catch (InterruptedException e) {
            String msg = "Failed to create meta data panels";
            logger.error(msg, e);
        }
    }

    // called when a background task finished to reset the icon
    @Override
    public void finished(TaskEvent<Void> event) {
        showIcon(getIcon());
    }

    /**
     * Show a different icon if the parent component is not null and an instance of DataContentDisplayPane
     *
     * @param icon icon to show
     */
    private void showIcon(Icon icon) {
        if (parentComponent != null && parentComponent instanceof ControllerContentPane && icon != null) {
            ControllerContentPane contentPane = (ControllerContentPane) parentComponent;
            contentPane.setTabIcon(contentPane.getMetaDataTabIndex(), icon);
        }
    }

    private void retrieveMetaData() {
        RetrieveMetaDataTask retrieveTask = new RetrieveMetaDataTask(controller);

        // register task listener
        retrieveTask.addTaskListener(this);

        // start the task
        retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));

        context.addTask(retrieveTask);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        metaDataContainer.removeAll();
        metaDataContainer.add(metaDataTopPanel, BorderLayout.NORTH);
        if (GENERAL.equals(cmd)) {
            metaDataContainer.add(generalMetadataPanel, BorderLayout.CENTER);
        } else if (SAMPLE_PROTOCOL.equals(cmd)) {
            metaDataContainer.add(sampleProtocolMetadataPanel, BorderLayout.CENTER);
        } else if (INSTRUMENT_SOFTWARE.equals(cmd)) {
            metaDataContainer.add(instrumentProcMetadataPanel, BorderLayout.CENTER);
        }
        metaDataContainer.revalidate();
        metaDataContainer.repaint();
    }

    private void createContainer() {
        metaDataContainer = new JPanel();
        metaDataContainer.setLayout(new BorderLayout());
        metaDataContainer.setBackground(Color.white);
    }

    private void createTopPanel() {
        metaDataTopPanel = new JPanel(new BorderLayout());
        metaDataTopPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));


        // create tool bar
        createToolbar();
        metaDataTopPanel.add(metaDataControlBar, BorderLayout.CENTER);

        // create help button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.white);
        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.general");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));

        buttonPanel.add(helpButton);
        metaDataTopPanel.add(buttonPanel, BorderLayout.EAST);

        metaDataContainer.add(metaDataTopPanel, BorderLayout.NORTH);
    }


    private void createToolbar() {
        metaDataControlBar = new JPanel();
        metaDataControlBar.setBackground(Color.white);
        metaDataControlBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup buttonGroup = new ButtonGroup();
        JToggleButton generalButton = new JToggleButton(GENERAL);
        generalButton.setActionCommand(GENERAL);
        generalButton.setPreferredSize(new Dimension(180, 25));
        JToggleButton proSamButton = new JToggleButton(SAMPLE_PROTOCOL);
        proSamButton.setActionCommand(SAMPLE_PROTOCOL);
        proSamButton.setPreferredSize(new Dimension(180, 25));
        JToggleButton insSofButton = new JToggleButton(INSTRUMENT_SOFTWARE);
        insSofButton.setActionCommand(INSTRUMENT_SOFTWARE);
        insSofButton.setPreferredSize(new Dimension(180, 25));
        generalButton.addActionListener(this);
        proSamButton.addActionListener(this);
        insSofButton.addActionListener(this);
        buttonGroup.add(generalButton);
        buttonGroup.add(proSamButton);
        buttonGroup.add(insSofButton);
        metaDataControlBar.add(generalButton);
        metaDataControlBar.add(proSamButton);
        metaDataControlBar.add(insSofButton);

        // set default selection
        generalButton.setSelected(true);
    }

    private void createMetaDataPanels(ExperimentMetaData metaData) {
        generalMetadataPanel = new GeneralMetadataPanel(metaData);
        sampleProtocolMetadataPanel = new SampleProtocolMetadataPanel(metaData);
        instrumentProcMetadataPanel = new InstrumentProcessingMetadataPanel(metaData);
        // set default panel
        metaDataContainer.add(generalMetadataPanel, BorderLayout.CENTER);
    }

}