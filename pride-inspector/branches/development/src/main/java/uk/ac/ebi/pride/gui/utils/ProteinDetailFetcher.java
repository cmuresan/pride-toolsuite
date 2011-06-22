package uk.ac.ebi.pride.gui.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.pride.gui.component.sequence.Protein;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final String UNIPROT_ENTRY_NAME_QUERY_STRING = "http://www.uniprot.org/uniprot/?query=mnemonic:%s&format=tab&columns=protein%%20names,sequence";
    private final String UNIPROT_ENTRY_QUERY_STRING = "http://www.uniprot.org/uniprot/%s";
    private final String UNIPROT_ACC_CONVERSION_QUERY_STRING = "http://www.uniprot.org/uniprot/?query=%s&format=tab&columns=id,reviewed"; // TODO: change to get the protein names right away
    private final String UNIPROT_FOREIGN_DETAILS_QUERY_STRING = "http://www.uniprot.org/uniprot/?query=%s&format=tab&columns=protein%%20names,reviewed,sequence";

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
        // TODO: add retries

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