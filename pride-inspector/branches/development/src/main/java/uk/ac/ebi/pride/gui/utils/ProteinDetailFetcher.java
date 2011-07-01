package uk.ac.ebi.pride.gui.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.pride.gui.component.sequence.Protein;
import uk.ac.ebi.pride.model.implementation.core.GelImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.ac.ebi.pride.gui.component.utils.Constants.TAB;

public class ProteinDetailFetcher {
    private Logger logger = LoggerFactory.getLogger(ProteinDetailFetcher.class);

    // query string for the NCBI esummary tool
    private final String ESUMMARY_QUERY_STRING = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=protein&id=%s";
    // query string for the NCBI esearch tool
    private final String ESEARCH_QUERY_STRING = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=protein&term=%s";
    // query string to fetch the protein sequence from NCBI
    private final String EFETCH_FORMAT_STRING = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id=%s&rettype=fasta&tool=pride_inspector";

    // query string to get uniprot names
    private final String UNIPROT_ACC_QUERY_STRING = "http://www.uniprot.org/uniprot/?query=accession:%s&format=tab&columns=protein%%20names,sequence";
    private final String UNIPROT_MULT_ACC_QUERY_STRING = "http://www.uniprot.org/uniprot/?query=%s&format=tab&columns=id,protein%%20names,sequence,reviewed,entry%%20name";
    private final String UNIPROT_ENTRY_NAME_QUERY_STRING = "http://www.uniprot.org/uniprot/?query=mnemonic:%s&format=tab&columns=protein%%20names,sequence";
    private final String UNIPROT_ENTRY_QUERY_STRING = "http://www.uniprot.org/uniprot/%s";
    private final String UNIPROT_ACC_CONVERSION_QUERY_STRING = "http://www.uniprot.org/uniprot/?query=%s&format=tab&columns=id,reviewed"; // TODO: change to get the protein names right away
    private final String UNIPROT_FOREIGN_DETAILS_QUERY_STRING = "http://www.uniprot.org/uniprot/?query=%s&format=tab&columns=protein%%20names,reviewed,sequence";
    // query strings to map accessions using the UniProt mapping service
    private final String UP_MAPPING_ENSEMBL = "http://www.uniprot.org/mapping/?from=ENSEMBL_PRO_ID&to=ACC&query=%s&format=tab";
    
    private enum AccessionType{UNIPROT_ACC, UNIPROT_ID, UNIPARC, IPI, REFSEQ, ENSEMBL, GI, UNKNOWN};
    // query string to get the fasta file for an ipi entry
    private final String IPI_FASTA_QUERY_STRING = "http://www.ebi.ac.uk/cgi-bin/dbfetch?db=IPI&id=%s&format=fasta&style=raw";

    private HashMap<String, String> pageBuffer = new HashMap<String, String>();
    // TODO: in case this class should be used in a multi-thread environment, this member variable should be changed to static + concurrentHashMap

    /**
     * Returns various details for the given protein (f.e. name,
     * sequence).
     * @param accession The protein's accession.
     * @return A Protein object containing the additional information.
     * @throws Exception error when retrieving protein accession
     */
    public Protein getProteinDetails(String accession) throws Exception {
        //logger.debug("Getting protein name for " + accession);
        // uniprot
        if (ProteinAccessionPattern.isSwissprotAccession(accession) || ProteinAccessionPattern.isSwissprotEntryName(accession)) {
            String[] parts = accession.split("-");
            return getUniprotDetails(parts[0]);
        }

        // uniparc
        if (ProteinAccessionPattern.isUniparcAccession(accession)) {
            return getUniprotDetails(accession);
        }

        // IPI
        if (ProteinAccessionPattern.isIPIAccession(accession)) {
            return getIpiDetails(accession);
        }

        // ENSEMBL
        if (ProteinAccessionPattern.isEnsemblAccession(accession)) {
            return getEnsemblDetails(accession);
        }

        // NCBI
        if (ProteinAccessionPattern.isRefseqAccession(accession)) {
            String ncbiId = getNcbiId(accession);
            Protein p = getNcbiDetails(ncbiId);
            p.setAccession(accession);
            
            return p;
        }

        // GI
        if (ProteinAccessionPattern.isGIAccession(accession)) {
            return getNcbiDetails(accession);
        }

        return null;
    }
    
