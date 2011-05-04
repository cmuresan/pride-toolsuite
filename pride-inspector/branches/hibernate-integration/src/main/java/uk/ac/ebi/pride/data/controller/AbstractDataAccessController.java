package uk.ac.ebi.pride.data.controller;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.util.Collection;

/**
 * This is just a convenience class
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 12:22:24
 */
public abstract class AbstractDataAccessController extends PropertyChangeHelper implements DataAccessController {

    private String name = null;
    private String description = null;
    private Object source = null;

    protected Comparable foregroundExperimentId= null;
    protected Spectrum foregroundSpectrum = null;
    protected Chromatogram foregroundChromatogram = null;
    protected TwoDimIdentification foregroundTwoDimIdent = null;
    protected GelFreeIdentification foregroundGelFreeIdent = null;

    protected AbstractDataAccessController(){
        this(null);
    }

    protected AbstractDataAccessController(Object source) {
        setSource(source);
    }

    public void close() {
        // all property change listeners must be removed before close.
        this.removeAllPropertyChangeListeners();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Object getSource() {
        return source;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSource(Object src) {
        this.source = src;
    }

    public boolean hasIdentification() {
        boolean ident = false;

        try {
            Collection<Comparable> gelFreeIdentIds = this.getGelFreeIdentIds();
            Collection<Comparable> twoDimIdentIds = this.getTwoDimIdentIds();
            if ((gelFreeIdentIds != null && !gelFreeIdentIds.isEmpty()) ||
                    twoDimIdentIds != null && !twoDimIdentIds.isEmpty()) {
                ident = true;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            //ToDo: logging and exception
        }

        return ident;
    }

    @Override
    public boolean hasSpectrum() {
        boolean spectrum = false;

        try {
            Collection<Comparable> specIds = this.getSpectrumIds();
            if (specIds != null && !specIds.isEmpty()) {
                spectrum = true;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            //ToDo: logging and exception
        }

        return spectrum;
    }

    @Override
    public boolean hasChromatogram() {
        boolean chroma = false;

        try {
            Collection<Comparable> chromIds = this.getChromatogramIds();
            if (chromIds != null && !chromIds.isEmpty()) {
                chroma = true;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            //ToDo: logging and exception
        }

        return chroma;
    }

    public synchronized Comparable getForegroundExperimentId() {
        return foregroundExperimentId;
    }

    public void setForegroundExperimentId(Comparable expId) throws DataAccessException {
        Comparable acc = foregroundExperimentId;
        if (!expId.equals(acc)) {
            Comparable oldExp, newExp;
            synchronized(this) {
                oldExp = foregroundExperimentId;
                foregroundExperimentId = expId;
                newExp = foregroundExperimentId;
            }
            firePropertyChange(FOREGROUND_EXPERIMENT_CHANGED, oldExp, newExp);
        }
    }

    public synchronized Chromatogram getForegroundChromatogram() {
        return foregroundChromatogram;
    }

    public void setForegroundChromatogramById(Comparable chromaId) throws DataAccessException {
        Chromatogram newChroma = this.getChromatogramById(chromaId);
        if (foregroundChromatogram == null ||
                (newChroma != null && !foregroundChromatogram.getId().equals(newChroma.getId()))) {
            Chromatogram oldChroma;
            synchronized(this) {
                oldChroma = foregroundChromatogram;
                foregroundChromatogram = newChroma;
            }
            firePropertyChange(FOREGROUND_CHROMATOGRAM_CHANGED, oldChroma, newChroma);
        }
    }

    public synchronized TwoDimIdentification getForegroundTwoDimIdent() {
        return foregroundTwoDimIdent;
    }

    public void setForegroundTwoDimIdentById(Comparable identId) throws DataAccessException {
        TwoDimIdentification newIdent = this.getTwoDimIdentById(identId);
        if (foregroundTwoDimIdent == null ||
                (newIdent != null && !foregroundTwoDimIdent.getAccession().equals(newIdent.getAccession()))) {
            TwoDimIdentification oldIdent;
            synchronized(this) {
                oldIdent = foregroundTwoDimIdent;
                foregroundTwoDimIdent = this.getTwoDimIdentById(identId);
            }
            firePropertyChange(FOREGROUND_TWODIM_IDENTIFICATION_CHANGED, oldIdent, newIdent);
        }
    }

    public synchronized GelFreeIdentification getForegroundGelFreeIdent() {
        return foregroundGelFreeIdent;
    }

    public void setForegroundGelFreeIdentById(Comparable identId) throws DataAccessException {
        GelFreeIdentification newIdent = this.getGelFreeIdentById(identId);
        if (foregroundGelFreeIdent == null ||
                (newIdent != null && !foregroundGelFreeIdent.getAccession().equals(newIdent.getAccession()))) {
            GelFreeIdentification oldIdent;
            synchronized(this) {
                oldIdent = foregroundGelFreeIdent;
                foregroundGelFreeIdent = this.getGelFreeIdentById(identId);
            }
            firePropertyChange(FOREGROUND_GELFREE_IDENTIFICATION_CHANGED, oldIdent, newIdent);
        }
    }

    public synchronized Spectrum getForegroundSpectrum() {
        return foregroundSpectrum;
    }

    public void setForegroundSpectrumById(Comparable specId) throws DataAccessException {
        Spectrum newSpec = this.getSpectrumById(specId);
        if (foregroundSpectrum == null ||
                (newSpec != null && !foregroundSpectrum.getId().equals(newSpec.getId()))) {
            Spectrum oldSpec;
            synchronized(this){
                oldSpec = foregroundSpectrum;
                foregroundSpectrum = this.getSpectrumById(specId);
            }
            firePropertyChange(FOREGROUND_SPECTRUM_CHANGED, oldSpec, newSpec);
        }
    }

    @Override
    public Collection<CVLookup> getCvLookups() throws DataAccessException {
        return null;  
    }

    @Override
    public FileDescription getFileDescription() throws DataAccessException {
        return null;  
    }

    @Override
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<Sample> getSamples() throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<Software> getSoftware() throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<ScanSetting> getScanSettings() throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<DataProcessing> getDataProcessings() throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<Comparable> getExperimentIds() throws DataAccessException {
        return null;  
    }

    @Override
    public Experiment getExperimentById(Comparable expId) throws DataAccessException {
        return null;  
    }

    @Override
    public MetaData getMetaData() throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<Comparable> getSpectrumIds() throws DataAccessException {
        return null;  
    }

    @Override
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<Comparable> getChromatogramIds() throws DataAccessException {
        return null;  
    }

    @Override
    public Chromatogram getChromatogramById(Comparable id) throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<Comparable> getTwoDimIdentIds() throws DataAccessException {
        return null;  
    }

    @Override
    public TwoDimIdentification getTwoDimIdentById(Comparable id) throws DataAccessException {
        return null;  
    }

    @Override
    public Collection<Comparable> getGelFreeIdentIds() throws DataAccessException {
        return null;  
    }

    @Override
    public GelFreeIdentification getGelFreeIdentById(Comparable id) throws DataAccessException {
        return null;  
    }
}
