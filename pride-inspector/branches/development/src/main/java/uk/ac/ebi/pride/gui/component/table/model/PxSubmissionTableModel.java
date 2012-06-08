package uk.ac.ebi.pride.gui.component.table.model;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import uk.ac.ebi.pride.gui.px.PxSubmissionEntry;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Table model for displaying ProteomeXchange submissions
 *
 * @author Rui Wang
 * @version $Id$
 */
public class PxSubmissionTableModel extends AbstractTreeTableModel {
    public enum TableHeader {

        ACC_COLUMN("Accession", "ProteomeXchange Accession"),
        DOI_COLUMN("DOI", "DOI Number"),
        FILE_ID_COLUMN("File ID", "File ID"),
        FILE_NAME_COLUMN("File Name", "File Name"),
        FILE_TYPE_COLUMN("Type", "Type of the file"),
        PRIDE_ACC_COLUMN("PRIDE Accession", "PRIDE Accession"),
        FILE_MAPPING_COLUMN("Mapping", "Mappings between files"),
        FILE_SIZE_COLUMN("Size (M)", "Download File Size (M)"),
        DOWNLOAD_COLUMN("Download", "Download Option");

        private final String header;
        private final String toolTip;

        private TableHeader(String header, String tooltip) {
            this.header = header;
            this.toolTip = tooltip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    private Map<PxSubmissionEntry, List<PxSubmissionEntry>> submissionEntries;

    public PxSubmissionTableModel() {
        super(new PxSubmissionEntry(null, null));
        this.submissionEntries = new LinkedHashMap<PxSubmissionEntry, List<PxSubmissionEntry>>();
    }

    @Override
    public int getColumnCount() {
        return TableHeader.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return TableHeader.values()[column].getHeader();
    }

    public int getColumnIndex(String header) {
        TableHeader[] headers = TableHeader.values();
        for (int i = 0; i < headers.length; i++) {
            TableHeader tableHeader = headers[i];
            if (tableHeader.getHeader().equals(header)) {
                return i;
            }
        }

        return -1;
    }

    public List<Object> getLeaves() {
        List<Object> leaves = new ArrayList<Object>();
        for (List<PxSubmissionEntry> pxSubmissionEntries : submissionEntries.values()) {
            leaves.addAll(pxSubmissionEntries);
        }
        return leaves;
    }

    /**
     * Add a list of submission details into the table model
     * Each entry in the list represents a single submitted file
     */
    public void addData(List<Map<String, String>> data) {
        Map<String, PxSubmissionEntry> parents = new LinkedHashMap<String, PxSubmissionEntry>();
        for (Map<String, String> entry : data) {
            String acc = entry.get(PxSubmissionEntry.ACCESSION);
            String doi = entry.get(PxSubmissionEntry.DOI);
            String fileID = entry.get(PxSubmissionEntry.FILE_ID);
            String fileName = entry.get(PxSubmissionEntry.FILE_NAME);
            String fileType = entry.get(PxSubmissionEntry.FILE_TYPE);
            String prideAcc = entry.get(PxSubmissionEntry.PRIDE_ACC);
            String fileMapping = entry.get(PxSubmissionEntry.FILE_MAPPING);
            double fileSize = Double.parseDouble(entry.get(PxSubmissionEntry.FILE_SIZE));

            PxSubmissionEntry parent = addPxSubmissionParentNode(acc, doi);
            PxSubmissionEntry child = new PxSubmissionEntry(acc, doi, fileID, fileName, fileType, prideAcc, fileMapping, fileSize);

            List<PxSubmissionEntry> entries = submissionEntries.get(parent);
            entries.add(child);

            // sum up the file sizes
            parent.setSize(parent.getSize() + child.getSize());

            submissionEntries.put(parent, entries);
        }

        modelSupport.fireTreeStructureChanged(new TreePath(root));
    }

    private PxSubmissionEntry addPxSubmissionParentNode(String accession, String doi) {
        PxSubmissionEntry parent = null;

        for (PxSubmissionEntry pxSubmissionEntry : submissionEntries.keySet()) {
            if (pxSubmissionEntry.getAccession().equals(accession)) {
                parent = pxSubmissionEntry;
            }
        }

        if (parent == null) {
            parent = new PxSubmissionEntry(accession, doi);
            submissionEntries.put(parent, new ArrayList<PxSubmissionEntry>());
        }

        return parent;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (root.equals(node)) {
            return null;
        }

        TableHeader columnHeader = TableHeader.values()[column];
        PxSubmissionEntry submissionEntry = (PxSubmissionEntry) node;
        switch (columnHeader) {
            case ACC_COLUMN:
                return submissionEntry.getAccession();
            case DOI_COLUMN:
                return submissionEntry.getDoi();
            case FILE_ID_COLUMN:
                return submissionEntry.getFileID();
            case FILE_NAME_COLUMN:
                return submissionEntry.getFileName();
            case FILE_TYPE_COLUMN:
                return submissionEntry.getFileType();
            case PRIDE_ACC_COLUMN:
                return submissionEntry.getPrideAccession();
            case FILE_MAPPING_COLUMN:
                return submissionEntry.getFileMapping();
            case FILE_SIZE_COLUMN:
                return submissionEntry.getSize();
            case DOWNLOAD_COLUMN:
                return submissionEntry.isToDownload();
        }
        return TableHeader.values()[column].getHeader();
    }

    @Override
    public Object getChild(Object parent, int index) {
        Object child = null;

        if (index >= 0) {
            if (root.equals(parent)) {
                List<PxSubmissionEntry> parentEntries = new ArrayList<PxSubmissionEntry>(submissionEntries.keySet());
                if (index < parentEntries.size()) {
                    child = parentEntries.get(index);
                }
            } else {
                List<PxSubmissionEntry> entries = submissionEntries.get(parent);
                if (entries != null && index < entries.size()) {
                    child = entries.get(index);
                }
            }
        }

        return child;
    }

    @Override
    public int getChildCount(Object parent) {
        int cnt = 0;

        if (root.equals(parent)) {
            cnt = submissionEntries.keySet().size();
        } else {
            List<PxSubmissionEntry> entries = submissionEntries.get(parent);
            if (entries != null) {
                cnt = entries.size();
            }
        }

        return cnt;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int index = -1;

        if (root.equals(parent)) {
            List<PxSubmissionEntry> parentEntries = new ArrayList<PxSubmissionEntry>(submissionEntries.keySet());
            index = parentEntries.indexOf(child);
        } else {
            List<PxSubmissionEntry> entries = submissionEntries.get(parent);
            if (entries != null) {
                index = entries.indexOf(child);
            }
        }

        return index;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (TableHeader.values()[column].equals(TableHeader.DOWNLOAD_COLUMN)) {
            return Boolean.class;
        }
        return super.getColumnClass(column);
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {
        if (!root.equals(node) && TableHeader.values()[column].equals(TableHeader.DOWNLOAD_COLUMN) && value instanceof Boolean) {
            ((PxSubmissionEntry) node).setToDownload((Boolean) value);
            Object parent = getParent(node);
            modelSupport.fireChildChanged(new TreePath(parent), getIndexOfChild(parent, node), node);
        }
    }

    public Object getParent(Object child) {
        if (submissionEntries.keySet().contains(child)) {
            return root;
        } else {
            for (PxSubmissionEntry pxSubmissionEntry : submissionEntries.keySet()) {
                if (submissionEntries.get(pxSubmissionEntry).contains(child)) {
                    return pxSubmissionEntry;
                }
            }
            return null;
        }
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return !root.equals(node) && TableHeader.values()[column].equals(TableHeader.DOWNLOAD_COLUMN);
    }
}