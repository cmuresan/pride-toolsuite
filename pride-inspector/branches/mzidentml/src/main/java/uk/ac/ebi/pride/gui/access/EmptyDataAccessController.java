package uk.ac.ebi.pride.gui.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.impl.AbstractDataAccessController;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.component.chart.PrideChartManager;
import uk.ac.ebi.pride.model.interfaces.core.Experiment;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This represents an dummy data access controller, which has not source and not data.
 *
 * User: rwang
 * Date: 01-Nov-2010
 * Time: 15:30:42
 */
public class EmptyDataAccessController extends AbstractDataAccessController {
    @Override
    public DataAccessMode getMode() {
        return null;
    }

    @Override
    public void setMode(DataAccessMode mode) {
    }

    @Override
    public Collection<Comparable> getExperimentAccs() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public ExperimentMetaData getExperimentMetaData() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<CVLookup> getCvLookups() throws DataAccessException {
        return Collections.emptyList();
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
    public Collection<Software> getSoftwareList() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<ScanSetting> getScanSettings() throws DataAccessException {
        return null;
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
    public ParamGroup getAdditional() throws DataAccessException {
        return null;
    }

    @Override
    public List<PrideChartManager> getChartData() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Collection<Comparable> getIdentificationIds() throws DataAccessException {
        return Collections.emptyList();
    }

    @Override
    public Identification getIdentificationById(Comparable id) throws DataAccessException {
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
}
