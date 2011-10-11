package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * The class Peptide Manage the information for peptide Identifications
 * User: yperez
 * Date: 08/08/11
 * Time: 12:39
 */
public class Peptide extends IdentifiableParamGroup {

    /**
     * The theoretical mass-to-charge value calculated for the peptide in Daltons / charge.
     */
    private double calculatedMassToCharge = 0.0;

    /**
     * The calculated isoelectric point of the (poly)peptide, with relevant
     * modifications included. Do not supply this value if the PI cannot be
     * calculated properly.
     */
    private double calculatedPI = 0.0;

    /**
     * The charge state of the identified peptide.
     */
    private int chargeState = -1;

    /**
     * The mass-to-charge value measured in the experiment in Daltons / charge.
     */
    private double experimentalMassToCharge = 0.0;

    /**
     * The product ions identified in this result.
     */
    private List<FragmentIon> fragmentation = null;

    /**
     * A reference should be given to the MassTable used to calculate the
     * sequenceMass only if more than one MassTable has been given.
     */
    private MassTable massTableRef = null;

    /**
     * Reference to the PeptideEvidence element identified. If a specific
     * sequence can be assigned to multiple proteins and or positions in a
     * protein all possible PeptideEvidence elements should be referenced here.
     */
    private List<PeptideEvidence> peptideEvidenceList = null;

    /**
     * PeptideScore stores a number of peptide scores for a list of search engines.
     */
    private PeptideScore peptideScore = null;

    /**
     * A reference to the identified (poly)peptide sequence in the Peptide element.
     */
    private PeptideSequence peptideSequence = null;

    /**
     * For an MS/MS result set, this is the rank of the identification quality as
     * scored by the search engine. 1 is the top rank. If multiple identifications
     * have the same top score, they should all be assigned rank =1. For PMF data, the
     * rank attribute may be meaningless and values of rank = 0 should be given.
     */
    private int rank = -1;

    /**
     * Set to true if the producers of the file has deemed that the identification
     * has passed a given threshold or been validated as correct.
     * If no such threshold has been set, value of true should be given for all
     * results.
     */
    private boolean passThreshold = false;

    /**
     * A reference should be provided to link the SpectrumIdentificationItem
     * to a Sample if more than one sample has been described in the
     * AnalysisSampleCollection.
     */
    private Sample sample = null;

    /**
     * A reference to a spectra data set (e.g. a spectra file).
     */
    private SpectraData spectraData = null;

    /**
     * The locally unique id for the spectrum in the spectra data set specified
     * by SpectraData_ref. External guidelines are provided on the use of
     * consistent identifiers for spectra in different external formats.
     */
    private Spectrum spectrum = null;

    /**
     * @param id
     * @param name
     * @param chargeState
     * @param experimentalMassToCharge
     * @param calculatedMassToCharge
     * @param calculatedPI
     * @param peptideSequence
     * @param rank
     * @param passThreshold
     * @param massTableRef
     * @param sample
     * @param peptideEvidenceList
     * @param fragmentation
     * @param peptideScore
     * @param spectrum
     * @param spectraData
     */
    public Peptide(Comparable id, String name, int chargeState, double experimentalMassToCharge,
                   double calculatedMassToCharge, double calculatedPI, PeptideSequence peptideSequence, int rank,
                   boolean passThreshold, MassTable massTableRef, Sample sample,
                   List<PeptideEvidence> peptideEvidenceList, List<FragmentIon> fragmentation,
                   PeptideScore peptideScore, Spectrum spectrum, SpectraData spectraData) {
        this(null, id, name, chargeState, experimentalMassToCharge, calculatedMassToCharge, calculatedPI,
             peptideSequence, rank, passThreshold, massTableRef, sample, peptideEvidenceList, fragmentation,
             peptideScore, spectrum, spectraData);
    }

    public Peptide(ParamGroup params, Comparable id, String name, int chargeState, double experimentalMassToCharge,
                   double calculatedMassToCharge, double calculatedPI, PeptideSequence peptideSequence, int rank,
                   boolean passThreshold, MassTable massTableRef, Sample sample,
                   List<PeptideEvidence> peptideEvidenceList, List<FragmentIon> fragmentation,
                   PeptideScore peptideScore, Spectrum spectrum, SpectraData spectraData) {
        super(params, id, name);
        this.chargeState              = chargeState;
        this.experimentalMassToCharge = experimentalMassToCharge;
        this.calculatedMassToCharge   = calculatedMassToCharge;
        this.calculatedPI             = calculatedPI;
        this.peptideSequence          = peptideSequence;
        this.rank                     = rank;
        this.passThreshold            = passThreshold;
        this.massTableRef             = massTableRef;
        this.sample                   = sample;
        this.peptideEvidenceList      = peptideEvidenceList;
        this.fragmentation            = fragmentation;
        this.peptideScore             = peptideScore;
        this.spectrum                 = spectrum;
        this.spectraData              = spectraData;
    }

    public int getChargeState() {
        return chargeState;
    }

    public void setChargeState(int chargeState) {
        this.chargeState = chargeState;
    }

    public double getExperimentalMassToCharge() {
        return experimentalMassToCharge;
    }

    public void setExperimentalMassToCharge(double experimentalMassToCharge) {
        this.experimentalMassToCharge = experimentalMassToCharge;
    }

    public double getCalculatedMassToCharge() {
        return calculatedMassToCharge;
    }

    public void setCalculatedMassToCharge(double calculatedMassToCharge) {
        this.calculatedMassToCharge = calculatedMassToCharge;
    }

    public double getCalculatedPI() {
        return calculatedPI;
    }

    public void setCalculatedPI(double calculatedPI) {
        this.calculatedPI = calculatedPI;
    }

    public PeptideSequence getPeptideSequence() {
        return peptideSequence;
    }

    public void setPeptideSequence(PeptideSequence peptideSequence) {
        this.peptideSequence = peptideSequence;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isPassThreshold() {
        return passThreshold;
    }

    public void setPassThreshold(boolean passThreshold) {
        this.passThreshold = passThreshold;
    }

    public MassTable getMassTableRef() {
        return massTableRef;
    }

    public void setMassTableRef(MassTable massTableRef) {
        this.massTableRef = massTableRef;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public List<PeptideEvidence> getPeptideEvidenceList() {
        return peptideEvidenceList;
    }

    public void setPeptideEvidenceList(List<PeptideEvidence> peptideEvidenceList) {
        this.peptideEvidenceList = peptideEvidenceList;
    }

    public List<FragmentIon> getFragmentation() {
        return fragmentation;
    }

    public void setFragmentation(List<FragmentIon> fragmentation) {
        this.fragmentation = fragmentation;
    }

    public PeptideScore getPeptideScore() {
        return peptideScore;
    }

    public void setPeptideScore(PeptideScore peptideScore) {
        this.peptideScore = peptideScore;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public SpectraData getSpectraData() {
        return spectraData;
    }

    public void setSpectraData(SpectraData spectraData) {
        this.spectraData = spectraData;
    }

    public boolean hasModification() {
        return (!(getPeptideSequence().getModificationList().isEmpty()));
    }

    public int getSequenceLength() {
        return getPeptideSequence().getSequence().length();
    }

    public List<Modification> getModifications() {
        return getPeptideSequence().getModificationList();
    }

    public String getSequence() {
        return getPeptideSequence().getSequence();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
