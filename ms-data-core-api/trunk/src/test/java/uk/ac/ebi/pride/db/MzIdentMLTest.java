package uk.ac.ebi.pride.db;

import uk.ac.ebi.jmzidml.xml.io.MzIdentMLUnmarshaller;

import java.io.File;

/**
 *
 * @author Rui Wang
 * @version $Id$
 */
public class MzIdentMLTest {

    public static void main(String[] args) {
        MzIdentMLUnmarshaller unmarshaller = new MzIdentMLUnmarshaller(new File(args[0]));
        System.out.println("MzIdentML read");
    }
}
