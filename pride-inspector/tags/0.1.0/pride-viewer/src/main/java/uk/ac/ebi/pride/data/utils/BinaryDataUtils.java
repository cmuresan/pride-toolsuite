package uk.ac.ebi.pride.data.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Endianess is often simply referred to as byte order
 * big endian is the most significant byte first. for example: 149 -> 10010101
 * little endian is the least significatn byte first.
 * User: rwang
 * Date: 29-Mar-2010
 * Time: 11:58:24
 */
public class BinaryDataUtils {

    public enum BinaryDataType {
        /** Signed 32-bit little-endian integer. */
        INT32BIT ("MS:1000519", "32-bit integer", 4),
        /** OBSOLETE Signed 16-bit float. */
        FLOAT16BIT ("MS:1000520", "16-bit float", 2),
        /** 32-bit precision little-endian floating point conforming to IEEE-754. */
        FLOAT32BIT ("MS:1000521", "32-bit float", 4),
        /** Signed 64-bit little-endian integer. */
        INT64BIT ("MS:1000522", "64-bit integer", 8),
        /** 64-bit precision little-endian floating point conforming to IEEE-754. */
        FLOAT64BIT ("MS:1000523", "64-bit float", 8);

        private final String accession;
        private final String name;
        private final int numOfByte;

        private BinaryDataType(String acc, String name, int nob) {
            this.accession = acc;
            this.name = name;
            this.numOfByte = nob;
        }

        public String getAccession() {
            return accession;
        }

        public String getName() {
            return name;
        }

        public int getNumOfByte() {
            return numOfByte;
        }
    }

    public static Number[] toNumberArray(byte[] arr, BinaryDataType dataType, ByteOrder order) {
        int numOfByte = dataType.getNumOfByte();
        int arrLength = arr.length;
        Number[] results = new Number[arrLength/numOfByte];
        ByteBuffer buffer = ByteBuffer.wrap(arr);
        buffer.order(order);
        for(int i = 0; i < arrLength; i+=numOfByte) {
            Number num = null;
            switch(dataType) {
                case INT32BIT   : num = buffer.getInt(i); break;
                case FLOAT16BIT : //ToDo: *** provide implementation here ; break;
                case FLOAT32BIT : num = buffer.getFloat(i); break;
                case INT64BIT   : num = buffer.getLong(i); break;
                case FLOAT64BIT : num = buffer.getDouble(i); break;
                default         : num = null;
            }
            results[i/numOfByte] = num;
        }
        
        return results;
    }

    public static double[] toDoubleArray(byte[] arr, BinaryDataType dataType, ByteOrder order) {
        Number[] numArr = toNumberArray(arr, dataType, order);
        double[] doubleArr = new double[numArr.length];

        for(int i = 0; i < numArr.length; i++) {
            doubleArr[i] = numArr[i].doubleValue();
        }
        return doubleArr;
    }
}
