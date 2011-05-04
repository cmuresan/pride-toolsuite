package uk.ac.ebi.pride.gui.action;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 09-Feb-2010
 * Time: 15:57:47
 */
public abstract class PrideAction extends AbstractAction implements MenuListener {

    private int keyCode;
    private int keyMod;
    private boolean hasAccelerator;
    private String menuLocation;
    private boolean isCheckBoxItem;

    public PrideAction() {
        super();
        initialize();
    }

    public PrideAction(String name) {
        super(name);
        initialize();
    }

    public PrideAction(String name, javax.swing.Icon icon) {
        super(name, icon);
        initialize();
    }

    public void initialize() {
        // 
    }

    @Override
    public abstract void actionPerformed(ActionEvent e);

    @Override
    public void menuSelected(MenuEvent e) { }

    @Override
    public void menuDeselected(MenuEvent e) { }

    @Override
    public void menuCanceled(MenuEvent e) { }

    /**
     * By default Pride Action will be in the menu bar
     * @return
     */
    public boolean isInMenuBar() {
        return true;
    }

    /**
     * By default Pride Action will not be in the tool bar
     * @return
     */
    public boolean isInToolBar() {
        return false;
    }

    public void setAccelerator(int keyCode, int keyMod) {
        hasAccelerator = true;
        this.keyCode = keyCode;
        this.keyMod = keyMod;
    }

    public boolean isAccelerated() {
        return hasAccelerator;        
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyMod() {
        return keyMod;
    }

    public void setKeyMod(int keyMod) {
        this.keyMod = keyMod;
    }

    public String getMenuLocation() {
        return menuLocation;
    }

    public void setMenuLocation(String menuLocation) {
        this.menuLocation = menuLocation;
    }

    public boolean isCheckBoxItem() {
        return isCheckBoxItem;
    }

    public void setCheckBoxItem(boolean checkBoxItem) {
        isCheckBoxItem = checkBoxItem;
    }
}
