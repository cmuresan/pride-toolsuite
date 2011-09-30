package uk.ac.ebi.pride.chart.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: dani
 * Date: 15-Apr-2010
 * Time: 15:55:48
 * To change this template use File | Settings | File Templates.
 */
public class LoggerUtils {

       public static void error(Logger logger, Object obj, Throwable error) {
        String msg = String.format("%s failed on : %s", obj, error);
        logger.log(Level.ERROR, msg, error);
    }
}
