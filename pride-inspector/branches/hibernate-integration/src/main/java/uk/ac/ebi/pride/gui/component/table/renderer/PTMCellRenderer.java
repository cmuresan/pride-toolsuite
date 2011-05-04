package uk.ac.ebi.pride.gui.component.table.renderer;

import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 27-Jul-2010
 * Time: 11:42:21
 */
public class PTMCellRenderer extends JLabel implements TableCellRenderer {

    private AttributedString ptmString;

    public PTMCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value != null && value instanceof Peptide) {

            Peptide peptide = (Peptide) value;
            // get the raw sequence
            String sequence = peptide.getSequence();
            // get the modifications
            java.util.List<Modification> mods = peptide.getModifications();
            // set the ptm string
            ptmString = getPTMString(sequence, mods);
            // set tooltips
            if (mods != null) {
                String tooltip = getToolTipText(mods);
                setToolTipText(tooltip);
            }
            // set background
            if (isSelected) {
                this.setBackground(table.getSelectionBackground());
            } else {
                this.setBackground(table.getBackground());
            }

            // repaint the component
            this.revalidate();
            this.repaint();
        }
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ptmString != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.drawString(ptmString.getIterator(), 0, 12);
        }
    }

    private AttributedString getPTMString(String sequence, java.util.List<Modification> mods) {
        AttributedString str = new AttributedString(sequence);
        if (mods != null) {
            for (Modification mod : mods) {
                int location = mod.getLocation();
                if (sequence.length() > location) {
                    str.addAttribute(TextAttribute.FOREGROUND, Color.red, location, location + 1);
                }
            }
        }
        return str;
    }

    private String getToolTipText(java.util.List<Modification> mods) {
        StringBuffer tip = new StringBuffer();
        if (mods != null) {
            tip.append("<html>");
            for (Modification mod : mods) {
                tip.append("<p>");
                tip.append("<b><font size=\"3\" color=\"red\">" + mod.getAccession() + "</font></b><br>");
                tip.append("<b>Name</b>:" + mod.getName() + "<br>");
                tip.append("<b>Location</b>:" + mod.getLocation() + "<br>");
                java.util.List<Double> avgs = mod.getAvgMassDeltas();
                if (avgs != null) {
                    for (Double avg : avgs) {
                        tip.append("<b>Average Mass Delta</b>:" + avg + "<br>");
                    }
                }
                java.util.List<Double> monos = mod.getMonoMassDeltas();
                if (monos != null) {
                    for (Double mono : monos) {
                        tip.append("<b>Mono Mass Delta</b>:" + mono + "<br>");
                    }
                }
                tip.append("</p>");
                tip.append("<br>");
            }
            tip.append("</html>");
        }
        return tip.toString();
    }
}
