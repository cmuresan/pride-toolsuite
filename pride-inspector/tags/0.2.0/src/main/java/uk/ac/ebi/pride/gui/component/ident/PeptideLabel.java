package uk.ac.ebi.pride.gui.component.ident;

import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 12:28:56
 */
public class PeptideLabel extends JPanel {
    private Peptide peptide;
    private AttributedString ptmString;

    public PeptideLabel(Peptide peptide) {
        this.peptide = peptide;
        ptmString = getPTMString(peptide);
        this.setOpaque(true);
    }

    public Peptide getPeptide() {
        return peptide;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString(ptmString.getIterator(), 0, 12);
    }

    private AttributedString getPTMString(Peptide peptide) {
        String sequence = peptide.getSequence();
        java.util.List<Modification> mods = peptide.getModifications();
        AttributedString str = new AttributedString(sequence);
        if (mods != null) {
            int seqLength = sequence.length();
            for (Modification mod : mods) {
                int location = mod.getLocation();
                location = location == 0 ? 1 : location;
                if (seqLength + 1 > location && location > 0) {
                    str.addAttribute(TextAttribute.FOREGROUND, Color.red, location - 1, location);
                }
            }
        }
        return str;
    }
}
