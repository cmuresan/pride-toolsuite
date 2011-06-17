package uk.ac.ebi.pride.gui.component.sequence;

import uk.ac.ebi.pride.data.Tuple;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.*;

/**
 * This class converts a AnnotatedProtein into a AttributedSequence for drawing
 * <p/>
 * It also takes into account the peptide annotations and ptm annotations
 * <p/>
 * User: rwang
 * Date: 17/06/11
 * Time: 15:10
 */
public class AttributedSequenceBuilder {
    public final static int PROTEIN_SEGMENT_LENGTH = 10;
    public final static String PROTEIN_SEGMENT_GAP = "        ";
    public final static Color PEPTIDE_BACKGROUND_COLOUR = Color.yellow;
    public final static Color PEPTIDE_OVERLAP_COLOUR = Color.cyan;
    public final static Color PEPTIDE_HIGHLIGHT_COLOUR = Color.blue;
    public final static Color PTM_BACKGROUND_COLOUR = Color.green;
    public final static Color PTM_HIGHLIGHT_COLOUR = Color.orange;

    private AnnotatedProtein protein;

    /**
     * This method is responsible for create a formatted and styled protein sequence
     * <p/>
     * 1. insert white space between a segment of the sequence (PROTEIN_SEGMENT_LENGTH)
     * 2. insert tabs between segments of the sequence (PROTEIN_SEGMENT_GAP)
     * 3. highlight the peptide annotations
     * 4. highlight the ptm annotations
     * 5. highlight the selected peptide
     *
     * @param protein protein to be converted to formatted sequence string
     * @return AttributedString    formatted sequence string
     */
    public static AttributedString build(AnnotatedProtein protein) {
        AttributedString formattedSequence = null;
        if (protein != null) {
            String sequence = protein.getSequenceString();
            if (sequence != null && !"".equals(sequence.trim())) {
                // add sequence segment gap
                String gappedSequence = insertSegmentGapToSequence(sequence);

                // create attributed string
                formattedSequence = new AttributedString(gappedSequence);

                // set overall font
                Font font = new Font("Tahoma", Font.PLAIN, 14);
                formattedSequence.addAttribute(TextAttribute.FONT, font);

                // color code peptides


                // color code ptm

            }
        }
        return formattedSequence;
    }

    /**
     * Add peptide annotation to the attributed protein sequence
     *
     * @param protein   annotated protein
     * @param formattedSequence formmated protein sequence
     */
    private static void addPeptideAnnotations(AnnotatedProtein protein, AttributedString formattedSequence) {
        java.util.List<PeptideAnnotation> peptides = protein.getAnnotations();
        if (peptides.size() > 0) {
            int numOfValidPeptides = 0;

            // remove invalid peptide
            Iterator<PeptideAnnotation> peptideIter = peptides.iterator();
            while(peptideIter.hasNext()) {
                PeptideAnnotation peptideAnnotation = peptideIter.next();
                if (!protein.isValidPeptideAnnotation(peptideAnnotation)) {
                    peptides.remove(peptideAnnotation);
                } else {
                    numOfValidPeptides++;
                }
            }

            // set number of Valid peptides
            protein.setNumOfValidPeptides(numOfValidPeptides);

            // keep only unique peptides
            Set<PeptideAnnotation> uniquePeptides = new LinkedHashSet<PeptideAnnotation>();
            uniquePeptides.addAll(peptides);
            protein.setNumOfUniquePeptides(uniquePeptides.size());

            // peptide coverage map
            // contains start and stop position, and whether it is an overlapping sequence
            Map<Tuple<Integer, Integer>, Boolean> coverageMap = new LinkedHashMap<Tuple<Integer, Integer>, Boolean>();
            for (PeptideAnnotation uniquePeptide : uniquePeptides) {

            }
        }
    }

    private static boolean isOverlapping(Tuple<Integer, Integer> c1, Tuple<Integer, Integer> c2) {
        boolean overlapping = false;

        Tuple<Integer, Integer> cs, ce;

        if (c1.getKey() > c2.getKey()) {
        } else {

        }

        return overlapping;
    }

    /**
     * Format protein sequence by inserting segment gaps
     * This is displaying purpose
     *
     * @param sequence protein sequence
     * @return String  formatted protein sequence
     */
    private static String insertSegmentGapToSequence(String sequence) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] cArr = sequence.trim().toCharArray();
        for (int i = 0; i < cArr.length; i++) {
            if (i != 0 && i % PROTEIN_SEGMENT_LENGTH == 0) {
                stringBuilder.append(PROTEIN_SEGMENT_GAP);
            }
            stringBuilder.append(cArr[i]);
        }
        return stringBuilder.toString();
    }

    /**
     * map a index of original protein sequence to a newly formatted protein sequence
     *
     * @param index original index
     * @return int mapped index
     */
    private static int mapIndex(int index) {
        // todo: is this the best implementation?
        // change to zero based
        index = index - 1;
        return ((index - index % PROTEIN_SEGMENT_LENGTH) / PROTEIN_SEGMENT_LENGTH) * PROTEIN_SEGMENT_GAP.length() + index;
    }
}