    /**
     * Returns the (guessed) accession type for the passed
     * accession. In case the accession is not recognized
     * UNKNOWN is returned.
     * @param accession The accession to guess the type for.
     * @return The accession's type.
     */
    private AccessionType getAccessionType(String accession) {
    	// swissprot accession
    	if (ProteinAccessionPattern.isSwissprotAccession(accession))
            return AccessionType.UNIPROT_ACC;
    	
    	if (ProteinAccessionPattern.isSwissprotEntryName(accession))
    		return AccessionType.UNIPROT_ID;

        // uniparc
        if (ProteinAccessionPattern.isUniparcAccession(accession))
            return AccessionType.UNIPARC;

        // IPI
        if (ProteinAccessionPattern.isIPIAccession(accession))
        	return AccessionType.IPI;

        // ENSEMBL
        if (ProteinAccessionPattern.isEnsemblAccession(accession))
        	return AccessionType.ENSEMBL;

        // NCBI
        if (ProteinAccessionPattern.isRefseqAccession(accession))
        	return AccessionType.REFSEQ;

        // GI
        if (ProteinAccessionPattern.isGIAccession(accession))
        	return AccessionType.GI;
        
        return AccessionType.UNKNOWN;
    }
    
    /**
     * Returns various details for the given protein (f.e. name,
     * sequence).
     * @param accession The protein's accession.
     * @return A Protein object containing the additional information.
     * @throws Exception error when retrieving protein accession
     */
    public HashMap<String, Protein> getProteinDetails(Collection<String> accessions) throws Exception {
    	// sort the passed accessions into Lists based on the (guessed) identifier system
    	HashMap<AccessionType, ArrayList<String>> sortedAccessions = new HashMap<AccessionType, ArrayList<String>>();
    	
    	for (String accession : accessions) {
    		// get the accessions type
    		AccessionType accType = getAccessionType(accession);
    		
    		// put the accession in the respective ArrayList
    		if (!sortedAccessions.containsKey(accType))
    			sortedAccessions.put(accType, new ArrayList<String>());
    		
    		sortedAccessions.get(accType).add(accession);
    	}
    	
    	// map the accessions
    	HashMap<String, Protein> proteins = new HashMap<String, Protein>();
    	for (AccessionType accType : sortedAccessions.keySet()) {
    		switch(accType) {
    			case UNIPROT_ACC:
    				proteins.putAll(getUniProtDetails(sortedAccessions.get(accType)));
    				break;
    			case UNIPROT_ID:
    				proteins.putAll(getUniProtDetails(sortedAccessions.get(accType)));
    				break;
    			case IPI:
    				proteins.putAll(getIpiDetails(sortedAccessions.get(accType)));
    				break;
    			case ENSEMBL:
    				proteins.putAll(getEnsemblDetails(sortedAccessions.get(accType)));
    				break;
    			case GI:
    				proteins.putAll(getNcbiDetails(sortedAccessions.get(accType), true));
    				break;
    			case REFSEQ:
    				proteins.putAll(getNcbiDetails(sortedAccessions.get(accType), false));
    				break;
    		}
    	}
    	
        return proteins;
    }
    
    /**
     * Returns the details for the given identifiers in
     * a HashMap with the identifier's key or GI number as key and a Protein
     * object holding the details as value.
     *
     * @param accession The accession to get the details for.
     * @return A Protein object containing the protein's details.
     * @throws Exception
     */
    private HashMap<String, Protein> getNcbiDetails(Collection<String> accessions, boolean useGiNumber) throws Exception {
    	// create the query string
    	String query = "";
    	
    	for (String accession : accessions)
    		query += ((query.length() > 0) ? "," : "") + accession;
    	
    	// get the IPI fasta entry
        String fastas = getPage(String.format(EFETCH_FORMAT_STRING, query));
        String[] lines = fastas.split("\n");
        
        // parse the fasta entries
        String fasta = "";
        
        HashMap<String, Protein> proteins = new HashMap<String, Protein>();
        
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
        	String line = lines[lineIndex];
        	
        	// process the current fasta
        	if (fasta.length() > 0 && line.startsWith(">")) {
        		Protein protein = convertNcbiFastaToProtein(fasta, useGiNumber);
                proteins.put(protein.getAccession(), protein);
                
                fasta = "";
        	}
        	
        	fasta += line + "\n";
        }
        
        if (!"".equals(fasta)) {
        	Protein protein = convertNcbiFastaToProtein(fasta, useGiNumber);
            proteins.put(protein.getAccession(), protein);
        }
        
