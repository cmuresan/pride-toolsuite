package uk.ac.ebi.pride.gui.menu;

import uk.ac.ebi.pride.gui.action.PrideAction;

import javax.swing.*;

/**
 *
 * User: rwang
 * Date: 29-Jan-2010
 * Time: 15:30:08
 */
public class MenuFactory {
    

    public static JMenuBar buildMenuBar(PrideAction... actions) {
        JMenuBar menuBar = new JMenuBar();

        for (PrideAction action : actions) {
            String location = action.getMenuLocation();
            String[] locationParts = location.split("\\.");
            String actionName = (String)action.getValue(Action.NAME);
            // ToDo: implementation required.
            /*
            // if action name is separator, then create a JSpearator component
            if (actionName == SEPARATOR) {
                menu.add(new JSeparator());
            } else {
                JMenuItem item = new JMenuItem();
                // set name
                item.setName(actionName);
                
                // set small icon
                Icon icon = (Icon)action.getValue(Action.SMALL_ICON);
                if (icon != null)
                    item.setIcon(icon);
                
                // set tool tips
                String shortDesc = (String)action.getValue(Action.SHORT_DESCRIPTION);
                if (shortDesc != null)
                    item.setToolTipText(shortDesc);

                // set mnemonic
                Character mnemonic = (Character)action.getValue(Action.MNEMONIC_KEY);
                if (mnemonic != null)
                    item.setMnemonic(mnemonic.charValue());

                menu.add(item);
            }
            */
        }
        return menuBar;
    }
}