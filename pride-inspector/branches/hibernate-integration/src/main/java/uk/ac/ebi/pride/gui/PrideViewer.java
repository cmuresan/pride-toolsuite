package uk.ac.ebi.pride.gui;

import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.action.*;
import uk.ac.ebi.pride.gui.component.*;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.impl.OpenHibernateSessionTask;
import uk.ac.ebi.pride.mzgraph.gui.util.SideToolBarPanel;

import javax.swing.*;
import java.awt.*;

/**
 * This is the main class to call to run PRIDE GUI
 *
 */
public class PrideViewer extends Desktop
{
    private JFrame mainFrame;
    private JMenuBar menuBar;
    private StatusBar statusBar;
    private DataSourceViewer dataSourceViewer = null;
    private ExperimentTreeViewer experimentTreeViewer = null;
    private DataBrowser dataBrowser = null;

    private final static String PRIDE_GUI = "PRIDE Viewer";
    private final static String VERSION_NUMBER = "Version 0.1.1";

    public static void main( String[] args )
    {
        Desktop.launch(PrideViewer.class, PrideViewerContext.class, args);
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
//            PlasticLookAndFeel.setPlasticTheme(new Silver());
//            //UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
//            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void buildMainDisplay() {
        // left Pane
        DataAccessMonitor monitor = ((PrideViewerContext)this.getDesktopContext()).getDataAccessMonitor();
        dataSourceViewer = new DataSourceViewer(monitor);
        JScrollPane dataSourceScrollPane = new JScrollPane(dataSourceViewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        experimentTreeViewer = new ExperimentTreeViewer();
        monitor.addPropertyChangeListener(dataSourceViewer);
        JScrollPane treeScrollPane = new JScrollPane(experimentTreeViewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        monitor.addPropertyChangeListener(experimentTreeViewer);

        // central pane
        dataBrowser = new DataBrowser();
        monitor.addPropertyChangeListener(dataBrowser);

        SideToolBarPanel mainDisplayPane = new SideToolBarPanel(dataBrowser, SideToolBarPanel.WEST);
        // add all the component
        mainDisplayPane.addGap(5);
        mainDisplayPane.addComponentToSideBar(null, "Data Sources", "Data Sources", "Data Sources", dataSourceScrollPane);
        mainDisplayPane.addGap(5);
        mainDisplayPane.addComponentToSideBar(null, "Experiments", "Experiments", "Experiments", treeScrollPane);

        mainFrame.getContentPane().add(mainDisplayPane, BorderLayout.CENTER);
    }

    private void buildMainFrame() {
        mainFrame = new JFrame(PRIDE_GUI);
        // ToDo: look and feel
        // ToDo: proper exit hooke
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void buildStatusBar() {
        
        VersionPanel versionPanel = new VersionPanel(VERSION_NUMBER);

        // create task monitor panel
        StatusBarPanel taskMonitorPane = new TaskMonitorPanel();
        this.getDesktopContext().getTaskManager().addPropertyChangeListener(taskMonitorPane);

        statusBar = new StatusBar(versionPanel, taskMonitorPane);
        mainFrame.getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * make menu bar a separate component
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
        //added by dani
        JMenuItem databaseMenuItem = new JMenuItem(new OpenDatabaseAction());
        fileMenu.add(databaseMenuItem);
        //added by andreas
        JMenuItem prideNextGenMenuItem = new JMenuItem(new OpenPrideNextGenAction());
        fileMenu.add(prideNextGenMenuItem);
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
