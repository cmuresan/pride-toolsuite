package uk.ac.ebi.pride.chart.graphics.implementation;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Exception to be thrown when the spectral chartData per experiment is inconsistent for a chart.</p>
 *
 * @author Antonio Fabregat
 * Date: 03-sep-2010
 * Time: 10:12:30
 */
public class PrideChartException extends Exception {
    private List<String> errorMessages = new ArrayList<String>();

    public PrideChartException() {}

    public PrideChartException(String msg) {
        errorMessages.add(msg);
    }

    public PrideChartException(List<String> errorMessages){
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages(){
        return errorMessages;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("");
        for(String msg : errorMessages){
            sb.append(msg);
            sb.append("\n");
        }
        return sb.toString();
    }
}
