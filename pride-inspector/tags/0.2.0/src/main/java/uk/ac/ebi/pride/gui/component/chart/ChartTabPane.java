package uk.ac.ebi.pride.gui.component.chart;

import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskManager;
import uk.ac.ebi.pride.gui.task.impl.LoadChartDataTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import java.awt.*;
import java.util.List;

/**
 * <p>Class to create a TabPane for integrating the PRIDE Chart into PRIDE Inspector.</p>
 *
 * @author Antonio Fabregat
 *         Date: 10-ago-2010
 *         Time: 14:10:41
 */
public class ChartTabPane extends DataAccessControllerPane<List<PrideChart>, String> {

    private static final String PANE_TITLE = "Quality charts";
    private PrideViewerContext context;
    private List<PrideChart> prideCharts;

    public ChartTabPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        setupInitialMainPane();
        resetTitle();
    }

    // set properties for ChartTabPane
    private void setupInitialMainPane() {
        this.setLayout(new GridLayout(2, 3));
    }

    private void resetTitle() {
        this.setTitle(PANE_TITLE);
    }

    private void createPrideCharts() {
        TaskManager taskMgr = context.getTaskManager();
        LoadChartDataTask lcd = new LoadChartDataTask(controller);

        // add a task listener
        lcd.addTaskListener(this);

        // start running the task
        lcd.setGUIBlocker(new DefaultGUIBlocker(lcd, GUIBlocker.Scope.NONE, null));
        taskMgr.addTask(lcd);
    }

    public void showChart(PrideChart prideChart) {
        removeAll();
        setLayout(new BorderLayout());
        add(new ChartBigPane(this, prideChart), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showPreviousChart(PrideChart prideChart) {
        int index = prideCharts.indexOf(prideChart) - 1;
        int previous = index < 0 ? prideCharts.size() - 1 : index;
        showChart(prideCharts.get(previous));
    }

    public void showNextChart(PrideChart prideChart) {
        int index = prideCharts.indexOf(prideChart) + 1;
        int next = index % prideCharts.size();
        showChart(prideCharts.get(next));
    }

    public void setThumbnailView() {
        removeAll();
        setupInitialMainPane();
        for (PrideChart prideChart : prideCharts) {
            ChartThumbnailPane ct = new ChartThumbnailPane(this, prideChart);
            this.add(ct);
        }
        revalidate();
        repaint();
    }

    @Override
    protected void addComponents() {
        removeAll();
        createPrideCharts();
    }

    @Override
    protected void updatePropertyChange() {
        removeAll();
        createPrideCharts();
    }

    @Override
    public void succeed(TaskEvent<List<PrideChart>> listTaskEvent) {
        prideCharts = listTaskEvent.getValue();
        setThumbnailView();
    }
}