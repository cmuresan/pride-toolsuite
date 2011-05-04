package uk.ac.ebi.pride.gui;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.Silver;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.action.OpenDatabaseAction;
import uk.ac.ebi.pride.gui.action.OpenMzMLAction;
import uk.ac.ebi.pride.gui.action.OpenPrideXmlAction;
import uk.ac.ebi.pride.gui.component.*;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.test.DummyAction;

import javax.swing.*;
import java.awt.*;

/**
 * This is the main class to call to run PRIDE GUI
 *
 */
public class PrideGUIMain extends Desktop
{
    private JFrame mainFrame;
    private JMenuBar menuBar;
    private StatusBar statusBar;
    private DataSourceViewer dataSourceViewer = null;
    private ExperimentTreeViewer experimentTreeViewer = null;
    private DataViewer dataViewer = null;

    private final static String PRIDE_GUI = "PRIDE GUI";

    public static void main( String[] args )
    {
        Desktop.launch(PrideGUIMain.class, PrideGUIContext.class, args);
    }

    @Override
    public void init(String[] args) {
        // Set Look and Feel
        setLookAndFeel();
        // build the main frame
        buildMainFrame();
        // build menu bar
        buildMenuBar();
        // build the bottom bar
        buildStatusBar();
        // build the main display area
        buildMainDisplay();
    }

    private void setLookAndFeel() {
        try {
            PlasticLookAndFeel.setPlasticTheme(new Silver());
            //UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void buildMainDisplay() {
        JPanel mainDisplayPane = new JPanel();
        mainDisplayPane.setLayout(new BorderLayout());

        // left Pane
        DataAccessMonitor monitor = ((PrideGUIContext)this.getDesktopContext()).getDataAccessMonitor();
        dataSourceViewer = new DataSourceViewer(monitor);
        experimentTreeViewer = new ExperimentTreeViewer();
        monitor.addPropertyChangeListener(dataSourceViewer);
        monitor.addPropertyChangeListener(experimentTreeViewer);
        JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dataSourceViewer, experimentTreeViewer);
        leftPane.setResizeWeight(0.5);
        leftPane.setOneTouchExpandable(false);
        leftPane.setDividerSize(10);

        // right pane
        dataViewer = new DataViewer();
        monitor.addPropertyChangeListener(dataViewer);

        //ToDo: a separate component to deal with resizing
        //ToDo: raise the component on each side
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, dataViewer);
        splitPane.setDividerLocation(UIComponentConstants.DATA_SOURCE_VIEWER_WIDTH);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(5);
        mainDisplayPane.add(splitPane);
        mainFrame.getContentPane().add(mainDisplayPane, BorderLayout.CENTER);
    }

    private void buildMainFrame() {
        mainFrame = new JFrame(PRIDE_GUI);
        // ToDo: look and feel
        // ToDo: proper exit hooke
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void buildStatusBar() {
        
        VersionPanel versionPanel = new VersionPanel("Version 0.1");

        // create task monitor panel
        StatusBarPanel taskMonitorPane = new TaskMonitorPanel();
        this.getDesktopContext().getTaskManager().addPropertyChangeListener(taskMonitorPane);

        statusBar = new StatusBar(versionPanel, taskMonitorPane);
        mainFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * make menu bar a separate component
     * create menu item and add menu item
     * 
     */
    private void buildMenuBar() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileMenuItem = new JMenuItem(new DummyAction());
        fileMenu.add(fileMenuItem);
        JMenuItem mzMLMenuItem = new JMenuItem(new OpenMzMLAction());
        fileMenu.add(mzMLMenuItem);
        JMenuItem prideXmlMenuItem = new JMenuItem(new OpenPrideXmlAction());
        fileMenu.add(prideXmlMenuItem);
        JMenuItem databaseMenuItem = new JMenuItem(new OpenDatabaseAction());
        fileMenu.add(databaseMenuItem);
        menuBar.add(fileMenu);
        mainFrame.setJMenuBar(menuBar);
    }

    @Override
    public void ready() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void show() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setBounds(150, 150, screenSize.width - 300, screenSize.height - 300);
        mainFrame.setVisible(true);
    }

    @Override
    public void finish() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public JFrame getMainComponent() {
        return mainFrame;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }
}
