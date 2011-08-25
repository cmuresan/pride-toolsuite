package uk.ac.ebi.pride.gui;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.action.impl.*;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.startup.MainDataVisualizer;
import uk.ac.ebi.pride.gui.component.status.NotificationPanel;
import uk.ac.ebi.pride.gui.component.status.StatusBar;
import uk.ac.ebi.pride.gui.component.status.StatusBarPanel;
import uk.ac.ebi.pride.gui.component.status.TaskMonitorPanel;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.menu.MenuFactory;
import uk.ac.ebi.pride.gui.task.impl.OpenValidPrideExperimentTask;
import uk.ac.ebi.pride.gui.utils.AccessionUtils;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.util.IOUtilities;

import javax.jnlp.ServiceManager;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This is the main class to call to run PRIDE GUI
 */
public class PrideInspector extends Desktop {
    private static final Logger logger = LoggerFactory.getLogger(PrideInspector.class);

    // command line option for experiment accessions
    private static final String ACCESSION_CMD = "accession";
    // command line option for user name
    private static final String USER_NAME_CMD = "username";
    // command line option for password
    private static final String PASSWORD_CMD = "password";

    private JFrame mainFrame;
    private JMenuBar menuBar;
    private StatusBar statusBar;
    private MainDataVisualizer visualizer;
    private SingleInstanceService sis;
    private SingleInstanceListener sisL;
    private Options cmdOptions;
    private CommandLineParser cmdParser;
    private String[] cmdArgs;

    private final static String PRIDE_GUI = "PRIDE Inspector";

    public static void main(String[] args) {
        Desktop.launch(PrideInspector.class, PrideInspectorContext.class, args);
    }

    @Override
    public void init(String[] args) {
        // register this instance as a single instance service listener for jnlp
        registerJnlpListener();
        // create command line parser
        createCmdLineParser();
        // store all the command line arguments
        storeCmdLineArgs(args);
        // load all properties
        loadProperties();
        // createAttributedSequence the main frame
        buildMainFrame();
        // createAttributedSequence menu bar
        buildMenuToolBar();
        // createAttributedSequence the bottom bar
        buildStatusBar();
        // set default logging
        setDefaultLogging();
        // createAttributedSequence the main display area
        buildMainDisplay();
    }

    /**
     * Register as a Java Web Start listener
     */
    private void registerJnlpListener() {
        try {
            sis = (SingleInstanceService) ServiceManager.lookup("javax.jnlp.SingleInstanceService");
            sisL = new SISListener();
            sis.addSingleInstanceListener(sisL);
        } catch (UnavailableServiceException e) {
            sis = null;
            logger.warn("Cannot create single instance service for web start");
        }
    }

    /**
     * Method to create command line parser
     */
    private void createCmdLineParser() {
        cmdOptions = new Options();

        // add a accession option
        cmdOptions.addOption(ACCESSION_CMD, true, "a list of pride experiment accessions, separated by comma");
        // add a user name option
        cmdOptions.addOption(USER_NAME_CMD, true, "pride user name");
        // add a password option
        cmdOptions.addOption(PASSWORD_CMD, true, "pride password");
        // create cmd line parser
        cmdParser = new GnuParser();
    }

    /**
     * Store command line arguments
     *
     * @param args
     */
    private void storeCmdLineArgs(String[] args) {
        this.cmdArgs = args;
    }

