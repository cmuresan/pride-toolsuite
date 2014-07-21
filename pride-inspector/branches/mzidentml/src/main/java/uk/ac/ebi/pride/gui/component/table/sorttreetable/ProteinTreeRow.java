package uk.ac.ebi.pride.gui.component.table.sorttreetable;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableRow;
import uk.ac.ebi.pride.gui.utils.ProteinAccession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yperez on 02/06/2014.
 */
public class ProteinTreeRow extends DefaultMutableTreeTableNode {

    private Boolean comparisonState;

    private ProteinAccession proteinAccession;

    private String proteinName;

    private String proteinAccessionStatus;

    private Double sequenceCoverage;

    private Double isoelectricPoint;

    private Double threshold;

    private Integer numberOfPeptides;

    private Integer numberOfUniquePeptides;

    private Integer numberOfPTMs;

    private Comparable proteinId;

    private Comparable proteinGroupId;

    private final List<Double> scores;

    private final List<Object> quantifications;

    private final List<ProteinTreeRow> childProteinTableRows;

    public ProteinTreeRow() {
        super(null);
        this.scores = new ArrayList<Double>();
        this.quantifications = new ArrayList<Object>();
        this.childProteinTableRows = new ArrayList<ProteinTreeRow>();
    }

    public ProteinTreeRow(Object[] data) {
        super(data);
        this.scores = new ArrayList<Double>();
        this.quantifications = new ArrayList<Object>();
        this.childProteinTableRows = new ArrayList<ProteinTreeRow>();
    }

    public ProteinTreeRow(ProteinTableRow proteinTableRow){
        super(null);
        this.comparisonState  = proteinTableRow.getComparisonState();
        this.proteinAccession = proteinTableRow.getProteinAccession();
        this.proteinName      = proteinTableRow.getProteinName();
        this.proteinAccessionStatus = proteinTableRow.getProteinAccessionStatus();
        this.sequenceCoverage  = proteinTableRow.getSequenceCoverage();
        this.isoelectricPoint = proteinTableRow.getIsoelectricPoint();
        this.threshold = proteinTableRow.getThreshold();
        this.numberOfPeptides = proteinTableRow.getNumberOfPeptides();
        this.numberOfUniquePeptides = proteinTableRow.getNumberOfUniquePeptides();
        this.numberOfPTMs = proteinTableRow.getNumberOfPTMs();
        this.proteinId    = proteinTableRow.getProteinId();
        this.proteinGroupId = proteinTableRow.getProteinGroupId();
        this.scores = proteinTableRow.getScores();
        this.quantifications = proteinTableRow.getQuantifications();
        this.childProteinTableRows = new ArrayList<ProteinTreeRow>();
        for(ProteinTableRow child: proteinTableRow.getChildProteinTableRows()){
            this.childProteinTableRows.add(new ProteinTreeRow(child));
        }
    }
    public Boolean getComparisonState() {
        return comparisonState;
    }

    public void setComparisonState(Boolean comparisonState) {
        this.comparisonState = comparisonState;
    }

    public ProteinAccession getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(ProteinAccession accession) {
        this.proteinAccession = accession;
    }

    public String getProteinName() {
        return proteinName;
    }

    public void setProteinName(String proteinName) {
        this.proteinName = proteinName;
    }

    public String getProteinAccessionStatus() {
        return proteinAccessionStatus;
    }

    public void setProteinAccessionStatus(String proteinAccessionStatus) {
        this.proteinAccessionStatus = proteinAccessionStatus;
    }

    public Double getSequenceCoverage() {
        return sequenceCoverage;
    }

    public void setSequenceCoverage(Double sequenceCoverage) {
        this.sequenceCoverage = sequenceCoverage;
    }

    public Double getIsoelectricPoint() {
        return isoelectricPoint;
    }

    public void setIsoelectricPoint(Double isoelectricPoint) {
        this.isoelectricPoint = isoelectricPoint;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Integer getNumberOfPeptides() {
        return numberOfPeptides;
    }

    public void setNumberOfPeptides(Integer numberOfPeptides) {
        this.numberOfPeptides = numberOfPeptides;
    }

    public Integer getNumberOfUniquePeptides() {
        return numberOfUniquePeptides;
    }

    public void setNumberOfUniquePeptides(Integer numberOfUniquePeptides) {
        this.numberOfUniquePeptides = numberOfUniquePeptides;
    }

    public Integer getNumberOfPTMs() {
        return numberOfPTMs;
    }

    public void setNumberOfPTMs(Integer numberOfPTMs) {
        this.numberOfPTMs = numberOfPTMs;
    }

    public Comparable getProteinId() {
        return proteinId;
    }

    public void setProteinId(Comparable proteinId) {
        this.proteinId = proteinId;
    }

    public Comparable getProteinGroupId() {
        return proteinGroupId;
    }

    public void setProteinGroupId(Comparable proteinGroupId) {
        this.proteinGroupId = proteinGroupId;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void addScore(Double score) {
        scores.add(score);
    }

    public List<Object> getQuantifications() {
        return quantifications;
    }

    public void addQuantifications(List<Object> quantifications) {
        this.quantifications.addAll(quantifications);
    }

    public List<ProteinTreeRow> getChildProteinTableRows() {
        return childProteinTableRows;
    }

    public void addChildProteinTableRow(ProteinTreeRow proteinTableRow) {
        this.childProteinTableRows.add(proteinTableRow);
    }


}