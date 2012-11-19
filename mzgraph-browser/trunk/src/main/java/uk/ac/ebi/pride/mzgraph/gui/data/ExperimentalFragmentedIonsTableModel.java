package uk.ac.ebi.pride.mzgraph.gui.data;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mol.NeutralLoss;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotationInfo;
import uk.ac.ebi.pride.mzgraph.psm.PSMMatcher;
import uk.ac.ebi.pride.mzgraph.psm.PSMParams;

import java.util.*;

/**
 * Based on the theoretical fragmented ions table model, we allow user add annotations in experimental data.
 * There are two type of annotations, one is automatic annotations, the other is manual annotations.
 * <ol>
 *     <li>automatic annotations: generate by the matching between peak list and theoretical m/z list. </li>
 *     <li>manual annotations: based on some data source, such as mascot. </li>
 * </ol>
 *
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTableModel extends TheoreticalFragmentedIonsTableModel {
    private PrecursorIon precursorIon;

    // used for storage automatically annotations
    private IonAnnotation[][] autoData = new IonAnnotation[getRowCount()][getColumnCount()];

    // used for storage manually annotations
    private IonAnnotation[][] manualData = new IonAnnotation[getRowCount()][getColumnCount()];

    /**
     * Whether show auto annotations, or show manual annotations. Default, the value is {@value}.
     * User can call {@link #setShowAuto(boolean)} to change this value.
     */
    private boolean showAuto = false;

    /**
     * Whether using PSM algorithm to generate auto annotations. For example, if delta m/z is bigger,
     * not calculate.
     */
    private boolean calcuteAuto = true;

    /**
     * store all manual annotations. including a, b, c, x, y, z ions annotations,
     * excluding immonium ion.
     */
    private List<IonAnnotation> manualAnnotations = new ArrayList<IonAnnotation>();

    /**
     * generate dynamically, based on the precursor ion, product ion pair, peak set and psm algorithm.
     */
    private List<IonAnnotation> autoAnnotations = new ArrayList<IonAnnotation>();

    /**
     * store all peak set, base on m/z ascent order.
     */
    private PeakSet peakSet = new PeakSet();

    /**
     * There are some observers of experimental table model data change.
     * In our system, we use a sorted map to store the observers, which key means the update order.
     * All observers update their data with ascending order.
     */
    private Map<Integer, ExperimentalTableModelObserver> observerList = new TreeMap<Integer, ExperimentalTableModelObserver>();

    public ExperimentalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair) {
        super(precursorIon, ionPair);
        this.precursorIon = precursorIon;
    }

    public ExperimentalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                List<IonAnnotation> manualAnnotations) {
        this(precursorIon, ionPair);
        addAllManualAnnotations(manualAnnotations);
    }

    /**
     * if change product ion pair successful, system should regenerate {@link #autoData} and {@link #autoAnnotations},
     * then call {@link #notifyObservers()} to update their contents.
     * @param ionPair
     * @return
     */
    @Override
    public boolean setProductIonPair(ProductIonPair ionPair) {
        boolean success = super.setProductIonPair(ionPair);

        if (success) {
            this.autoData = match();
            this.autoAnnotations = toList(autoData);

            notifyObservers();
            return true;
        }

        return false;
    }

    public boolean isShowAuto() {
        return showAuto;
    }

    /**
     * Experimental fragmented ions table model can only show auto or manual annotations at one time.
     * @see #getAnnotations()
     * @see #getMatchedData()
     * @param showAuto set true means show auto annotations, set false show manual annotations.
     */
    public void setShowAuto(boolean showAuto) {
        this.showAuto = showAuto;
        notifyObservers();
    }

    private List<IonAnnotation> toList(IonAnnotation[][] matrix) {
        List<IonAnnotation> annotationList = new ArrayList<IonAnnotation>();

        if (matrix == null) {
            return annotationList;
        }

        IonAnnotation annotation;
        for (IonAnnotation[] row : matrix) {
            for (IonAnnotation cell : row) {
                annotation = cell;
                if (annotation != null) {
                    annotationList.add(annotation);
                }
            }
        }

        return annotationList;
    }

    /**
     * if change peak set successful, system should regenerate {@link #autoData} and {@link #autoAnnotations},
     * then call {@link #notifyObservers()} to update their contents.
     */
    public void setPeaks(PeakSet peakSet) {
        this.peakSet = peakSet;

        this.autoData = match();
        this.autoAnnotations = toList(autoData);

        notifyObservers();
    }


    /**
     * @see #setPeaks(PeakSet)
     */
    public void setPeaks(double[] mzArray, double[] intensityArray) {
        PeakSet peaks = PeakSet.getInstance(mzArray, intensityArray);
        setPeaks(peaks);
    }

    /**
     * Calculate the column offset, based on charge and neutral loss type. maxCharge is the count of max charges
     * of product ion.
     */
    private int getOffset(int charge, NeutralLoss loss, int precursorCharge) {
        if (precursorCharge > 3) {
            precursorCharge = 3;
        }

        if (charge > precursorCharge) {
            return -1;
        }

        if (loss == null) {
            return charge;
        } else if (loss == NeutralLoss.AMMONIA_LOSS) {
            return precursorCharge + charge;
        } else {
            return 2 * precursorCharge + charge;
        }
    }

    /**
     * Get the table column number based on the {@link IonAnnotationInfo.Item}
     */
    public int getColumnNumber(FragmentIonType type, int charge, NeutralLoss loss) {
        int precursorCharge = precursorIon.getCharge();

        int offset = getOffset(charge, loss, precursorCharge);

        if (type.equals(FragmentIonType.X_ION) || type.equals(FragmentIonType.Y_ION) || type.equals(FragmentIonType.Z_ION)) {
            return offset;
        } else if (type.equals(FragmentIonType.A_ION) || type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.C_ION)) {
            return getColumnCount() / 2 + offset;
        } else {
            return -1;
        }
    }

    /**
     * Get the table row number based on the position of {@link IonAnnotationInfo.Item}.
     */
    public int getRowNumber(FragmentIonType type, int location) {
        int row = -1;

        if (type.equals(FragmentIonType.X_ION) || type.equals(FragmentIonType.Y_ION) || type.equals(FragmentIonType.Z_ION)) {
            row = getRowCount() - location - 1;
        } else if (type.equals(FragmentIonType.A_ION) || type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.C_ION)) {
            row = location;
        }

        return row;
    }

    /**
     * Add a manual annotation, and update manual data matrix. This method will reused by the
     * {@link #addManualAnnotation(uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation)}
     * {@link #addAllManualAnnotations(java.util.List)}
     * methods.
     */
    private boolean addAnnotation(IonAnnotation annotation) {
        int charge;
        FragmentIonType type;
        int location;
        NeutralLoss loss;
        int row;
        int col;

        IonAnnotationInfo annotationInfo;
        IonAnnotationInfo.Item item;

        annotationInfo = annotation.getAnnotationInfo();

        for (int i = 0; i < annotationInfo.getNumberOfItems(); i++) {
            item = annotationInfo.getItem(i);
            charge = item.getCharge();
            type = item.getType();
            loss = item.getNeutralLoss();
            location = item.getLocation();

            // ignore the annotation which location on n-terminal or c-terminal.
            if (location == getRowCount()) {
                return true;
            }

            row = getRowNumber(type, location);
            col = getColumnNumber(type, charge, loss);

            if (row == -1 || col == -1) {
                return false;
            }

            manualData[row][col] = annotation;
        }

        return true;
    }

    /**
     * Add a manual annotation, and update manual data matrix. This method will reused by the
     * {@link #addManualAnnotation(uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation)}
     * {@link #addAllManualAnnotations(java.util.List)}
     * methods.
     */
    public boolean addManualAnnotation(IonAnnotation annotation) {
        if (! addAnnotation(annotation)) {
            return false;
        }

        manualAnnotations.add(annotation);

        notifyObservers();
        return true;
    }

    /**
     * Based on Product Ions Pairs to decide whether add manual annotations, or not.
     */
    private boolean isFit(IonAnnotation annotation) {
        FragmentIonType type = annotation.getAnnotationInfo().getItem(0).getType();
        switch (getIonPair()) {
            case B_Y:
                if (type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.Y_ION)) {
                    return true;
                } else {
                    return false;
                }
            case A_X:
                if (type.equals(FragmentIonType.A_ION) || type.equals(FragmentIonType.X_ION)) {
                    return true;
                } else {
                    return false;
                }
            case C_Z:
                if (type.equals(FragmentIonType.C_ION) || type.equals(FragmentIonType.Z_ION)) {
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

    /**
     * Based on Product Ions Pairs to show according manual annotations.
     * This is a read only ion annotation list. Can not do add, remove operations, just used for browse.
     */
    public List<IonAnnotation> getManualAnnotations() {
        List<IonAnnotation> newManualAnnotations = new ArrayList<IonAnnotation>();

        for (IonAnnotation annotation : manualAnnotations) {
            if (isFit(annotation)) {
                newManualAnnotations.add(annotation);
            }
        }

        return Collections.unmodifiableList(newManualAnnotations);
    }

    /**
     * Get all manual annotations, not based on the product ions pairs.
     * This is unmodifiable list.
     * @return
     */
    public List<IonAnnotation> getAllManualAnnotations() {
        return Collections.unmodifiableList(manualAnnotations);
    }

    public List<IonAnnotation> getAutoAnnotations() {
        return Collections.unmodifiableList(autoAnnotations);
    }

    /**
     * based on {@link #isShowAuto()} the decide to show auto annotations or manual annotations.
     * @return
     */
    public List<IonAnnotation> getAnnotations() {
        if (showAuto) {
            return getAutoAnnotations();
        } else {
            return getManualAnnotations();
        }
    }

    /**
     * Generate matched data matrix based on manual annotation list.
     * If patch operation failure, system will rollback all add annotations.
     */
    public boolean addAllManualAnnotations(List<IonAnnotation> manualAnnotationList) {
        if (manualAnnotationList == null || manualAnnotationList.size() == 0) {
            return false;
        }

        // clone manual annotation list.
        List<IonAnnotation> tempAnnotationList = new ArrayList<IonAnnotation>();
        for (IonAnnotation annotation : this.manualAnnotations) {
            try {
                tempAnnotationList.add((IonAnnotation) annotation.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        // clone manual data matrix.
        IonAnnotation[][] tempManualData = new IonAnnotation[getRowCount()][getColumnCount()];
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                tempManualData[i][j] = this.manualData[i][j];
            }
        }

        for (IonAnnotation annotation : manualAnnotationList) {
            if (! addAnnotation(annotation)) {
                // rollback
                this.manualAnnotations = tempAnnotationList;
                this.manualData = tempManualData;

                return false;
            }
            this.manualAnnotations.add(annotation);
        }

        notifyObservers();

        return true;
    }

    public PrecursorIon getPrecursorIon() {
        return precursorIon;
    }

    /**
     * call PSM algorithm to generate auto annotations.
     * @return
     */
    private IonAnnotation[][]  match() {
        if (! calcuteAuto) {
            return new IonAnnotation[getRowCount()][getColumnCount()];
        } else {
            return PSMMatcher.getInstance().match(this, this.peakSet);
        }
    }

    /**
     * whether calculate auto annotations.
     * @param calcuteAuto {@link #calcuteAuto}
     */
    public void setCalcuteAuto(boolean calcuteAuto) {
        if (calcuteAuto != this.calcuteAuto) {
            this.autoData = match();
            this.autoAnnotations = toList(autoData);

            notifyObservers();
        }
    }

    /**
     * based on {@link #showAuto} parameter, display auto data matrix or manual data matrix.
     * @see #setShowAuto(boolean)
     */
    public IonAnnotation[][] getMatchedData() {
        if (showAuto) {
            return autoData;
        } else {
            IonAnnotation[][] matchedData = new IonAnnotation[getRowCount()][getColumnCount()];
            for (int i = 0; i < getRowCount(); i++) {
                for (int j = 0; j < getColumnCount(); j++) {
                    // based on product ion pair the show manual data.
                    if (manualData[i][j] != null && isFit(manualData[i][j])) {
                        matchedData[i][j] = manualData[i][j];
                    }
                }
            }

            return matchedData;
        }
    }

    /**
     * If user modify interval range, system will store the new value into {@link PSMParams},
     * then call PSM algorithm to regenerate {@link #autoAnnotations} and {@link #autoData},
     * and call {@link #notifyObservers()} to update their contents.
     * @param range
     */
    public void setRange(double range) {
        PSMParams.getInstance().setRange(range);

        //if range changed, we need to re-calculate the auto data matrix.
        this.autoData = match();
        this.autoAnnotations = toList(autoData);

        notifyObservers();
    }

    /**
     * add a {@link ExperimentalTableModelObserver}
     * @param order : is update order, the minor order observer will update earlier.
     */
    public void addObserver(int order, ExperimentalTableModelObserver observer) {
        this.observerList.put(order, observer);
    }

    private void notifyObservers() {
        if (observerList == null) {
            return;
        }

        for (ExperimentalTableModelObserver observer : observerList.values()) {
            observer.update(this);
        }
    }
}
