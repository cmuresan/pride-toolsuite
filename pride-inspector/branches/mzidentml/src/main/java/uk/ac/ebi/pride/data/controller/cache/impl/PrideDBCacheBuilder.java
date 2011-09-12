package uk.ac.ebi.pride.data.controller.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.cache.CacheCategory;
import uk.ac.ebi.pride.data.controller.impl.PrideDBAccessControllerImpl;
import uk.ac.ebi.pride.data.coreIdent.*;
import uk.ac.ebi.pride.data.io.db.DBUtilities;
import uk.ac.ebi.pride.data.io.db.PooledConnectionFactory;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DBAccessCacheBuilder is responsible for populating the cache for DBAccessController
 * <p/>
 * User: rwang
 * Date: 06-Sep-2010
 * Time: 09:29:56
 */
public class PrideDBCacheBuilder extends AbstractAccessCacheBuilder {
    private static final Logger logger = LoggerFactory.getLogger(PrideDBCacheBuilder.class);

    /**
     * Temporary stores the spectrum identification to spectrum id mapping
     * This is populated in populateSpectrumInfo and cleared in populatePeptideInfo
     */
    private final Map<Comparable, Comparable> spectrumRefToId;

//    private boolean memorySaving = false;

    public PrideDBCacheBuilder(PrideDBAccessControllerImpl dbAccessController) {
        super(dbAccessController);
        spectrumRefToId = new HashMap<Comparable, Comparable>();
    }

    @Override
    public void populate() throws Exception {
        super.populate();

        Comparable foregroundExperimentAcc = controller.getForegroundExperimentAcc();
        if (foregroundExperimentAcc == null) {
            // populate experiment accession only once
            populateExperimentAccs();
        } else {
            // check the memory mode
//            memorySaving = isMemorySavingMode(foregroundExperimentAcc);
            // populate the rest every time the foreground experiment accession has changed.
            populateMetadataInfo();
            populateSpectrumInfo(foregroundExperimentAcc);
            populateIdentificationInfo(foregroundExperimentAcc);
            populatePrecursorInfo(foregroundExperimentAcc);
            populatePeptideInfo(foregroundExperimentAcc);
            populatePTMInfo(foregroundExperimentAcc);
            populateFragmentIonInfo(foregroundExperimentAcc);
            populateIdentificationParamInfo(foregroundExperimentAcc);
            populatePeptideParamInfo(foregroundExperimentAcc);
            populateTheRest();
        }
    }


    private void populateMetadataInfo() throws DataAccessException {
        logger.info("Initializing experiment metadata");
        // clear cache
        cache.clear(CacheCategory.EXPERIMENT_METADATA);
        controller.getExperimentMetaData();
    }

