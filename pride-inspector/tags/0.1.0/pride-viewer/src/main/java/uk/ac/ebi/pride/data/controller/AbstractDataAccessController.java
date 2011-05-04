package uk.ac.ebi.pride.data.controller;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

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
    private boolean experimentFriendly = false;
    private boolean spectrumFriendly = false;
    private boolean identificationFriendly = false;
    private Object source = null;
    protected Experiment foregroundExperiment = null;
    protected Spectrum foregroundSpectrum = null;
    protected Chromatogram foregroundChromatogram = null;
    protected TwoDimIdentification foregroundTwoDimIdent = null;
    protected GelFreeIdentification foregroundGelFreeIdent = null;

    public abstract void close();

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final Object getSource() {
        return source;
    }

    public final void setSource(Object source) {
        this.source = source;
    }


    public boolean isExperimentFriendly() {
        return experimentFriendly;
    }

    public void setExperimentFriendly(boolean experimentFriendly) {
        this.experimentFriendly = experimentFriendly;
    }

    public final boolean isIdentificationFriendly() {
        return identificationFriendly;
    }

    public final void setIdentificationFriendly(boolean identificationFriendly) {
        this.identificationFriendly = identificationFriendly;
    }

    public final boolean isSpectrumFriendly() {
        return spectrumFriendly;
    }

    public final void setSpectrumFriendly(boolean spectrumFriendly) {
        this.spectrumFriendly = spectrumFriendly;
    }

    public final synchronized Experiment getForegroundExperiment() {
        return foregroundExperiment;
    }

    public final void setForegroundExperimentById(String expId) throws DataAccessException {
        String acc = foregroundExperiment == null ? null : foregroundExperiment.getAccession();
        if (!expId.equals(acc)) {
            Experiment oldExp, newExp;
            synchronized(this) {
                oldExp = foregroundExperiment;
                foregroundExperiment = this.getExperimentById(expId);
                newExp = foregroundExperiment;
            }
            firePropertyChange(FOREGROUND_EXPERIMENT_CHANGED, oldExp, newExp);
        }
    }

    public final synchronized Chromatogram getForegroundChromatogram() {
        return foregroundChromatogram;
    }

    public final void setForegroundChromatogramById(String chromaId) throws DataAccessException {
        String id = foregroundChromatogram == null ? null : foregroundChromatogram.getId();
        if (!chromaId.equals(id)) {
            Chromatogram oldChroma, newChroma;
            synchronized(this) {
                oldChroma = foregroundChromatogram;
                foregroundChromatogram = this.getChromatogramById(chromaId);
                newChroma = foregroundChromatogram;
            }
            firePropertyChange(FOREGROUND_CHROMATOGRAM_CHANGED, oldChroma, newChroma);
        }
    }

    public synchronized TwoDimIdentification getForegroundTwoDimIdent() {
        return foregroundTwoDimIdent;
    }

    public void setForegroundTwoDimIdentById(String identId) throws DataAccessException {
        String acc = foregroundTwoDimIdent == null ? null: foregroundTwoDimIdent.getAccession();
        if (!identId.equals(acc)) {
            TwoDimIdentification oldIdent, newIdent;
            synchronized(this) {
                oldIdent = foregroundTwoDimIdent;
                foregroundTwoDimIdent = this.getTwoDimIdentById(identId);
                newIdent = foregroundTwoDimIdent;
            }
            firePropertyChange(FOREGROUND_TWODIM_IDENTIFICATION_CHANGED, oldIdent, newIdent);
        }
    }

    public synchronized GelFreeIdentification getForegroundGelFreeIdent() {
        return foregroundGelFreeIdent;
    }

    public final void setForegroundGelFreeIdentById(String identId) throws DataAccessException {
        String acc = foregroundGelFreeIdent == null ? null : foregroundGelFreeIdent.getAccession();
        if (!identId.equals(acc)) {
            GelFreeIdentification oldIdent, newIdent;
            synchronized(this) {
                oldIdent = foregroundGelFreeIdent;
                foregroundGelFreeIdent = this.getGelFreeIdentById(identId);
                newIdent = foregroundGelFreeIdent;
            }
            firePropertyChange(FOREGROUND_GELFREE_IDENTIFICATION_CHANGED, oldIdent, newIdent);
        }
    }

    public final synchronized Spectrum getForegroundSpectrum() {
        return foregroundSpectrum;
    }

    public final void setForegroundSpectrumById(String specId) throws DataAccessException {
        String id = foregroundSpectrum == null ? null : foregroundSpectrum.getId();
        if (!specId.equals(id)) {
            Spectrum oldSpec, newSpec;
            synchronized(this){
                oldSpec = foregroundSpectrum;
                foregroundSpectrum = this.getSpectrumById(specId);
                newSpec = foregroundSpectrum;
            }
            firePropertyChange(FOREGROUND_SPECTRUM_CHANGED, oldSpec, newSpec);
        }
    }

    public List<String> getExperimentIds() throws DataAccessException {
        return null;
    }

    public Experiment getExperimentById(String experimentId) throws DataAccessException {
        return null;
    }

    public MzML getMzMLMetaData() throws DataAccessException {
        return null;
    }

    public List<String> getSpectrumIds() throws DataAccessException {
        return null;
    }

    public Spectrum getSpectrumById(String id) throws DataAccessException {
        return null;
    }

    public List<String> getChromatogramIds() throws DataAccessException {
        return null;
    }

    public Chromatogram getChromatogramById(String id) throws DataAccessException {
        return null;
    }

    public List<String> getTwoDimIdentIds() throws DataAccessException {
        return null;
    }

    public TwoDimIdentification getTwoDimIdentById(String id) throws DataAccessException {
        return null;
    }

    public List<String> getGelFreeIdentIds() throws DataAccessException {
        return null;
    }

    public GelFreeIdentification getGelFreeIdentById(String id) throws DataAccessException {
        return null;
    }
}
