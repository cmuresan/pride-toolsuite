package uk.ac.ebi.pride.data.core;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08-Feb-2010
 * Time: 10:47:53
 */
public class Peptide extends ParamGroup {
    /** unique id within the scope of the document */
    private String id = null;
    /** peptide name */
    private String name = null;
    /** peptide sequence */
    private String sequence = null;
    /** the start position of the petitide in the protein */
    private BigInteger start = null;
    /** the end position of the petitide in the protein */
    private BigInteger end = null;
    /** a list of modifications */
    private List<Modification> modifications = null;
    /** a list of fragmentIons */
    private List<ParamGroup> fragmentIons = null;
    /** reference to the spectrum */
    private Spectrum spectrum = null;

    public Peptide(ParamGroup params, String id, String name, String sequence,
                   BigInteger start, BigInteger end, List<Modification> modifications,
                   List<ParamGroup> fragmentIons, Spectrum spectrum) {
        super(params);
        this.id = id;
        this.name = name;
        this.sequence = sequence;
        this.start = start;
        this.end = end;
        this.modifications = modifications;
        this.fragmentIons = fragmentIons;
        this.spectrum = spectrum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getSequenceLength() {
        return sequence != null ? sequence.codePointCount(0, sequence.length()) : -1;
    }

    public BigInteger getStart() {
        return start;
    }

    public void setStart(BigInteger start) {
        this.start = start;
    }

    public BigInteger getEnd() {
        return end;
    }

    public void setEnd(BigInteger end) {
        this.end = end;
    }

    public List<Modification> getModifications() {
        return modifications;
    }

    public void setModifications(List<Modification> modifications) {
        this.modifications = modifications;
    }

    public List<ParamGroup> getFragmentIons() {
        return fragmentIons;
    }

    public void setFragmentIons(List<ParamGroup> fragmentIons) {
        this.fragmentIons = fragmentIons;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }
}
