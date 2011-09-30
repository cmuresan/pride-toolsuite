package uk.ac.ebi.pride.chart.model.implementation;

import org.apache.commons.codec.binary.Base64;
import uk.ac.ebi.pride.chart.utils.BinaryDataUtils;
import uk.ac.ebi.pride.term.CvTermReference;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

/**
 * <p>Container for the m/z and intensity values of every SpectrumData of a PRIDE experiment.</p>
 *
 * @author Antonio Fabregat
 * Date: 08-jul-2010
 * Time: 10:08:52
 */
public class PeakList {
    /**
     * Contains the SpectrumData ID
     */
    private Integer spectrumID;

    /**
     * Contains the decoded binary array converted into an array of double values
     */
    private double[] decodedArray;

    /**
     * <p> Creates an instance of this PeakList object, setting all fields as per description below.</p>
     *
     * @param spectrumID the associated SpectrumData ID.
     * @param dataEndian the byte order is used when reading or writing multibyte values
     *                   stored as mzData element .../chartData/endian.  Only possible values are defined by the
     *                   static String members of this class 'BIG_ENDIAN_LABEL' (or "big") and 'LITTLE_ENDIAN_LABEL' (or "little").
     * @param aBase64String the binary contents of the array as an array of bytes. (mzData element .../binaryDataGroup.)
     *                      Note that the contents of this field can be obtained from a database by calling the
     *                      java.sql.Blob.getBytes():byte[]  method
     * @param dataPrecision the precision of the binary array (mzData element .../chartData/precision) that indicates
     *                      if the array contains encoded double values or encoded float values.
     *                      Only possible values for this parameter are defined byt he static String members of
     *                      this class 'FLOAT_PRECISION' (or "32") and 'DOUBLE_PRECISION' (or "64").
     */
    public PeakList(Integer spectrumID,
                    String dataEndian,
                    String aBase64String,
                    String dataPrecision) {

        this.spectrumID = spectrumID;
        decodedArray = decodeArray(aBase64String, dataEndian, dataPrecision);
    }

    /**
     * Decodes the chartData in function of the  byte order and the precision of the binary array
     *
     * @param aBase64String the binary contents of the array as an array of bytes. (mzData element .../binaryDataGroup.)
     *                                      Note that the contents of this field can be obtained from a database by calling the
     *                                      java.sql.Blob.getBytes():byte[]  method.
     * @param dataEndian the byte order is used when reading or writing multibyte values
     *                                  stored as mzData element .../chartData/endian.  Only possible values are defined by the
     *                                  static String members of this class 'BIG_ENDIAN_LABEL' (or "big") and 'LITTLE_ENDIAN_LABEL' (or "little").
     * @param dataPrecision the precision of the binary array (mzData element .../chartData/precision) that indicates
     *                                      if the array contains encoded double values or encoded float values.
     *                                      Only possible values for this parameter are defined byt he static String members of
     *                                      this class 'FLOAT_PRECISION' (or "32") and 'DOUBLE_PRECISION' (or "64").
     * @return the decoded binary array converted into an array of double values.
     */
    private double[] decodeArray(String aBase64String, String dataEndian, String dataPrecision) {
        aBase64String = aBase64String.replaceAll("\n", "");
        double[] binaryDoubleArr;

        ByteOrder order = "big".equals(dataEndian) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        CvTermReference dataType = "32".equals(dataPrecision) ? CvTermReference.FLOAT_32_BIT : CvTermReference.FLOAT_64_BIT;

        if (aBase64String != null) {
            try {
                binaryDoubleArr = BinaryDataUtils.toDoubleArray(Base64.decodeBase64(aBase64String.getBytes("ASCII")), dataType, order);
            } catch (UnsupportedEncodingException e) {
                binaryDoubleArr = null;
            }
        } else {
            binaryDoubleArr = BinaryDataUtils.toDoubleArray(null, dataType, order);
        }

        return binaryDoubleArr;
    }

    /**
     * <p>Returns the decoded binary array converted into an array of double values</p>
     *
     * @return the decoded binary array.
     */
    public double[] getDoubleArray() {
        return decodedArray;
    }

    /**
     * <p>Returns the SpectrumData ID</p>
     *
     * @return the SpectrumData ID
     */
    public Integer getSpectrumID() {
        return spectrumID;
    }

    /**
     *
     * Returns a useful String representation of this PeakList instance that
     * includes details of all fields.
     *
     * @return a useful String representation of this PeakList instance.
     */
    @Override
    public String toString() {
        return "PeakList{" +
                "spectrumID=" + spectrumID +
                ", decodedArray length=" + decodedArray.length +
                '}';
    }
}
