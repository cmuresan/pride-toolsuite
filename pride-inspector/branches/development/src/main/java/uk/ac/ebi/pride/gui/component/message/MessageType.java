package uk.ac.ebi.pride.gui.component.message;

import uk.ac.ebi.pride.gui.GUIUtilities;

import javax.swing.*;

/**
 * MessageType contains a list of message types with their icon types.
 *
 * User: rwang
 * Date: 16-Nov-2010
 * Time: 11:36:56
 */
public enum MessageType {
    INFO(GUIUtilities.loadIcon(uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getProperty("info.message.small.icon"))),
    WARNING(GUIUtilities.loadIcon(uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getProperty("warning.message.small.icon"))),
    ERROR(GUIUtilities.loadIcon(uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getProperty("error.message.small.icon")));

    private Icon icon;

    private MessageType(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }
}
