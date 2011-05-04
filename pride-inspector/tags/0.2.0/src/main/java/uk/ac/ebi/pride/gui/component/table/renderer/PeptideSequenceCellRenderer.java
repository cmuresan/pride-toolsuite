package uk.ac.ebi.pride.gui.component.table.renderer;

import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.gui.component.ident.PeptideLabel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 27-Jul-2010
 * Time: 11:42:21
 */
public class PeptideSequenceCellRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        PeptideLabel label = null;

        if (value != null && value instanceof Peptide) {

            Peptide peptide = (Peptide) value;
            label = new PeptideLabel(peptide);
            // get the modifications
            java.util.List<Modification> mods = peptide.getModifications();
            // set the ptm string
            // set tooltips
            if (mods != null) {
                String tooltip = getToolTipText(mods, peptide.getSequence().length());
                label.setToolTipText(tooltip);
            }
            // set background
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
            } else {
                label.setBackground(table.getBackground());
            }

            // repaint the component
            label.revalidate();
            label.repaint();
        }
        return label;
    }

    private String getToolTipText(java.util.List<Modification> mods, int seqLength) {
        StringBuffer tip = new StringBuffer();
        if (mods != null) {
            tip.append("<html>");
            for (Modification mod : mods) {
                tip.append("<p>");
                tip.append("<b><font size=\"3\" color=\"red\">" + mod.getAccession() + "</font></b><br>");
                tip.append("<b>Name</b>:" + mod.getName() + "<br>");
                tip.append("<b>Location</b>:");
                int location = mod.getLocation();
                if (location == 0) {
                    tip.append("N-Terminal");
                } else if (location == seqLength) {
                    tip.append("C-Terminal");
                } else {
                    tip.append(location);
                }
                tip.append("<br>");
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
