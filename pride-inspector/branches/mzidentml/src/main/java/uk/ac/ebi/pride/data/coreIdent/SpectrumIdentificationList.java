package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

/**
 * Represents the set of all search results from SpectrumIdentification.
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 08/08/11
 * Time: 12:14
 */
public class SpectrumIdentificationList extends IdentifiableParamGroup {
    /**
     * The number of database sequences searched against. This value should
     * be provided unless a de novo search has been performed.
     */
    private int numSequenceSearched = -1;
    /**
     * Contains the types of measures that will be reported in generic arrays for
     * each SpectrumIdentificationItem e.g. product ion m/z, product ion
     * intensity, product ion m/z error. Fragmentation Table is used as
     */
    private List<IdentifiableParamGroup> fragmentationTable = null;
    /**
     * All identifications made from searching one spectrum.
     * For PMF data, all peptide identifications will be listed underneath as
     * SpectrumIdentificationItems. For MS/MS data, there will be ranked
     * SpectrumIdentificationItems corresponding to possible different
     * peptide IDs.
     */
    private List<Peptide> spectrumIdentificationList = null;

    public SpectrumIdentificationList(Comparable id,
                                      String name,
                                      int numSequenceSearched,
                                      List<IdentifiableParamGroup> fragmentationTable,
                                      List<Peptide> spectrumIdentificationList) {
        super(id, name);
        this.numSequenceSearched = numSequenceSearched;
        this.fragmentationTable = fragmentationTable;
        this.spectrumIdentificationList = spectrumIdentificationList;
    }

    public SpectrumIdentificationList(ParamGroup params,
                                      Comparable id,
                                      String name,
                                      int numSequenceSearched,
                                      List<IdentifiableParamGroup> fragmentationTable,
                                      List<Peptide> spectrumIdentificationList) {
        super(params, id, name);
        this.numSequenceSearched = numSequenceSearched;
        this.fragmentationTable = fragmentationTable;
        this.spectrumIdentificationList = spectrumIdentificationList;
    }

    public int getNumSequenceSearched() {
        return numSequenceSearched;
    }

    public void setNumSequenceSearched(int numSequenceSearched) {
        this.numSequenceSearched = numSequenceSearched;
    }

    public List<IdentifiableParamGroup> getFragmentationTable() {
        return fragmentationTable;
    }

    public void setFragmentationTable(List<IdentifiableParamGroup> fragmentationTable) {
        this.fragmentationTable = fragmentationTable;
    }

    public List<Peptide> getSpectrumIdentificationResultList() {
        return spectrumIdentificationList;
    }

    public void setSpectrumIdentificationResultList(List<Peptide> spectrumIdentificationItemList) {
        this.spectrumIdentificationList = spectrumIdentificationItemList;
    }
}