    /**
     * Processing the input command line arguments
     *
     * @param args a list of pride db accessions
     */
    private void processCmdArgs(String[] args) {
        try {
            // parse command line input
            CommandLine cmd = cmdParser.parse(cmdOptions, args);

            // get accessions
            java.util.List<Comparable> accs = null;
            if (cmd.hasOption(ACCESSION_CMD)) {
                String accStr = cmd.getOptionValue(ACCESSION_CMD);
                accs = new ArrayList<Comparable>(AccessionUtils.expand(accStr));
            }

            // get user name
            String username = null;
            if (cmd.hasOption(USER_NAME_CMD)) {
                username = cmd.getOptionValue(USER_NAME_CMD);
            }

            // get password
            String password = null;
            if (cmd.hasOption(PASSWORD_CMD)) {
                password = cmd.getOptionValue(PASSWORD_CMD);
            }

            if (accs != null || username != null) {
                OpenValidPrideExperimentTask task = new OpenValidPrideExperimentTask(accs, username, password);
                task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));
                getDesktopContext().addTask(task);
            }
        } catch (ParseException e) {
            System.err.println("Parsing command line option failed. Reason: " + e.getMessage());
        }
    }

    private void loadProperties() {
        DesktopContext context = getDesktopContext();
        try {
            context.loadSystemProps(this.getClass().getClassLoader().getResourceAsStream("prop/gui.prop"));
            context.loadSystemProps(this.getClass().getClassLoader().getResourceAsStream("prop/settings.prop"));
            context.loadSystemProps(this.getClass().getClassLoader().getResourceAsStream("prop/database.prop"));
        } catch (IOException e) {
            logger.error("Error while loading properties", e);
        }
    }

    private void buildMainDisplay() {
        visualizer = new MainDataVisualizer();
        mainFrame.getContentPane().add(visualizer, BorderLayout.CENTER);
    }

    private void setDefaultLogging() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Logger logger = LoggerFactory.getLogger(t.getClass());
                logger.error("Exception in thread [" + t.getName() + "]", e);
                PrideInspector.this.getDesktopContext().addThrowableEntry(new ThrowableEntry(MessageType.ERROR, "Unexpected Exception", e));
            }
        });
    }

    private void buildMainFrame() {
        DesktopContext context = getDesktopContext();
        mainFrame = new JFrame(PRIDE_GUI + " " + context.getProperty("pride.inspector.version"));
        ImageIcon icon = GUIUtilities.loadImageIcon(context.getProperty("pride.inspector.logo.medium.icon"));
        mainFrame.setIconImage(icon.getImage());
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");

        } catch (Exception e) {
            logger.error("Failed to set the look and feel", e);
        }
        // ToDo: proper exit hooke
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void buildStatusBar() {
        // create task monitor panel
        StatusBarPanel taskMonitorPane = new TaskMonitorPanel();

        // create notification handler
        StatusBarPanel notificationPane = new NotificationPanel();
        statusBar = new StatusBar(taskMonitorPane, notificationPane);
        mainFrame.getContentPane().add(statusBar, BorderLayout.PAGE_END);
    }

    /**
     * make menu bar a separate component
     */
    private void buildMenuToolBar() {
        // get property manager
        PrideInspectorContext context = (PrideInspectorContext) getDesktopContext();

        // create all the actions
        // file open action
        Icon openFileIcon = GUIUtilities.loadIcon(context.getProperty("open.file.icon.small"));
        String openFileDesc = context.getProperty("open.file.title");
        PrideAction openFileAction = new OpenFileAction(openFileDesc, openFileIcon);

        // database open action
        Icon openDbIcon = GUIUtilities.loadIcon(context.getProperty("open.database.icon.small"));
        String openDbDesc = context.getProperty("open.database.title");
        PrideAction openDbAction = new OpenDatabaseAction(openDbDesc, openDbIcon);

        // open reviewer
        Icon openReviewerIcon = GUIUtilities.loadIcon(context.getProperty("reviewer.download.icon.small"));
        String openReviewerDesc = context.getProperty("reviewer.download.title");
        PrideAction openReviewerAction = new OpenReviewAction(openReviewerDesc, openReviewerIcon);

        // close
        String closeDesc = context.getProperty("close.source.title");
        String closeAllDesc = context.getProperty("close.all.soruce.title");
        PrideAction closeAction = new CloseControllerAction(closeDesc, null);
        PrideAction closeAllAction = new CloseAllControllersAction(closeAllDesc, null);

        // try pride xml sample
        String openPrideXmlExampleDesc = context.getProperty("open.pride.xml.example.title");
        File prideXmlExampleFile = getExampleFiles(context.getProperty("pride.inspector.pride.example.file"));
        java.util.List<File> prideXmlFiles = new ArrayList<File>();
        if (prideXmlExampleFile != null) {
            prideXmlFiles.add(prideXmlExampleFile);
        }
        PrideAction openPrideXmlExampleAction = new OpenFileAction(openPrideXmlExampleDesc, null, prideXmlFiles);
        openPrideXmlExampleAction.setEnabled(prideXmlExampleFile != null && prideXmlExampleFile.exists());

        // try mzml sample
        String openMzMLExampleDesc = context.getProperty("open.mzml.example.title");
        File mzMLExampleFile = getExampleFiles(context.getProperty("pride.inspector.mzml.example.file"));
        java.util.List<File> mzMLFiles = new ArrayList<File>();
        if (mzMLExampleFile != null) {
            mzMLFiles.add(mzMLExampleFile);
        }
        PrideAction openMzMLExampleAction = new OpenFileAction(openMzMLExampleDesc, null, mzMLFiles);
        openMzMLExampleAction.setEnabled(mzMLExampleFile != null && mzMLExampleFile.exists());

        // help
        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
        String helpDesc = context.getProperty("help.title");
        PrideAction helpAction = new OpenHelpAction(helpDesc, helpIcon);

        // faq
        String faqDesc = context.getProperty("faq.title");
        PrideAction faqAction = new OpenFAQAction(faqDesc, null);

        // pride website
        String prideWeb = context.getProperty("open.pride.website.title");
        String prideWebUrl = context.getProperty("pride.website");
        PrideAction prideWebAction = new OpenUrlAction(prideWeb, null, prideWebUrl);

        // pride website
        String inspectorWeb = context.getProperty("open.pride.inspector.website.title");
        String inspectorWebUrl = context.getProperty("pride.inspector.website");
        PrideAction inspectorWebAction = new OpenUrlAction(inspectorWeb, null, inspectorWebUrl);

        // feedback
        String feedbackDesc = context.getProperty("feedback.title");
        PrideAction feedBackAction = new FeedbackAction(feedbackDesc, null);

        //export
        String exportDesc = context.getProperty("export.title");
        PrideAction exportAction = new ExportSpectrumAction(exportDesc, null);

        // export identification
        String exportIdentDesc = context.getProperty("export.identification.title");
        PrideAction exportIdentAction = new ExportIdentificationPeptideAction(exportIdentDesc, null);

        // export spectrum description
        String exportSpectrumDesc = context.getProperty("export.spectrum.desc.title");
        PrideAction exportSpectrumDescAction = new ExportSpectrumDescAction(exportSpectrumDesc, null);

        // export identification description
        String exportIdentDescTitle = context.getProperty("export.identification.desc.title");
        PrideAction exportIdentDescAction = new ExportIdentificationDescAction(exportIdentDescTitle, null);

        // export peptide description
        String exportPeptideDescTitle = context.getProperty("export.peptide.desc.title");
        PrideAction exportPeptideAction = new ExportPeptideDescAction(exportPeptideDescTitle, null);

        // make experiment public
        String makeExpPublicTitle = context.getProperty("make.experiment.public.title");
        PrideAction makeExpPublicAction = new MakeExperimentPublicAction(makeExpPublicTitle, null);

        String createReviewerTitle = context.getProperty("create.reviewer.title");
        PrideAction createReviewerAction = new CreateReviewerAction(createReviewerTitle, null);

        // check update
        String updateDescTitle = context.getProperty("check.update.desc.title");
        PrideAction updateAction = new UpdateAction(updateDescTitle, null);

        // about
        String aboutDesc = context.getProperty("about.title");
        PrideAction aboutAction = new AboutAction(aboutDesc, null);

        // exit
        String exitDesc = context.getProperty("exit.title");
        PrideAction exitAction = new ExitAction(exitDesc, null);

        // menu items
        menuBar = new JMenuBar();

        // file menu
        JMenu fileMenu = MenuFactory.createMenu("File",
                openFileAction, openDbAction, openReviewerAction, MenuFactory.ACTION_SEPARATOR,
                closeAction, closeAllAction, MenuFactory.ACTION_SEPARATOR, exitAction);
        fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
        menuBar.add(fileMenu);

        // try samples
        JMenu trySampleMenu = MenuFactory.createMenu("Examples",
                openPrideXmlExampleAction, openMzMLExampleAction);
        trySampleMenu.setMnemonic(java.awt.event.KeyEvent.VK_X);
        menuBar.add(trySampleMenu);

        // export menu
        JMenu exportMenu = MenuFactory.createMenu("Export",
                exportAction, exportSpectrumDescAction, exportIdentAction, exportIdentDescAction, exportPeptideAction);
        exportMenu.setMnemonic(java.awt.event.KeyEvent.VK_E);
        menuBar.add(exportMenu);

        // curation menu
        boolean showCurationMenu = Boolean.parseBoolean(context.getProperty("include.curation.menu"));
        if (showCurationMenu) {
            JMenu curationMenu = MenuFactory.createMenu("Curation", makeExpPublicAction, createReviewerAction);
            curationMenu.setMnemonic(java.awt.event.KeyEvent.VK_C);
            menuBar.add(curationMenu);
        }

        // help menu
        JMenu helpMenu = MenuFactory.createMenu("Help",
                helpAction, faqAction, MenuFactory.ACTION_SEPARATOR,
                prideWebAction, inspectorWebAction,
                MenuFactory.ACTION_SEPARATOR, feedBackAction,
                MenuFactory.ACTION_SEPARATOR, updateAction, aboutAction);
        helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
        menuBar.add(helpMenu);

        // add menus
        mainFrame.setJMenuBar(menuBar);
    }

    @Override
    public void ready() {
    }

    @Override
    public void show() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width - 100, screenSize.height - 100);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setVisible(true);
    }

    @Override
    public void postShow() {
        processCmdArgs(cmdArgs);
    }

    @Override
    public void finish() {
        sis.removeSingleInstanceListener(sisL);
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

    public MainDataVisualizer getVisualizer() {
        return visualizer;
    }

    /**
     * Get examples file based on the sub path
     *
     * @param subPath sub path to the example file
     * @return File   example file
     */
    private File getExampleFiles(String subPath) {
        URL path = IOUtilities.getFullPath(this.getClass(), subPath);
        File file;
        try {
            file = IOUtilities.convertURLToFile(path);
        } catch (IllegalArgumentException ex) {
            return null;
        }
        return file;
    }

    private class SISListener implements SingleInstanceListener {
        @Override
        public void newActivation(String[] args) {
            processCmdArgs(args);
        }
    }
}
