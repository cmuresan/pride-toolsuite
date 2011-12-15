package uk.ac.ebi.pride.mol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 14/09/11
 * Time: 15:24
 *
 */
public class GraviUtilities {
    private static final Map<String, Double> graviIndexes = new HashMap<String, Double>(){
        {
            put("A",  1.800); // Ala
            put("R", -4.500); // Arg
            put("N", -3.500); // Asn
            put("D", -3.500); // Asp
            put("C",  2.500); // Cys
            put("E", -3.500); // Gln
            put("Q", -3.500); // Glu
            put("G", -0.400); // Gly
            put("H", -3.200); // His
            put("I",  4.500); // Ile
            put("L",  3.800); // Leu
            put("K", -3.900); // Lys
            put("M",  1.900); // Met
            put("F",  2.800); // Phe
            put("P", -1.600); // Pro
            put("S", -0.800); // Ser
            put("T", -0.700); // Thr
            put("W", -0.900); // Trp
            put("Y", -1.300); // Tyr
            put("V",  4.200); // Val
        }
    };

    public static double calculate(String seq){
       double graviindex = 0.0;
       int count = 0;
       for(int i = 0; i < seq.length(); i++){
         if(graviIndexes.containsKey(String.valueOf(seq.charAt(i)))) {
            graviindex += graviIndexes.get(String.valueOf(seq.charAt(i)));
            count++;
         }
       }
       double pHround = Math.round((graviindex/count) * 1000.0D);
	   return (pHround / 1000.0D);
    }
}
