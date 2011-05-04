package uk.ac.ebi.pride.data.controller;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is just a convenience class
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 12:22:24
 */
public abstract class AbstractDataAccessController extends PropertyChangeHelper implements DataAccessController {

    private String name = null;
    private String description = null;
    private DataAccessControllerType type =  null;
    private Object source = null;

    protected Comparable foregroundExperimentAcc = null;
    protected Spectrum foregroundSpectrum = null;
    protected Chromatogram foregroundChromatogram = null;
    protected TwoDimIdentification foregroundTwoDimIdent = null;
    protected GelFreeIdentification foregroundGelFreeIdent = null;

    protected AbstractDataAccessController() {
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
    public DataAccessControllerType getType() {
        return type;
    }

    @Override
    public void setType(DataAccessControllerType type) {
        this.type = type;
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

    public synchronized Comparable getForegroundExperimentAcc() {
        return foregroundExperimentAcc;
    }

    public void setForegroundExperimentAcc(Comparable expAcc) throws DataAccessException {
        Comparable acc = foregroundExperimentAcc;
        if (!expAcc.equals(acc)) {
            Comparable oldExp, newExp;
            synchronized (this) {
                oldExp = foregroundExperimentAcc;
                foregroundExperimentAcc = expAcc;
                newExp = foregroundExperimentAcc;
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
            synchronized (this) {
                oldChroma = foregroundChromatogram;
                foregroundChromatogram = newChroma;
            }
            firePropertyChange(FOREGROUND_CHROMATOGRAM_CHANGED, oldChroma, newChroma);
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
            synchronized (this) {
                oldSpec = foregroundSpectrum;
                foregroundSpectrum = this.getSpectrumById(specId);
            }
            firePropertyChange(FOREGROUND_SPECTRUM_CHANGED, oldSpec, newSpec);
        }
    }

    public MzGraph getForegroundMzGraph(Class<? extends MzGraph> classType) {
        MzGraph mzGraph = null;

        if (classType.equals(Spectrum.class)) {
            mzGraph = getForegroundSpectrum();
        } else if (classType.equals(Chromatogram.class)) {
            mzGraph = getForegroundChromatogram();
        }

        return mzGraph;
    }

    public synchronized TwoDimIdentification getForegroundTwoDimIdent() {
        return foregroundTwoDimIdent;
    }

    public void setForegroundTwoDimIdentById(Comparable identId) throws DataAccessException {
        TwoDimIdentification newIdent = this.getTwoDimIdentById(identId);
        if (foregroundTwoDimIdent == null ||
                (newIdent != null && !foregroundTwoDimIdent.getAccession().equals(newIdent.getAccession()))) {
            TwoDimIdentification oldIdent;
            synchronized (this) {
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
            synchronized (this) {
                oldIdent = foregroundGelFreeIdent;
                foregroundGelFreeIdent = this.getGelFreeIdentById(identId);
            }
            firePropertyChange(FOREGROUND_GELFREE_IDENTIFICATION_CHANGED, oldIdent, newIdent);
        }
    }

    public int getNumberOfIdentifications() throws DataAccessException {
        int number = getNumberOfGelFreeIdentifications();
        number += getNumberOfTwoDimIdentifications();
        return number;
    }

    public List<Identification> getIdentificationsByIndex(int start, int offset) throws DataAccessException {
        List<Identification> identifications = new ArrayList<Identification>();

        int twoIdentStart = 0;
        int twoIdentEnd = 0;
        int gelFreeStart = 0;
        int gelFreeEnd = 0;

        //get both lists of Identifications
        Collection<Comparable> twoDimIds = getTwoDimIdentIds();
        Collection<Comparable> gelFreeIds = getGelFreeIdentIds();

        //figure out range to get from each of the lists
        if (twoDimIds != null && twoDimIds.size() > start) {
            twoIdentStart = start;
            if (twoDimIds.size() <= start + offset) {
                twoIdentEnd = twoDimIds.size();
                if (gelFreeIds.size() > 0) {
                    if (gelFreeIds.size() <= start + offset - twoDimIds.size()) {
                        gelFreeEnd = gelFreeIds.size();
                    } else {
                        gelFreeEnd = start + offset - twoDimIds.size();
                    }
                }
            } else {
                twoIdentEnd = start + offset;
            }
        } else {
            //start goes beyond that list
            if (gelFreeIds != null && gelFreeIds.size() > 0) {
                start = start - (twoDimIds == null ? 0 : twoDimIds.size()); //recalculate start
                gelFreeStart = start;
                if (gelFreeIds.size() <= start + offset) {
                    gelFreeEnd = gelFreeIds.size();
                } else {
                    gelFreeEnd = start + offset;
                }
            }
        }
        //finally get the identifications
        for (int i = twoIdentStart; i < twoIdentEnd; i++) {
            identifications.add(getTwoDimIdentById(CollectionUtils.getElement(twoDimIds, i)));
        }
        for (int i = gelFreeStart; i < gelFreeEnd; i++) {
            identifications.add(getGelFreeIdentById(CollectionUtils.getElement(gelFreeIds, i)));
        }

        return identifications;
    }

    public int getNumberOfGelFreeIdentifications() throws DataAccessException {
        int number = 0;

        Collection<Comparable> idents = getGelFreeIdentIds();
        if (idents != null) {
            number = idents.size();
        }

        return number;
    }

    public int getNumberOfTwoDimIdentifications() throws DataAccessException {
        int number = 0;

        Collection<Comparable> idents = getTwoDimIdentIds();
        if (idents != null) {
            number = idents.size();
        }

        return number;
    }

    @Override
    public Collection<CVLookup> getCvLookups() throws DataAccessException {
        return Collections.emptyList();
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
        return Collections.emptyList();
    }

    @Override
    public Collection<Software> getSoftware() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<ScanSetting> getScanSettings() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<DataProcessing> getDataProcessings() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<Comparable> getExperimentAccs() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Experiment getExperimentByAcc(Comparable expId) throws DataAccessException {
        return null;
    }

    @Override
    public MetaData getMetaData() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Comparable> getSpectrumIds() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Comparable> getChromatogramIds() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Chromatogram getChromatogramById(Comparable id) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Comparable> getTwoDimIdentIds() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public TwoDimIdentification getTwoDimIdentById(Comparable id) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<Comparable> getGelFreeIdentIds() throws DataAccessException {
        return Collections.emptyList();
    }


    @Override
    public GelFreeIdentification getGelFreeIdentById(Comparable id) throws DataAccessException {
        return null;
    }
}
