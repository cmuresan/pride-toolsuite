package uk.ac.ebi.pride.pia.intermediate;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * A ModificationType describes the type of a modification via its amino acids,
 * mass shift and a modification name. These settings are usually given by a
 * search engine.
 * 
 * @author julian
 *
 */
public class Modification {
	/** amino acid residue of this modification */
	private Character residue;
	
	/** the accession (if accessible) */
	private String accession;

	/**
	 * Basic constructor, sets all values of the modification type.
	 * 
	 * @param residue
	 */
	public Modification(Character residue,
			String acc) {
		this.residue = residue;
		this.accession = acc;

	}
	
	
	/**
	 * Getter for the residue, i.e. the amino acids, where the modification
	 * occurs.
	 * 
	 * @return
	 */
	public Character getResidue() {
		return residue;
	}

   /**
	 * Getter for the Modification accession.
	 * 
	 * @return
	 */
	public String getAccession() {
		return accession;
	}
}
