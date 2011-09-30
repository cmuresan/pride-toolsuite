package uk.ac.ebi.pride.chart.model.implementation;

import uk.ac.ebi.pride.chart.controller.MSFileReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Container for a MS chartData array of a PRIDE experiment or loaded from a file.</p>
 *
 * @author Antonio Fabregat
 * Date: 13-jul-2010
 * Time: 10:37:08
 */
public class MSDataArray {
    /**
     * Contains the mass chartData
     */
    private double[] massData;

    /**
     * <p> Creates an instance of this MSDataArray object, setting all fields as per description below.</p>
     *
     * @param massData the chartData to store in the object previously converted in an array of double
     */
    public MSDataArray(double[] massData){
        this.massData = massData;
    }

    public MSDataArray(List<Double> massData){
        this.massData = new double[massData.size()];
        for(int i=0; i<this.massData.length; i++){
            this.massData[i] = massData.get(i);
        }
    }

    /**
     * <p> Creates an instance of this MSDataArray object, setting all fields as per description below.</p>
     *
     * @param filePath the file which contains the chartData in Fasta format
     * @throws MSDataArrayException
     */
    public MSDataArray(String filePath) throws MSDataArrayException {
        MSFileReader mfr = new MSFileReader(filePath);
        initialize(mfr);
    }

    /**
     * <p> Creates an instance of this MSDataArray object, setting all fields as per description below.</p>
     * 
     * @param is an input stream of the file which contains the chartData in Fasta format
     * @throws MSDataArrayException
     */
    public MSDataArray(InputStream is) throws MSDataArrayException {
        MSFileReader mfr = new MSFileReader(is);
        initialize(mfr);
    }

    /**
     * <p> Creates an instance of this MSDataArray object, setting all fields as per description below.</p>
     * 
     * @param mfr an instance of MSFileReader from which the chartData can be extracted
     * @throws MSDataArrayException
     */
    public MSDataArray(MSFileReader mfr) throws MSDataArrayException {
        initialize(mfr);
    }
    
    private void initialize(MSFileReader mfr) throws MSDataArrayException {
        massData = mfr.getMassData();
        if(massData.length==0) throw new MSDataArrayException("Mass array does not contains values");
    }

    /**
     * Returns the mass chartData stored in the object
     *
     * @return the mass chartData array
     */
    public double[] getData(){
        return massData;
    }

    /**
     * Return the mass value of the index position
     *
     * @param index the position of the array to be returned
     * @return a mass value of the index position
     */
    public double getAt(int index){
        return massData[index];
    }

    /**
     * Obtains and return the element with the maximum value of the array
     *
     * @return the element with the maximum value of the array
     */
    public double getMaxValue() {
        double max = massData[0];
        int i=1;
        while(i<massData.length){
            if(massData[i]>max) max=massData[i];
            i++;
        }
        return max;
    }

    /**
     * Obtains and return the element with the minimum value of the array
     *
     * @return the element with the minimum value of the array
     */
    public double getMinValue(){
        double min = massData[0];
        int i=1;
        while(i<massData.length){
            if(massData[i]<min){
                min=massData[i];
            }
            i++;
        }
        return min;
    }

    /**
     * Returns the chartData length value
     *
     * @return the chartData length value
     */
    public int size(){
        return massData.length;
    }

    /**
     * Emulates the behavior of the which function in 'R' and returns an array of all the values greater than the value passed in the parameter
     *
     * @param value the threshold value for deciding which elements that compose the returned array
     * @return an array with the values greater than the value passed in the parameter
     */
    public double[] whichMax(double value){
        List<Double> ld = new ArrayList<Double>();
        for (double aMassData : massData) {
            if (aMassData > value) ld.add(aMassData);
        }
        double[] aux = new double[ld.size()];
        for(int i=0; i<aux.length; i++) aux[i]= ld.get(i);
        return aux;
    }

    /**
     * Emulates the behavior of the which function in 'R' and returns an array of all the values less than the value passed in the parameter
     *
     * @param value the threshold value for deciding which elements that compose the returned array
     * @return an array with the values less than the value passed in the parameter
     */
    public double[] whichMin(double value){
        List<Double> ld = new ArrayList<Double>();
        for (double aMassData : massData) {
            if (aMassData < value) ld.add(aMassData);
        }
        double[] aux = new double[ld.size()];
        for(int i=0; i<aux.length; i++) aux[i]= ld.get(i);
        return aux;
    }
}
