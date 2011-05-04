package uk.ac.ebi.pride.data.controller.impl;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.DataProcessing;
import uk.ac.ebi.pride.data.core.FileDescription;
import uk.ac.ebi.pride.data.core.InstrumentConfiguration;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.ReferenceableParamGroup;
import uk.ac.ebi.pride.data.core.Sample;
import uk.ac.ebi.pride.data.core.Software;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.io.HibernateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.context.ManagedSessionContext;

/**
 * MzMLControllerImpl provides methods to access mzML files.
 * <p/>
 * User: aschoen
 * Date: 23-Jun-2010
 * Time: 12:15
 */
public class MzMLHibernateControllerImpl extends AbstractDataAccessController {

    private static Logger logger = Logger.getLogger(MzMLHibernateControllerImpl.class);
    private MzML foregroundMzML;
    // helper hashmaps for quick mapping between shown Spectrum and Chromatogram ids
    // and real Hibernate Ids.
    // Avoid unneccessary querying.
    // get reset when setForeGroundExperimentIds is executed!
    private HashMap<String, Long> spectrumIdHelper;
    private HashMap<String, Long> chromatogramIdHelper;
    // Hibernate session
    private Session session;

    public MzMLHibernateControllerImpl(String dataSourceName) throws DataAccessException {
        super(null);
        initialize(dataSourceName);
    }

    // For debugging purposes only
    //Todo: remove this later on, only use for Hibernate Session debugging.
    private void checkSession() {
        if (session == null) {
            logger.debug(Thread.currentThread().getId() + "Session is null");
        } else {
            logger.debug(Thread.currentThread().getId() + "Session != null");
            if (session.isOpen()) {
                logger.debug(Thread.currentThread().getId() + "Session is open");
            } else {
                logger.debug(Thread.currentThread().getId() + "Session is closed");
            }
        }
    }

