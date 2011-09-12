package uk.ac.ebi.pride.gui.component.table.renderer;

import uk.ac.ebi.pride.data.coreIdent.Modification;
import uk.ac.ebi.pride.data.coreIdent.Peptide;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * Cell renderer for peptide sequence  with PTMs
 * User: rwang
 * Date: 27-Jul-2010
 * Time: 11:42:21
 */
public class PeptideSequenceCellRenderer extends JLabel implements TableCellRenderer {
    private static final Color PTM_COLOR = new Color(255, 0, 0, 150);
    public final static Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 16);
    public final static Font DEFAULT_FONT_BOLD = new Font(Font.MONOSPACED, Font.BOLD, 16);
    private AttributedString ptmString = null;

    public PeptideSequenceCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        if (value != null && value instanceof Peptide) {

            Peptide peptide = (Peptide) value;
            ptmString = getPTMString(peptide);
            // get the modifications
            java.util.List<Modification> mods = peptide.getPeptideSequence().getModificationList();
            // set the ptm string
            // set tooltips
            if (mods != null) {
                String tooltip = getToolTipText(mods, peptide.getPeptideSequence().getSequence().length());
                if (!tooltip.trim().equals("")) {
                    this.setToolTipText(tooltip);
                }
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
        Graphics2D g2 = (Graphics2D) g.create();
        int offset = 0;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString(ptmString.getIterator(), offset, 15);
        g2.dispose();
    }

    private AttributedString getPTMString(Peptide peptide) {
        String sequence = peptide.getPeptideSequence().getSequence();
        java.util.List<Modification> mods = peptide.getPeptideSequence().getModificationList();
        AttributedString str = new AttributedString(sequence);
        str.addAttribute(TextAttribute.FONT, DEFAULT_FONT);
        if (mods != null) {
            int seqLength = sequence.length();
            for (Modification mod : mods) {
                int location = mod.getLocation();
                location = location == 0 ? 1 : location;
                if (seqLength + 1 > location && location > 0) {
                    str.addAttribute(TextAttribute.FOREGROUND, PTM_COLOR, location - 1, location);
                    str.addAttribute(TextAttribute.FONT, DEFAULT_FONT_BOLD, location - 1, location);
                }
            }
        }
        return str;
    }

    private String getToolTipText(java.util.List<Modification> mods, int seqLength) {
        StringBuilder tip = new StringBuilder();
        if (mods != null && !mods.isEmpty()) {
            tip.append("<html>");
            for (Modification mod : mods) {
                tip.append("<p>");
                tip.append("<b><font size=\"3\" color=\"red\">");
                tip.append(mod.getId().toString());
                tip.append("</font></b><br>");
                tip.append("<b>Name</b>:");
                tip.append(mod.getName());
                tip.append("<br>");
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
                java.util.List<Double> avgs = mod.getAvgMassDelta();
                if (avgs != null) {
                    for (Double avg : avgs) {
                        tip.append("<b>Average Mass Delta</b>:");
                        tip.append(avg);
                        tip.append("<br>");
                    }
                }
                java.util.List<Double> monos = mod.getMonoisotopicMassDelta();
                if (monos != null) {
                    for (Double mono : monos) {
                        tip.append("<b>Mono Mass Delta</b>:");
                        tip.append(mono);
                        tip.append("<br>");
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
