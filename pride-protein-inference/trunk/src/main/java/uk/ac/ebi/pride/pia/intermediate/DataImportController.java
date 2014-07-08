package uk.ac.ebi.pride.pia.intermediate;


public interface DataImportController {
	
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