    private void initialize(String dataSourceName) throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "initialize");
        setName(dataSourceName);

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            ManagedSessionContext.bind((org.hibernate.classic.Session) session);

            List<Comparable> accesssionList = getExperimentIds();

            if (accesssionList != null && !accesssionList.isEmpty()) {
                // show first experiment in database as default
                setForegroundExperimentId(accesssionList.get(0));

                // only populate the spectrumId & chromatogramId list if there is a valid MzML accession
                List<Comparable> spectrumIds = getSpectrumIds();
                if (spectrumIds != null && !spectrumIds.isEmpty()) {
                    setForegroundSpectrumById(spectrumIds.get(0));
                }

                List<Comparable> chromatogramIds = getChromatogramIds();
                if (chromatogramIds != null && !chromatogramIds.isEmpty()) {
                    setForegroundChromatogramById(chromatogramIds.get(0));
                }

            } else {
                logger.info("No mzML accessions found in the database");
                //Todo: proper warning message for end user
            }

        } catch (HibernateException ex) {
            throw new DataAccessException("HibernateException: ", ex);
        }
    }

    @Override
    public void close() {
        logger.info("close method");
        //session.close();
        //ManagedSessionContext.unbind(session.getSessionFactory());
        
    }

    @Override
    public void setForegroundExperimentId(Comparable accession) throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "setForegroundExperimentId");

        foregroundMzML = getMzMLByAccession(accession);

        // (re)-initialize spectrumId and chromatogramId HashMaps
        spectrumIdHelper = new HashMap<String, Long>();
        chromatogramIdHelper = new HashMap<String, Long>();
        //TODO: perhaps move this Id caching behaviour out in the AbstractController

        super.setForegroundExperimentId(accession);
    }

    private MzML getMzMLByAccession(Comparable accession) throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getMzMLByAccession");

        MzML mzML = null;
        try {
            List<MzML> mzMLList = session.createQuery("from MzML where accession = ?").setString(0, (String) accession).list();
            if (!mzMLList.isEmpty()) {
                mzML = mzMLList.get(0);
            }

        } catch (HibernateException ex) {
            session.getTransaction().rollback();
            throw new DataAccessException("Failed to get MzML for accesion: " + accession, ex);
        }

        return mzML;
    }

    private List<String> getAllAccessions() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getAllAccessions");
        List<String> accessions = null;

        try {

            accessions = session.createQuery("select accession from MzML").list();

        } catch (HibernateException ex) {
            session.getTransaction().rollback();
            throw new DataAccessException("Failed to get all experiment Ids: ", ex);
        }

        return accessions;
    }

    @Override
    public void setForegroundChromatogramById(Comparable chromaId) throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "setForegroundChromatogramById");
        Chromatogram newChroma = this.getChromatogramById(chromaId);

        String id = foregroundChromatogram == null ? null : foregroundChromatogram.getId();
        if (!newChroma.getId().equals(id)) {
            Chromatogram oldChroma;
            synchronized (this) {
                oldChroma = foregroundChromatogram;
                foregroundChromatogram = newChroma;
            }
            firePropertyChange(FOREGROUND_CHROMATOGRAM_CHANGED, oldChroma, newChroma);
        }
    }

    @Override
    public void setForegroundSpectrumById(Comparable specId) throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "setForegroundSpectrumById");

        Spectrum newSpectrum = this.getSpectrumById(specId);
        String id = foregroundSpectrum == null ? null : foregroundSpectrum.getId();
        if (!newSpectrum.getId().equals(id)) {
            Spectrum oldSpectrum;
            synchronized (this) {
                oldSpectrum = foregroundSpectrum;
                foregroundSpectrum = newSpectrum;
            }
            firePropertyChange(FOREGROUND_SPECTRUM_CHANGED, oldSpectrum, newSpectrum);
        }
    }

    @Override
    public MetaData getMetaData() throws DataAccessException {
        // id, accession and version
        String id = foregroundMzML.getMzMLId();
        String accession = foregroundMzML.getAccession();
        String version = foregroundMzML.getVersion();
        // FileDescription
        FileDescription fileDesc = getFileDescription();
        // Sample List
        List<Sample> samples = getSamples();
        // Software list
        List<Software> softwares = getSoftware();
        // ScanSettings list
        List<ScanSetting> scanSettings = getScanSettings();
        // Instrument configuration
        List<InstrumentConfiguration> instrumentConfigurations = getInstrumentConfigurations();
        // Data processing list
        List<DataProcessing> dataProcessings = getDataProcessings();
        // Param group
        ParamGroup params = null;

        return new MetaData(id, accession, version, fileDesc,
                samples, softwares, scanSettings, instrumentConfigurations,
                dataProcessings, params);
    }

    @Override
    public List<Comparable> getExperimentIds() throws DataAccessException {
        List<Comparable> accessions = new ArrayList<Comparable>();

        try {
            session.beginTransaction();
            accessions = session.createQuery("select accession from MzML").list();
            session.getTransaction().commit();

        } catch (HibernateException ex) {
            session.getTransaction().rollback();
            throw new DataAccessException("Failed to get experiment Ids: ", ex);
        }

        return accessions;
    }

    @Override
    public List<Comparable> getSpectrumIds() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getSpectrumIds()");

        List<Comparable> ids = new ArrayList<Comparable>();
        try {
            List<uk.ac.ebi.jmzml.model.mzml.Spectrum> spectrumList = foregroundMzML.getRun().getSpectrumList().getSpectrum();
            if (spectrumList != null) {
                for (uk.ac.ebi.jmzml.model.mzml.Spectrum spectrum : spectrumList) {
                    ids.add(spectrum.getId());
                    spectrumIdHelper.put(spectrum.getId(), spectrum.getHid());
                }
            }

        } catch (Exception ex) {
            session.getTransaction().rollback();
            throw new DataAccessException("Failed to get spectrum Ids: ", ex);
        }

        return ids;
    }

    @Override
    public List<Comparable> getChromatogramIds() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getChromatogramIds()");

        List<Comparable> ids = new ArrayList<Comparable>();
        try {
            List<uk.ac.ebi.jmzml.model.mzml.Chromatogram> chromatogramList = foregroundMzML.getRun().getChromatogramList().getChromatogram();
            if (chromatogramList != null) {
                for (uk.ac.ebi.jmzml.model.mzml.Chromatogram chromatogram : chromatogramList) {
                    ids.add(chromatogram.getId());
                    chromatogramIdHelper.put(chromatogram.getId(), chromatogram.getHid());
                }
            }
        } catch (Exception ex) {
            session.getTransaction().rollback();
            throw new DataAccessException("Failed to get chromatogram Ids: ", ex);
        }

        return ids;
    }

    @Override
    public List<CVLookup> getCvLookups() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getCvLookups()");

        try {
            CVList rawCvList = foregroundMzML.getCvList();
            return MzMLTransformer.transformCVList(rawCvList);
        } catch (HibernateException e) {
            throw new DataAccessException("Exception while trying to read a list of cv lookups: ", e);
        }
    }

    @Override
    public FileDescription getFileDescription() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getFileDescription()");

        try {
            uk.ac.ebi.jmzml.model.mzml.FileDescription rawFileDesc = foregroundMzML.getFileDescription();
            return MzMLTransformer.transformFileDescription(rawFileDesc);
        } catch (HibernateException e) {
            throw new DataAccessException("Exception while trying to read file description: ", e);
        }
    }

    @Override
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getReferenceableParamGroup()");

        try {
            ReferenceableParamGroupList rawRefParamGroup = foregroundMzML.getReferenceableParamGroupList();
            return MzMLTransformer.transformReferenceableParamGroupList(rawRefParamGroup);
        } catch (HibernateException e) {
            throw new DataAccessException("Exception while trying to read referenceable param group: ", e);
        }
    }

    @Override
    public List<Sample> getSamples() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getSamples()");

        try {
            SampleList rawSample = foregroundMzML.getSampleList();
            return MzMLTransformer.transformSampleList(rawSample);
        } catch (HibernateException e) {
            throw new DataAccessException("Exception while trying to read samples: ", e);
        }
    }

    @Override
    public List<Software> getSoftware() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getSoftware()");

        try {
            SoftwareList rawSoftware = foregroundMzML.getSoftwareList();
            return MzMLTransformer.transformSoftwareList(rawSoftware);
        } catch (HibernateException e) {
            throw new DataAccessException("Exception while trying to read software: ", e);
        }
    }

    @Override
    public List<ScanSetting> getScanSettings() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getScanSettings()");

        try {
            ScanSettingsList rawScanSettingList = foregroundMzML.getScanSettingsList();
            return MzMLTransformer.transformScanSettingList(rawScanSettingList);
        } catch (HibernateException e) {
            throw new DataAccessException("Exception while trying to read scan settings list: ", e);
        }
    }

    @Override
    public List<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getInstrumentConfigurations()");

        try {
            InstrumentConfigurationList rawInstrumentList = foregroundMzML.getInstrumentConfigurationList();
            return MzMLTransformer.transformInstrumentConfigurationList(rawInstrumentList);
        } catch (HibernateException e) {
            throw new DataAccessException("Exception while trying to read instrument configuration list: ", e);
        }
    }

    @Override
    public List<DataProcessing> getDataProcessings() throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getDataProcessings()");

        try {
            uk.ac.ebi.jmzml.model.mzml.DataProcessingList rawDataProcList = foregroundMzML.getDataProcessingList();
            return MzMLTransformer.transformDataProcessingList(rawDataProcList);
        } catch (HibernateException e) {
            throw new DataAccessException("Exception while trying to read data processing list: ", e);
        }
    }

    @Override
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getSpectrumById()");

        try {
            Long hid = spectrumIdHelper.get(id);
            uk.ac.ebi.jmzml.model.mzml.Spectrum rawSpectrum = null;

            if (hid != null) {
                rawSpectrum = (uk.ac.ebi.jmzml.model.mzml.Spectrum) session.get(uk.ac.ebi.jmzml.model.mzml.Spectrum.class, hid);

            } else {
                logger.error("hid was not cached in spectrumIdHelper");
            }

            return MzMLTransformer.transformSpectrum(rawSpectrum);
        } catch (HibernateException ex) {
            throw new DataAccessException("Exception while trying to read Spectrum using Spectrum ID: ", ex);
        }
    }

    @Override
    public Chromatogram getChromatogramById(Comparable id) throws DataAccessException {
        logger.debug(Thread.currentThread().getId() + "getChromatogramById()");

        try {
            Long hid = chromatogramIdHelper.get(id);
            uk.ac.ebi.jmzml.model.mzml.Chromatogram rawChroma = null;

            if (hid != null) {
                rawChroma = (uk.ac.ebi.jmzml.model.mzml.Chromatogram) session.get(uk.ac.ebi.jmzml.model.mzml.Chromatogram.class, hid);
            } else {
                logger.error("hid was not cached in chromatogramIdHelper");
            }
            return MzMLTransformer.transformChromatogram(rawChroma);
        } catch (HibernateException ex) {
            throw new DataAccessException("Exception while trying to read Chromatogram using chromatogram ID: ", ex);
        }
    }

    public MzML getForegroundMzML() {
        return foregroundMzML;
    }

    public void setForegroundMzML(MzML foregroundMzML) {
        this.foregroundMzML = foregroundMzML;
    }
}
