package uk.ac.ebi.pride.gui.utils;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ProteinAccession {
    private String accession;
    private String mappedAccession;

    public ProteinAccession(String accession, String mappedAccession) {
        this.accession = accession;
        this.mappedAccession = mappedAccession;
    }

    public String getAccession() {
        return accession;
    }

    public String getMappedAccession() {
        return mappedAccession;
    }
}
