package uk.ac.ebi.pride.gui.component.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.SideToolBarPanel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * MainDataVisualizer is the main display area of the pride inspector.
 * It initializes the welcome pane, data source browser and data content browser.
 * In addition, it listens to the property change event from data access monitor, which
 * triggers the switching between the welcome pane and the data content panes.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 19-Sep-2010
 * Time: 09:18:43
 */
public class MainDataVisualizer extends JPanel implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(MainDataVisualizer.class);

    /**
     * Main content display area
     */
    private SideToolBarPanel mainDisplayPane;

    /**
     * Reference to pride inspector context
     */
    private PrideInspectorContext context;

    private  LeftControlPane dataSourceBrowser;



    public MainDataVisualizer() {
        // setup the main pane
        setupMainPane();

        // add the rest of components
        addComponents();
    }

    /**
     * Setup the main pane
     */
    private void setupMainPane() {
        this.setLayout(new BorderLayout());

        // get context
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        context.addPropertyChangeListener(this);
    }

    /**
     * Initialize data source browser and data content browser
     */
    private void addComponents() {

        // data source browser
        dataSourceBrowser = new LeftControlPane();

        // central pane
        CentralContentPane dataContentBrowser = new CentralContentPane();

        // the main display area
        mainDisplayPane = new SideToolBarPanel(dataContentBrowser, SideToolBarPanel.WEST);

        // get icon for data source browser
        Icon dataSourceIcon = GUIUtilities.loadIcon(context.getProperty("data.source.small.icon"));
        String dataSourceDesc = context.getProperty("data.source.title");
        String dataSourceTooltip = context.getProperty("data.source.tooltip");

        // add all the component
        mainDisplayPane.addGap(5);
        mainDisplayPane.addComponent(dataSourceIcon, null, dataSourceTooltip, dataSourceDesc, dataSourceBrowser);

        this.add(mainDisplayPane, BorderLayout.CENTER);
    }

    /**
     * Listens to events which change the visibility of the data source browser
     * 
     * @param evt   property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();

        if (PrideInspectorContext.LEFT_CONTROL_PANE_VISIBILITY.equals(evtName)) {
            // set the visibility of data source browser
            logger.info("Data source browser's visibility has changed to: {}", evt.getNewValue());
            String dataSourceDesc = context.getProperty("data.source.title");
            if (!mainDisplayPane.isToggled(dataSourceDesc)) {
                mainDisplayPane.invokeAction(dataSourceDesc);
            }
        }
    }
}
