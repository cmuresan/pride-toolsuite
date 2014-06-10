package uk.ac.ebi.pride.pia.intermediate;


public interface DataImportController {
	
	/**
	 * Adds all spectrum Identifications to the {@link IntermediateStructureCreator}
	 */
	public void addAllSpectrumIdentificationsToStructCreator(IntermediateStructureCreator structCreator);
}
