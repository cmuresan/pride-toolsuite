package uk.ac.ebi.pride.gui.component.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartException;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessController.ContentCategory;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.component.utils.Iconable;
import uk.ac.ebi.pride.gui.component.PrideInspectorLoadingPanel;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.impl.LoadChartDataTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Class to create a TabPane for integrating the PRIDE-Chart into PRIDE-Inspector.</p>
 *
 * @author Antonio Fabregat
 *         Date: 10-ago-2010
 *         Time: 14:10:41
 */
public class ChartTabPane extends DataAccessControllerPane<List<PrideChartManager>, String>
        implements Iconable {
    private static final Logger logger = LoggerFactory.getLogger(ChartTabPane.class);
    /**
     * The tab title
     */
    private static final String PANE_TITLE = "Summary Charts";

    /**
     * The number of columns
     */
    private static final int COLS = 3;

    /**
     * Reference to inspector context
     */
    private PrideInspectorContext viewerContext;

    /**
     * The list of charts to be managed in the tab
     */
    private List<PrideChartManager> managedPrideCharts;

    /**
     * Constructor
     *
     * @param controller data access controller
     * @param parentComp parent container
     */
    public ChartTabPane(DataAccessController controller, JComponent parentComp) {
        super(controller, parentComp);
    }

    /**
     * Setup the main pane
     */
    @Override
    protected void setupMainPane() {
        viewerContext = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        setTitle(PANE_TITLE);

        // set the final icon
        PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        this.setIcon(GUIUtilities.loadIcon(context.getProperty("chart.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(context.getProperty("chart_loading.icon.small")));
    }

    /**
     * Set properties for ChartTabPane
     *
     * @param charts the number of charts to be displayed
     */
    private void setupInitialMainPane(int charts) {
        int border = 3;
        int rows = (int) Math.ceil(charts / (double) COLS);
        setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
        setLayout(new GridLayout(rows, COLS, border, border));
    }

    private void resetTitle() {
        int numberOfCharts = (managedPrideCharts == null) ? 0 : managedPrideCharts.size();
        String title = PANE_TITLE + ((numberOfCharts == 0) ? "" : " (" + numberOfCharts + ")");
        setTitle(title);

        ControllerContentPane dataContentPane = (ControllerContentPane) viewerContext.getDataContentPane(controller);
        if (dataContentPane != null) {
            dataContentPane.setTabTitle(dataContentPane.getChartTabIndex(), title);
        }
    }

    private void createPrideCharts() {
        if (DataAccessController.Type.XML_FILE.equals(controller.getType())) {
            String msg = viewerContext.getProperty("chart.time.warning.message");
            showWarningMessage(msg, false);
        }

        LoadChartDataTask lcd = new LoadChartDataTask(controller);

        // add a task listener
        lcd.addTaskListener(this);

        // start running the task
        lcd.setGUIBlocker(new DefaultGUIBlocker(lcd, GUIBlocker.Scope.NONE, null));
        viewerContext.addTask(lcd);
    }

    public void showChart(PrideChartManager managedPrideChart) {
        removeAll();
        setLayout(new BorderLayout());
        add(new ChartBigPane(this, managedPrideChart), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showPreviousChart(PrideChartManager managedPrideChart) {
        int index = managedPrideCharts.indexOf(managedPrideChart) - 1;
        int previous = index < 0 ? managedPrideCharts.size() - 1 : index;
        showChart(managedPrideCharts.get(previous));
    }

    public void showNextChart(PrideChartManager managedPrideChart) {
        int index = managedPrideCharts.indexOf(managedPrideChart) + 1;
        int next = index % managedPrideCharts.size();
        showChart(managedPrideCharts.get(next));
    }

    public void setThumbnailView() {
        removeAll();
        setupInitialMainPane(managedPrideCharts.size());
        for (PrideChartManager managedPrideChart : managedPrideCharts) {
            ChartThumbnailPane ct = new ChartThumbnailPane(this, managedPrideChart);
            add(ct);
        }
        revalidate();
        repaint();
    }

    private void showWarningMessage(String msg, boolean launchButton) {
        this.setLayout(new BorderLayout());

        JPanel msgPanel = new JPanel();
        msgPanel.setPreferredSize(new Dimension(500, 40));
        msgPanel.setBackground(Color.white);
        msgPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.LINE_AXIS));

        msgPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        // warning message label
        JLabel msgLabel = new JLabel(GUIUtilities.loadIcon(viewerContext.getProperty("chart.warning.icon.small")));
        msgLabel.setText(msg);
//        msgLabel.setPreferredSize(new Dimension(800, 25));
        msgPanel.add(msgLabel);

        // add a glue to fill the empty space
        msgPanel.add(Box.createHorizontalGlue());

        if (launchButton) {
            // button to start calculate charts
            JButton computeButton = new JButton("Start");
            computeButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ChartTabPane.this.removeAll();
                    createPrideCharts();
                }
            });
            msgPanel.add(computeButton);
        }

        msgPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        this.add(msgPanel, BorderLayout.NORTH);

        if (!launchButton) {
            JPanel loadingPanel = new PrideInspectorLoadingPanel();
            this.add(loadingPanel, BorderLayout.CENTER);
        }

    }

    /**
     * Add the rest of components
     */
    @Override
    public void populate() {
        removeAll();
        //get spectra threshold
        if (DataAccessController.Type.DATABASE.equals(controller.getType())) {
            createPrideCharts();
        } else {
            String msg = viewerContext.getProperty("chart.warning.message");
            showWarningMessage(msg, true);
        }
    }

    @Override
    public void succeed(TaskEvent<List<PrideChartManager>> listTaskEvent) {
        managedPrideCharts = new ArrayList<PrideChartManager>();
        for (PrideChartManager managedPrideChart : listTaskEvent.getValue()) {
            try {
                ChartCategoryMapping ccm = new ChartCategoryMapping(managedPrideChart.getPrideChart());

                boolean addChart = false;
                for (ContentCategory category : controller.getContentCategories()) {
                    addChart |= ccm.belong(category);
                }
                if (addChart) managedPrideCharts.add(managedPrideChart);
            } catch (PrideChartException e) {/*Nothing here*/}
        }
        setThumbnailView();
        resetTitle();
    }

    @Override
    public void started(TaskEvent event) {
        showIcon(getLoadingIcon());
    }

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
            contentPane.setTabIcon(contentPane.getChartTabIndex(), icon);
        }
    }

    @Override
    public void process(TaskEvent<List<String>> listTaskEvent) {
        super.process(listTaskEvent);
    }
}
