package uk.ac.ebi.pride.gui.component.sequence;

import uk.ac.ebi.pride.data.controller.DataAccessController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08/06/11
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class ProteinSequencePane extends JPanel {
    private DataAccessController controller;

    public ProteinSequencePane(DataAccessController controller) {
        this.controller = controller;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int x = 10;
        int y = 30;
        int width = getWidth();
        int height = getHeight();

        Graphics2D g2 = (Graphics2D)g.create();

        // set the rendering hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // get current font
        Font font = g2.getFont().deriveFont(15f);

        // set the new font
        g2.setFont(font);

        // get font metrics
        FontMetrics metrics = g2.getFontMetrics(font);

        // get the height of the text
        int textHeight = metrics.getHeight();

        String seq = "M A S D F A S D A S D A S D      ";


        // get the width of the text
        int textWidth = metrics.stringWidth(seq);

        // set composite
//        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2.setPaint(Color.yellow);
        g2.fillRect(x - 1, y - textHeight + 5, textWidth + 2, textHeight - 1);

        g2.setPaint(Color.black);
        g2.drawString(seq, x, y);

        g2.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                JScrollPane scrollPane = new JScrollPane(new ProteinSequencePane(null), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                frame.setContentPane(scrollPane);
                frame.setPreferredSize(new Dimension(200, 200));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public void subscribeToEventBus() {
        //To change body of created methods use File | Settings | File Templates.
    }
}
