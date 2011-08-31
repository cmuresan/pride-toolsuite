package uk.ac.ebi.pride.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.protein.DecoyFilterDialog;
import uk.ac.ebi.pride.gui.component.quant.QuantExportDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.utils.EDTUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * Action to show a decoy filter dialog
 *
 * User: rwang
 * Date: 31/08/2011
 * Time: 11:34
 */
public class DecoyFilterAction extends PrideAction {
    private static final Logger logger = LoggerFactory.getLogger(DecoyFilterAction.class);
    /**
     * JTable where protein name will be displayed
     */
    private JTable table;

    /**
     * Decoy filter dialog
     */
    private JDialog decoyFilterDialog;

    /**
     * Constructor
     *
     * @param table protein table
     */
    public DecoyFilterAction(JTable table) {
        super(Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.title"),
                GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.small.icon")));
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Runnable code = new Runnable() {

            @Override
            public void run() {
                if (decoyFilterDialog == null) {
                    decoyFilterDialog = new DecoyFilterDialog(Desktop.getInstance().getMainComponent(), table);
                }
                decoyFilterDialog.setVisible(true);
            }
        };

        try {
            EDTUtils.invokeAndWait(code);
        } catch (InvocationTargetException e1) {
            logger.error("Failed to create an new instance of DecoyFilterDialog", e1);
        } catch (InterruptedException e1) {
            logger.error("Failed to create an new instance of DecoyFilterDialog", e1);
        }
    }
}

