package uk.ac.ebi.pride.gui;

import org.junit.Test;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 31-Mar-2010
 * Time: 16:51:45
 */
public class PrideXmlControllerPerformenceTest {
    private PrideXmlControllerImpl controller;

    @Test
    public void speedTest() throws DataAccessException {
        File file = new File("src\\test\\resources\\pride-test-1.xml");
        // create access controller
        long start = System.currentTimeMillis();
        controller = new PrideXmlControllerImpl(file);
        long end = System.currentTimeMillis();
        System.out.println("Time spent on initializing PrideXmlControllerImpl (millisecond): " + (end - start));
    }
}
