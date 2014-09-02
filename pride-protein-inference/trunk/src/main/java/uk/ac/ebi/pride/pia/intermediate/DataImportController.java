package uk.ac.ebi.pride.pia.intermediate;


/**
 * An interface for the data import controllers for PIA input files.
 * 
 * @author julian
 *
 */
public interface DataImportController {
	
	/**
	 * Returns the ID of the controller.
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Comparable getID();
	
	
	/**
	 * Returns the file name which is handled by this controller
	 * 
	 * @return
	 */
	public String getInputFileName();
	
	
	/**
	 * Adds all spectrum Identifications to the {@link IntermediateStructureCreator}
	 * 
	 * @param structCreator
	 * @param filterPSMsOnImport whether the PSMs should be filered during the import
	 */
	public void addAllSpectrumIdentificationsToStructCreator(IntermediateStructureCreator structCreator);
	
	
	/**
	 * Some controllers should be closed after usage.
	 */
	public void close();
}
