package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.pride.data.controller.AbstractDataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.io.PrideXmlUnmarshaller;
import uk.ac.ebi.pride.data.jaxb.pridexml.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 02-Feb-2010
 * Time: 12:31:30
 */
public class PrideXmlControllerImpl extends AbstractDataAccessController {

    private PrideXmlUnmarshaller unmarshaller = null;

    public PrideXmlControllerImpl(File file) throws DataAccessException {
        unmarshaller = new PrideXmlUnmarshaller(file);
        this.setName(file.getName());
        this.setExperimentFriendly(true);
        this.setSpectrumFriendly(true);
        this.setIdentificationFriendly(true);
        this.setSource(file);
        initialize();
    }

    private void initialize() throws DataAccessException {
        List<String> expIds = this.getExperimentIds();
        if (expIds != null && !expIds.isEmpty()) {
            System.out.println("getting experiment");
            foregroundExperiment = this.getExperimentById(expIds.get(0));
        }

        List<String> spectrumIds = this.getSpectrumIds();
        if (spectrumIds != null && !spectrumIds.isEmpty()) {
            foregroundSpectrum = this.getSpectrumById(spectrumIds.get(0));
        }

        List<String> tdIdentIds = this.getTwoDimIdentIds();
        if (tdIdentIds != null && !tdIdentIds.isEmpty()) {
            foregroundTwoDimIdent = this.getTwoDimIdentById(tdIdentIds.get(0));
        }

        List<String> gfIdentIds = this.getGelFreeIdentIds();
        if (gfIdentIds != null && !gfIdentIds.isEmpty()) {
            foregroundGelFreeIdent = this.getGelFreeIdentById(gfIdentIds.get(0));
        }
    }

    @Override
    public List<String> getExperimentIds() throws DataAccessException {
        return unmarshaller.getExperimentIds();
    }

    @Override
    public Experiment getExperimentById(String expId) throws DataAccessException {
        String title = unmarshaller.getExperimentTitle(expId);
        String shortLabel = unmarshaller.getShortLabel(expId);
        // protocol
        ExperimentType.Protocol rawProt = unmarshaller.getProtocol(expId);
        Protocol protocol = null;
        if (rawProt != null) {
            protocol = PrideXmlTransformer.transformProtocol(rawProt);
        }
        // reference
        List<ReferenceType> rawRefs = unmarshaller.getReferences(expId);
        List<Reference> refs = null;
        if (rawRefs != null && !rawRefs.isEmpty()) {
            refs = new ArrayList<Reference>();
            for(ReferenceType rawRef : rawRefs) {
                Reference ref = PrideXmlTransformer.transformReference(rawRef);
                refs.add(ref);
            }
        }

        // additional params
        ParamType rawAdditional = unmarshaller.getAdditionalParam(expId);
        ParamGroup params = PrideXmlTransformer.transformParams(rawAdditional);
        
        Experiment experiment = new Experiment(title, expId, shortLabel, protocol, refs,
                                               null, null, false, null, params);
        
        return experiment;
    }

    @Override
    public synchronized List<String> getSpectrumIds() throws DataAccessException {
        Experiment experiment = this.getForegroundExperiment();
        System.out.println(experiment);
        return unmarshaller.getSpectrumIds(experiment.getAccession());
    }

    @Override
    public Spectrum getSpectrumById(String id) throws DataAccessException {
        SpectrumType rawSpec = null;
        Spectrum spec = null;
        synchronized(foregroundExperiment) {
            rawSpec = unmarshaller.getSpectrumById(foregroundExperiment.getAccession(), id);
        }
        if (rawSpec != null)
            spec = PrideXmlTransformer.transformSpectrum(rawSpec);

        return spec;
    }

    @Override
    public synchronized List<String> getTwoDimIdentIds() throws DataAccessException {
        Experiment experiment = this.getForegroundExperiment();
        return unmarshaller.getTwoDimIdentAccs(experiment.getAccession());
    }

    @Override
    public TwoDimIdentification getTwoDimIdentById(String id) throws DataAccessException {
        TwoDimensionalIdentificationType rawIdent = null;
        TwoDimIdentification ident = null;
        synchronized(foregroundExperiment) {
            rawIdent = unmarshaller.getTwoDimIdentByAcc(foregroundExperiment.getAccession(), id);
        }
        if (rawIdent != null)
            ident = PrideXmlTransformer.transformTwoDimIdent(rawIdent);

        return ident;
    }

    @Override
    public synchronized List<String> getGelFreeIdentIds() throws DataAccessException {
        Experiment experiment = this.getForegroundExperiment();
        return unmarshaller.getGelFreeIdentAccs(experiment.getAccession());
    }

    @Override
    public GelFreeIdentification getGelFreeIdentById(String id) throws DataAccessException {
        GelFreeIdentificationType rawIdent = null;
        GelFreeIdentification ident = null;
        synchronized(foregroundExperiment) {
            rawIdent = unmarshaller.getGelFeeIdentByAcc(foregroundExperiment.getAccession(), id);
        }
        if (rawIdent != null) {
            ident = PrideXmlTransformer.transformGelFreeIdent(rawIdent);
        }

        return ident;
    }

    @Override
    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
