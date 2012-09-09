package uk.ac.ebi.pride.gui.component.table.model;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import uk.ac.ebi.pride.gui.px.PxSubmissionEntry;
import uk.ac.ebi.pride.util.NumberUtilities;

import javax.swing.tree.TreePath;
import java.util.*;

/**
 * Table model for displaying ProteomeXchange submissions
 *
 * @author Rui Wang
 * @version $Id$
 */
public class PxSubmissionTableModel extends AbstractTreeTableModel {
    public enum TableHeader {

        FILE_NAME_COLUMN("File Name", "File Name"),
        FILE_SIZE_COLUMN("Size (M)", "Download File Size (M)"),
        FILE_TYPE_COLUMN("Type", "Type of the file"),
        PRIDE_ACC_COLUMN("PRIDE Accession", "PRIDE Accession"),
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

    public PxSubmissionTableModel() {
        super(new PxSubmissionEntry(null, null));
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

    public Set<Object> getNoneParentNodes() {
        Set<Object> leaves = new HashSet<Object>();

        for (PxSubmissionEntry parent : ((PxSubmissionEntry) root).getChildren()) {
            for (PxSubmissionEntry child : parent.getChildren()) {
                leaves.add(child);
                leaves.addAll(child.getChildren());
            }
        }

        return leaves;
    }

    /**
     * Add a list of submission details into the table model
     * Each entry in the list represents a single submitted file
     */
    public void addData(List<Map<String, String>> data) {
        Set<PxSubmissionEntry> allChildren = new LinkedHashSet<PxSubmissionEntry>();

        for (Map<String, String> entry : data) {
            String acc = entry.get(PxSubmissionEntry.ACCESSION);
            String doi = entry.get(PxSubmissionEntry.DOI);
            String fileID = entry.get(PxSubmissionEntry.FILE_ID);
            String fileName = entry.get(PxSubmissionEntry.FILE_NAME);
            String fileType = entry.get(PxSubmissionEntry.FILE_TYPE);
            String prideAcc = entry.get(PxSubmissionEntry.PRIDE_ACC);
            String fileMapping = entry.get(PxSubmissionEntry.FILE_MAPPING);
            double fileSize = Double.parseDouble(entry.get(PxSubmissionEntry.FILE_SIZE));
            fileSize = NumberUtilities.scaleDouble(fileSize / (1024 * 1024), 2);

            // add file to the px submission entry
            PxSubmissionEntry child = new PxSubmissionEntry(acc, doi, fileID, fileName, fileType, prideAcc, fileMapping, fileSize);
            allChildren.add(child);
        }

        // iterate to find all the child which has file mappings
        for (PxSubmissionEntry child : allChildren) {
            if (child.hasFileMappings()) {
                // get parent and add parent if not exist
                PxSubmissionEntry parent = addPxSubmissionParentNode(child.getAccession(), child.getDoi());
                parent.addChild(child);
                child.addParent(parent);
                for (String fileID : child.getFileMappings()) {
                    // find child of child
                    for (PxSubmissionEntry subChild : allChildren) {
                        if (subChild.getFileID().equals(fileID)) {
                            child.addChild(subChild);
                            subChild.addParent(child);
                            subChild.setFileMapped(true);
                            break;
                        }
                    }
                }
            }
        }

        // iterate to find all the child which don't have any file mapping
        for (PxSubmissionEntry child : allChildren) {
            if (!child.hasFileMappings() && !child.isFileMapped()) {
                // get parent and add parent if not exist
                PxSubmissionEntry parent = addPxSubmissionParentNode(child.getAccession(), child.getDoi());
                parent.addChild(child);
                child.addParent(parent);
            }
        }


        modelSupport.fireTreeStructureChanged(new TreePath(root));
    }

    private PxSubmissionEntry addPxSubmissionParentNode(String accession, String doi) {
        PxSubmissionEntry parent = null;

        for (PxSubmissionEntry pxSubmissionEntry : ((PxSubmissionEntry) root).getChildren()) {
            if (pxSubmissionEntry.getAccession().equals(accession)) {
                parent = pxSubmissionEntry;
                break;
            }
        }

        if (parent == null) {
            parent = new PxSubmissionEntry(accession, doi);
            ((PxSubmissionEntry) root).addChild(parent);
            parent.addParent((PxSubmissionEntry) root);
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
            case FILE_NAME_COLUMN:
                return submissionEntry.getFileID() == null ? submissionEntry.getAccession() : submissionEntry.getFileName();
            case FILE_TYPE_COLUMN:
                return submissionEntry.getFileType();
            case PRIDE_ACC_COLUMN:
                return submissionEntry.getPrideAccession();
            case FILE_SIZE_COLUMN:
                return submissionEntry.getFileID() == null ? sumChildrenSize(submissionEntry) : submissionEntry.getSize();
            case DOWNLOAD_COLUMN:
                return submissionEntry.isToDownload();
        }
        return TableHeader.values()[column].getHeader();
    }

    private double sumChildrenSize(PxSubmissionEntry submissionEntry) {
        double sum = 0.0;

        for (PxSubmissionEntry child : submissionEntry.getChildren()) {
            sum += child.getSize();
            for (PxSubmissionEntry nestedChild : child.getChildren()) {
                sum += nestedChild.getSize();
            }
        }

        return sum;
    }

    @Override
    public Object getChild(Object parent, int index) {
        Object child = null;

        PxSubmissionEntry parentEntry = (PxSubmissionEntry) parent;
        Set<PxSubmissionEntry> children = parentEntry.getChildren();
        if (index >= 0 && index < children.size()) {
            child = new LinkedList<PxSubmissionEntry>(children).get(index);
        }

        return child;
    }

    @Override
    public int getChildCount(Object parent) {
        return ((PxSubmissionEntry) parent).getNumberOfChild();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int index = -1;

        PxSubmissionEntry parentEntry = (PxSubmissionEntry) parent;
        PxSubmissionEntry childEntry = (PxSubmissionEntry) child;
        Set<PxSubmissionEntry> children = parentEntry.getChildren();
        if (children.contains(childEntry)) {
            index = new LinkedList<PxSubmissionEntry>(children).indexOf(childEntry);
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
            Set<PxSubmissionEntry> parents = ((PxSubmissionEntry) node).getParents();
            for (PxSubmissionEntry parent : parents) {
                modelSupport.fireChildChanged(new TreePath(parent), getIndexOfChild(parent, node), node);
            }
        }
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return !root.equals(node) && TableHeader.values()[column].equals(TableHeader.DOWNLOAD_COLUMN);
    }
}