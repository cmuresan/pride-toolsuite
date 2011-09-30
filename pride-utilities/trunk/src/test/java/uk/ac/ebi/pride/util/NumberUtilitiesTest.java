package uk.ac.ebi.pride.util;

import org.junit.Test;
import uk.ac.ebi.pride.mol.GraviUtilities;
import uk.ac.ebi.pride.mol.IsoelectricPointUtils;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 30/06/2011
 * Time: 10:56
 */
public class NumberUtilitiesTest {
    @Test
    public void testIsNumber() throws Exception {
        String str = "7.60056300136405E-4";
        assertTrue(NumberUtilities.isNumber(str));
        str = "7.60056300136405E--4";
        assertFalse(NumberUtilities.isNumber(str));
        str = "7.60056300e136405E-4";
        assertFalse(NumberUtilities.isNumber(str));
    }

    @Test
    public void testIsInteger() throws Exception {

    }

    @Test
    public void testIsNonNegativeInteger() throws Exception {

    }

    @Test
    public void testScaleDouble() throws Exception {

    }
    @Test
    public void testIsoPointcaculate(){
        String seq = "EAFQNAYLELGGLGER";
        double value = IsoelectricPointUtils.calculate(seq);
        System.out.println(value);
    }
    @Test
    public void testGravicalculate(){
        String seq = "MEKKPAAKKAGSDAAASRPRAAKVAKKVHPKGKKPKKAKPHCSRNPVLVRGIGRYSRSAMYSRKALYKRKYSAAKTKVEKKKKKEKVLATVTKTVGGDKNGGTRVVKLRKMPRYYPTEDVPRKLLSHGKKPFSQHVRRLRSSITPGTVLIILTGRHRGKRVVFLKQLDSGLLLVTGPLVI";
        double value = GraviUtilities.calculate(seq);
        System.out.println(value);
    }

}
