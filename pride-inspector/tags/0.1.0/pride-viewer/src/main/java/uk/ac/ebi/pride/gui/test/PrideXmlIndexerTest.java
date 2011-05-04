package uk.ac.ebi.pride.gui.test;

import uk.ac.ebi.pride.data.io.PrideXmlUnmarshaller;
import uk.ac.ebi.pride.data.jaxb.pridexml.GelFreeIdentificationType;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 17-Mar-2010
 * Time: 11:05:54
 */
public class PrideXmlIndexerTest {
    public static void main(String[] args) {
        File folder = new File(args[0]);
        File[] files = folder.listFiles();
        /*
        for(File file : files) {
            System.out.println(file.getName());
            long startTime = System.currentTimeMillis();
            PrideXmlUnmarshaller um = new PrideXmlUnmarshaller(file);
            long endTime = System.currentTimeMillis();
            System.out.println("Time for building index: " + (endTime - startTime));
            startTime = System.currentTimeMillis();
            List<String> twoIds = um.getGelFreeIdentAccs();
            if (!twoIds.isEmpty()) {
                GelFreeIdentificationType twoIdent = um.getGelFeeIdentByAcc(twoIds.get(twoIds.size() - 1));
                System.out.println("Gel Free Ident accession: " + twoIdent.getAccession());
            }
            endTime = System.currentTimeMillis();
            System.out.println("Time for getting GelFreeIdentificationType: " + (endTime - startTime));
        }
        */
    }
}