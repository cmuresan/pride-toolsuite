package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.ui.tabbedui.VerticalLayout;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * <p></p>
 *
 * @author Antonio Fabregat
 * Date: 23-sep-2010
 * Time: 14:35:15
 */
public class ChartErrorPanel extends JPanel {
    public enum Type {
        SMALL,
        LARGE
    }
    
    public ChartErrorPanel(List<String> messages, Type type){

        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        GridBagConstraints c = getGridBagConstraints();
        JPanel center = centralPanel(messages, type);
        add(center, c);
    }

    private GridBagConstraints getGridBagConstraints(){
        GridBagConstraints c = new GridBagConstraints();

        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;

        return c;
    }

    private JPanel centralPanel(List<String> messages, Type type){
        JPanel center = new JPanel(new VerticalLayout());
        center.setBackground(Color.WHITE);

        // get property manager
        DesktopContext context = uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        Icon warnIcon;
        int fontSize;
        switch(type){
            case LARGE:
                warnIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_warning.icon.medium"));
                fontSize = 15;
                break;
            case SMALL:
                warnIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_warning.icon.small"));
                fontSize = 12;
                break;
            default:
                warnIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_warning.icon.small"));
                fontSize = 10;
        }

        JLabel iconLabel = new JLabel(warnIcon, JLabel.CENTER);
        iconLabel.setAlignmentX(0.5f);
        center.add(iconLabel);

        String msg = "This chart could not be generated because:";
        JLabel common = new JLabel(msg);
        common.setFont(new Font("Serif", Font.BOLD, fontSize));
        common.setAlignmentX(0.5f);
        center.add(common);

        for(String message : messages){
            JLabel error = new JLabel(" - "+message);
            error.setFont(new Font("Serif", Font.PLAIN, fontSize));
            error.setAlignmentX(0.5f);
            center.add(error);
        }

        return center;
    }
}