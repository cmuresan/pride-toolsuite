package uk.ac.ebi.pride.pia.intermediate.compiler;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.pia.intermediate.Accession;
import uk.ac.ebi.pride.pia.intermediate.Group;
import uk.ac.ebi.pride.pia.intermediate.PIAInputFile;
import uk.ac.ebi.pride.pia.intermediate.Peptide;
import uk.ac.ebi.pride.pia.intermediate.PeptideSpectrumMatch;
import uk.ac.ebi.pride.pia.intermediate.compiler.parser.OBOMapper;
import uk.ac.ebi.pride.pia.tools.MzIdentMLTools;
import uk.ac.ebi.pride.pia.tools.PIAConstants;
import uk.ac.ebi.pride.pia.tools.PIATools;


/**
 * This class is used to read in one or several input files and compile them
 * into one PIA XML intermediate file.
 * 
 * @author julian
 *
 */
public class PIACompiler {
	
	/** just a name for the compilation */
	private String compilationName;
	
	/** the date, when the compiler was initialised */
	private Date startDate;
	
	/** map of the used files, maps from file ID to PIAFile */
	private Map<Long, PIAInputFile> files;
	
	/** map of the accessions, maps from the accession String to the Accession */
	private Map<String, Accession> accessions;
	
	/** map of peptides, maps from the sequence to the peptides */
	private Map<String, Peptide> peptides;
	
	/** map of spectra, maps from the */
	private Set<PeptideSpectrumMatch> spectra;
	
	/** map of the groups */
	private Map<Long, Group> groups;
	
	/** maps from the accessions to the peptides, used to calculate the clusters */
	private Map<String, Set<Peptide>> accPepMap;
	
	/** maps from the peptide to the accessions, used to calculate the clusters */
	private Map<String, Set<Accession>> pepAccMap;
	
	/**
	 * list of  maps from the peptide string to the accessions, used for
	 * building up the intermediate structure. each map represents one cluster
	 * and can be processed as thread.
	 */
	private List<Map<String, Map<String, Accession>>> clusteredPepAccMap;
	
	/** the iterator over the cluster */
	private ListIterator<Map<String, Map<String, Accession>>> clusterIterator;
	
	/** how many clusters are processed until now */
	private Long buildProgress;
	
	/** offset for the merged clusters */
	private Long clusterOffset;
	
	/** the SpectraData (like in mzIdentML) */
	private Map<String, SpectraData> spectraDataMap;
	
	/** the SearchDatabases (like in mzIdentML) */
	private Map<String, SearchDataBase> searchDatabasesMap;
	
	/** the analysis software for identifications (class from mzIdentML) */
	private Map<String, Software> softwareMap;
	
	/** the OBO mapper, to get additional data */
	private OBOMapper oboMapper;
	
	/** the number of used threads */
	private int numThreads;
	
	
	/** logger for this class */
	private static final Logger logger = Logger.getLogger(PIACompiler.class);
	
	/** helper description */
	private static final String helpDescription =
			"PIACompiler is used to compile (multiple) search engine results " +
			"into one PIA XML file. Use a high enough amount of memory (e.g. " +
			"use the Java setting -Xmx8G).";
	
	
	/**
	 * Basic constructor
	 */
	public PIACompiler() {
		compilationName = null;
		startDate = new Date();
		
		files = new HashMap<Long, PIAInputFile>();
		accessions = new HashMap<String, Accession>();
		peptides = new HashMap<String, Peptide>();
		spectra = new HashSet<PeptideSpectrumMatch>();
		
		spectraDataMap = new HashMap<String, SpectraData>();
		searchDatabasesMap = new HashMap<String, SearchDataBase>();
		softwareMap = new HashMap<String, Software>();
		
		accPepMap = new HashMap<String, Set<Peptide>>();
		pepAccMap = new HashMap<String, Set<Accession>>();
		
		clusterIterator = null;

		oboMapper = null;
		
		numThreads = 0;
	}

