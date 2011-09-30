package uk.ac.ebi.pride.chart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Mass file reader.</p>
 *
 * @author Antonio Fabregat
 * Date: 13-jul-2010
 * Time: 9:49:40
 */
public class MSFileReader {
    private static final Logger logger = LoggerFactory.getLogger(MSFileReader.class);

    /**
     * Contains the mass chartData loaded from the file
     */
    private List<Double> massData = new ArrayList<Double>();

    /**
     * <p> Creates an instance of this MSFileReader object, setting all fields as per description below.</p>
     *  
     * @param inputStream an input stream of the file with the mass chartData
     */
    public MSFileReader(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        initialize(br);
    }

    /**
     * <p> Creates an instance of this MSFileReader object, setting all fields as per description below.</p>
     *
     * @param filePath the path of the file with the mass chartData
     */
    public MSFileReader(String filePath) {
        File file = new File(filePath);

        if (!file.exists() || !file.canRead()) {
            logger.error("Can't read " + file);
            return;
        }

        try {
            FileReader fr = new FileReader(file);
            BufferedReader in = new BufferedReader(fr);
            initialize(in);
        } catch (FileNotFoundException e) {
            logger.error("File Disappeared", e);
        }
    }

    private void initialize(BufferedReader br){
        String line;
        try {
            while ((line = br.readLine()) != null) {
                massData.add(new Double(line));
            }
        } catch (IOException e) {
            logger.error("I/O Exception",e);
        }
    }

    /**
     * Returns the chartData loaded from the mass file
     *
     * @return the chartData from the mass file
     */
    public double[] getMassData(){
        double[] data = new double[this.massData.size()];
        Object[] aux = this.massData.toArray();
        for(int i=0; i<aux.length; i++){
            data[i]= (Double) aux[i];
        }
        return data;
    }
}