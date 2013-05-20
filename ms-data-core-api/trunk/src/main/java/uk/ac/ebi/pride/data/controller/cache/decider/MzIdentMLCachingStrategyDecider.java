package uk.ac.ebi.pride.data.controller.cache.decider;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.cache.CachingStrategy;
import uk.ac.ebi.pride.data.controller.cache.CachingStrategyDecider;
import uk.ac.ebi.pride.data.controller.cache.strategy.MzIdentMLEagarCachingStrategy;
import uk.ac.ebi.pride.data.controller.cache.strategy.MzIdentMLProteinGroupCachingStrategy;
import uk.ac.ebi.pride.data.controller.cache.strategy.MzIdentMLQuickCachingStrategy;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Decider for choosing caching strategy for mzIdentML file
 *
 * @author Rui Wang
 * @version $Id$
 */
public class MzIdentMLCachingStrategyDecider implements CachingStrategyDecider {
    private static final String PROTEIN_AMBIGUITY_GROUP = "ProteinAmbiguityGroup";

    /**
     * Above this threshold, use quick caching strategy if no protein groups present
     */
    private long fileSizeThreshold;

    public MzIdentMLCachingStrategyDecider(long fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }

    @Override
    public CachingStrategy decide(File file) {

        try {
            if (tail(file, 1500).contains(PROTEIN_AMBIGUITY_GROUP)) {
                return new MzIdentMLProteinGroupCachingStrategy();
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to read the end of the file", e);
        }

        if (file.length() > fileSizeThreshold) {
            return new MzIdentMLQuickCachingStrategy();
        }

        return new MzIdentMLEagarCachingStrategy();
    }

    public String tail(File file, int numberOfChars) throws IOException {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new java.io.RandomAccessFile(file, "r");
            long fileLength = file.length() - 1;
            StringBuilder sb = new StringBuilder();

            for (long filePointer = fileLength; filePointer > (fileLength - numberOfChars); filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();
                sb.append((char) readByte);
            }

            return sb.reverse().toString();
        } finally {
            if (fileHandler != null)
                fileHandler.close();
        }
    }
}