        return proteins;
    }
    
    /**
     * Converts an NCBI gi fasta entry to a Protein object.
     * @param fasta The fasta to convert.
     * @param useGi Indicates whether the GI number or the source accession should be set as the Protein's accession.
     * @return Protein
     * @throws Exception
     */
    private Protein convertNcbiFastaToProtein(String fasta, boolean useGi) throws Exception {
    	// only use the first line
        String header = fasta.substring(0, fasta.indexOf('\n'));
        
        // get the sequence
        String sequence = fasta.substring(fasta.indexOf('\n') + 1);
        // remove all whitespaces
        sequence = sequence.replaceAll("\\s", "");

        // extract the protein name
        Pattern pat = Pattern.compile(">[^|]+\\|([^|]+)\\|([^|]+)\\|([^|]+)\\|(.*)");

        Matcher matcher = pat.matcher(header);

        // make sure it matches
        if (!matcher.find())
            throw new Exception("Unexpected fasta format encountered");

        String gi = matcher.group(1);
        String source = matcher.group(2);
        String accession = matcher.group(3);
        String name = matcher.group(4).trim();
        
        if ("ref".equals(source))
        	source = "RefSeq";
        
        // create the protein object
        Protein protein = new Protein((useGi) ? gi : accession);
        protein.setName(name);
        protein.setSequenceString(sequence);
        protein.setSource(useGi ? "NCBI gi" : source);
        
        return protein;
    }
    
    /**
     * Returns the details for the given IPI identifiers in
     * a HashMap with the IPI identifier as key and a Protein
     * object holding the details as value.
     *
     * @param accession The IPI accession to get the name for.
     * @return A Protein object containing the protein's details.
     * @throws Exception
     */
    private HashMap<String, Protein> getIpiDetails(Collection<String> accessions) throws Exception {
    	// create the query string
    	String query = "";
    	
    	for (String accession : accessions)
    		query += ((query.length() > 0) ? "," : "") + accession;
    	
    	// get the IPI fasta entry
        String fastas = getPage(String.format(IPI_FASTA_QUERY_STRING, query));
        String[] lines = fastas.split("\n");
        
        // parse the fasta entries
        String fasta = "";
        
        HashMap<String, Protein> proteins = new HashMap<String, Protein>();
        
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
        	String line = lines[lineIndex];
        	
        	// process the current fasta
        	if (fasta.length() > 0 && line.startsWith(">")) {
        		Protein protein = convertIPIFastaToProtein(fasta);
                proteins.put(protein.getAccession().substring(0, 11), protein);
                
                fasta = "";
        	}
        	
        	fasta += line + "\n";
        }
        
        if (!"".equals(fasta)) {
        	Protein protein = convertIPIFastaToProtein(fasta);
            proteins.put(protein.getAccession().substring(0, 11), protein);
        }
        	
        
        return proteins;
    }
    
    /**
     * Converts an IPI fasta entry to a Protein object.
     * @param fasta
     * @return
     * @throws Exception
     */
    private Protein convertIPIFastaToProtein(String fasta) throws Exception {
    	// only use the first line
        String header = fasta.substring(0, fasta.indexOf('\n'));
        
        // get the sequence
        String sequence = fasta.substring(fasta.indexOf('\n') + 1);
        // remove all whitespaces
        sequence = sequence.replaceAll("\\s", "");

        // extract the protein name
        Pattern pat = Pattern.compile("(IPI[\\d\\.]+) (.*)$");

        Matcher matcher = pat.matcher(header);

        // make sure it matches
        if (!matcher.find())
            throw new Exception("Unexpected fasta format encountered");

        String accession = matcher.group(1);
        String name = matcher.group(2);
        
        // create the protein object
        Protein protein = new Protein(accession);
        protein.setName(name);
        protein.setSequenceString(sequence);
        protein.setSource("IPI");
        
        return protein;
    }

    /**
     * Returns the details for the given IPI identifier.
     *
     * @param accession The IPI accession to get the name for.
     * @return A Protein object containing the protein's details.
     * @throws Exception
     */
    private Protein getIpiDetails(String accession) throws Exception {
        // make sure it's an IPI accession
        if (!accession.startsWith("IPI"))
            throw new Exception("Malformatted IPI accession");

        // get the IPI fasta entry
        String fasta = getPage(String.format(IPI_FASTA_QUERY_STRING, accession));

        // only use the first line
        String header = fasta.substring(0, fasta.indexOf('\n'));
        
        // get the sequence
        String sequence = fasta.substring(fasta.indexOf('\n') + 1);
        // remove all whitespaces
        sequence = sequence.replaceAll("\\s", "");

        // extract the protein name
        Pattern pat = Pattern.compile("IPI[\\d\\.]+ (.*)$");

        Matcher matcher = pat.matcher(header);

        // make sure it matches
        if (!matcher.find())
            throw new Exception("Unexpected fasta format encountered");

        String name = matcher.group(1);
        
        // create the protein object
        Protein protein = new Protein(accession);
        protein.setName(name);
        protein.setSequenceString(sequence);
        protein.setSource("IPI");
        
        return protein;
    }

    /**
     * Returns the details for the given ENSEMBL accession. This function
     * currently is a hack since it converts the given accession
     * to a UniProt accession and just returns the first fitting name.
     *
     * @param accession The ENSEMBL accession to get the name for.
     * @return The protein's name.
     * @throws Exception
     */
    private Protein getEnsemblDetails(String accession) throws Exception {
        // make sure it's an ENSEMBL accession
        if (!accession.startsWith("ENS"))
            throw new Exception("Malformatted ENSEMBL accession");

        // try get the uniprot name for the given accession
        return getForeignUniprotDetails(accession);
    }

    /**
     * Uses the UniProt query function to get the details for the given accession.
     * The accession must not be an UniProt accession. Otherwise unexpected results
     * will be returned. In case there are more hits for the given accession the
     * first reviewed entry is returned. If there is no reviewed entry among the hits
     * the first unreviewed one is returned.
     *
     * @param accession The (NON UNIPROT) accession to get the name for
     * @return The protein's name.
     * @throws Exception
     */
    private Protein getForeignUniprotDetails(String accession) throws Exception {
        // get the page
        String page = getPage(String.format(UNIPROT_FOREIGN_DETAILS_QUERY_STRING, accession));

        // split the page into lines
        String[] lines = page.split("\n");

        // check if entries were found
        if (page.equals("") || lines.length < 2)
            throw new Exception("No UniProt accession available for " + accession);

        // separate the reviewed and non-reviewed hits
        ArrayList<Protein> swissprot = new ArrayList<Protein>();
        ArrayList<Protein> trembl = new ArrayList<Protein>();

        for (int i = 1; i < lines.length; i++) {
        	String fields[] = lines[i].split("\t");
        	
        	if (fields.length != 3)
        		continue;
        	
        	// create the protein
        	Protein protein = new Protein(accession);
        	protein.setName(fields[0].trim());
        	protein.setSequenceString(fields[2].replaceAll("\\s", ""));
        	
            if (lines[i].contains("unreviewed")) {
            	trembl.add(protein);
            }
            else
                swissprot.add(protein);
        }

        // return the first swissprot name if there is one, otherwise the first trembl name
        if (swissprot.size() > 0)
            return swissprot.get(0);
        else
            return trembl.get(0);
    }
    
    /**
     * Returns the details for the given Ensembl identifiers in
     * a HashMap with the Ensembl identifier as key and a Protein
     * object holding the details as value. Uses the UniProt
     * mapping service to map ensembl entries to UniProt accessions.
     * Then the call is passed on to getUniProtDetails.
     *
     * @param accession The Ensembl accession to get the name for.
     * @return A Protein object containing the protein's details.
     * @throws Exception
     */
    private HashMap<String, Protein> getEnsemblDetails(Collection<String> accessions) throws Exception {
    	// use the uniprot mapping service to convert the ensembl accessions
    	String query = "";
    	
    	for (String acc : accessions)
    		query += (query.length() > 0 ? "," : "") + acc;
    	
    	// map the accessions
    	String page = getPage(String.format(UP_MAPPING_ENSEMBL, query));
    	String[] lines = page.split("\n");
    	
    	HashMap<String, String> uniprotToEnsemblMapping = new HashMap<String, String>();
    	HashMap<String, Integer> fieldMapping = new HashMap<String, Integer>();
    	
    	for (int i = 0; i < lines.length; i++) {
    		String[] fields = lines[i].split("\t");
    		
    		if (i == 0) {
    			for (int j = 0; j < fields.length; j++)
    				fieldMapping.put(fields[j], j);
    			
    			// make sure the required fields are there
    			if (!fieldMapping.containsKey("To") || !fieldMapping.containsKey("From"))
    				throw new Exception("Unexpected response retrieved from UniProt mapping service.");
    			
    			continue;
    		}
    		
    		// save the mapping
    		uniprotToEnsemblMapping.put(fields[fieldMapping.get("To")], fields[fieldMapping.get("From")]);
    	}
    	
    	// get the UniProt mappings
    	Map<String, Protein> proteins = getUniProtDetails(uniprotToEnsemblMapping.keySet());
    	
    	// create a new HashMap changing the UniProt accessions to ENSEMBL accessions
    	HashMap<String, Protein> ensemblProteins = new HashMap<String, Protein>();
    	
    	for (Protein p : proteins.values()) {
    		String ensemblAcc = uniprotToEnsemblMapping.get(p.getAccession());
    		
    		if (ensemblAcc == null)
    			continue;
    		
    		p.setAccession(ensemblAcc);
    		
    		ensemblProteins.put(ensemblAcc, p);
    	}
    	
    	return ensemblProteins;
    }

    /**
     * Uses the UniProt page to convert the given accession into a
     * UniProt accession. In case more than one accession was found
     * only the first SwissProt accession is returned. If no SwissProt
     * accession matches the first Trembl accession is returned.
     *
     * @param accession
     * @return
     * @throws Exception
     */
    private String convertToUniprotAccession(String accession) throws Exception {
        // get the page
        String page = getPage(String.format(UNIPROT_ACC_CONVERSION_QUERY_STRING, accession));

        // split the page into lines
        String[] lines = page.split("\n");

        // check if entries were found
        if (page.equals("") || lines.length < 2)
            throw new Exception("No UniProt accession available for " + accession);

        // separate the reviewed and non-reviewed hits
        ArrayList<String> swissprot = new ArrayList<String>();
        ArrayList<String> trembl = new ArrayList<String>();

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].contains("unreviewed"))
                trembl.add(lines[i].substring(0, lines[i].indexOf(TAB)));
            else
                swissprot.add(lines[i].substring(0, lines[i].indexOf(TAB)));
        }

        // return the first swissprot accession if there is one, otherwise the first trembl accession
        if (swissprot.size() > 0)
            return swissprot.get(0);
        else
            return trembl.get(0);
    }

    /**
     * Retrieves the protein details from UniProt from the
     * given UniProt accession. Returns null in case nothing
     * was retrieved.
     *
     * @param accession The UniProt accession.
     * @return A Protein object containing the protein's details or null if the accession doesn't exist
     * @throws Exception In case something went wrong
     */
    private Protein getUniprotDetails(String accession) throws Exception {
        // This call is not required, thus disabled
        //accession = getCurrentUniprotAccession(accession);

        // get the page
        String page = getPage(String.format(UNIPROT_ACC_QUERY_STRING, accession));
        if ("".equals(page.trim())) {
            page = getPage(String.format(UNIPROT_ENTRY_NAME_QUERY_STRING, accession));
        }

        String[] lines = page.split("\n");

        // if there's only one line or the page was empty no protein names were retrieved
        if (page.equals("") || lines.length < 2) {
            return null;
        }
        
        String[] fields = lines[1].split("\t");
        
        if (fields.length != 2)
        	return null;
        
        String sequence = fields[1];
        sequence = sequence.replaceAll("\\s", "");
        
        Protein protein = new Protein(accession);
        protein.setName(fields[0].trim());
        protein.setSequenceString(sequence);

        // return the first name, that should be sufficient
        return protein;
    }
    
    /**
     * Retrieves the protein details from UniProt from the
     * given UniProt accessions. Returns null in case nothing
     * was retrieved. <br />
     * <b>Warning:</b> The function first tries to retrieve the protein details
     * expecting them to be accessions. Only if no results are retrieved that way
     * a second request is send interpreting the passed strings as ids. In case
     * accessions and ids are mixed, only the proteins identified through accessions
     * will be returned.
     * @param accession The UniProt accessions of the proteins.
     * @return A Collection of Protein objects containing the proteins' details or null if the accession doesn't exist
     * @throws Exception In case something went wrong
     */
    private Map<String, Protein> getUniProtDetails(Collection<String> accessions) throws Exception {
    	// build the query string for the accessions
    	String query = "";
    	Boolean usingAccession = true;
    	
    	for (String accession : accessions)
    		query += ((query.length() > 1) ? "%20or%20" : "") + "accession:" + accession;
    	
    	String url = String.format(UNIPROT_MULT_ACC_QUERY_STRING, query);
    	
    	// get the page
        String page = getPage(url);
        if ("".equals(page.trim())) {
        	// try with uniprot ids
        	usingAccession = false;
            page = getPage(String.format(UNIPROT_MULT_ACC_QUERY_STRING, query.replace("accession:", "mnemonic:")));
        }

        String[] lines = page.split("\n");

        // if there's only one line or the page was empty no protein names were retrieved
        if (page.equals("") || lines.length < 2) {
            return null;
        }
        
        HashMap<String, Integer> fieldIndex = new HashMap<String, Integer>();
        HashMap<String, Protein> proteins = new HashMap<String, Protein>();
        
        // create the retrieved proteins
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
        	 String[] fields = lines[lineIndex].split("\t");
        	// if it's the first line build the field index
        	if (lineIndex == 0) {
        		for (int i = 0; i < fields.length; i++)
        			fieldIndex.put(fields[i], i);
        		
        		// make sure the required fields were found
        		if (!fieldIndex.containsKey("Accession") || !fieldIndex.containsKey("Protein names") ||
        			!fieldIndex.containsKey("Sequence") || !fieldIndex.containsKey("Status") ||
        			!fieldIndex.containsKey("Entry name"))
        			throw new Exception("Unexpected UniProt response retrieved.");
        		
        		continue;
        	}

        	if (fields.length < 1)
        		continue;
        	
        	// check if the protein was demerged
        	if (fields[1].startsWith("Merged into")) {
        		// extract the new accession
        		String newAccession = fields[1].substring(12, 18);
        		// get the protein
        		proteins.put(fields[0], proteins.get(newAccession));
        	}
        	else {
        		// make sure the line is in the expected format
            	if (fields.length != fieldIndex.size())
            		throw new Exception("Unexpected UniProt answer retrieved. Line has a different number of fields than defined in the header: <" + lines[lineIndex] + ">");
            	
            	// create the protein object
            	Protein p = new Protein(fields[fieldIndex.get("Accession")]);
            	p.setName(fields[fieldIndex.get("Protein names")]);
            	p.setSource((fields[fieldIndex.get("Status")].equals("reviewed")) ? "UniProt/Swiss-Prot" : "UniProt/TrEMBL");
            	
            	String sequence = fields[fieldIndex.get("Sequence")];
            	sequence = sequence.replaceAll("\\s", "");
            	
            	// TODO: check for inactive proteins (? how to react to inactive proteins)
            	
            	p.setSequenceString(sequence);
            	
            	proteins.put((usingAccession) ? fields[fieldIndex.get("Accession")] : fields[fieldIndex.get("Entry name")], p);
        	}
        }
        
        // return the proteins
        return proteins;
    }

    /**
     * Retrieves the currently active primary accession for
     * the given UniProt accession.
     *
     * @param accession A UniProt accession the check.
     * @return The current active primary UniProt accession.
     * @throws Exception Thrown if something goes wrong.
     */
    private String getCurrentUniprotAccession(String accession) throws Exception {
        // create the url
        URL url = new URL(String.format(UNIPROT_ENTRY_QUERY_STRING, accession));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        HttpURLConnection.setFollowRedirects(true); // follow any redirect

        // only use a head command
        connection.setRequestMethod("HEAD");

        connection.connect();

        // check if there is a redirect set
        if (connection.getResponseCode() != 200) {
            logger.debug("{} seems to be no UniProt accession", accession);
            throw new Exception("Missing UniProt entry for " + accession);
        }

        String effectiveUrl = connection.getURL().toString();

        // extract the UniProt accession
        String confirmedAccession = effectiveUrl.substring(effectiveUrl.lastIndexOf('/') + 1);

        logger.debug("getCurrentUniprotAccession: {} -> {}", new Object[]{accession, confirmedAccession});

        return confirmedAccession;
    }

    /**
     * Returns the details for the given NCBI protein.
     *
     * @param accession The accession of the protein to get the name for.
     * @return A protein object containing the protein's details.
     * @throws Exception Thrown if something goes wrong.
     */
    private Protein getNcbiDetails(String accession) throws Exception {
        // get the id for the accession
        String ncbiId = getNcbiId(accession);

        // make sure an NCBI id was retrieved
        if (ncbiId == null)
            throw new Exception(accession + " is not a valid NCBI accession");

        // get the properties
        HashMap<String, String> properties = getNcbiProperties(ncbiId);

        // create the name
        String name = properties.get("Title");

        // make the name readable
        if (name.contains("AltName") || name.contains("Short"))
            name += ")";

        name = name.replace("RecName: ", "");
        name = name.replace("Full=", "");
        name = name.replace("AltName: ", "(");
        name = name.replace("; Short=", ", ");
        name = name.replace(";", ")");
        
        // remove the first )
        if (name.contains(")"))
            name = name.substring(0, name.indexOf(')')) + name.substring(name.indexOf(')') + 1);
        
        // get the sequence
        String fasta = getPage(String.format(EFETCH_FORMAT_STRING, ncbiId));
        
        String sequence = fasta.substring(fasta.indexOf('\n') + 1);
        sequence = sequence.replaceAll("\\s", "");

        // create the protein
        Protein p = new Protein(accession);
        p.setName(name);
        p.setSequenceString(sequence);

        return p;
    }

    /**
     * Returns the NCBI id for the given protein accession using
     * the NCBI's esearch function. If more than one identifier
     * is found for the given accession, the first match
     * is returned. Returns null if no matching identifier was found.
     *
     * @param accession The accession to get the NCBI identifier for.
     * @return The NCBI identifier for the given accession. Null if no identifier was found.
     * @throws Exception
     */
    private String getNcbiId(String accession) throws Exception {
        // create the url
        URL url = new URL(String.format(ESEARCH_QUERY_STRING, accession));

        // create the xml object
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(url);

        // get the document summary element
        Element root = doc.getRootElement();

        if (root == null)
            throw new Exception("Failed to retrieve NCBI search result for " + accession);

        Element idList = root.getChild("IdList");

        if (idList == null)
            throw new Exception("Failed to retrieve id list for " + accession);

        // get all the items
        @SuppressWarnings("unchecked")
		List<Element> idElements = idList.getChildren("Id");

        // initialize the return variable
        String ids[] = new String[idElements.size()];
        int counter = 0;

        // parse the items
        for (Element idElement : idElements) {
            ids[counter++] = idElement.getValue();
        }

        // if no id was found, return 0
        if (ids.length < 1)
            return null;

        // give a warning if more than one id was found
        if (ids.length > 1)
            logger.warn("More than one id found for {} ({})", new Object[]{accession, ids.toString()});

        return ids[0];
    }

    /**
     * Returns the properties of the given identifier fetched
     * using the NCBI esummary tool and returns them as a HashMap.
     *
     * @param ncbiId The accession to retrieve the properties for.
     * @return A HashMap with the property's name as key and its value as value.
     * @throws Exception Thrown in case something went wrong.
     */
    private HashMap<String, String> getNcbiProperties(String ncbiId) throws Exception {
        // create the url
        URL url = new URL(String.format(ESUMMARY_QUERY_STRING, ncbiId));

        // create the xml object
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(url);

        // get the document summary element
        Element root = doc.getRootElement();

        if (root == null)
            throw new Exception("Failed to retrieve xml summary for " + ncbiId);

        Element docSum = root.getChild("DocSum");

        if (docSum == null)
            throw new Exception("Failed to retrieve xml summary for " + ncbiId);

        // get all the items
        @SuppressWarnings("unchecked")
		List<Element> items = docSum.getChildren("Item");

        // initialize the return variable
        HashMap<String, String> properties = new HashMap<String, String>();

        // parse the items
        for (Element item : items) {
            properties.put(item.getAttributeValue("Name"), item.getValue());
        }

        return properties;
    }

    /**
     * Gets the page from the given address. Returns the
     * retrieved page as a string.
     *
     * @param urlString The address of the resource to retrieve.
     * @return The page as a String
     * @throws Exception Thrown on any problem.
     */
    private String getPage(String urlString) throws Exception {
        // check if the page is cached
        if (pageBuffer.containsKey(urlString))
            return pageBuffer.get(urlString);

        // create the url
        URL url = new URL(urlString);

        // send the request
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.connect();

        // get the page
        BufferedReader in = null;
        StringBuilder page = new StringBuilder();
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                page.append(line);
                page.append("\n");
            }
        } catch (IOException ioe) {
            logger.warn("Failed to read web page");
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return page.toString();
    }
}