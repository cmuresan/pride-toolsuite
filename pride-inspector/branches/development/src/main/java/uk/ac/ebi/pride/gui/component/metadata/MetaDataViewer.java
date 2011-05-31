package uk.ac.ebi.pride.gui.component.metadata;

import uk.ac.ebi.pride.data.core.Parameter;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.component.utils.SharedLabels;
import uk.ac.ebi.pride.gui.url.HyperLinkFollower;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.Collection;

/**
 * User: rwang
 * Date: 25-May-2010
 * Time: 11:01:19
 */
public class MetaDataViewer extends JPanel {
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(246, 246, 248);
    private final Collection<Parameter> params;

    public MetaDataViewer(Collection<Parameter> params) {
        this.params = params;
        setMainPane();
        addComponents();
    }

    private void setMainPane() {
        this.setLayout(new GridBagLayout());
        this.setBackground(DEFAULT_BACKGROUND_COLOR);
    }

    private void addComponents() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(1, 10, 1, 10);
        c.anchor = GridBagConstraints.WEST;

        Font font = UIManager.getDefaults().getFont("Label.font");
        Font newFont = new Font(font.getFamily(), Font.PLAIN, font.getSize() + 2);

        for (int i = 0; i < params.size(); i++) {
            Parameter param = CollectionUtils.getElement(params, i);
            String name = param.getName();
            String value = param.getValue();
            if (value == null || "".equals(value.trim())) {
                value = name;
                name = SharedLabels.PARAMETER;
            }
            // add name label
            name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
            JLabel label = new JLabel(name);
            label.setPreferredSize(new Dimension(200, 20));
            label.setToolTipText(name);
            label.setFont(newFont);
            c.gridx = 0;
            c.gridy = i;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 0;
            this.add(label, c);
            // add value textfield
            JComponent textComp;
            if (value.contains("<html>")) {
                JTextPane textPane = new JTextPane();
                DefaultCaret caret = (DefaultCaret) textPane.getCaret();
                caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
                textPane.setFont(newFont);
                textPane.setContentType("text/html");
                textPane.setEditorKit(new HTMLEditorKit());
                textPane.setText(value);
                textPane.setEditable(false);
                textPane.addHyperlinkListener(new HyperLinkFollower());
                textComp = textPane;
            } else {
                JTextArea textArea = new JTextArea();
                DefaultCaret caret = (DefaultCaret) textArea.getCaret();
                caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
                textArea.setFont(newFont);
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setText(value);
                textComp = textArea;
            }

            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            this.add(textComp, c);
        }
    }
}
