package uk.ac.ebi.pride.gui.component.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartCategory;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;

/**
 * <p>Class to mapping the categories between the PRIDE-Chart and PRIDE-Inspector.</p>
 *
 * @author Antonio Fabregat
 * Date: 08-nov-2010
 * Time: 16:12:38
 */
public class ChartCategoryMapping {
    /**
     * Contains the logger object for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ChartCategoryMapping.class.getName());

    /**
     * Identifier associated with the PrideChart object
     */
    private int id;

    /**
     * <p> Creates an instance of this ChartCategoryMapping object, setting all fields as per description below.</p>
     *
     * @param prideChart a managedPrideChart object
     * @throws PrideChartException a managedPrideChart exception
     */
    public ChartCategoryMapping(PrideChart prideChart) throws PrideChartException {
        try {
            id = PrideChartFactory.getPrideChartIdentifier(prideChart);
        } catch (PrideChartException e) {
            logger.error("Failed to get the chart identifier", e);
            throw e; 
        }
    }

    /**
     * Return true if the pride chart object passed to the constructor belongs to the corresponding specified category
     * 
     * @param category the category the be checked
     * @return true if the chart belongs to the specified category
     */
    public boolean belong(DataAccessController.ContentCategory category) {
        boolean belong;
        switch (category) {
            case CHROMATOGRAM:
                belong = PrideChartCategory.isOfCategory(id, PrideChartCategory.CHROMATOGRAM);
                break;

            case PEPTIDE:
                belong = PrideChartCategory.isOfCategory(id, PrideChartCategory.PEPTIDE);
                break;

            case PROTEIN:
                belong = PrideChartCategory.isOfCategory(id, PrideChartCategory.PROTEIN);
                break;

            case SPECTRUM:
                belong = PrideChartCategory.isOfCategory(id, PrideChartCategory.SPECTRUM);
                break;

            default:
                belong = false;
        }
        return belong;
    }
}
