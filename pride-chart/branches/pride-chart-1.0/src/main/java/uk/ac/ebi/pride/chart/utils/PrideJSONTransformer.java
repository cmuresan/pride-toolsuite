package uk.ac.ebi.pride.chart.utils;

import org.apache.log4j.Logger;
import org.json.JSONException;
import uk.ac.ebi.pride.chart.io.DataAccessReader;
import uk.ac.ebi.pride.chart.io.PrideDataReader;
import uk.ac.ebi.pride.chart.io.PrideJSONWriter;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import static uk.ac.ebi.pride.chart.utils.PridePlotConstants.NEW_LINE;

/**
 * User: qingwei
 * Date: 03/07/13
 */
public class PrideJSONTransformer {
    private static Logger logger = Logger.getLogger(PrideJSONTransformer.class);

    private enum FileType {PRIDE_XML, mzIdentML}

    private void writeJSONFile(PrideJSONWriter writer, File outFile) throws JSONException, IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("1, ").append(writer.getPeakIntensity().toString()).append(NEW_LINE);
        sb.append("2, ").append(writer.getPreCharge().toString()).append(NEW_LINE);
        sb.append("3, ").append(writer.getAvg().toString()).append(NEW_LINE);
        sb.append("4, ").append(writer.getPreMasses().toString()).append(NEW_LINE);
        sb.append("5, ").append(writer.getPeptides().toString()).append(NEW_LINE);
        sb.append("6, ").append(writer.getPeaksMS().toString()).append(NEW_LINE);
        sb.append("7, ").append(writer.getDelta().toString()).append(NEW_LINE);
        sb.append("8, ").append(writer.getMissed().toString());

        BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
        out.write(sb.toString());
        out.close();
    }

    private String getCost(long start, long end) {
        double time = (end - start) / 1000d;
        BigDecimal cost = new BigDecimal(time).setScale(2);
        return cost.toString();
    }

    public void transform(File inFile, File outDir) throws Exception {
        PrideDataReader reader;
        PrideJSONWriter writer;
        DataAccessController controller = null;
        long start;
        long end;

        String inFileName = inFile.getName();
        String outFileName = inFileName.replaceAll("\\.(xml|mzid)$", ".json");
        File outFile = new File(outDir, outFileName);

        FileType type = null;
        if (inFileName.endsWith("xml")) {
            type = FileType.PRIDE_XML;
        } else if (inFileName.endsWith("mzid")) {
            type = FileType.mzIdentML;
        }

        if (type == null) {
            return;
        }

        logger.debug("Begin load " + type + " file: " + inFile.getName());
        start = System.currentTimeMillis();
        switch (type) {
            case PRIDE_XML:
                controller = new PrideXmlControllerImpl(inFile);
                break;
            case mzIdentML:
                controller = new MzIdentMLControllerImpl(inFile);
                break;
        }
        end = System.currentTimeMillis();
        logger.debug("End load " + type + " file: " + inFile.getName() + ". Cost " + getCost(start, end) + "(s)");


        logger.debug("Begin export json file " + outFile.getAbsolutePath());
        start = System.currentTimeMillis();
        reader = new DataAccessReader(controller);
        writer = new PrideJSONWriter(reader);
        writeJSONFile(writer, outFile);

        end = System.currentTimeMillis();
        logger.debug("End export json file: " + outFile.getAbsolutePath() + ". Cost " + getCost(start, end) + "(s)");
    }

    public static void main(String[] args) throws Exception {
        File inDir = new File(args[0]);
        File outDir = new File(args[1]);

        PrideJSONTransformer transformer = new PrideJSONTransformer();

        if (! outDir.exists()) {
            outDir.mkdirs();
        }

        for (File inFile : inDir.listFiles()) {
            transformer.transform(inFile, outDir);
        }
    }
}