	/**
	 * Returns the {@link Accession} mapped by the given acc in the accessions
	 * map.
	 * 
	 * @param acc
	 * @return
	 */
	public Accession getAccession(String acc) {
		return accessions.get(acc);
	}
	
	
	/**
	 * Inserts a new accession into the map of accessions.
	 * 
	 * @param accession
	 * @param dbSequence
	 * @return
	 */
	public Accession insertNewAccession(String accession, String dbSequence) {
		Accession acc;
		Long id = (long)accessions.size()+1;
		
		acc = new Accession(id, accession, dbSequence);
		accessions.put(accession, acc);
		
		return acc;
	}
	
	
	/**
	 * Returns the peptide from the peptides map with the given sequence.
	 * 
	 * @param sequence
	 * @return
	 */
	public Peptide getPeptide(String sequence) {
		return peptides.get(sequence);
	}
	
	
	/**
	 * Inserts a new peptide into the map of peptides.
	 * 
	 * @param sequence
	 * @return
	 */
	public Peptide insertNewPeptide(String sequence) {
		Peptide peptide;
		Long id = (long)peptides.size()+1;
		
		peptide = new Peptide(id, sequence);
		peptides.put(sequence, peptide);
		
		return peptide;
	}
	
	
	/**
	 * Inserts a new PSM with the given data into the spectra set.
	 * 
	 * @return the newly created PSM
	 */
	public PeptideSpectrumMatch insertNewSpectrum(int charge,
			double massToCharge, double deltaMass, Double rt, String sequence,
			int missed, String sourceID, String spectrumTitle,
			PIAInputFile file, SpectrumIdentification spectrumID) {
		PeptideSpectrumMatch psm;
		Long id = spectra.size() + 1L;
		
		psm = new PeptideSpectrumMatch(id, charge, massToCharge, deltaMass, rt,
				sequence, missed, sourceID, spectrumTitle,
				file, spectrumID);
		
		if (!spectra.add(psm)) {
			// TODO: better warning / error
			logger.error("ERROR: spectrum was already in list, this should not have happened! " +
					psm.getSequence());
		}
		
		return psm;
	}
	
	
	/**
	 * Returns the Set of {@link Peptide}s from the accPepMap for the given key.
	 * @param acc
	 * @return
	 */
	public Set<Peptide> getFromAccPepMap(String acc) {
		return accPepMap.get(acc);
	}
	
	
	/**
	 * Puts the given Set into the accPepMap with the given key.
	 * 
	 * @param key
	 * @param peps
	 * @return
	 */
	public Set<Peptide> putIntoAccPepMap(String key, Set<Peptide> peps) {
		return accPepMap.put(key, peps);
	}
	
	
	/**
	 * Returns the Set of {@link Accession}s from the pepAccMap for the given key.
	 * @param pep
	 * @return
	 */
	public Set<Accession> getFromPepAccMap(String pep) {
		return pepAccMap.get(pep);
	}
	
	
	/**
	 * Puts the given Set into the pepAccMap with the given key.
	 * 
	 * @param key
	 * @param accs
	 * @return
	 */
	public Set<Accession> putIntoPepAccMap(String key, Set<Accession> accs) {
		return pepAccMap.put(key, accs);
	}
	
	
	/**
	 * Puts the given {@link Software} into the softwareMap, if it is
	 * not already in there. While doing so, set the software ID for internal
	 * purposes.
	 * 
	 * @return the ID of the software in the softwareMap
	 */
	public Software putIntoSoftwareMap(Software software) {
		// go through the list of software and look for an equal software
		for (Map.Entry<String, Software> swIt : softwareMap.entrySet()) {
			
			if (MzIdentMLTools.paramsEqual(software,
					swIt.getValue())) {
				boolean equal = true;
				
				equal &= PIATools.bothNullOrEqual(swIt.getValue().getName(),
						software.getName());
				
				equal &= PIATools.bothNullOrEqual(swIt.getValue().getUri(),
						software.getUri());
				
				equal &= PIATools.bothNullOrEqual(swIt.getValue().getVersion(),
						software.getVersion());
				
				// TODO: maybe check for the contact as well... for now, assume if everythin is equal, the contact does not matter
				
				equal &= PIATools.bothNullOrEqual(swIt.getValue().getCustomization(),
						software.getCustomization());
				
				if (equal) {
					// the software is already in the list, return it
					return swIt.getValue();
				}
			}
		}
		
		String strID = PIAConstants.software_prefix + (softwareMap.size() + 1L);
		softwareMap.put(strID, software);
		software.setId(strID);
		return software;
	}
	
	
	
