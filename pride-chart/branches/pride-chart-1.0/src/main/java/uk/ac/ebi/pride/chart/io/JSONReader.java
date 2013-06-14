package uk.ac.ebi.pride.chart.io;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.utils.PridePlotConstants;

import java.io.*;

/**
 * User: Qingwei
 * Date: 11/06/13
 */
public class JSONReader extends PrideDataReader {
    private Logger logger = LoggerFactory.getLogger(JSONReader.class);
    private String source = "JSON";

    private JSONObject json = null;

    private void init(JSONObject json) {
        if (json == null) {
            throw new IllegalArgumentException("JSON Object can not set null!");
        }

        this.json = json;
    }

    public JSONReader(JSONObject json) {
        init(json);
    }

    public JSONReader(String jsonString) {
        try {
            init(new JSONObject(jsonString));
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
    }

    public JSONReader(File jsonFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(PridePlotConstants.NEW_LINE);
            }

            init(new JSONObject(sb.toString()));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected void start() {
        super.start(source);
    }

    @Override
    protected void reading() {

    }

    @Override
    protected void end() {
        super.end(source);
    }
}
