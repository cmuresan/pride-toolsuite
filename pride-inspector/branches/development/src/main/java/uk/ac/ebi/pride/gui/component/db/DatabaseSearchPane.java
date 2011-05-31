package uk.ac.ebi.pride.gui.component.db;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
/*
 * Created by JFormDesigner on Tue May 24 09:50:00 BST 2011
 */


/**
 * @author User #2
 */
public class DatabaseSearchPane extends JPanel {
    public DatabaseSearchPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        searchLabel = new JLabel();
        categoryComboBox = new JComboBox();
        criteriaComboBox = new JComboBox();
        searchTextField = new JTextField();
        searchButton = new JButton();
        panel4 = new JPanel();
        scrollPane1 = new JScrollPane();
        searchResultTable = new JTable();
        resultSummaryPanel = new JPanel();
        searchResultLabel = new JLabel();

        //======== this ========
        setBackground(Color.white);
        setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setBackground(Color.white);
            panel1.setOpaque(false);
            panel1.setLayout(new FlowLayout());

            //======== panel2 ========
            {
                panel2.setBackground(Color.white);
                panel2.setOpaque(false);

                //======== panel3 ========
                {
                    panel3.setBorder(new LineBorder(Color.black));
                    panel3.setBackground(new Color(214, 241, 249));

                    //---- searchLabel ----
                    searchLabel.setText("Search for:");

                    //---- categoryComboBox ----
                    categoryComboBox.setOpaque(false);

                    //---- criteriaComboBox ----
                    criteriaComboBox.setOpaque(false);

                    //---- searchButton ----
                    searchButton.setText("Search");
                    searchButton.setOpaque(false);

                    GroupLayout panel3Layout = new GroupLayout(panel3);
                    panel3.setLayout(panel3Layout);
                    panel3Layout.setHorizontalGroup(
                        panel3Layout.createParallelGroup()
                            .add(panel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(searchLabel)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(categoryComboBox, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(criteriaComboBox, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                .add(12, 12, 12)
                                .add(searchTextField, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(searchButton)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                    panel3Layout.setVerticalGroup(
                        panel3Layout.createParallelGroup()
                            .add(GroupLayout.TRAILING, panel3Layout.createSequentialGroup()
                                .addContainerGap(16, Short.MAX_VALUE)
                                .add(panel3Layout.createParallelGroup(GroupLayout.BASELINE)
                                    .add(searchLabel)
                                    .add(categoryComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .add(searchTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .add(searchButton)
                                    .add(criteriaComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(19, 19, 19))
                    );
                }

                GroupLayout panel2Layout = new GroupLayout(panel2);
                panel2.setLayout(panel2Layout);
                panel2Layout.setHorizontalGroup(
                    panel2Layout.createParallelGroup()
                        .add(panel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .add(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                );
                panel2Layout.setVerticalGroup(
                    panel2Layout.createParallelGroup()
                        .add(panel2Layout.createSequentialGroup()
                            .add(20, 20, 20)
                            .add(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(0, 0, 0))
                );
            }
            panel1.add(panel2);
        }
        add(panel1, BorderLayout.NORTH);

        //======== panel4 ========
        {
            panel4.setBackground(Color.white);

            //======== scrollPane1 ========
            {
                scrollPane1.setOpaque(false);

                //---- searchResultTable ----
                searchResultTable.setBorder(null);
                searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                searchResultTable.setFillsViewportHeight(true);
                scrollPane1.setViewportView(searchResultTable);
            }

            //======== resultSummaryPanel ========
            {
                resultSummaryPanel.setBackground(Color.white);
                resultSummaryPanel.setOpaque(false);
                resultSummaryPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                resultSummaryPanel.add(searchResultLabel);
            }

            GroupLayout panel4Layout = new GroupLayout(panel4);
            panel4.setLayout(panel4Layout);
            panel4Layout.setHorizontalGroup(
                panel4Layout.createParallelGroup()
                    .add(GroupLayout.TRAILING, panel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(panel4Layout.createParallelGroup(GroupLayout.TRAILING)
                            .add(GroupLayout.LEADING, scrollPane1, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                            .add(GroupLayout.LEADING, resultSummaryPanel, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
                        .addContainerGap())
            );
            panel4Layout.setVerticalGroup(
                panel4Layout.createParallelGroup()
                    .add(panel4Layout.createSequentialGroup()
                        .add(resultSummaryPanel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(scrollPane1, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                        .addContainerGap())
            );
        }
        add(panel4, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JLabel searchLabel;
    private JComboBox categoryComboBox;
    private JComboBox criteriaComboBox;
    private JTextField searchTextField;
    private JButton searchButton;
    private JPanel panel4;
    private JScrollPane scrollPane1;
    private JTable searchResultTable;
    private JPanel resultSummaryPanel;
    private JLabel searchResultLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
