package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 25-Mar-2010
 * Time: 12:36:55
 */
public class Gel extends ParamGroup {
    private String gelLink = null;
    private double xCoordinate = -1;
    private double yCoordinate = -1;
    private double molecularWeight = -1;
    private double pI = -1;

    public Gel(ParamGroup params, String gelLink,
               double xCoordinate, double yCoordinate,
               double molecularWeight, double pI) {
        super(params);
        this.gelLink = gelLink;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.molecularWeight = molecularWeight;
        this.pI = pI;
    }

    public String getGelLink() {
        return gelLink;
    }

    public void setGelLink(String gelLink) {
        this.gelLink = gelLink;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public double getMolecularWeight() {
        return molecularWeight;
    }

    public void setMolecularWeight(double molecularWeight) {
        this.molecularWeight = molecularWeight;
    }

    public double getpI() {
        return pI;
    }

    public void setpI(double pI) {
        this.pI = pI;
    }
}
