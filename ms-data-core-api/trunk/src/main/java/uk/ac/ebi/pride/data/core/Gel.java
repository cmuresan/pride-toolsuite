package uk.ac.ebi.pride.data.core;

/**
 * Descriptions of the gel.
 * <p/>
 * User: rwang
 * Date: 25-Mar-2010
 * Time: 12:36:55
 */
public class Gel extends ParamGroup {

    /**
     * link to the image of a gel
     */
    private String gelLink;

    /**
     * molecular weight
     */
    private double molecularWeight;

    /**
     * pI value
     */
    private double pI;

    /**
     * x coordinate
     */
    private double xCoordinate;

    /**
     * y coordinate
     */
    private double yCoordinate;

    /**
     * Constructor
     *
     * @param params          optional.
     * @param gelLink         optional since gel is optional.
     * @param xCoordinate     optional.
     * @param yCoordinate     optional.
     * @param molecularWeight optional.
     * @param pI              optional.
     */
    public Gel(ParamGroup params, String gelLink, double xCoordinate, double yCoordinate, double molecularWeight,
               double pI) {
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

    public double getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public double getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public double getMolecularWeight() {
        return molecularWeight;
    }

    public void setMolecularWeight(double molecularWeight) {
        this.molecularWeight = molecularWeight;
    }

    public double getPI() {
        return pI;
    }

    public void setPI(double pI) {
        this.pI = pI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Gel gel = (Gel) o;

        return Double.compare(gel.molecularWeight, molecularWeight) == 0 && Double.compare(gel.pI, pI) == 0 && Double.compare(gel.xCoordinate, xCoordinate) == 0 && Double.compare(gel.yCoordinate, yCoordinate) == 0 && !(gelLink != null ? !gelLink.equals(gel.gelLink) : gel.gelLink != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (gelLink != null ? gelLink.hashCode() : 0);
        temp = molecularWeight != +0.0d ? Double.doubleToLongBits(molecularWeight) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = pI != +0.0d ? Double.doubleToLongBits(pI) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = xCoordinate != +0.0d ? Double.doubleToLongBits(xCoordinate) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = yCoordinate != +0.0d ? Double.doubleToLongBits(yCoordinate) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}



