package uk.ac.ebi.pride.gui.component.quant;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.PrideInspectorTabPane;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 11/08/2011
 * Time: 08:42
 * To change this template use File | Settings | File Templates.
 */
public class QuantTabPane extends PrideInspectorTabPane {

    public QuantTabPane(DataAccessController controller, JComponent parentComponent) {
        super(controller, parentComponent);
    }
}
