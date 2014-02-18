package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;

import java.io.File;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ManuelMzIdentMLTest {

    public static void main(String[] args) {
        File file = new File(args[0]);

        MzIdentMLControllerImpl mzIdentMLController = new MzIdentMLControllerImpl(file, true);

        System.out.println(mzIdentMLController.getProteinIds().size());
    }
}
