package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.ident.IdentTabPane;
import uk.ac.ebi.pride.gui.component.metadata.MetaDataTabPane;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 29-Jul-2010
 * Time: 14:07:59
 */
public class DataContentDisplayPane extends JPanel implements PropertyChangeListener {
    public static final int META_TAB = 0;
    public  static final int MZGRAPH_TAB = 1;
    public static final int IDENTIFICATION_TAB = 2;
    private static final int CHART_TAB = 3;

    private final DataAccessController controller;
    private final JTabbedPane contentTabPane;

    private JTabbedPane tabPane = null;

    public DataContentDisplayPane(DataAccessController controller) {
        this.controller = controller;
        this.contentTabPane = createTabbedPane(this.controller);
        this.setLayout(new BorderLayout());
        this.add(contentTabPane, BorderLayout.CENTER);
        setTabVisibility();
        ((AbstractDataAccessController) controller).addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessController.FOREGROUND_EXPERIMENT_CHANGED.equals(evtName)) {
            if (SwingUtilities.isEventDispatchThread()) {
                setTabVisibility();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        setTabVisibility();
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }
    }

    private void setTabVisibility() {
        contentTabPane.setEnabledAt(MZGRAPH_TAB, (controller.hasChromatogram() || controller.hasSpectrum()));
        contentTabPane.setEnabledAt(IDENTIFICATION_TAB, (controller.hasIdentification()));
        contentTabPane.setSelectedIndex(META_TAB);
    }

    private JTabbedPane createTabbedPane(DataAccessController cne) {
        tabPane = new JTabbedPane();
        
        // the type of data contains experiment data
        MetaDataTabPane metaDataTabPane = new MetaDataTabPane(cne);
        tabPane.insertTab(metaDataTabPane.getTitle(), null, metaDataTabPane, metaDataTabPane.getTitle(), META_TAB);
        // spectra data tab
        MzDataTabPane spectraTabPane = new MzDataTabPane(cne);
        tabPane.insertTab(spectraTabPane.getTitle(), null, spectraTabPane, spectraTabPane.getTitle(), MZGRAPH_TAB);
        // identification data tab
        IdentTabPane identTabPane = new IdentTabPane(cne);
        tabPane.insertTab(identTabPane.getTitle(), null, identTabPane, identTabPane.getTitle(), IDENTIFICATION_TAB);
        // chart tab
//        ChartTabPane chartTabPane = new ChartTabPane(cne);
//        tabPane.insertTab(chartTabPane.getTitle(), null, chartTabPane, chartTabPane.getTitle(), CHART_TAB);
        
        return tabPane;
    }

    /**
     * Set the title of the tab
     * @param index index of the tab
     * @param title title of the tab
     */
    public void setTabTitle(int index, String title){
        tabPane.setTitleAt(index,title);
    }

    /**
     * Set data content pane's tab icon
     * @param index index of the tab
     * @param icon  icon of the tab
     */
    public void setTabIcon(int index, Icon icon) {
        tabPane.setIconAt(index, icon);
    }
}
