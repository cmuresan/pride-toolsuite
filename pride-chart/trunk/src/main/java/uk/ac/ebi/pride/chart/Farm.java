package uk.ac.ebi.pride.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.controller.DBAccessController;
import uk.ac.ebi.pride.chart.controller.PrideChartSummaryData;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartFactory;
import uk.ac.ebi.pride.chart.model.implementation.SpectralDataPerExperimentException;

import java.util.List;

/**
 * <p>Main class for running PRIDE-Chart on the FARM</p>
 *
 * @author Antonio Fabregat
 * Date: 2-July-2010
 * Time: 14:23:57
 */
public class Farm {
    private static DBAccessController dbac;
    private static final Logger logger = LoggerFactory.getLogger(Farm.class);


    public static void main(String[] args) {
        String dbName = "";
        if(args.length==0){
            logger.error("Database name (pride_1|pride_2) has to be passed as first parameter");
            System.exit(1);
        }else{
            dbName = args[0];
            if(!dbName.equals("pride_1") && !dbName.equals("pride_2")){
                logger.error("Database name (pride_1|pride_2) has to be passed as first parameter");
                System.exit(1);
            }
        }
        logger.warn("Connecting to public instance at 193.62.194.210:5000, hardcoded in code");
        String dbAlias = "//193.62.194.210:5000/" + dbName;
        dbac = new DBAccessController(dbAlias);

        PrideChartSummaryData.setVerbose(false);
        logger.info("** Starting chart calculation... **");
        List<String> experimentAccessionNumbers = dbac.getAllExperimentsAccessionNumber();
        //List<String> experimentAccessionNumbers = new ArrayList<String>();
        //experimentAccessionNumbers.add("10548");
        for(String accessionNumber : experimentAccessionNumbers){
            System.out.print("[" + accessionNumber + "] ");
            processCharts(accessionNumber);
        }
        logger.info("");
        logger.info("** Work finished successfully **");
    }

    public static void processCharts(String accessionNumber) {
        //Retrieving data from database
        List<PrideChart> prideCharts = getChartData(accessionNumber);

        //Here you can check if size is equal to a number if you add a new chart and want to recalculate everything
        if (prideCharts.isEmpty()) {
            try {
                PrideChartSummaryData  summaryData = new PrideChartSummaryData(accessionNumber, dbac);

                prideCharts = PrideChartFactory.getAllCharts(summaryData);

                for (PrideChart chart : prideCharts)
                    storeChartData(summaryData.getExperimentID(), chart);

                logger.info(" has been successfully calculated");
            } catch (SpectralDataPerExperimentException e) {
                System.out.println( e.getMessage() );
            }
        } else {
            logger.info(" has been successfully calculated");
        }
    }

    private static void storeChartData(int experimentID, PrideChart prideChart) {
        try {
            int type = PrideChartFactory.getPrideChartIdentifier(prideChart);
            dbac.storeChartData(experimentID, type, prideChart.getChartJSonData());
        } catch (PrideChartException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static List<PrideChart> getChartData(String accessionNumber) {
        int experimentID = dbac.getExperimentID(accessionNumber);
        return dbac.getChartData(experimentID);
    }
}