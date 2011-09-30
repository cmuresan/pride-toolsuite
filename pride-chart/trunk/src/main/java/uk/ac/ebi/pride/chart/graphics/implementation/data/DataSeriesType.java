package uk.ac.ebi.pride.chart.graphics.implementation.data;

import java.awt.*;

/**
 * <p></p>
 * 
 * User: Antonio Fabregat
 * Date: 18-nov-2010
 * Time: 15:56:07
 */
public enum DataSeriesType {
    IDENTIFIED_SPECTRA("Identified Spectra", Color.green),
    UNIDENTIFIED_SPECTRA("Unidentified Spectra", Color.red),
    ALL_SPECTRA("All Spectra", Color.blue),
    PROTEIN("Protein", Color.red),
    PEPTIDE("Peptide", Color.red);

    private String type;
    private Color color;

    DataSeriesType(String type, Color color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public Color getColor(){
        return color;
    }

    public static DataSeriesType getSeriesType(String label){
        for (DataSeriesType type : values()) {
            if(type.getType().equals(label))
                return type;
        }
        return null;
    }
}