	/**
	 * Puts the given {@link uk.ac.ebi.pride.data.core.SearchDataBase} into the searchDatabasesMap, if it
	 * is not already in it. While doing so, set the identification ID for
	 * internal purposes.
	 * 
	 * @return the ID of the spectrumIdentification in the spectrumIdentificationsMap
	 */
	public SearchDataBase putIntoSearchDatabasesMap(SearchDataBase database) {
		// go through the databases and check, if any equals the given database
		for (Map.Entry<String, SearchDataBase> dbIt : searchDatabasesMap.entrySet()) {
			if (dbIt.getValue().getLocation().equals(database.getLocation())) {
				boolean equal = true;
				
				equal &= PIATools.bothNullOrEqual(dbIt.getValue().getName(),
						database.getName());
				
				equal &= PIATools.bothNullOrEqual(dbIt.getValue().getNumDatabaseSequence(),
						database.getNumDatabaseSequence());
				
				equal &= PIATools.bothNullOrEqual(dbIt.getValue().getNumDatabaseResidue(),
						database.getNumDatabaseResidue());
				
				equal &= PIATools.bothNullOrEqual(dbIt.getValue().getReleaseDate(),
						database.getReleaseDate());
				
				equal &= PIATools.bothNullOrEqual(dbIt.getValue().getVersion(),
						database.getVersion());
				
				equal &= PIATools.bothNullOrEqual(dbIt.getValue().getExternalFormatDocumentationURI(),
						database.getExternalFormatDocumentationURI());
				
				equal &= MzIdentMLTools.fileFormatsEqualOrNull(dbIt.getValue().getFileFormat(),
						database.getFileFormat());
				
				equal &= MzIdentMLTools.paramsEqual(dbIt.getValue().getNameDatabase(),
						database.getNameDatabase());
				
				if (equal) {
					List<CvParam> params1 = dbIt.getValue().getDescription();
					List<CvParam> params2 = database.getDescription();
					equal = MzIdentMLTools.cvParamsEqualOrNull(params1, params2);
				}
				
				if (equal) {
					// the database is already in the list, return its ID
					return dbIt.getValue();
				}
			}
		}
		
		String strID = PIAConstants.databases_prefix +
				(searchDatabasesMap.size() + 1L);
		searchDatabasesMap.put(strID, database);
		database.setId(strID);
		return database;
	}
	
	
	/**
	 * Puts the given {@link uk.ac.ebi.pride.data.core.SearchDataBase} into the searchDatabasesMap, if it
	 * is not already in it. While doing so, set the identification ID for
	 * internal purposes.
	 * 
	 * @return the ID of the spectrumIdentification in the spectrumIdentificationsMap
	 */
	public SpectraData putIntoSpectraDataMap(SpectraData spectra) {
		// go through the databases and check, if any equals the given database
		for (Map.Entry<String, SpectraData> spectraIt : spectraDataMap.entrySet()) {
			if (spectraIt.getValue().getLocation().equals(spectra.getLocation())) {
				boolean equal = true;
				
				equal &= PIATools.bothNullOrEqual(spectraIt.getValue().getName(),
						spectra.getName());
				
				equal &= PIATools.bothNullOrEqual(
						spectraIt.getValue().getExternalFormatDocumentationURI(),
						spectra.getExternalFormatDocumentationURI());
				
				equal &= MzIdentMLTools.fileFormatsEqualOrNull(
						spectraIt.getValue().getFileFormat(), spectra.getFileFormat());
				
				equal &= MzIdentMLTools.getCvParamEqualsOrNull(
						spectraIt.getValue().getSpectrumIdFormat(),
						spectra.getSpectrumIdFormat());
				
				if (equal) {
					return spectraIt.getValue();
				}
			}
		}
		
		String strID = PIAConstants.spectra_data_prefix + 
				(spectraDataMap.size() + 1L);
		spectraDataMap.put(strID, spectra);
		spectra.setId(strID);
		return spectra;
	}
	
	
	
