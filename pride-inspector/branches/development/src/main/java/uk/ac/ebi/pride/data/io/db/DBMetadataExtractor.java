package uk.ac.ebi.pride.data.io.db;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.impl.PrideDBAccessControllerImpl;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * DBMetadataExtractor
 * User: rwang
 * Date: 27/09/2011
 * Time: 15:25
 */
public class DBMetadataExtractor {

    public static void main(String[] args) throws SQLException, IOException {
        // metadata output file
        String metadataFile = args[0];

        // list to store all experiment accessions
        List<Integer> accessions = new ArrayList<Integer>();

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Get connection
            connection = PooledConnectionFactory.getConnection();

            stmt = connection.prepareStatement("select accession from pride_experiment order by accession");
            rs = stmt.executeQuery();
            while (rs.next()) {
                String accession = rs.getString("accession");
                accessions.add(new Integer(accession));
            }
        } catch (SQLException ex) {
            System.err.println("Failed to get all the accessions");
        } finally {
            DBUtilities.releaseResources(connection, stmt, rs);
        }


        // sort accessions
        Collections.sort(accessions);

        // remove unwanted accessions
        removeUnwantedExperiments(accessions);

        // load PTM mappings file
        Map<String, Tuple<String, String>> ptmMappings = getPtmMappings();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(new File(metadataFile)));

            // print header
            writer.println("Accession\tTitle\tProject\tSpecies\tTaxonomy ID\tTissue\tBRENDA ID (Tissue)\tPTM\t#Spectra\t#Proteins\t#Peptides\tReference\tPubMed ID");

            for (Integer accession : accessions) {
                System.out.println(accession);

                // Data access controller
                PrideDBAccessControllerImpl controller = new PrideDBAccessControllerImpl(accession);

                // accession
                writeEntry(writer, accession + "");

                // experiment metadata
                Experiment experiment = (Experiment) controller.getMetaData();


                // experiment title
                String expTitle = experiment.getTitle();
                writeEntry(writer, expTitle);


                // project name
                String project = DataAccessUtilities.getProjectName(experiment);
                writeEntry(writer, project);

                List<Sample> samples = experiment.getSamples();
                if (samples != null && samples.size() > 0) {
                    Sample sample = samples.get(0);
                    // species, taxonomy id
                    List<CvParam> cvParams = sample.getCvParams();
                    Set<CvParam> speciesCvParams = new LinkedHashSet<CvParam>();
                    for (CvParam cvParam : cvParams) {
                        if (cvParam.getCvLookupID().toLowerCase().equals("newt")) {
                            speciesCvParams.add(cvParam);
                        }
                    }

                    if (speciesCvParams.size() == 0) {
                        writer.print("\t");
                        writer.print("\t");
                    } else {
                        String species = "";
                        String speciesAccs = "";
                        for (CvParam speciesCvParam : speciesCvParams) {
                            species += speciesCvParam.getName() + ";";
                            speciesAccs += speciesCvParam.getAccession() + ";";
                        }
                        species = species.substring(0, species.length() - 1);
                        speciesAccs = speciesAccs.substring(0, speciesAccs.length() - 1);
                        writeEntry(writer, species);
                        writeEntry(writer, speciesAccs);
                    }

                    // tissue, brenda id
                    Set<CvParam> tissueCvParams = new LinkedHashSet<CvParam>();
                    for (CvParam cvParam : cvParams) {
                        if (cvParam.getCvLookupID().toLowerCase().equals("bto")) {
                            tissueCvParams.add(cvParam);
                        }
                    }

                    if (tissueCvParams.size() == 0) {
                        writer.print("\t");
                        writer.print("\t");
                    } else {
                        String tissue = "";
                        String tissueAccs = "";
                        for (CvParam tissueCvParam : tissueCvParams) {
                            tissue += tissueCvParam.getName() + ";";
                            tissueAccs += tissueCvParam.getAccession() + ";";
                        }
                        tissue = tissue.substring(0, tissue.length() - 1);
                        tissueAccs = tissueAccs.substring(0, tissueAccs.length() - 1);
                        writeEntry(writer, tissue);
                        writeEntry(writer, tissueAccs);
                    }

                } else {
                    writer.print("\t");
                    writer.print("\t");
                    writer.print("\t");
                    writer.print("\t");
                }

                // get unique PTMs
                Collection<Comparable> identIds = controller.getIdentificationIds();
                Map<String, Modification> modifications = new LinkedHashMap<String, Modification>();
                if (identIds != null) {
                    for (Comparable identId : identIds) {
                        Collection<Comparable> peptideIds = controller.getPeptideIds(identId);
                        if (peptideIds != null) {
                            for (Comparable peptideId : peptideIds) {
                                List<Modification> ptms = controller.getPTMs(identId, peptideId);
                                for (Modification ptm : ptms) {
                                    if (ptm.getName() != null) {
                                        modifications.put(ptm.getAccession(), ptm);
                                    }
                                }
                            }
                        }
                    }
                }


                if (modifications.size() == 0) {
                    writer.print("\t");
                } else {
                    String modName = "";
                    List<String> ptmNames = new LinkedList<String>();
                    for (String s : modifications.keySet()) {
                        Tuple<String, String> ptmMappingDetail = ptmMappings.get(s);
                        if (ptmMappingDetail == null) {
                            if (!ptmNames.contains(modifications.get(s).getName())) {
                                ptmNames.add(modifications.get(s).getName());
                            }
                        } else {
                            if (!ptmNames.contains(ptmMappingDetail.getKey())) {
                                ptmNames.add(0, ptmMappingDetail.getKey());
                            }
                        }
                    }

                    for (String ptmName : ptmNames) {
                        modName += ptmName + ";";
                    }
                    modName = modName.substring(0, modName.length() - 1);
                    writeEntry(writer, modName);
                }

                // number spectra
                writeEntry(writer, controller.getNumberOfSpectra() + "");

                // number proteins
                writeEntry(writer, controller.getNumberOfIdentifications() + "");

                // number peptides
                writeEntry(writer, controller.getNumberOfPeptides() + "");

                // reference line, pubmed id
                List<Reference> references = experiment.getReferences();
                if (references != null && references.size() > 0) {
                    String refLine = "";
                    String pubmedId = "";
                    for (Reference reference : references) {
                        refLine += reference.getFullReference() + ";";
                        List<CvParam> cvParams = reference.getCvParams();
                        for (CvParam cvParam : cvParams) {
                            if (cvParam.getCvLookupID().toLowerCase().equals("pubmed")) {
                                String pubMed = cvParam.getAccession();
                                if (NumberUtilities.isInteger(pubMed)) {
                                    pubmedId += pubMed + ",";
                                }
                            }
                        }
                    }

                    if (refLine.length() > 0) {
                        refLine = refLine.substring(0, refLine.length() - 1);
                    }
                    if (pubmedId.length() > 0) {
                        pubmedId = pubmedId.substring(0, pubmedId.length() - 1);
                    }
                    writeEntry(writer, refLine);
                    writeEntry(writer, pubmedId);
                } else {
                    writer.print("\t");
                    writer.print("\t");
                }


                writer.println();
                writer.flush();

                // close controller
                controller.close();
            }
        } catch (DataAccessException ex) {
            System.err.println("Failed to retrieve data from PRIDE experiment");
        } catch (IOException e) {
            System.err.println("Failed to write data to file");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static Map<String, Tuple<String, String>> getPtmMappings() throws IOException {
        Map<String, Tuple<String, String>> ptmMappings = new LinkedHashMap<String, Tuple<String, String>>();

        InputStream inputStream = DBMetadataExtractor.class.getClassLoader().getResourceAsStream("metadata/ptm_mapping.tsv");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // ignore the first line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] parts = line.split("\t");
                ptmMappings.put(parts[2], new Tuple<String, String>(parts[1], parts[0]));
            }

        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return ptmMappings;
    }

    private static void writeEntry(PrintWriter writer, String entry) {
        if (entry != null) {
            entry = entry.replace("\n", "");
            entry = entry.replace("\t", "");
            writer.print(entry);
        }
        writer.print("\t");
    }

    private static void removeUnwantedExperiments(List<Integer> expAccs) {
        expAccs.remove(118);
        expAccs.remove(119);
        expAccs.remove(120);
        expAccs.remove(1688);
        expAccs.remove(2573);
        expAccs.remove(2620);
        expAccs.remove(2623);
        expAccs.remove(2639);
        expAccs.remove(8159);
        expAccs.remove(8160);
        expAccs.remove(11900);
    }
}
