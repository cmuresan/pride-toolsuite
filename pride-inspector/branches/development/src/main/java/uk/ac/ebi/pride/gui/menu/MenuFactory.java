package uk.ac.ebi.pride.gui.menu;

import uk.ac.ebi.pride.gui.action.PrideAction;

import javax.swing.*;

/**
 * MenuFactory defines the factory to create menus
 *
 * User: rwang
 * Date: 11-Oct-2010
 * Time: 12:17:55
 */
public class MenuFactory {

    /** action separator is used to describe a menu separator */
    public static final PrideAction ACTION_SEPARATOR = null;

    /**
     * Create a new menu, it creates a menu item for every PrideAction from the argument
     * @param name  menu name
     * @param actions   a list of actions
     * @return JMenu    menu
     */
    public static JMenu createMenu(String name, PrideAction ... actions) {
        JMenu menu = new JMenu(name);

        for (PrideAction action : actions) {
            // add a jseparator
            if (action == ACTION_SEPARATOR) {
                menu.addSeparator();
            } else {
                // add a new menu item
                JMenuItem item;

                if (action.isCheckBoxItem()) {
                    item = new JCheckBoxMenuItem(action);
                } else if (action.isRadioItem()) {
                    item = new JRadioButtonMenuItem(action);
                } else {
                    item = new JMenuItem(action);
                }

                // add accelerator if exists
                if (action.isAccelerated()) {
                  item.setAccelerator(KeyStroke.getKeyStroke(action.getKeyCode(), action.getKeyMod()));
                }
                menu.add(item);
            }
        }

        return menu;
    }
}
