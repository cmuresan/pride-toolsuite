package uk.ac.ebi.pride.gui.component.metadata;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.component.table.TableFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 2/1/12
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class PeptideIdentificationMetadataPanel extends JPanel{

    public PeptideIdentificationMetadataPanel(SpectrumIdentificationProtocol peptideProtocol) {
        populateComponents(peptideProtocol);
        initComponents();
    }

    private void populateComponents(SpectrumIdentificationProtocol peptideProtocol) {
        // SearchType

        ParamGroup searchType = peptideProtocol.getSearchType();
        ParamGroup analysisParam = peptideProtocol.getAnalysisParam();
        if(searchType != null){
            if(analysisParam != null){
                searchType.addCvParams(analysisParam.getCvParams());
                searchType.addUserParams(analysisParam.getUserParams());
            }
        }else if(analysisParam!=null){
            searchType = analysisParam;
        }


        if(searchType != null){
           searchtypeTable = TableFactory.createParamTable(searchType);
        } else {
            searchtypeTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }

        // Enzyme
        List<Enzyme> enzymeList = peptideProtocol.getEnzymeList();
        ParamGroup enzymeParamGroup = new ParamGroup();
        for (Enzyme enzyme: enzymeList){
            if(enzyme.getEnzymeName().getCvParams() !=null) enzymeParamGroup.addCvParams(enzyme.getEnzymeName().getCvParams());
            if(enzyme.getEnzymeName().getUserParams() !=null) enzymeParamGroup.addUserParams(enzyme.getEnzymeName().getUserParams());
        }

        if (enzymeParamGroup != null) {
            thresholdTable = TableFactory.createParamTable(enzymeParamGroup);
        } else {
            thresholdTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }

        // detector
        List<CvParam> fragmentTolerance = peptideProtocol.getFragmentTolerance();
        List<CvParam> parentTolerance   = peptideProtocol.getParentTolerance();
        ParamGroup threshold = peptideProtocol.getThreshold();

        ParamGroup allThreshold = new ParamGroup();
        if(threshold.getCvParams()!=null) allThreshold.addCvParams(threshold.getCvParams());
        if(threshold.getUserParams()!=null) allThreshold.addUserParams(threshold.getUserParams());
        if(fragmentTolerance != null) allThreshold.addCvParams(fragmentTolerance);
        if(parentTolerance != null) allThreshold.addCvParams(parentTolerance);

        if (allThreshold != null) {
            enzymesTable = TableFactory.createParamTable(allThreshold);
        } else {
            enzymesTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        searchtypeLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        enzymesLabel = new JLabel();
        thresholdLabel = new JLabel();
        scrollPane2 = new JScrollPane();
        scrollPane3 = new JScrollPane();

        //======== this ========

        //---- searchtypeLabel ----
        searchtypeLabel.setText("Search Type & Search Parameters");


        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(searchtypeTable);
        }

        //---- enzymesLabel ----
        enzymesLabel.setText("Enzymes");

        //---- thresholdLabel ----
        thresholdLabel.setText("Thresholds & Fragment/Parent Tolerances");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(enzymesTable);
        }

        //======== scrollPane3 ========
        {
            scrollPane3.setViewportView(thresholdTable);
        }

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, scrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, scrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, scrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, searchtypeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 210, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, enzymesLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, thresholdLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 310, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .add(layout.createSequentialGroup()
                    .add(8, 8, 8)
                    .add(searchtypeLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(scrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(enzymesLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(scrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(thresholdLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(scrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel searchtypeLabel;
    private JScrollPane scrollPane1;
    private JTable searchtypeTable;
    private JLabel enzymesLabel;
    private JLabel thresholdLabel;
    private JScrollPane scrollPane2;
    private JTable enzymesTable;
    private JScrollPane scrollPane3;
    private JTable thresholdTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
