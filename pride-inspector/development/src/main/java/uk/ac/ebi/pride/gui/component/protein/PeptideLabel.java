package uk.ac.ebi.pride.gui.component.protein;

import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;

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
    private static final Color PTM_COLOR = new Color(255, 0, 0, 150);
    private ImageIcon expandIcon;
    private Peptide peptide;
    private AttributedString ptmString;
    private boolean drawIcon;

    public PeptideLabel(Peptide peptide, boolean drawIcon) {
        this.peptide = peptide;
        this.drawIcon = drawIcon;
        ptmString = getPTMString(peptide);
        this.setOpaque(true);
        DesktopContext context = uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        expandIcon = (ImageIcon) GUIUtilities.loadIcon(context.getProperty("ptm.expand.small.icon"));
    }

    public Peptide getPeptide() {
        return peptide;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int offset = 0;
        if (drawIcon && hasModification(peptide)) {
            g2.drawImage(expandIcon.getImage(), 0, 3,this);
            offset = 20;
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString(ptmString.getIterator(), offset, 15);
    }

    private boolean hasModification(Peptide peptide) {
        java.util.List<Modification> mods = peptide.getModifications();
        return mods != null && !mods.isEmpty();
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
                    str.addAttribute(TextAttribute.FOREGROUND, PTM_COLOR, location - 1, location);
                }
            }
        }
        return str;
    }
}
