package uk.ac.ebi.pride.mzgraph.gui.data;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.mol.NeutralLoss;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotationInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTableModel extends TheoreticalFragmentedIonsTableModel {
    private PrecursorIon precursorIon;

    // used for storage automatically annotations
    private Double[][] autoData = new Double[getRowCount()][getColumnCount()];

    // used for storage manually annotations
    private Double[][] manualData = new Double[getRowCount()][getColumnCount()];

    private boolean showAuto = false;
    private boolean showManual = true;

    private List<IonAnnotation> manualAnnotations = new ArrayList<IonAnnotation>();

    private List<IonAnnotation> autoAnnotations = new ArrayList<IonAnnotation>();

    private double[] mzArray;
    private double[] intensityArray;

    private double range = 0.1;

    private List<ExperimentalTableModelObserver> observerList = new ArrayList<ExperimentalTableModelObserver>();

    public boolean isShowManual() {
        return showManual;
    }

    public void setShowManual(boolean showManual) {
        this.showManual = showManual;
        notifyObservers();
    }

    public boolean isShowAuto() {
        return showAuto;
    }

    public void setShowAuto(boolean showAuto) {
        this.showAuto = showAuto;
        notifyObservers();
    }

    /**
     * mzArray is a ascendant order. In the function, we do two tasks:
     *
     * <P>
     * we check product ion mass over charge in the theoretical table model,
     * if abs(mz - theoretical) <= range, then we put the mz into the matched
     * data matrix.
     * </P>
     * <P>
     * create a couple of ion annotation, which can be used in to spectrum browser.
     * </P>
     *
     */
    public void setPeaks(double[] mzArray, double[] intensityArray) {
        if (mzArray == null || intensityArray == null) {
            return;
        }

        if (mzArray.length != intensityArray.length) {
            throw new IllegalArgumentException("mass array length not equal with intensity array");
        }

        // clear auto data matrix
        for (int i = 0; i < autoData.length; i++) {
            for (int j = 0; j < autoData[i].length; j++) {
                autoData[i][j] = null;
            }
        }
        this.autoAnnotations.clear();

        this.mzArray = mzArray;
        this.intensityArray = intensityArray;

        double mz;
        double intensity;
        double theoretical;
        Object cell;
        ProductIon ion;
        IonAnnotation annotation;
        IonAnnotationInfo annotationInfo;
        int charge;
        FragmentIonType type;
        int location;
        NeutralLoss loss;

        Arrays.sort(mzArray);
        for (int i = 0; i < mzArray.length; i++) {
            mz = mzArray[i];
            intensity = intensityArray[i];

            annotationInfo = new IonAnnotationInfo();
            for (int row = 0; row < getRowCount(); row++) {
                for (int col = 0; col < getColumnCount(); col++) {
                    cell = getValueAt(row, col);
                    if (cell instanceof ProductIon) {
                        ion = (ProductIon) cell;
                        theoretical = ion.getMassOverCharge();
                        if (Math.abs(mz - theoretical) <= range) {
                            autoData[row][col] = mz;
                            charge = ion.getCharge();
                            location = ion.getPosition() + 1;
                            type = ion.getType().getGroup();
                            loss = ion.getType().getLoss();

                            annotationInfo.addItem(charge, type, location, loss);
                        }
                    }
                }
            }

            if (annotationInfo.getNumberOfItems() != 0) {
                annotation = new IonAnnotation(mz, intensity, annotationInfo);
                this.autoAnnotations.add(annotation);
            }
        }

        notifyObservers();
    }

    /**
     * Calculate the column offset, based on charge and neutral loss type. maxCharge is the count of max charges
     * of product ion.
     */
    private int getOffset(int charge, NeutralLoss loss, int maxCharge) {
        if (charge > maxCharge) {
            return -1;
        }

        if (loss == null) {
            return charge;
        } else if (loss == NeutralLoss.AMMONIA_LOSS) {
            return maxCharge + charge;
        } else {
            return 2 * maxCharge + charge;
        }
    }

    private int getCol(FragmentIonType type, int charge, NeutralLoss loss, int maxCharge) {
        int offset = getOffset(charge, loss, maxCharge);

        if (type.equals(FragmentIonType.X_ION) || type.equals(FragmentIonType.Y_ION) || type.equals(FragmentIonType.Z_ION)) {
            return offset;
        } else if (type.equals(FragmentIonType.A_ION) || type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.C_ION)) {
            return getColumnCount() / 2 + offset;
        } else {
            return -1;
        }
    }

    private boolean addAnnotation(IonAnnotation annotation) {
        int charge;
        FragmentIonType type;
        int location;
        NeutralLoss loss;
        double mz;
        int row;
        int col;

        IonAnnotationInfo annotationInfo;
        IonAnnotationInfo.Item item;
        int maxCharge = precursorIon.getCharge();

        mz = annotation.getMz().doubleValue();
        annotationInfo = annotation.getAnnotationInfo();

        for (int i = 0; i < annotationInfo.getNumberOfItems(); i++) {
            item = annotationInfo.getItem(i);
            charge = item.getCharge();
            type = item.getType();
            loss = item.getNeutralLoss();
            location = item.getLocation();

            if (type.equals(FragmentIonType.X_ION) || type.equals(FragmentIonType.Y_ION) || type.equals(FragmentIonType.Z_ION)) {
                row = getRowCount() - location;
            } else if (type.equals(FragmentIonType.A_ION) || type.equals(FragmentIonType.B_ION) || type.equals(FragmentIonType.C_ION)) {
                row = location - 1;
            } else {
                row = -1;
            }

            col = getCol(type, charge, loss, maxCharge);

            if (row == -1 || col == -1) {
                return false;
            }

            manualData[row][col] = mz;
        }

        return true;
    }

    public boolean addManualAnnotation(IonAnnotation annotation) {
        if (! addAnnotation(annotation)) {
            return false;
        }

        manualAnnotations.add(annotation);

        notifyObservers();
        return true;
    }

    /**
     * This is a read only ion annotation list. Can not do add, remove operations, just used for browse.
     */
    public List<IonAnnotation> getManualAnnotations() {
        return Collections.unmodifiableList(manualAnnotations);
    }

    public List<IonAnnotation> getAutoAnnotations() {
        return Collections.unmodifiableList(autoAnnotations);
    }

    public List<IonAnnotation> getAnnotations() {
        if (showAuto && ! showManual) {
            return getAutoAnnotations();
        }

        if (!showAuto && showManual) {
            return getManualAnnotations();
        }

        List<IonAnnotation> annotationList = new ArrayList<IonAnnotation>();
        if (!showAuto && !showManual) {
            return annotationList;
        }

        annotationList.addAll(autoAnnotations);
        annotationList.addAll(manualAnnotations);
        return Collections.unmodifiableList(annotationList);
    }

    /**
     * Generate matched data matrix based on manual annotation list.
     */
    public boolean addAllManualAnnotation(List<IonAnnotation> manualAnnotationList) {
        if (manualAnnotationList == null || manualAnnotationList.size() == 0) {
            return false;
        }

        // copy manual annotation list.
        List<IonAnnotation> tempAnnotationList = new ArrayList<IonAnnotation>();
        for (IonAnnotation annotation : this.manualAnnotations) {
            tempAnnotationList.add((IonAnnotation) annotation.clone());
        }

        // copy manual data matrix.
        Double[][] tempManualData = new Double[getRowCount()][getColumnCount()];
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

    public ExperimentalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair) {
        super(precursorIon, ionPair);
        this.precursorIon = precursorIon;
    }

    public ExperimentalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                double[] mzArray, double [] intensityArray) {
        this(precursorIon, ionPair);
        setPeaks(mzArray, intensityArray);
    }

    public ExperimentalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair,
                                                List<IonAnnotation> manualAnnotations) {
        this(precursorIon, ionPair);
        addAllManualAnnotation(manualAnnotations);
    }

    /**
     * based on {@link #showAuto}, {@link #showManual} parameter, combine auto data matrix and manual
     * data matrix.
     */
    public Double[][] getMatchedData() {
        if (showAuto && !showManual) {
            return autoData;
        }

        if (showManual && !showAuto) {
            return manualData;
        }

        Double[][] matchedData = new Double[getRowCount()][getColumnCount()];

        if (!showAuto && !showManual) {
            return matchedData;
        }

        // need combine autoData and manualData matrix
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if (manualData[i][j] != null) {
                    matchedData[i][j] = manualData[i][j];
                } else if (autoData[i][j] != null) {
                    matchedData[i][j] = autoData[i][j];
                }
            }
        }

        return matchedData;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;

         //if range changed, we need to re-calculate the auto data matrix.
        if (this.mzArray != null && this.intensityArray != null) {
            setPeaks(mzArray, intensityArray);
        }
    }

    public void addObserver(ExperimentalTableModelObserver observer) {
        this.observerList.add(observer);
    }

    public void notifyObservers() {
        for (ExperimentalTableModelObserver observer : this.observerList) {
            observer.update(this);
        }
    }
}
