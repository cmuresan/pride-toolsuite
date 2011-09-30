package uk.ac.ebi.pride.gui;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Methods to run on event dispatch thread (ETD)
 *
 * User: rwang
 * Date: 02/06/11
 * Time: 09:27
 */
public class EDTUtils {

    /**
     * Invoke a piece of code on ETD
     *
     * @param code  code to run
     */
    public static void invokeLater(Runnable code) {
        if (SwingUtilities.isEventDispatchThread()) {
                code.run();
            } else {
                EventQueue.invokeLater(code);
            }
    }

    /**
     * Invoke a piece of code on ETD and wait
     *
     * @param code  code to run
     * @throws InterruptedException exception for interruption
     * @throws java.lang.reflect.InvocationTargetException invocation exception
     */
    public static void invokeAndWait(Runnable code) throws InvocationTargetException, InterruptedException {
        if (SwingUtilities.isEventDispatchThread()) {
                code.run();
            } else {
                EventQueue.invokeAndWait(code);
            }
    }
}
