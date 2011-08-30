package uk.ac.ebi.pride.data.coreIdent;

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
    private String gelLink = null;
    /**
     * x coordinate
     */
    private double xCoordinate = -1;
    /**
     * y coordinate
     */
    private double yCoordinate = -1;
    /**
     * molecular weight
     */
    private double molecularWeight = -1;
    /**
     * pI value
     */
    private double pI = -1;

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
    public Gel(ParamGroup params,
               String gelLink,
               double xCoordinate,
               double yCoordinate,
               double molecularWeight,
               double pI) {
        super(params);
        setGelLink(gelLink);
        setXCoordinate(xCoordinate);
        setYCoordinate(yCoordinate);
        setMolecularWeight(molecularWeight);
        setPI(pI);
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
}
