package uk.ac.ebi.pride.gui.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessMode;
import uk.ac.ebi.pride.data.controller.cache.Cache;
import uk.ac.ebi.pride.data.controller.cache.CacheBuilder;
import uk.ac.ebi.pride.data.controller.impl.AbstractDataAccessController;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.ParamGroup;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.component.chart.PrideChartManager;

import java.util.ArrayList;
import java.util.Collection;
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
        throw new UnsupportedOperationException("This is a empty data access controller");
    }

    @Override
    public void setMode(DataAccessMode mode) {
        throw new UnsupportedOperationException("This is a empty data access controller");
    }

    @Override
    public Collection<Comparable> getExperimentAccs() throws DataAccessException {
        throw new UnsupportedOperationException("This is a empty data access controller");
    }

    @Override
    public ParamGroup getAdditional() throws DataAccessException {
        throw new UnsupportedOperationException("This is a empty data access controller");
    }

    @Override
    public List<PrideChartManager> getChartData() throws DataAccessException {
        throw new UnsupportedOperationException("This is a empty data access controller");
    }

    @Override
    public Collection<Comparable> getIdentificationIds() throws DataAccessException {
        return new ArrayList<Comparable>();
    }

    @Override
    public Identification getIdentificationById(Comparable id) throws DataAccessException {
        throw new UnsupportedOperationException("This is a empty data access controller");
    }

    @Override
    public Collection<Comparable> getSpectrumIds() throws DataAccessException {
        return new ArrayList<Comparable>();
    }

    @Override
    public Spectrum getSpectrumById(Comparable id) throws DataAccessException {
        throw new UnsupportedOperationException("This is a empty data access controller");
    }

    @Override
    public Collection<Comparable> getChromatogramIds() throws DataAccessException {
        return new ArrayList<Comparable>();
    }

    @Override
    public Chromatogram getChromatogramById(Comparable id) throws DataAccessException {
        throw new UnsupportedOperationException("This is a empty data access controller");
    }
}
