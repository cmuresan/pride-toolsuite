package uk.ac.ebi.pride.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessMonitor;
import uk.ac.ebi.pride.gui.action.impl.*;
import uk.ac.ebi.pride.gui.component.*;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.utils.GUIUtilities;
import uk.ac.ebi.pride.mzgraph.gui.util.SideToolBarPanel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * This is the main class to call to run PRIDE GUI
 */
public class PrideViewer extends Desktop {
    private static final Logger logger = LoggerFactory.getLogger(uk.ac.ebi.pride.gui.PrideViewer.class);
    private JFrame mainFrame;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private StatusBar statusBar;
    private DataBrowser dataBrowser = null;

    private final static String PRIDE_GUI = "PRIDE Inspector";
    private final static String VERSION_NUMBER = "Version 0.1.1";

    public static void main(String[] args) {
        Desktop.launch(PrideViewer.class, PrideViewerContext.class, args);
    }

    @Override
    public void init(String[] args) {
        // load all properties
        loadProperties();
        // build the main frame
        buildMainFrame();
        // build menu bar
        buildMenuToolBar();
        // build the bottom bar
        buildStatusBar();
        // build the main display area
        buildMainDisplay();
    }

    private void loadProperties() {
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();
        try {
            propMgr.loadSystemProps(ClassLoader.getSystemResourceAsStream("prop/gui.prop"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    private void buildMainDisplay() {
        // welcome pane
        //WelcomePane welcomePane = new WelcomePane();
        // data source browser
        DataSourceBrowser dataSourceBrowser = new DataSourceBrowser();
        logger.info("Finish creating data source browser");
        // central pane
        dataBrowser = new DataBrowser();

        SideToolBarPanel mainDisplayPane = new SideToolBarPanel(dataBrowser, SideToolBarPanel.WEST);
        // get property manager
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        Icon dataSourceIcon = GUIUtilities.loadIcon(propMgr.getProperty("data.source.small.icon"));
        String dataSourceDesc = propMgr.getProperty("data.source.title");
        String dataSourceTooltip = propMgr.getProperty("data.source.tooltip");

        // add all the component
        mainDisplayPane.addGap(5);
        mainDisplayPane.addComponentToSideBar(dataSourceIcon, null, dataSourceTooltip, dataSourceDesc, dataSourceBrowser);

        mainFrame.getContentPane().add(mainDisplayPane, BorderLayout.CENTER);
    }

    private void buildMainFrame() {
        mainFrame = new JFrame(PRIDE_GUI);
        try {
            //PlasticLookAndFeel.setPlasticTheme(new LightGray());
            //UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
            //UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            //UIManager.setLookAndFeel(new WindowsLookAndFeel());
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
        // ToDo: proper exit hooke
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void buildStatusBar() {

        VersionPanel versionPanel = new VersionPanel(VERSION_NUMBER);

        // create task monitor panel
        StatusBarPanel taskMonitorPane = new TaskMonitorPanel();
        this.getDesktopContext().getTaskManager().addPropertyChangeListener(taskMonitorPane);

        statusBar = new StatusBar(versionPanel, taskMonitorPane);
        mainFrame.getContentPane().add(statusBar, BorderLayout.PAGE_END);
    }

    /**
     * make menu bar a separate component
     */
    private void buildMenuToolBar() {


        // get property manager
        PrideViewerContext context = (PrideViewerContext)Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();
        DataAccessMonitor monitor = context.getDataAccessMonitor();

        // create all the actions
        // file open action
        Icon openFileIcon = GUIUtilities.loadIcon(propMgr.getProperty("open.file.icon.small"));
        String openFileDesc = propMgr.getProperty("open.file.title");
        String openFileTooltip = propMgr.getProperty("open.file.tooltip");
        String openFileMenuLocation = propMgr.getProperty("open.file.menu.location");
        Action openFileAction = new OpenFileAction(openFileDesc, openFileIcon, openFileMenuLocation);

        // database open action
        Icon openDbIcon = GUIUtilities.loadIcon(propMgr.getProperty("open.database.icon.small"));
        String openDbDesc = propMgr.getProperty("open.database.title");
        String openDbTooltip = propMgr.getProperty("open.database.tooltip");
        String openDbMenuLocation = propMgr.getProperty("open.database.menu.location");
        Action openDbAction = new OpenDatabaseAction(openDbDesc, openDbIcon, openDbMenuLocation);

        // open reviewer
        Icon openReviewerIcon = GUIUtilities.loadIcon(propMgr.getProperty("reviewer.download.icon.small"));
        String openReviewerDesc = propMgr.getProperty("reviewer.download.title");
        String openReviewerTooltip = propMgr.getProperty("reviewer.download.tooltip");
        String openReviewerMenuLocation = propMgr.getProperty("reviewer.download.menu.location");
        Action openReviewerAction = new OpenReviewAction(openReviewerDesc, openReviewerIcon, openReviewerMenuLocation);

        // refresh
        Icon refreshEnabledIcon = GUIUtilities.loadIcon(propMgr.getProperty("reload.source.enable.icon.small"));
        Icon refreshDisabledIcon = GUIUtilities.loadIcon(propMgr.getProperty("reload.source.disable.icon.small"));
        String refreshDesc = propMgr.getProperty("reload.source.title");
        String refreshTooltip = propMgr.getProperty("reload.source.tooltip");
        String refreshMenuLocation = propMgr.getProperty("reload.source.menu.location");
        Action refreshAction = new ReloadSourceAction(refreshDesc, refreshEnabledIcon, refreshMenuLocation);
        monitor.addPropertyChangeListener((PropertyChangeListener)refreshAction);

        // close
        Icon closeEnabledIcon = GUIUtilities.loadIcon(propMgr.getProperty("close.source.enable.icon.small"));
        Icon closeDisabledIcon = GUIUtilities.loadIcon(propMgr.getProperty("close.source.disable.icon.small"));
        String closeDesc = propMgr.getProperty("close.source.title");
        String closeTooltip = propMgr.getProperty("close.source.tooltip");
        String closeMenuLocation = propMgr.getProperty("close.source.menu.location");
        Action closeAction = new CloseControllerAction(closeDesc, closeEnabledIcon, closeMenuLocation);
        monitor.addPropertyChangeListener((PropertyChangeListener)closeAction);

        // help
        Icon helpIcon = GUIUtilities.loadIcon(propMgr.getProperty("help.icon.small"));
        String helpDesc = propMgr.getProperty("help.title");
        String helpTooltip = propMgr.getProperty("help.tooltip");
        String helpMenuLocation = propMgr.getProperty("help.menu.location");
        Action helpAction = new OpenHelpAction(helpDesc, helpIcon, helpMenuLocation);

        //export
        Icon exportEnabledIcon = GUIUtilities.loadIcon(propMgr.getProperty("export.enable.icon.small"));
        Icon exportDisabledIcon = GUIUtilities.loadIcon(propMgr.getProperty("export.disable.icon.small"));
        String exportDesc = propMgr.getProperty("export.title");
        String exportToolTip = propMgr.getProperty("export.tooltip");
       // String exportMenuLocation = propMgr.getProperty("export.menu.location");     not needed yet
        Action exportAction = new BatchDownloadAction(exportDesc,exportEnabledIcon, null);
        monitor.addPropertyChangeListener((PropertyChangeListener)exportAction);

        // about
        Icon aboutIcon = GUIUtilities.loadIcon(propMgr.getProperty("about.icon.small"));
        String aboutDesc = propMgr.getProperty("about.title");
        String aboutTooltip = propMgr.getProperty("about.tooltip");
        String aboutMenuLocation = propMgr.getProperty("about.menu.location");
        Action aboutAction = new AboutAction(aboutDesc, aboutIcon, aboutMenuLocation);

        // exit
        String exitDesc = propMgr.getProperty("exit.title");
        String exitMenuLocation = propMgr.getProperty("exit.menu.location");
        Action exitAction = new ExitAction(exitDesc, null, exitMenuLocation);

        // menu items
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        // file
        JMenuItem openFileMenuItem = new JMenuItem(openFileAction);
        fileMenu.add(openFileMenuItem);
        // db
        JMenuItem openDbMenuItem = new JMenuItem(openDbAction);
        fileMenu.add(openDbMenuItem);
        // reviewer
        JMenuItem reviewerMenuItem = new JMenuItem(openReviewerAction);
        fileMenu.add(reviewerMenuItem);
        // separator
        fileMenu.addSeparator();
        // refresh
        JMenuItem refreshMenuItem = new JMenuItem(refreshAction);
        fileMenu.add(refreshMenuItem);
        // close
        JMenuItem closeMenuItem = new JMenuItem(closeAction);
        fileMenu.add(closeMenuItem);
        // separator
        fileMenu.addSeparator();
        // exit
        JMenuItem exitMenuItem = new JMenuItem(exitAction);
        fileMenu.add(exitMenuItem);
        // help
        JMenuItem helpMenuItem = new JMenuItem(helpAction);
        helpMenu.add(helpMenuItem);
        // about
        JMenuItem aboutMenuItem = new JMenuItem(aboutAction);
        helpMenu.add(aboutMenuItem);
        // add menus
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        mainFrame.setJMenuBar(menuBar);

        // tool bar
        toolBar = new JToolBar();

        // open file
        JButton openFileButton = new JButton();
        openFileButton.setAction(openFileAction);
        openFileButton.setText("");
        openFileButton.setToolTipText(openFileTooltip);
        openFileButton.setFocusable(false);
        toolBar.add(openFileButton);
        // open database
        JButton openDbButton = new JButton();
        openDbButton.setAction(openDbAction);
        openDbButton.setText("");
        openDbButton.setToolTipText(openDbTooltip);
        openDbButton.setFocusable(false);
        toolBar.add(openDbButton);
        // open reviewer download
        JButton openReviewerButton = new JButton();
        openReviewerButton.setAction(openReviewerAction);
        openReviewerButton.setText("");
        openReviewerButton.setToolTipText(openReviewerTooltip);
        openReviewerButton.setFocusable(false);
        toolBar.add(openReviewerButton);
        // separator
        toolBar.addSeparator();
        // refresh
        JButton refreshButton = new JButton();
        refreshButton.setAction(refreshAction);
        refreshButton.setText("");
        refreshButton.setToolTipText(refreshTooltip);
        refreshButton.setDisabledIcon(refreshDisabledIcon);
        refreshButton.setFocusable(false);
        toolBar.add(refreshButton);
        // close connection
        JButton closeButton = new JButton();
        closeButton.setAction(closeAction);
        closeButton.setText("");
        closeButton.setToolTipText(closeTooltip);
        closeButton.setDisabledIcon(closeDisabledIcon);
        closeButton.setFocusable(false);
        toolBar.add(closeButton);
        // separator
        toolBar.addSeparator();
        // export mgf
        JButton exportButton = new JButton();
        exportButton.setAction(exportAction);
        exportButton.setText("");
        exportButton.setToolTipText(exportToolTip);
        exportButton.setDisabledIcon(exportDisabledIcon);
        exportButton.setFocusable(false);
        toolBar.add(exportButton);
        // separator
        toolBar.addSeparator();

        // help
        JButton helpButton = new JButton(helpIcon);
        helpButton.setAction(helpAction);
        helpButton.setText("");
        helpButton.setToolTipText(helpTooltip);
        helpButton.setFocusable(false);
        toolBar.add(helpButton);
        mainFrame.getContentPane().add(toolBar, BorderLayout.PAGE_START);
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

    public JToolBar getToolBar() {
        return toolBar;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }
}
