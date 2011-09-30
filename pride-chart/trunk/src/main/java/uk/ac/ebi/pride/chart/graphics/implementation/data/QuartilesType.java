package uk.ac.ebi.pride.chart.graphics.implementation.data;

import java.awt.*;

/**
 * <p></p>
 * 
 * User: Antonio Fabregat
 * Date: 9-mar-2011
 * Time: 16:30:07
 */
public enum QuartilesType {
    NONE("No quartiles reference", null, Color.white, Color.white),
    PRIDE("PRIDE reference", "pridePMDQuartiles.csv", new Color(0,0,255), new Color(0,0,255, 100)),       //blue
    HUMAN("Human reference", "humanPMDQuartiles.csv", new Color(70,130,180), new Color(70,130,180, 150)), //steel blue
    MOUSE("Mouse reference", "mousePMDQuartiles.csv", new Color(95,158,160),new Color(95,158,160, 150));  //cadet blue

    private String type;
    private String fileName;
    private Color colorMiddle;
    private Color colorBounds;


    QuartilesType(String type, String fileName, Color colorMiddle, Color colorBounds) {
        this.type= type;
        this.fileName = fileName;
        this.colorMiddle = colorMiddle;
        this.colorBounds = colorBounds;
    }

    public String getType() {
        return type;
    }

    public String getFileName(){
        return fileName;
    }

    public Color getMiddleColor(){
        return colorMiddle;
    }

    public Color getBoundsColor(){
        return colorBounds;
    }

    public static QuartilesType getQuartilesType(String label){
        for (QuartilesType type : values()) {
            if(type.getType().equals(label))
                return type;
        }
        return null;
    }
}
