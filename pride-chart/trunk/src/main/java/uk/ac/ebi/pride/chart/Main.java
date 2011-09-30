package uk.ac.ebi.pride.chart;

import org.jfree.chart.ChartFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.controller.DBAccessController;
import uk.ac.ebi.pride.chart.controller.PrideChartSummaryData;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartFactory;
import uk.ac.ebi.pride.chart.model.implementation.SpectralDataPerExperimentException;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Main class for running PRIDE-Chart stand-alone</p>
 *
 * @author Antonio Fabregat
 * Date: 2-July-2010
 * Time: 14:23:57
 */
public class Main {
    private static DBAccessController dbac;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static final boolean READ_DB = true;
    public static final boolean WRITE_DB = false;

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
//        dbac = new DBAccessController();

        //String accessionNumber = "9776"; // The BIGGEST xDD
        //String accessionNumber = "9177"; // 2 Million
        //String accessionNumber = "9178"; // 2 Million
        //String accessionNumber = "9179"; //347.000 spectra
        //String accessionNumber = "10638";// 365,386 spectra
        //String accessionNumber = "9175";
        //String accessionNumber = "9759";
        //String accessionNumber = "9758";
        //String accessionNumber = "9757";
        //String accessionNumber = "9751";
        //String accessionNumber = "10045";
        //String accessionNumber = "10047";
        //String accessionNumber = "10050";
        //String accessionNumber = "9269";
        String accessionNumber = "16649";
        //String accessionNumber = "1";
        //String accessionNumber = "10055";
        //String accessionNumber = "10065";
        //String accessionNumber = "10150";
        //String accessionNumber = "9163"; // Repeat!!
        //String accessionNumber = "12011";
        //String accessionNumber = "10030";
        //String accessionNumber = "10020";
        //String accessionNumber = "10010";
        //String accessionNumber = "7597";
        //String accessionNumber = "10205";
        //String accessionNumber = "9350";
        //String accessionNumber = "35";
        //String accessionNumber = "1";
        //String accessionNumber = "11566";
        //String accessionNumber = "10105";
        //String accessionNumber = "13321";
        //String accessionNumber = "10885";
        //String accessionNumber = "2780";
        //String accessionNumber = "8261";        //Contains error messages
        //String accessionNumber = "9181";
        //String accessionNumber = "10548";

        List<PrideChart> prideCharts;
        if(READ_DB)
            prideCharts = getChartData(accessionNumber);
        else
            prideCharts = new ArrayList<PrideChart>();
        
        if (prideCharts.size() == 0) {
            PrideChartSummaryData summaryData = null;
            try {
                PrideChartSummaryData.setVerbose(true);
                summaryData = new PrideChartSummaryData(accessionNumber, dbac);
                prideCharts = PrideChartFactory.getAllCharts(summaryData);

                if( WRITE_DB )
                    storeChartData(summaryData.getExperimentID(), prideCharts);
            } catch (SpectralDataPerExperimentException e) {
                System.err.println(e);
                System.exit(1);
            }
        }
        showCharts(prideCharts);
    }

    // create and display the frames
    private static void showCharts(List<PrideChart> prideCharts){
        for (PrideChart chart : prideCharts) {
            String name = chart.getClass().getName();
            System.out.println(chart.getChartJSonData());
            try {
                ChartFrame frame = new ChartFrame(name, chart.getChart());
                frame.pack();
                frame.setVisible(true);
            } catch (PrideChartException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void storeChartData(int experimentID, List<PrideChart> prideCharts) {
        try {
            for (PrideChart prideChart : prideCharts) {
                int type = PrideChartFactory.getPrideChartIdentifier(prideChart);
                dbac.storeChartData(experimentID, type, prideChart.getChartJSonData());
            }
        } catch (PrideChartException e) {
            System.out.println(e.getMessage());
        }
    }

    private static List<PrideChart> getChartData(String accessionNumber) {
        int experimentID = dbac.getExperimentID(accessionNumber);
        return dbac.getChartData(experimentID);
    }
}