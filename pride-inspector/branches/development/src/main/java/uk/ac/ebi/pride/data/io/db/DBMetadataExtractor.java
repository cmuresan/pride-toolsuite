package uk.ac.ebi.pride.data.io.db;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.controller.impl.PrideDBAccessControllerImpl;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.Experiment;
import uk.ac.ebi.pride.data.core.Reference;
import uk.ac.ebi.pride.data.core.Sample;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DBMetadataExtractor
 * User: rwang
 * Date: 27/09/2011
 * Time: 15:25
 */
public class DBMetadataExtractor {

    public static void main(String[] args) throws SQLException {
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

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(new File(metadataFile)));

            // print header
            writer.println("Accession\tTitle\tProject\tSpecies\tTaxonomy ID\tTissue\tBRENDA ID (Tissue)\t#Spectra\t#Proteins\t#Peptides\tReference\tPubMed ID");

            for (Integer accession : accessions) {

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
                    boolean foundNewt = false;
                    for (CvParam cvParam : cvParams) {
                        if (cvParam.getCvLookupID().toLowerCase().equals("newt")) {
                            writeEntry(writer, cvParam.getName());
                            writeEntry(writer, cvParam.getAccession());
                            foundNewt = true;
                            break;
                        }
                    }

                    if (!foundNewt) {
                        writer.print("\t");
                        writer.print("\t");
                    }

                    // tissue, brenda id
                    boolean foundTissue = false;
                    for (CvParam cvParam : cvParams) {
                        if (cvParam.getCvLookupID().toLowerCase().equals("bto")) {
                            writeEntry(writer, cvParam.getName());
                            writeEntry(writer, cvParam.getAccession());
                            foundTissue = true;
                            break;
                        }
                    }

                    if (!foundTissue) {
                        writer.print("\t");
                        writer.print("\t");
                    }
                } else {
                    writer.print("\t");
                    writer.print("\t");
                    writer.print("\t");
                    writer.print("\t");
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
            if (writer !=  null) {
                writer.close();
            }
        }
    }

    private static void writeEntry(PrintWriter writer, String entry) {
        if (entry != null) {
            entry = entry.replace("\n", "");
            entry = entry.replace("\t", "");
            writer.print(entry);
        }
        writer.print("\t");
    }
}