    /**
     * Populate expeirment accessions
     *
     * @throws SQLException an error while querying database.
     */
    private void populateExperimentAccs() throws SQLException {
        logger.info("Initializing experiment accessions");
        // clear previous experiment accessions
        cache.clear(CacheCategory.EXPERIMENT_ACC);
        List<Comparable> array = new ArrayList<Comparable>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            st = connection.prepareStatement("SELECT accession FROM pride_experiment ORDER BY ABS(accession)");
            rs = st.executeQuery();
            while (rs.next()) {
                array.add(rs.getString("accession"));
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("Querying experiment accessions", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }
        // cache experiment accessions
        cache.storeInBatch(CacheCategory.EXPERIMENT_ACC, array);
    }

    /**
     * Populate spectrum ids and ms level
     *
     * @param expAcc foreground experiment accession from DBAccessController
     * @throws java.sql.SQLException an error while querying database
     */
    private void populateSpectrumInfo(Comparable expAcc) throws SQLException {
        logger.info("Initializing spectrum ids and ms levels");
        // clear caches
        cache.clear(CacheCategory.SPECTRUM_ID);
        cache.clear(CacheCategory.MS_LEVEL);
        spectrumRefToId.clear();

        // spectrum id list
        List<Comparable> specIds = new ArrayList<Comparable>();
        // ms level map
        Map<Comparable, Integer> msLevels = new HashMap<Comparable, Integer>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            // get mz data array id
            st = connection.prepareStatement("select spectrum_id, spectrum_identifier, ms_level from mzdata_spectrum " +
                    "join mzdata_mz_data using(mz_data_id) where accession_number=?");
            st.setString(1, expAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                Comparable id = rs.getString("spectrum_id");
                int level = rs.getInt("ms_level");
                Comparable ref = rs.getString("spectrum_identifier");
                specIds.add(id);
                msLevels.put(id, level);
                spectrumRefToId.put(ref, id);
            }
        } catch (SQLException e) {
            logger.error("Querying spectrum id and ms level", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }

        // cache spectrum ids
        cache.storeInBatch(CacheCategory.SPECTRUM_ID, specIds);
        // cache ms levels
        cache.storeInBatch(CacheCategory.MS_LEVEL, msLevels);
    }

    /**
     * Populate identification ids, protein acccessions, scores and thresholds
     *
     * @param expAcc foreground experiment accession from DBAccessController
     * @throws java.sql.SQLException an error while querying database
     */
    private void populateIdentificationInfo(Comparable expAcc) throws SQLException {
        logger.info("Initializing identification ids, protein accessions, scores and thresholds");
        // clear caches
        cache.clear(CacheCategory.IDENTIFICATION_ID);
        cache.clear(CacheCategory.PROTEIN_ACCESSION);
        cache.clear(CacheCategory.PROTEIN_ACCESSION_VERSION);
        cache.clear(CacheCategory.PROTEIN_SEARCH_DATABASE);
        cache.clear(CacheCategory.SCORE);
        cache.clear(CacheCategory.THRESHOLD);

        // identification id list
        List<Comparable> identIds = new ArrayList<Comparable>();
        // protein accession map
        Map<Comparable, String> protAccs = new HashMap<Comparable, String>();
        // protein accession version map
        Map<Comparable, String> protAccVersions = new HashMap<Comparable, String>();
        // database map
        Map<Comparable, String> databases = new HashMap<Comparable, String>();
        // scores map
        Map<Comparable, Double> scores = new HashMap<Comparable, Double>();
        // thresholds map
        Map<Comparable, Double> threholds = new HashMap<Comparable, Double>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            // get mz data array id
            st = connection.prepareStatement("select identification_id, accession_number, accession_version, search_database, score, threshold from pride_identification " +
                    "join pride_experiment using(experiment_id) where accession=?");
            st.setString(1, expAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                Comparable id = rs.getString("identification_id");
                String acc = rs.getString("accession_number");
                String accVersion = rs.getString("accession_version");
                String database = rs.getString("search_database");
                Double sc = rs.getDouble("score");
                Double thd = rs.getDouble("threshold");
                identIds.add(id);
                protAccs.put(id, acc);
                protAccVersions.put(id, accVersion);
                databases.put(id, database);
                scores.put(id, sc);
                threholds.put(id, thd);
            }
        } catch (SQLException e) {
            logger.error("Querying identification ids, protein accessions, scores and thresholds", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }

        // cache everything
        cache.storeInBatch(CacheCategory.IDENTIFICATION_ID, identIds);
        cache.storeInBatch(CacheCategory.PROTEIN_ACCESSION, protAccs);
        cache.storeInBatch(CacheCategory.PROTEIN_ACCESSION_VERSION, protAccVersions);
        cache.storeInBatch(CacheCategory.PROTEIN_SEARCH_DATABASE, databases);
        cache.storeInBatch(CacheCategory.SCORE, scores);
        cache.storeInBatch(CacheCategory.THRESHOLD, threholds);
    }

    /**
     * Populate precursor charge, m/z and intensity.
     *
     * @param expAcc foreground experiment accession.
     * @throws SQLException an error while querying database.
     */
    private void populatePrecursorInfo(Comparable expAcc) throws SQLException {
        logger.info("Initializing precursor charge, m/z and intensity");
        // clear caches
        cache.clear(CacheCategory.PRECURSOR_CHARGE);
        cache.clear(CacheCategory.PRECURSOR_MZ);
        cache.clear(CacheCategory.PRECURSOR_INTENSITY);

        // precursor charges
        Map<Comparable, Integer> charges = new HashMap<Comparable, Integer>();
        // precursor m/z
        Map<Comparable, Double> mzs = new HashMap<Comparable, Double>();
        // precursor intensity
        Map<Comparable, Double> intensities = new HashMap<Comparable, Double>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            // get mz data array id
            st = connection.prepareStatement("select pre.spectrum_id, ion.accession, ion.value from mzdata_ion_selection_param ion, " +
                    "mzdata_spectrum spec, mzdata_precursor pre, mzdata_mz_data mz where mz.accession_number=? " +
                    "and mz.mz_data_id=spec.mz_data_id and spec.spectrum_id=pre.spectrum_id and pre.precursor_id = ion.parent_element_fk");
            st.setString(1, expAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                Comparable id = rs.getString("spectrum_id");
                String acc = rs.getString("accession");
                String val = rs.getString("value");

                if (!NumberUtilities.isNumber(val)) {
                    logger.warn("Value is not a number: spectrum: {} Accession: {} Charge: {}", new Object[]{id, acc, val});
                } else {
                    // store the charge, m/z and intensity
                    if (CvTermReference.PSI_ION_SELECTION_CHARGE_STATE.getAccession().equals(acc)
                            || CvTermReference.ION_SELECTION_CHARGE_STATE.getAccession().equals(acc)) {
                        charges.put(id, Integer.parseInt(val));
                    } else if (CvTermReference.PSI_ION_SELECTION_MZ.getAccession().equals(acc)
                            || CvTermReference.ION_SELECTION_MZ.getAccession().equals(acc)) {
                        mzs.put(id, Double.parseDouble(val));
                    } else if (CvTermReference.PSI_ION_SELECTION_INTENSITY.getAccession().equals(acc)
                            || CvTermReference.ION_SELECTION_INTENSITY.getAccession().equals(acc)) {
                        intensities.put(id, Double.parseDouble(val));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Querying precursor charge, m/z and intensity", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }

        // createAttributedSequence cache
        cache.storeInBatch(CacheCategory.PRECURSOR_CHARGE, charges);
        cache.storeInBatch(CacheCategory.PRECURSOR_MZ, mzs);
        cache.storeInBatch(CacheCategory.PRECURSOR_INTENSITY, intensities);
    }

    /**
     * Populate peptide related details
     *
     * @param expAcc foreground experiment accession.
     * @throws SQLException an error while querying database.
     */
    private void populatePeptideInfo(Comparable expAcc) throws SQLException {
        logger.info("Initializing peptide id, start, end, sequence and spectrum reference");
        // clear caches
        cache.clear(CacheCategory.IDENTIFICATION_TO_PEPTIDE);
        cache.clear(CacheCategory.PEPTIDE_START);
        cache.clear(CacheCategory.PEPTIDE_END);
        cache.clear(CacheCategory.PEPTIDE_SEQUENCE);
        cache.clear(CacheCategory.PEPTIDE_TO_SPECTRUM);

        // identification to peptide map
        Map<Comparable, List<Comparable>> identToPeptide = new HashMap<Comparable, List<Comparable>>();
        // peptide start map
        Map<Comparable, Integer> peptideStart = new HashMap<Comparable, Integer>();
        // peptide end map
        Map<Comparable, Integer> peptideEnd = new HashMap<Comparable, Integer>();
        // peptide sequence
        Map<Comparable, String> peptideSequence = new HashMap<Comparable, String>();
        // peptide spectrum id map
        Map<Comparable, Comparable> peptideToSpectrum = new HashMap<Comparable, Comparable>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            // get mz data array id
            st = connection.prepareStatement("select pe.identification_id, pe.peptide_id, pe.pep_start, pe.pep_end, pe.sequence, pe.spectrum_ref from pride_peptide pe " +
                    "join pride_identification pi using(identification_id) " +
                    "join pride_experiment exp using(experiment_id)where exp.accession=?");
            st.setString(1, expAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                Comparable identId = rs.getString("identification_id");
                Comparable peptideId = rs.getString("peptide_id");
                Integer start = rs.getInt("pep_start");
                Integer end = rs.getInt("pep_end");
                String seq = rs.getString("sequence");
                Comparable specRef = rs.getString("spectrum_ref");

                // store identification to peptide mapping
                List<Comparable> peptideIds = identToPeptide.get(identId);
                if (peptideIds == null) {
                    peptideIds = new ArrayList<Comparable>();
                    identToPeptide.put(identId, peptideIds);
                }
                peptideIds.add(peptideId);
                // store peptide start
                peptideStart.put(peptideId, start);
                // store peptide stop
                peptideEnd.put(peptideId, end);
                // store sequence
                peptideSequence.put(peptideId, seq);
                // store spectrum reference
                Comparable specId = spectrumRefToId.get(specRef);
                if (specId != null) {
                    peptideToSpectrum.put(peptideId, specId);
                } else {
                    logger.debug("Cannot find the correct spectrum id by reference for experiment: "
                            + expAcc + " on spectrum: " + specRef);
                }
            }
        } catch (SQLException e) {
            logger.error("Querying peptide related information", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }

        // cache
        cache.storeInBatch(CacheCategory.IDENTIFICATION_TO_PEPTIDE, identToPeptide);
        cache.storeInBatch(CacheCategory.PEPTIDE_START, peptideStart);
        cache.storeInBatch(CacheCategory.PEPTIDE_END, peptideEnd);
        cache.storeInBatch(CacheCategory.PEPTIDE_SEQUENCE, peptideSequence);
        cache.storeInBatch(CacheCategory.PEPTIDE_TO_SPECTRUM, peptideToSpectrum);
        // clear temporary spectrum identification to id mappings
        spectrumRefToId.clear();
    }

    /**
     * Populate PTM information
     *
     * @param expAcc foreground experiment accessions.
     * @throws SQLException an error while querying database.
     */
    private void populatePTMInfo(Comparable expAcc) throws SQLException {
        logger.info("Initializing ptm locations");
        // clear cache
        cache.clear(CacheCategory.PEPTIDE_TO_MODIFICATION);
        cache.clear(CacheCategory.MODIFICATION);

        // map of peptide id to location
        Map<Comparable, List<Tuple<String, Integer>>> locations = new HashMap<Comparable, List<Tuple<String, Integer>>>();
        Map<String, Comparable> accToModId = new HashMap<String, Comparable>();
        Map<String, Modification> modifications = new HashMap<String, Modification>();

        // query modification table
        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            st = connection.prepareStatement("SELECT pm.peptide_id, pm.modification_id, pm.accession, pm.location FROM pride_modification pm " +
                    "join pride_peptide pe using(peptide_id) " +
                    "join pride_identification pi using(identification_id) " +
                    "join pride_experiment exp using(experiment_id)where exp.accession=?");
            st.setString(1, expAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                Comparable peptideId = rs.getString("peptide_id");
                Comparable modId = rs.getString("modification_id");
                String modAcc = rs.getString("accession");
                Integer location = rs.getInt("location");
                // add ptm location
                List<Tuple<String, Integer>> entries = locations.get(peptideId);
                if (entries == null) {
                    entries = new ArrayList<Tuple<String, Integer>>();
                    locations.put(peptideId, entries);
                }
                entries.add(new Tuple<String, Integer>(modAcc, location));
                // store accession and mod id into a temporary structure
                accToModId.put(modAcc, modId);
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("Querying PTM locations", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }

        // query detla mass table
        Connection innerConnection = null;
        PreparedStatement innerStmt = null;
        ResultSet innerRs = null;
        for (Map.Entry<String, Comparable> entry : accToModId.entrySet()) {
            try {
                String modAcc = entry.getKey();
                Comparable modId = entry.getValue();
                String modDB = null;
                String modDBVersion = null;
                List<Double> monoMasses = new ArrayList<Double>();
                List<Double> avgMasses = new ArrayList<Double>();
                logger.debug("Getting mass delta value");
                connection = PooledConnectionFactory.getConnection();
                st = connection.prepareStatement("SELECT ms.modification_id, ms.mass_delta_value, ms.classname, pm.mod_database, pm.mod_database_version FROM pride_mass_delta ms " +
                        "join pride_modification pm using(modification_id) where ms.modification_id= ?");
                st.setString(1, modId.toString());
                rs = st.executeQuery();
                while (rs.next()) {
                    String className = rs.getString("classname");
                    double massDelta = rs.getDouble("mass_delta_value");
                    modDB = rs.getString("mod_database");
                    modDBVersion = rs.getString("mod_database_version");
                    // set mass delta
                    if ("uk.ac.ebi.pride.rdbms.ojb.model.core.MonoMassDeltaBean".equals(className)) {
                        monoMasses.add(massDelta);
                    } else {
                        avgMasses.add(massDelta);
                    }
                }

                // get modification cv params
                List<CvParam> cvParams = new ArrayList<CvParam>();
                logger.debug("Getting ptm accession");
                innerConnection = PooledConnectionFactory.getConnection();
                innerStmt = innerConnection.prepareStatement("SELECT accession, name, value, cv_label FROM pride_modification_param WHERE parent_element_fk = ? AND cv_label is not null");
                innerStmt.setString(1, modId.toString());
                innerRs = innerStmt.executeQuery();
                while (innerRs.next()) {
                    cvParams.add(new CvParam(innerRs.getString("accession"), innerRs.getString("name"),
                            innerRs.getString("cv_label"), innerRs.getString("value"), "", "", ""));
                }
                innerRs.close();

                ParamGroup paramGroup = new ParamGroup(cvParams, null);
                // create a modification
                // Note: location is a pseudo location, need to be replace before use
                Modification mod = new Modification(paramGroup, modAcc,modDB,0,null,avgMasses,avgMasses,modDB, modDBVersion);
                modifications.put(modAcc, mod);
            } catch (SQLException e) {
                logger.error("Querying PTM delta mass", e);
                throw e;
            } finally {
                DBUtilities.releaseResources(connection, st, rs);
                DBUtilities.releaseResources(innerConnection, innerStmt, innerRs);
            }
        }

        // add to cache
        cache.storeInBatch(CacheCategory.PEPTIDE_TO_MODIFICATION, locations);
        cache.storeInBatch(CacheCategory.MODIFICATION, modifications);
    }

    private void populateFragmentIonInfo(Comparable expAcc) throws SQLException {
        logger.info("Initializing number of fragment ions");
        // clear cache
        cache.clear(CacheCategory.NUMBER_OF_FRAGMENT_IONS);

        // map of peptide id to number of fragment ions
        Map<Comparable, Integer> numOfIons = new HashMap<Comparable, Integer>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            st = connection.prepareStatement("SELECT peptide_id, count(*) cnt FROM pride_fragment_ion " +
                    "join pride_peptide using(peptide_id) " +
                    "join pride_identification using(identification_id) " +
                    "join pride_experiment using(experiment_id)where accession=? group by peptide_id");
            st.setString(1, expAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                Comparable peptideId = rs.getString("peptide_id");
                Integer count = rs.getInt("cnt");
                numOfIons.put(peptideId, count);
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("Querying number of fragment ions", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }
        // add to cache
        cache.storeInBatch(CacheCategory.NUMBER_OF_FRAGMENT_IONS, numOfIons);
    }


    private void populateIdentificationParamInfo(Comparable foregroundExperimentAcc) throws SQLException{
        logger.info("Initializing protein params");

        // clear cache
        cache.clear(CacheCategory.IDENTIFICATION_TO_PARAM);

        // map of protein id to params
        Map<Comparable, ParamGroup> params = new HashMap<Comparable, ParamGroup>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            st = connection.prepareStatement("SELECT pride_identification_param.* FROM pride_identification_param " +
                    "join pride_identification on(parent_element_fk=identification_id) " +
                    "join pride_experiment using(experiment_id)where pride_experiment.accession=?");
            st.setString(1, foregroundExperimentAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                Comparable peptideId = rs.getString("parent_element_fk");
                // get or create param list
                ParamGroup paramGroup = params.get(peptideId);
                if (paramGroup == null) {
                    paramGroup =  new ParamGroup();
                    params.put(peptideId, paramGroup);
                }
                // store parameters
                String cvLabel = rs.getString("cv_label");
                String name = rs.getString("name");
                String accession = rs.getString("accession");
                String value = rs.getString("value");
                if (cvLabel == null) {
                    // user param
                    UserParam newParam = new UserParam(name, accession, value, null, null, null);
                    paramGroup.addUserParam(newParam);
                } else {
                    // cv param
                    CvParam newParam = new CvParam(accession, name, cvLabel, value, null, null, null);
                    paramGroup.addCvParam(newParam);
                }
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("Querying identification params", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }
        // add to cache
        cache.storeInBatch(CacheCategory.IDENTIFICATION_TO_PARAM, params);
    }
    
    private void populatePeptideParamInfo(Comparable expAcc) throws SQLException {
        logger.info("Initializing peptide params");
        // clear cache
        cache.clear(CacheCategory.PEPTIDE_TO_PARAM);
        
        // map of peptide id to params
        Map<Comparable, ParamGroup> params = new HashMap<Comparable, ParamGroup>();

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            connection = PooledConnectionFactory.getConnection();
            st = connection.prepareStatement("SELECT pride_peptide_param.* FROM pride_peptide_param " +
                    "join pride_peptide on(parent_element_fk=peptide_id) " +
                    "join pride_identification using(identification_id) " +
                    "join pride_experiment using(experiment_id)where pride_experiment.accession=?");
            st.setString(1, expAcc.toString());
            rs = st.executeQuery();
            while (rs.next()) {
                Comparable peptideId = rs.getString("parent_element_fk");
                // get or create param list
                ParamGroup paramGroup = params.get(peptideId);
                if (paramGroup == null) {
                    paramGroup =  new ParamGroup();
                    params.put(peptideId, paramGroup);
                }
                // store parameters
                String cvLabel = rs.getString("cv_label");
                String name = rs.getString("name");
                String accession = rs.getString("accession");
                String value = rs.getString("value");
                if (cvLabel == null) {
                    // user param
                    UserParam newParam = new UserParam(name, accession, value, null, null, null);
                    paramGroup.addUserParam(newParam);
                } else {
                    // cv param
                    CvParam newParam = new CvParam(accession, name, cvLabel, value, null, null, null);
                    paramGroup.addCvParam(newParam);
                }
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("Querying peptide params", e);
            throw e;
        } finally {
            DBUtilities.releaseResources(connection, st, rs);
        }
        // add to cache
        cache.storeInBatch(CacheCategory.PEPTIDE_TO_PARAM, params);
        
    }

    /**
     * Clear the rest of cache
     */
    private void populateTheRest() {
        cache.clear(CacheCategory.SUM_OF_INTENSITY);
        cache.clear(CacheCategory.NUMBER_OF_PEAKS);
    }
}
