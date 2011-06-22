package uk.ac.ebi.pride.gui.component.sequence;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
    public final static String PROTEIN_SEGMENT_GAP = "  ";
    public final static Color PEPTIDE_BACKGROUND_COLOUR = new Color(251, 182, 1, 100);
    public final static Color PEPTIDE_FOREGROUND_COLOUR = Color.BLACK;
    public final static Color PEPTIDE_OVERLAP_COLOUR = new Color(251, 182, 1);
    public final static Color PEPTIDE_HIGHLIGHT_COLOUR = Color.yellow;
    public final static Color PTM_BACKGROUND_COLOUR = new Color(40, 175, 99);
    public final static Color PTM_HIGHLIGHT_COLOUR = Color.yellow.darker();
    public final static Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.BOLD, 16);
    public final static Color DEFAULT_FOREGROUND = Color.GRAY;

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
                formattedSequence.addAttribute(TextAttribute.FONT, DEFAULT_FONT);
                formattedSequence.addAttribute(TextAttribute.FOREGROUND, DEFAULT_FOREGROUND);

                // color code peptides
                addPeptideAnnotations(protein, formattedSequence);
            }
        }
        return formattedSequence;
    }

    /**
     * Add peptide annotation to the attributed protein sequence
     *
     * @param protein           annotated protein
     * @param formattedSequence formmated protein sequence
     */
    private static void addPeptideAnnotations(AnnotatedProtein protein, AttributedString formattedSequence) {
        java.util.List<PeptideAnnotation> peptides = protein.getAnnotations();
        if (peptides.size() > 0) {
            int numOfValidPeptides = 0;

            // remove invalid peptide
            Iterator<PeptideAnnotation> peptideIter = peptides.iterator();
            while (peptideIter.hasNext()) {
                PeptideAnnotation peptideAnnotation = peptideIter.next();
                if (!protein.isValidPeptideAnnotation(peptideAnnotation)) {
                    peptideIter.remove();
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

            // peptide coverage array
            // it is the length of the protein sequence, and contains the count of sequence coverage for each position
            int length = protein.getSequenceString().trim().length();
            int[] coverageArr = new int[length];
            int[] ptmArr = new int[length];
            for (PeptideAnnotation uniquePeptide : uniquePeptides) {
                int start = uniquePeptide.getStart() - 1;
                int end = uniquePeptide.getEnd() - 1;
                int peptideLen = end - start + 1;
                boolean selected = uniquePeptide.equals(protein.getSelectedAnnotation());

                // iterate peptide
                for (int i = start; i <= end; i++) {
                    if (selected) {
                        coverageArr[i] = -1;
                    } else if (coverageArr[i] != -1) {
                        coverageArr[i] += 1;
                    }
                }

                // ptms
                java.util.List<PTMAnnotation> ptms = uniquePeptide.getPtmAnnotations();
                for (PTMAnnotation ptm : ptms) {
                    int location = ptm.getLocation();
                    if (location >= 0) {
                        if (location > 0 && location <= peptideLen) {
                            location -= 1;
                        } else if (location == peptideLen + 1) {
                            location -= 2;
                        }

                        if (selected) {
                            ptmArr[start + location] = -1;
                        } else {
                            ptmArr[start + location] = 1;
                        }
                    }
                }
            }

            // colour code the peptide positions
            int numOfAminoAcidCovered = 0;
            for (int i = 0; i < coverageArr.length; i++) {
                int count = coverageArr[i];
                int index = mapIndex(i);
                if (count != 0) {
                    if (count == 1) {
                        formattedSequence.addAttribute(TextAttribute.BACKGROUND, PEPTIDE_BACKGROUND_COLOUR, index, index + 1);
                    } else if (count > 1) {
                        formattedSequence.addAttribute(TextAttribute.BACKGROUND, PEPTIDE_OVERLAP_COLOUR, index, index + 1);
                    } else if (count < 0) {
                        formattedSequence.addAttribute(TextAttribute.BACKGROUND, PEPTIDE_HIGHLIGHT_COLOUR, index, index + 1);
                    }
                    formattedSequence.addAttribute(TextAttribute.FOREGROUND, PEPTIDE_FOREGROUND_COLOUR, index, index + 1);
                    numOfAminoAcidCovered++;
                }
            }
            // set number of amino acid being covered
            protein.setNumOfAminoAcidCovered(numOfAminoAcidCovered);


            // colour code the ptm positions
            for (int i = 0; i < ptmArr.length; i++) {
                int count = ptmArr[i];
                int index = mapIndex(i);

                switch (count) {
                    case 1:
                        formattedSequence.addAttribute(TextAttribute.BACKGROUND, PTM_BACKGROUND_COLOUR, index, index + 1);
                        break;
                    case -1:
                        formattedSequence.addAttribute(TextAttribute.BACKGROUND, PTM_HIGHLIGHT_COLOUR, index, index + 1);
                        break;
                }
            }
        }
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
        return ((index - index % PROTEIN_SEGMENT_LENGTH) / PROTEIN_SEGMENT_LENGTH) * PROTEIN_SEGMENT_GAP.length() + index;
    }
}
