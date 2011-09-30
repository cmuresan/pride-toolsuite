package uk.ac.ebi.pride.chart.model.implementation;

import uk.ac.ebi.pride.chart.controller.DBAccessController;

import java.util.*;

/**
 * <p> PeakList manager for querying the database when needed instead of storing all the chartData in memory.</p>
 *
 * Implements an sliding window wrapped in an iterable interface in order to abstract the use
 *
 * @author Antonio Fabregat
 * Date: 15-sep-2010
 * Time: 13:35:39
 */
public class PeakListManager implements Iterator<PeakList>, Iterable<PeakList> {
    /**
     * Contains an instance of the chartData base access controller
     */
    private DBAccessController dbac;

    /**
     * Contains an association of what is the related spectrum to each binary array id
     * Map<binary_array_id, spectrum_id>
     */
    private Map<Integer, Integer> binaryArrayIdSpectrum;

    /**
     * Contains an ordered binaryIDList of all the binary array ID items to iterate
     */
    private List<Integer> binaryIDList;

    /**
     * Contains an ordered spectrumIDList of all the spectrum ID (same order than respective binaryIDList
     */
    private List<Integer> spectrumIDList;

    /**
     * Contains the byte order is used when reading or writing multibyte values
     * stored as mzData element .../chartData/endian.  Only possible values are defined by the
     * static String members of this class 'BIG_ENDIAN_LABEL' (or "big") and 'LITTLE_ENDIAN_LABEL' (or "little").
     */
    private String dataEndian;

    /**
     * Contains the precision of the binary array (mzData element .../chartData/precision) that indicates
     * if the array contains encoded double values or encoded float values.
     * Only possible values for this parameter are defined byt he static String members of
     * this class 'FLOAT_PRECISION' (or "32") and 'DOUBLE_PRECISION' (or "64").
     */
    private String dataPrecision;

    /**
     * Contains the size of the binaryIDList of binary_array_items  (size is used instead of binaryIDList.size()
     * for avoiding the method execution each time 'hasNext' is called)
     */
    private int size;

    /**
     * Contains the index of the last element of the binary array ID binaryIDList served
     */
    private int lastServed;

    /**
     * Contains the position of the binary array ID binaryIDList where the sliding window starts
     */
    private int windowStart;

    /**
     * Contains the position of the binary array ID binaryIDList where the sliding window ends
     */
    private int windowEnd;

    /**
     * Defines the width of the sliding window
     */
    private static final int WINDOW_WIDTH = 10000;

    /**
     * Contains the result of the query for the sliding window
     */
    private Map<Integer, String> windowContent;

    /**
     * <p> Creates an instance of this PeakListManager object using an existing database connection object</p>
     *
     * @param dbac          an instance of the chartData base access controller
     * @param listBasicInfo the basic information of a PeakList (in order to iterate over it)
     */
    public PeakListManager(DBAccessController dbac, PeakListBasicInfo listBasicInfo) {
        this.dbac = dbac;
        dataEndian = listBasicInfo.getDataEndian();
        dataPrecision = listBasicInfo.getDataPrecision();
        binaryArrayIdSpectrum = listBasicInfo.getList();
    }

    /**
     * Reset the values of all the internal parameters in order to set the initial state of the iterator
     */
    private void initialize() {
        lastServed = -1;
        windowStart = -1;
        windowEnd = -1;
        windowContent = null;

        // Conversion of the HashMap KeySet to an ArrayList in order to have
        // an ordered structure for retrieving chartData avoiding the repeated values
        // and keeping order of the spectrumID (is supposed to be a natural order)
        binaryIDList = new ArrayList<Integer>();
        spectrumIDList = new ArrayList<Integer>();
        for (int key : binaryArrayIdSpectrum.keySet()){
            spectrumIDList.add(key);
            binaryIDList.add(binaryArrayIdSpectrum.get(key));
        }

        // size is used instead of binaryIDList.size() for avoiding
        // the method execution each time 'hasNext' is called
        this.size = binaryIDList.size();
    }

    /**
     * Returns true if there is a new element available in the binaryIDList
     *
     * @return true if there is a new element available in the binaryIDList
     */
    public boolean hasNext() {
        boolean next = (lastServed + 1) < size;

        // Improve the memory usage deleting the binaryIDList and the window content
        // in the last iteration of every iterator usage
        if (!next) {
            binaryIDList = null;
            windowContent = null;
        }

        return next;
    }

    /**
     * Move the sliding window to the next position taking into account the setting of the class
     */
    private void moveWindow() {
        windowStart = (windowStart == -1) ? 0 : windowStart + WINDOW_WIDTH;
        int aux = windowStart + WINDOW_WIDTH;
        windowEnd = (aux > size) ? size : aux;

        windowContent = dbac.getBinaryData(binaryIDList.subList(windowStart, windowEnd));
    }

    /**
     * Returns the next PeakList available in the binaryIDList
     *
     * @return the next PeakList available in the binaryIDList
     */
    @Override
    public PeakList next() {
        // Every time all the values of window have been readed, the sliding window is moved
        // For the first element, move the window means load it with the first elements
        if (windowStart == -1 || lastServed == windowEnd - 1) {
            moveWindow();
        }

        int binaryArrayId = binaryIDList.get(++lastServed);
        int spectrumID = spectrumIDList.get(lastServed);
        String data = windowContent.get(binaryArrayId);

        return new PeakList(spectrumID, dataEndian, data, dataPrecision);
    }

    /**
     * Remove the actual PeakList reference from the binaryIDList of binary array IDs
     */
    public void remove() {
        binaryArrayIdSpectrum.remove(binaryIDList.get(lastServed));
    }

    /**
     * Returns an iterator over the PeakList who manage the sliding window
     *
     * @return an iterator over the PeakList who manage the sliding window
     */
    @Override
    public Iterator<PeakList> iterator() {
        //Every time the method is called means that the object state needs to be initialized
        initialize();
        return this;
    }
}