	/**
	 * Builds up the list of peptide accession maps. The list is clustered, i.e.
	 * each entry in the list may be processed in parallel.
	 * <p>
	 * Before calling this method, some data should be read in by
	 * {@link }.
	 */
	public void buildClusterList() {
		logger.info("start sorting clusters");
		
		Set<String> peptidesDone = new HashSet<String>(peptides.size());
		Set<String> accessionsDone = new HashSet<String>(accessions.size());
		clusteredPepAccMap = new ArrayList<Map<String,Map<String,Accession>>>();
		
		for (Map.Entry<String, Set<Peptide>> accsPeps : accPepMap.entrySet()) {
			
			if (!accessionsDone.contains(accsPeps.getKey())) {
				// this accession is not yet clustered, so start a new cluster
				// and insert all the "connected" peptides and accessions
				Map<String,Map<String,Accession>> pepAccMapCluster =
					createCluster(accsPeps.getKey(), peptidesDone, accessionsDone);
				
				if (pepAccMapCluster != null) {
					clusteredPepAccMap.add(pepAccMapCluster);
				} else {
					// TODO: error / exception
					logger.error("cluster could not be created!");
				}
			}
			
		}
		
		// the maps are no longer needed
		accPepMap.clear();
		pepAccMap.clear();
		
		logger.info("clusters sorted: "+clusteredPepAccMap.size());
	}
	
	
	/**
	 * Inserts the cluster of the given accession into the peptide accession
	 * map cluster.<br/>
	 * This method should only be called by {@link PIACompiler#buildClusterList()}.
	 *
	 * @param </>
     * @param accession
	 * @param peptidesDone
	 * @param accessionsDone
	 */
	private Map<String,Map<String,Accession>> createCluster(String accession,
			Set<String> peptidesDone, Set<String> accessionsDone) {
		Map<String, Accession> clusterAccessions = new HashMap<String, Accession>();
		Map<String, Peptide> clusterPeptides = new HashMap<String, Peptide>();
		
		int newAccessions = 0;
		int newPeptides = 0;
		
		// initialize the cluster's peptides with the peptides of the given accession
		newAccessions = 1;	// for the given accession
		for (Peptide pep : accPepMap.get(accession)) {
			clusterPeptides.put(pep.getSequence(), pep);
			newPeptides++;
		}
		
		// repeat as long, as we get more accessions or peptides
		while ((newAccessions > 0) || (newPeptides > 0)) {
			newAccessions = 0;
			newPeptides = 0;
			
			// get accessions for peptides, which are new since the last loop
			for (String seq : clusterPeptides.keySet()) {
				if (!peptidesDone.contains(seq)) {
					for (Accession acc : pepAccMap.get(seq)) {
						if (!clusterAccessions.containsKey(acc.getAccession())) {
							clusterAccessions.put(acc.getAccession(), acc);
							newAccessions++;
						}
					}
					
					peptidesDone.add(seq);
				}
			}
			
			// get peptides for accessions, which are new since the last loop
			for (String acc : clusterAccessions.keySet()) {
				if (!accessionsDone.contains(acc)) {
					for (Peptide pep : accPepMap.get(acc)) {
						if (!clusterPeptides.containsKey(pep.getSequence())) {
							clusterPeptides.put(pep.getSequence(), pep);
							newPeptides++;
						}
					}
					
					accessionsDone.add(acc);
				}
			}
		}
		
		// now we have the whole cluster, so put it into the pepAccMapCluster
		Map<String,Map<String,Accession>> pepAccMapCluster =
			new HashMap<String, Map<String,Accession>>();
		
		for (Map.Entry<String, Peptide> pepIt : clusterPeptides.entrySet()) {
			
			Map<String,Accession> pepAccessions = new HashMap<String, Accession>();
			for (Accession acc : pepAccMap.get(pepIt.getKey())) {
				pepAccessions.put(acc.getAccession(), acc);
			}
			
			pepAccMapCluster.put(pepIt.getKey(), pepAccessions);
		}
		
		return pepAccMapCluster;
	}
	
	
	/**
	 * Build up the intermediate structure.<br/>
	 * Before this method is called, {@link PIACompiler#buildClusterList()}
	 * must be called.
	 */
	public void buildIntermediateStructure() {
		int NUM_THREADS;
		
		if (numThreads > 0) {
			NUM_THREADS = numThreads;
		} else {
			NUM_THREADS = Runtime.getRuntime().availableProcessors();
		}
		
		logger.info("Using " + NUM_THREADS + " threads.");
		
		List<CompilerWorkerThread> threads;
		
		
		if (clusteredPepAccMap == null) {
			// TODO: make exception or something
			logger.error("the cluster map is not yet build!");
			return;
		}
		
		// initialize the groups map
		groups = new HashMap<Long, Group>();
		
		// initialize the clusterIterator
		clusterIterator = clusteredPepAccMap.listIterator();
		buildProgress = 0L;
		clusterOffset = 0L;
		
		// start the threads
		threads = new ArrayList<CompilerWorkerThread>(NUM_THREADS);
		for (int i = 0; i < NUM_THREADS; i++) {
			CompilerWorkerThread thread = new CompilerWorkerThread(i+1, this);
			threads.add(thread);
			
			thread.start();
		}
		
		// wait for the threads to finish
		for (CompilerWorkerThread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				logger.error("thread got interrupted!");
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Returns the next cluster in the cluster map.
	 * 
	 * @return
	 */
	public synchronized Map<String, Map<String,Accession>> getNextCluster() {
		synchronized (clusterIterator) {
			if (clusterIterator != null) {
				if (clusterIterator.hasNext()) {
					return clusterIterator.next();
				} else {
					return null;
				}
			} else {
				// TODO: throw exception or something
				logger.error("The cluster iterator is not yet initialized!");
				return null;
			}
		}
	}
	
	
	/**
	 * Increase the number of build clusters.
	 */
	public synchronized void increaseBuildProgress() {
		buildProgress++;
	}
	
	
	/**
	 * Merge the given groups into the groups map.
	 * 
	 * @param subGroups
	 */
	public synchronized void mergeClustersIntoMap(Map<Long, Group> subGroups,
			long nrClusters) {
		synchronized (groups) {
			long groupOffset = groups.size();
			
			for (Group group : subGroups.values()) {
				group.setOffset(groupOffset);
				group.setTreeID(group.getTreeID()+clusterOffset);
				groups.put(group.getID(), group);
			}
			
			clusterOffset += nrClusters;
		}
	}
	
	
	/**
	 * Setter for the name
	 * @param name
	 */
	public void setName(String name) {
		this.compilationName = name;
	}
	
	
	/**
	 * Getter for the name.
	 * @return
	 */
	public String getName() {
		return compilationName;
	}
	
	
	/**
	 * Sets the number of used threads. If this is smaller than 1, all available
	 * threads (Runtime.getRuntime().availableProcessors()) are used.
	 * 
	 * @param threads
	 */
	public void setNrThreads(int threads) {
		numThreads = threads;
	}
	
	
	/**
	 * Gets the number of used threads
	 * @param
	 */
	public int getNrThreads() {
		return numThreads;
	}
	
	
	/**
	 * Write out the intermediate structure into an XML file.
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public void writeOutXML(String fileName) {
		logger.info("start writing the XML file "+fileName);
		

	}
	
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		CommandLineParser parser = new GnuParser();
		Options options = new Options();
		
		options.addOption(OptionBuilder
				.isRequired(true)
				.withArgName("outputFile")
                .hasArg()
                .withDescription( "path to the created PIA XML file" )
                .create("outfile"));
		
		options.addOption(OptionBuilder
				.withArgName("name")
                .hasArg()
                .withDescription( "name of the PIA compilation" )
                .create("name"));
		
		options.addOption(OptionBuilder
				.withArgName("inputFile")
                .hasArg()
                .withDescription( "input file with possible further information " +
                		"separated by semicolon. This option may be called " +
                		"multiple times. Any further information not given " +
                		"will be treated as null, the information is in this " +
                		"order:\n" +
                		"name of the input file (as shown in the PIA viewers, " +
                		"if not given will be set to the path of the input file), " +
                		"type of the file (usually guessed, but may also be " +
                		"explicitly given, possible values are " +

                		"), " +
                		"additional information file (very seldom used)")
                .create("infile"));
		
		String outFileName = null;
		String piaName = null;
		
		if (args.length > 0) {
			PIACompiler piaCompiler = new PIACompiler();
			
			
			// parse the command line arguments
			try {
				CommandLine line = parser.parse( options, args );
				
				if (line.hasOption("outfile")) {
					outFileName = line.getOptionValue("outfile");
				}
				
				if (line.hasOption("infile")) {
					
					for (String arg : line.getOptionValues("infile")) {
						String[] values = arg.split(";");
						String file = values[0];
						String name = values[0];
						String additionalInfoFile = null;
						String type = null;
						
						if (values.length > 1) {
							name = (values[1].length() > 0) ? values[1] : null;
							
							if (values.length > 2) {
								type = (values[2].length() > 0) ? values[2] : null;
								
								if (values.length > 3) {
									additionalInfoFile = (values[3].length() > 0) ? values[3] : null;
								}
							}
						}
						
						System.out.println("file: " + file +
								"\n\tname: " + name +
								"\n\ttype: " + type +
								"\n\tadditional info file: " + additionalInfoFile);
						

					}
				}
				
				if (line.hasOption("name")) {
					piaName = line.getOptionValue("name");
				}
			} catch (ParseException e) {
				System.err.println(e.getMessage());
				PIATools.printCommandLineHelp(options, helpDescription);
				System.exit(-1);
			}
			
			if (outFileName != null) {
				piaCompiler.buildClusterList();
				
				piaCompiler.buildIntermediateStructure();
				
				if (piaName == null) {
					piaName = outFileName;
				}
				piaCompiler.setName(piaName);
				piaCompiler.writeOutXML(outFileName);
			} else {
				PIATools.printCommandLineHelp(options, helpDescription);
				System.exit(-1);
			}
		} else {
			PIATools.printCommandLineHelp(options, helpDescription);
		}
	}
}
