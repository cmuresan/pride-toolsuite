package uk.ac.ebi.pride.mzgraph.gui.data;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.iongen.utils.ProductIonFactory;
import uk.ac.ebi.pride.mol.AminoAcid;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mol.ProductIonType;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 10/10/12
 */

public class TheoreticalFragmentedIonsTableModel extends DefaultTableModel {
    private List<List<ProductIon>> predMatrix = null;
    private List<List<ProductIon>> postMatrix = null;
    private ProductIonPair ionPair;

    private List<List<ProductIon>> createProductIonListByCharge(PrecursorIon precursorIon, ProductIonType type) {
        List<List<ProductIon>> matrix = new ArrayList<List<ProductIon>>();

        int charge = precursorIon.getCharge();
        if (charge <= 0) {
            throw new IllegalArgumentException("precursor charge can not less than 1");
        }

        int prodCharge;
        if (charge <= 3) {
            prodCharge = charge;
        } else {
            prodCharge = 3;
        }

        for (int i = 1; i <= prodCharge; i++) {
            matrix.add(ProductIonFactory.createDefaultProductIons(precursorIon, type, i));
        }

        return matrix;
    }

    private List<List<ProductIon>> createProductIonMatrix(PrecursorIon precursorIon, FragmentIonType ionGroup) {
        List<List<ProductIon>> matrix = new ArrayList<List<ProductIon>>();

        if (ionGroup.equals(FragmentIonType.B_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.B));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.B_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.B_H2O));
        } else if (ionGroup.equals(FragmentIonType.Y_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Y));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Y_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Y_H2O));
        } else if (ionGroup.equals(FragmentIonType.A_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.A));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.A_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.A_H2O));
        }else if (ionGroup.equals(FragmentIonType.X_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.X));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.X_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.X_H2O));
        }else if (ionGroup.equals(FragmentIonType.C_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.C));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.C_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.C_H2O));
        }else if (ionGroup.equals(FragmentIonType.Z_ION)) {
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Z));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Z_NH3));
            matrix.addAll(createProductIonListByCharge(precursorIon, ProductIonType.Z_H2O));
        }

        return matrix;
    }

    private Object[] createColumnNames(List<List<ProductIon>> predMatrix, List<List<ProductIon>> postMatrix) {
        List<String> columns = new ArrayList<String>();

        columns.add("#");
        for (List<ProductIon> ionList : predMatrix) {
            columns.add(ionList.get(0).getName());
        }
        columns.add("seq");
        for (List<ProductIon> ionList : postMatrix) {
            columns.add(ionList.get(0).getName());
        }
        columns.add("#");

        return columns.toArray();
    }

    private Object[][] createData(PrecursorIon precursorIon,
                                  List<List<ProductIon>> predMatrix,
                                  List<List<ProductIon>> postMatrix) {
        List<AminoAcid> acidList = precursorIon.getPeptide().getAminoAcids();
        Object[][] data = new Object[acidList.size()][predMatrix.size() + postMatrix.size() + 3];


        for (int i = 1; i < acidList.size(); i++) {
            data[i - 1][0] = acidList.size() - i;
        }

        for (int i = 0; i < acidList.size(); i++) {
            data[i][predMatrix.size() + 1] = acidList.get(i);
        }

        for (int i = 1; i < acidList.size(); i++) {
            data[i][predMatrix.size() + postMatrix.size() + 2] = i;
        }

        List<ProductIon> column;
        for (int j = 0; j < predMatrix.size(); j++) {
            column = predMatrix.get(j);
            int offset = 1;
            for (int i = 0; i < column.size(); i++) {
                data[i][offset + j] = column.get(i);
            }

            column = postMatrix.get(j);
            offset += predMatrix.size() + 1;
            for (int i = 0; i < column.size(); i++) {
                data[i + 1][offset + j] = column.get(i);
            }
        }

        return data;
    }

   public TheoreticalFragmentedIonsTableModel(PrecursorIon precursorIon, ProductIonPair ionPair) {
        Object[] columnNames;
        Object[][] data;

        this.ionPair = ionPair;

        if (precursorIon == null) {
            throw new IllegalArgumentException("Precursor ion can not be null!");
        }

        switch (ionPair) {
            case A_X:
                predMatrix =  createProductIonMatrix(precursorIon, FragmentIonType.X_ION);
                postMatrix = createProductIonMatrix(precursorIon, FragmentIonType.A_ION);
                break;
            case B_Y:
                predMatrix =  createProductIonMatrix(precursorIon, FragmentIonType.Y_ION);
                postMatrix = createProductIonMatrix(precursorIon, FragmentIonType.B_ION);
                break;
            case C_Z:
                predMatrix =  createProductIonMatrix(precursorIon, FragmentIonType.Z_ION);
                postMatrix = createProductIonMatrix(precursorIon, FragmentIonType.C_ION);
                break;
        }

        columnNames = createColumnNames(predMatrix, postMatrix);
        for (int i = 0; i < columnNames.length; i++) {
            addColumn(columnNames[i]);
        }

        data = createData(precursorIon, predMatrix, postMatrix);
        for (int i = 0; i < data.length; i++) {
            addRow(data[i]);
        }
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public boolean isMassColumn(int columnIndex) {
        return isPredMatrixColumn(columnIndex) || isPostMatrixColumn(columnIndex);
    }

    public int getMassColumnCount() {
        return getColumnCount() - 3;
    }

    public boolean isPredMatrixColumn(int columnIndex) {
        return columnIndex > 0 && columnIndex < getColumnCount() / 2;
    }

    public boolean isPostMatrixColumn(int columnIndex) {
        return columnIndex < getColumnCount() - 1 && columnIndex > getColumnCount() / 2;
    }

    public boolean isSeqColumn(int columnIndex) {
        return columnIndex == getColumnCount() / 2;
    }

    public boolean isIDColumn(int columnIndex) {
        return columnIndex == 0 || columnIndex == getColumnCount() - 1;
    }

    public List<List<ProductIon>> getPredMatrix() {
        return predMatrix;
    }

    public List<List<ProductIon>> getPostMatrix() {
        return postMatrix;
    }

    public ProductIonPair getIonPair() {
        return ionPair;
    }
}
