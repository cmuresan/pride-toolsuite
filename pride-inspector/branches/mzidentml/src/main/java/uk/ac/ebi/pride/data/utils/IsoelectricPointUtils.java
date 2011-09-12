package uk.ac.ebi.pride.data.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * IsoelectricPointUtils is used to calculate the theoretical isoelectric point of a peptide
 * <p/>
 * At the moment we only support one method, and the peptide PTMs are not taken into account
 * <p/>
 * The implementation is provided by Yasset Perez Riverol
 * <p/>
 * <p/>
 * User: rwang
 * Date: 03/08/2011
 * Time: 13:55
 */
public class IsoelectricPointUtils {

    private final static BjellpI bjellpI = new BjellpI();

    public static double calculate(String peptideSeq) {
        return bjellpI.calculate(peptideSeq);
    }

    /**
     * @author yperez
     */
    private static class BjellpI {

        private Map<String,Double> Cterm_pI_Bjell = new HashMap<String, Double>(); // pk at CTerm position Bjell
        private Map<String,Double> Nterm_pI_Bjell = new HashMap<String, Double>(); // pk at the NTerm position
        private Map<String,Double> sideGroup_pI_Bjell = new HashMap<String, Double>(); // in oder position


        private Map<String,Double> Cterm_pI_Skoog = new HashMap<String, Double>();
        private Map<String,Double> Nterm_pI_Skoog = new HashMap<String,Double>();
        private Map<String, Double> sideGroup_pI_Skoog = new HashMap<String,Double>();


        private Map<String,Double> Cterm_pI_expasy = new HashMap<String,Double>();
        private Map<String,Double> Nterm_pI_expasy = new HashMap<String,Double>();
        private Map<String,Double> sideGroup_pI_expasy = new HashMap<String,Double>();


        private Map<String,Double> Cterm_pI_calibrated = new HashMap<String,Double>(); // pk set from the Proteomics 2008,8, 4898-4906
        private Map<String,Double> Nterm_pI_calibrated = new HashMap<String,Double>();
        private Map<String,Double> sideGroup_pI_calibrated = new HashMap<String,Double>();

        private Map<String,Double> AA_pI_mod = new HashMap<String,Double>();

        private double FoRmU = 0.0D;

        private String seq = "nAAAEDTNSNVTQNPSGSDAPK"; // SequenceAA
        private double pH = -1.0D;
        private int numberOfPhosfoptides = 0; // number of phosphopeptides

        private double STpKa1/* = 2.12D*/;        // pk values for phospho-amino like
        private double STpKa2/* = 7.12D*/;

        private double YpKa1/* = 1.0D*/;
        private double YpKa2/* = 7.0D*/;

        private int foundPhosphoNumber = 0;

        private double pKa1/* = 1.2D*/;
        private double pKa2/* = 6.9D*/;

        public String useAApI = "calibrated";     // use by default calibrated.
        private boolean PhosphoAADep; //= true;  // compute all the phospho modification with the same pka and pkb set.
        private boolean methylated;// = false;   // methylated the entire protein.


        /* This function fill all the pk set withe the
        * experimentals values.
        */

        public BjellpI() {
            fillMaps();
        }

        /* This function set an especific set of pk values to
        * do the computation of pI of a SequenceAA.
        */

        public void setAApI(String pIset) {
            if (pIset.equals("Bjell")) {
                this.useAApI = "Bjell";
            } else if (pIset.equals("Skoog")) {
                this.useAApI = "Skoog";
            } else if (pIset.equals("Calibrated")) {
                this.useAApI = "calibrated";
            } else
                this.useAApI = "expasy";
        }

        /* Methylated the entire SequenceAA. This function say that all the protein
        * is methylated.
        */

        public void setMethylatedPeptides(boolean meth) {
            this.methylated = meth;
        }


        public ArrayList<SequenceAA> pIGroup(ArrayList<SequenceAA> sequences) {
            ArrayList<SequenceAA> tmp = sequences;
            for (int i = 0; i < sequences.size(); i++) {
                String keySeq = sequences.get(i).getSeq();
                SequenceAA newSeq = new SequenceAA();
                newSeq.setSeq(keySeq);
                newSeq.setPI(calculate(keySeq));
                tmp.set(i, newSeq);
            }
            return tmp;
        }


        /* Some considerations about isoelectric point, when the methylation
        * is consider for the SequenceAA then the AA Aspartic (D),
        * and Glutamic (E) must be removed.
        */
        public double calculate(String sequence) {

            Map<String,Double> AApI_n; // Variables for pk Values in N-Term position (Depend of the method)
            Map<String,Double> AApI_c; // Variables for pk Values in C-Term position (Depend of the method)
            Map<String,Double> AApI_side; // Variables for pk Values in other position (Depend of the method)
            this.seq = sequence; // SequenceAA

            if (this.useAApI.equals("Bjell")) {
                AApI_n = this.Nterm_pI_Bjell;
                AApI_c = this.Cterm_pI_Bjell;
                AApI_side = this.sideGroup_pI_Bjell;
            } else if (this.useAApI.equals("Skoog")) {
                AApI_n = this.Nterm_pI_Skoog;
                AApI_c = this.Cterm_pI_Skoog;
                AApI_side = this.sideGroup_pI_Skoog;
            } else if (this.useAApI.equals("expasy")) {
                AApI_n = this.Nterm_pI_expasy;
                AApI_c = this.Cterm_pI_expasy;
                AApI_side = this.sideGroup_pI_expasy;
            } else if (this.useAApI.equals("calibrated")) {
                AApI_n = this.Nterm_pI_calibrated;
                AApI_c = this.Cterm_pI_calibrated;
                AApI_side = this.sideGroup_pI_calibrated;
            } else {
                AApI_n = this.Nterm_pI_expasy;
                AApI_c = this.Cterm_pI_expasy;
                AApI_side = this.sideGroup_pI_expasy;
            }

            if (this.methylated) {
                Map<String,Double> tempmap = AApI_side;
                tempmap.remove("D");  // When the SequenceAA is methylated the function delete the
                tempmap.remove("E");  // Aspartic and Glutamic AA contributions in the any position.
                AApI_side = tempmap;
            }

            double piInt = 0.5D;

            /* This algorithm used this strategy:
            * Take an of step = 0.5 and make a loop while
            * the charge of the SequenceAA was >= 0.0 then take
            * the last value of the charge and the last value
            * of ph to make an exaustive step with and step of 0.0001.
            */

            int count = 0;
            do {
                if (getpI(AApI_n, AApI_c, AApI_side, this.pH) < 0.0D) break;
                this.pH += piInt;
                ++count;
            } while (this.pH <= 14.0D);

            this.pH -= piInt;

            piInt = 0.001D; // take a short step to compute the pI in this range.
            // this algorithm is very exaustive because the error
            // in the algorithm is in the 3t decimal of the number.
            count = 0;
            double getpI = 0.0;
            do {
                getpI = getpI(AApI_n, AApI_c, AApI_side, this.pH);
                if (getpI < 0.0D) {
                    this.pH -= piInt;
                    break;
                }
                this.pH += piInt;
                ++count;
            } while (this.pH <= 14.0D);

            if (getpI >= 0.0D) this.pH = 100.001D; // this is an unreal value.

            double pHround = Math.round(this.pH * 100.0D);

            if (this.PhosphoAADep) ;
            double reportpH = this.pH; // doubt
            this.pH = -1.0D;

            return (pHround / 100.0D);
        }

        public int getPhosphoNumber(String sEq) {
            int count = 0;

            for (int t = 0; t < sEq.length(); ++t) {
                String AA = String.valueOf(sEq.charAt(t));
                if (AA.equals("p")) {
                    ++count;
                }
            }
            return count;
        }

        /* This function compute the isoelectric point to a SequenceAA,
        * take to account the composition of the SequenceAA. Each phosphorylation
        * was represented as (p), the acethylation of the N-term residue
        * was represented as (a), the methionine oxidation of a residue
        * was represented as (o) (the effect of this modification in the pI
        * value is minimal), the metionine (M) oxidation in the N-term
        * was represented as (m).
        */
        private double getpI(Map<String,Double> AApI_n, Map<String,Double> AApI_c, Map<String,Double> AApI_side, double PH) {

            String sideAA; //281
            double pHpK = 0.0D;
            this.FoRmU = 0.0D;
            boolean donotuseNterm = false;
            boolean usephosphoNterm = false;
            boolean useoxidation = false;
            boolean useSideChain = true;
            String ntermAA = null;

            // Validate the nature of the modification in the N-term position.
            // In the N-term position the peptide may have these chemical modifications
            // a (acethylation N-term), o (oxidation), p (phosphorylation of N-term amino),
            // m (oxidation acetylation of metionine N-term).

            int i = 0;
            do {
                ntermAA = String.valueOf(this.seq.charAt(i));
                if (ntermAA.equals("p")) {                         // if the N-term was phosphorylated we used.
                    ntermAA = String.valueOf(this.seq.charAt(i + 1));
                    usephosphoNterm = true;
                } else if (ntermAA.equals("a")) {                  // if the N-term was acethylated we not used.
                    donotuseNterm = true;
                    ntermAA = String.valueOf(this.seq.charAt(i + 1));
                } else if (ntermAA.equals("o")) {                  // if the N-term was oxidated we used.
                    ntermAA = String.valueOf(this.seq.charAt(i + 1));
                    useoxidation = true;
                } else if (ntermAA.equals("m")) {                  // if the N-term is the Metionine AA and the modification is the Oxidation we not used.
                    donotuseNterm = true;
                    ntermAA = String.valueOf(this.seq.charAt(i + 1));
                }
                i++;
            } while (ntermAA.equals("p") || ntermAA.equals("a") || ntermAA.equals("o") || ntermAA.equals("m"));

            if (!donotuseNterm) {
                pHpK = PH - Double.valueOf(AApI_n.get(ntermAA).toString()).doubleValue();
                this.FoRmU += 1.0D / (1.0D + Math.pow(10.0D, pHpK));
            }

            if (!(this.methylated)) {  // if the peptide is methylated then the cterm also and we don't take it.
                String cterm = String.valueOf(this.seq.charAt(this.seq.length() - 1));
                pHpK = Double.valueOf(AApI_c.get(cterm).toString()).doubleValue() - PH;
                this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, pHpK));
            }

            // Consideraciones de Side Group
            this.foundPhosphoNumber = 0;
            for (int t = i + 1; t < this.seq.length(); ++t) {
                String AA = String.valueOf(this.seq.charAt(t));
                if (AApI_side.containsKey(AA)) { // if no is a modification
                    String mod = "";
                    sideAA = AA;
                    if (t == i) {
                        if (usephosphoNterm) {
                            mod = String.valueOf("p"); // modification
                        } else if (useoxidation) {
                            mod = String.valueOf("o"); // modification
                        }
                    } else {
                        mod = String.valueOf(this.seq.charAt(t - 1));
                        if(!(mod.equals("o")) && !(mod.equals("p"))) {
                            useSideChain = true;
                        } else {
                            useSideChain = false;
                        }
                    }

                    if (mod.equals("o") || (useSideChain)) {
                        double value = Double.valueOf(AApI_side.get(sideAA).toString()).doubleValue();
                        if (value < 0.0D) {
                            pHpK = PH + value;
                            this.FoRmU += 1.0D / (1.0D + Math.pow(10.0D, pHpK));
                        } else {
                            pHpK = value - PH;
                            this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, pHpK));
                        }
                    } else if (mod.equals("p")) {
                        if (this.PhosphoAADep) {
                            this.foundPhosphoNumber = 0;
                            if ((sideAA.equals("S")) || (sideAA.equals("T"))) {
                                this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.STpKa1 - PH));
                                this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.STpKa2 - PH));
                                this.foundPhosphoNumber += 1;
                            } else if (sideAA.equals("Y")) {
                                this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.YpKa1 - PH));
                                this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.YpKa2 - PH));
                                this.foundPhosphoNumber += 1;
                            }
                        }
                    } else if (AApI_side.containsKey(seq)) {

                    }
                } else if (String.valueOf(this.seq.charAt(t - 1)).equals("p")) {
                    if (this.PhosphoAADep) {
                        this.foundPhosphoNumber = 0;
                        if ((AA.equals("S")) || (AA.equals("T"))) {
                            this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.STpKa1 - PH));
                            this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.STpKa2 - PH));
                            this.foundPhosphoNumber += 1;
                        } else if (AA.equals("Y")) {
                            this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.YpKa1 - PH));
                            this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.YpKa2 - PH));
                            this.foundPhosphoNumber += 1;
                        }
                    } else {
                        this.foundPhosphoNumber += 1;
                    }
                }
            }

            if (!this.PhosphoAADep) {
                for (int t = 0; t < this.numberOfPhosfoptides; ++t) {
                    this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.pKa1 - PH));
                    this.FoRmU += -1.0D / (1.0D + Math.pow(10.0D, this.pKa2 - PH));
                }
            }

            return this.FoRmU;
        }

        public void resetParams() {
            this.pH = 0.0D;
            this.FoRmU = 0.0D;
            this.numberOfPhosfoptides = 0;
            this.seq = "";
            this.AA_pI_mod = new HashMap<String,Double>();
            this.PhosphoAADep = false;
            this.STpKa1 = 2.12D;
            this.STpKa2 = 7.12D;
            this.YpKa1 = 1.0D;
            this.YpKa2 = 7.0D;
            this.pKa1 = 1.2D;
            this.pKa2 = 6.9D;

            fillMaps();
        }

        public void printParameters() {
            System.out.println("numberOfPhosfoptides: " + numberOfPhosfoptides);
            System.out.println("PhosphoAADep: " + PhosphoAADep);
            System.out.println("STpKa1: " + STpKa1);
            System.out.println("STpKa2: " + STpKa2);
            System.out.println("YpKa1: " + YpKa1);
            System.out.println("pKa1: " + pKa1);
            System.out.println("pKa2: " + pKa2);
        }

        private void fillMaps() {

            Cterm_pI_Bjell.put("A", 2.35D);
            Cterm_pI_Bjell.put("R", 2.17D);
            Cterm_pI_Bjell.put("N", 2.02D);
            Cterm_pI_Bjell.put("D", 2.09D);
            Cterm_pI_Bjell.put("C", 1.71D);
            Cterm_pI_Bjell.put("E", 2.19D);
            Cterm_pI_Bjell.put("Q", 2.17D);
            Cterm_pI_Bjell.put("G", 2.34D);
            Cterm_pI_Bjell.put("H", 1.82D);
            Cterm_pI_Bjell.put("I", 2.36D);
            Cterm_pI_Bjell.put("L", 2.36D);
            Cterm_pI_Bjell.put("K", 2.18D);
            Cterm_pI_Bjell.put("M", 2.28D);
            Cterm_pI_Bjell.put("F", 1.83D);
            Cterm_pI_Bjell.put("P", 1.99D);
            Cterm_pI_Bjell.put("S", 2.21D);
            Cterm_pI_Bjell.put("T", 2.63D);
            Cterm_pI_Bjell.put("W", 2.38D);
            Cterm_pI_Bjell.put("Y", 2.2D);
            Cterm_pI_Bjell.put("V", 2.32D);

            Nterm_pI_Bjell.put("A", 7.5D);
            Nterm_pI_Bjell.put("R", 6.76D);
            Nterm_pI_Bjell.put("N", 7.22D);
            Nterm_pI_Bjell.put("D", 7.7D);
            Nterm_pI_Bjell.put("C", 8.119999999999999D);
            Nterm_pI_Bjell.put("E", 7.19D);
            Nterm_pI_Bjell.put("Q", 6.73D);
            Nterm_pI_Bjell.put("G", 7.5D);
            Nterm_pI_Bjell.put("H", 7.18D);
            Nterm_pI_Bjell.put("I", 7.48D);
            Nterm_pI_Bjell.put("L", 7.46D);
            Nterm_pI_Bjell.put("K", 6.67D);
            Nterm_pI_Bjell.put("M", 6.98D);
            Nterm_pI_Bjell.put("F", 6.96D);
            Nterm_pI_Bjell.put("P", 8.359999999999999D);
            Nterm_pI_Bjell.put("S", 6.86D);
            Nterm_pI_Bjell.put("T", 7.02D);
            Nterm_pI_Bjell.put("W", 7.11D);
            Nterm_pI_Bjell.put("Y", 6.83D);
            Nterm_pI_Bjell.put("V", 7.44D);

            sideGroup_pI_Bjell.put("R", -12.5D);
            sideGroup_pI_Bjell.put("D", 4.07D);
            sideGroup_pI_Bjell.put("C", 8.279999999999999D);
            sideGroup_pI_Bjell.put("E", 4.45D);
            sideGroup_pI_Bjell.put("H", -6.08D);
            sideGroup_pI_Bjell.put("K", -9.800000000000001D);
            sideGroup_pI_Bjell.put("Y", 9.84D);

            Cterm_pI_Skoog.put("A", 2.35D);
            Cterm_pI_Skoog.put("R", 2.17D);
            Cterm_pI_Skoog.put("N", 2.02D);
            Cterm_pI_Skoog.put("D", 2.09D);
            Cterm_pI_Skoog.put("C", 1.71D);
            Cterm_pI_Skoog.put("E", 2.19D);
            Cterm_pI_Skoog.put("Q", 2.17D);
            Cterm_pI_Skoog.put("G", 2.34D);
            Cterm_pI_Skoog.put("H", 1.82D);
            Cterm_pI_Skoog.put("I", 2.36D);
            Cterm_pI_Skoog.put("L", 2.36D);
            Cterm_pI_Skoog.put("K", 2.18D);
            Cterm_pI_Skoog.put("M", 2.28D);
            Cterm_pI_Skoog.put("F", 1.83D);
            Cterm_pI_Skoog.put("P", 1.99D);
            Cterm_pI_Skoog.put("S", 2.21D);
            Cterm_pI_Skoog.put("T", 2.63D);
            Cterm_pI_Skoog.put("W", 2.38D);
            Cterm_pI_Skoog.put("Y", 2.2D);
            Cterm_pI_Skoog.put("V", 2.32D);

            Nterm_pI_Skoog.put("A", 9.69D);
            Nterm_pI_Skoog.put("R", 9.039999999999999D);
            Nterm_pI_Skoog.put("N", 8.800000000000001D);
            Nterm_pI_Skoog.put("D", 9.82D);
            Nterm_pI_Skoog.put("C", 10.779999999999999D);
            Nterm_pI_Skoog.put("E", 9.76D);
            Nterm_pI_Skoog.put("Q", 9.130000000000001D);
            Nterm_pI_Skoog.put("G", 9.6D);
            Nterm_pI_Skoog.put("H", 9.17D);
            Nterm_pI_Skoog.put("I", 9.68D);
            Nterm_pI_Skoog.put("L", 9.6D);
            Nterm_pI_Skoog.put("K", 8.949999999999999D);
            Nterm_pI_Skoog.put("M", 9.210000000000001D);
            Nterm_pI_Skoog.put("F", 9.130000000000001D);
            Nterm_pI_Skoog.put("P", 10.6D);
            Nterm_pI_Skoog.put("S", 9.15D);
            Nterm_pI_Skoog.put("T", 10.43D);
            Nterm_pI_Skoog.put("W", 9.390000000000001D);
            Nterm_pI_Skoog.put("Y", 9.109999999999999D);
            Nterm_pI_Skoog.put("V", 9.619999999999999D);
            sideGroup_pI_Skoog.put("R", -12.48D);
            sideGroup_pI_Skoog.put("D", 3.86D);
            sideGroup_pI_Skoog.put("C", 8.33D);
            sideGroup_pI_Skoog.put("E", 4.25D);
            sideGroup_pI_Skoog.put("H", -6.0D);
            sideGroup_pI_Skoog.put("K", -10.529999999999999D);
            sideGroup_pI_Skoog.put("Y", 10.07D);

            Cterm_pI_expasy.put("A", 3.55D);
            Cterm_pI_expasy.put("R", 3.55D);
            Cterm_pI_expasy.put("N", 3.55D);
            Cterm_pI_expasy.put("D", 4.55D);
            Cterm_pI_expasy.put("C", 3.55D);
            Cterm_pI_expasy.put("E", 4.75D);
            Cterm_pI_expasy.put("Q", 3.55D);
            Cterm_pI_expasy.put("G", 3.55D);
            Cterm_pI_expasy.put("H", 3.55D);
            Cterm_pI_expasy.put("I", 3.55D);
            Cterm_pI_expasy.put("L", 3.55D);
            Cterm_pI_expasy.put("K", 3.55D);
            Cterm_pI_expasy.put("M", 3.55D);
            Cterm_pI_expasy.put("F", 3.55D);
            Cterm_pI_expasy.put("P", 3.55D);
            Cterm_pI_expasy.put("S", 3.55D);
            Cterm_pI_expasy.put("T", 3.55D);
            Cterm_pI_expasy.put("W", 3.55D);
            Cterm_pI_expasy.put("Y", 3.55D);
            Cterm_pI_expasy.put("V", 3.55D);

            Nterm_pI_expasy.put("A", 7.59D);
            Nterm_pI_expasy.put("R", 7.5D);
            Nterm_pI_expasy.put("N", 7.5D);
            Nterm_pI_expasy.put("D", 7.5D);
            Nterm_pI_expasy.put("C", 7.5D);
            Nterm_pI_expasy.put("E", 7.7D);
            Nterm_pI_expasy.put("Q", 7.5D);
            Nterm_pI_expasy.put("G", 7.5D);
            Nterm_pI_expasy.put("H", 7.5D);
            Nterm_pI_expasy.put("I", 7.5D);
            Nterm_pI_expasy.put("L", 7.5D);
            Nterm_pI_expasy.put("K", 7.5D);
            Nterm_pI_expasy.put("M", 7.0D);
            Nterm_pI_expasy.put("F", 7.5D);
            Nterm_pI_expasy.put("P", 8.359999999999999D);
            Nterm_pI_expasy.put("S", 6.93D);
            Nterm_pI_expasy.put("T", 6.82D);
            Nterm_pI_expasy.put("W", 7.5D);
            Nterm_pI_expasy.put("Y", 7.5D);
            Nterm_pI_expasy.put("V", 7.44D);

            sideGroup_pI_expasy.put("R", -12.0D);
            sideGroup_pI_expasy.put("D", 4.05D);
            sideGroup_pI_expasy.put("C", 9.0D);
            sideGroup_pI_expasy.put("E", 4.45D);
            sideGroup_pI_expasy.put("H", -5.98D);
            sideGroup_pI_expasy.put("K", -10.0D);
            sideGroup_pI_expasy.put("Y", 10.0D);

            Cterm_pI_calibrated.put("A", 3.55D);
            Cterm_pI_calibrated.put("R", 3.55D);
            Cterm_pI_calibrated.put("N", 3.55D);
            Cterm_pI_calibrated.put("D", 4.55D);
            Cterm_pI_calibrated.put("C", 3.55D);
            Cterm_pI_calibrated.put("E", 4.75D);
            Cterm_pI_calibrated.put("Q", 3.55D);
            Cterm_pI_calibrated.put("G", 3.55D);
            Cterm_pI_calibrated.put("H", 3.55D);
            Cterm_pI_calibrated.put("I", 3.55D);
            Cterm_pI_calibrated.put("L", 3.55D);
            Cterm_pI_calibrated.put("K", 3.55D);
            Cterm_pI_calibrated.put("M", 3.55D);
            Cterm_pI_calibrated.put("F", 3.55D);
            Cterm_pI_calibrated.put("P", 3.55D);
            Cterm_pI_calibrated.put("S", 3.55D);
            Cterm_pI_calibrated.put("T", 3.55D);
            Cterm_pI_calibrated.put("W", 3.55D);
            Cterm_pI_calibrated.put("Y", 3.55D);
            Cterm_pI_calibrated.put("V", 3.55D);

            Nterm_pI_calibrated.put("A", 7.59D);
            Nterm_pI_calibrated.put("R", 7.5D);
            Nterm_pI_calibrated.put("N", 6.7D);
            Nterm_pI_calibrated.put("D", 7.5D);
            Nterm_pI_calibrated.put("C", 6.5D);
            Nterm_pI_calibrated.put("E", 7.7D);
            Nterm_pI_calibrated.put("Q", 7.5D);
            Nterm_pI_calibrated.put("G", 7.5D);
            Nterm_pI_calibrated.put("H", 7.5D);
            Nterm_pI_calibrated.put("I", 7.5D);
            Nterm_pI_calibrated.put("L", 7.5D);
            Nterm_pI_calibrated.put("K", 7.5D);
            Nterm_pI_calibrated.put("M", 7.0D);
            Nterm_pI_calibrated.put("F", 7.5D);
            Nterm_pI_calibrated.put("P", 8.359999999999999D);
            Nterm_pI_calibrated.put("S", 6.93D);
            Nterm_pI_calibrated.put("T", 6.82D);
            Nterm_pI_calibrated.put("W", 7.5D);
            Nterm_pI_calibrated.put("Y", 7.5D);
            Nterm_pI_calibrated.put("V", 7.44D);

            sideGroup_pI_calibrated.put("R", -12.0D);
            sideGroup_pI_calibrated.put("D", 4.05D);
            sideGroup_pI_calibrated.put("C", 9.0D);
            sideGroup_pI_calibrated.put("E", 4.45D);
            sideGroup_pI_calibrated.put("H", -5.98D);
            sideGroup_pI_calibrated.put("K", -10.0D);
            sideGroup_pI_calibrated.put("Y", 10.0D);
        }
    }

    /**
     * @author Yasset
     */
    private static class SequenceAA {
        private String seq = null;
        private double pI = 0.0;
        private double chargeAtPH = 0.0;
        private ArrayList<AminoAcid> seqList = null;
        private AminoAcid Nterm = null;
        private AminoAcid Cterm = null;

        public AminoAcid getCterm() {
            return Cterm;
        }

        public SequenceAA() {
            this.seq = null;
            this.pI = 0.0;
            this.chargeAtPH = 0.0;
            seqList = null;
            this.Nterm = null;
            this.Cterm = null;
        }

        public SequenceAA(String aSequence, double apI, double achargeAtPH) {
            this.seq = aSequence;
            this.pI = apI;
            this.chargeAtPH = achargeAtPH;
            this.seqList = parseSequence(aSequence);
        }

        private ArrayList<AminoAcid> parseSequence(String aSequence) {
            ArrayList<AminoAcid> tmpList = new ArrayList<AminoAcid>();

            /* To take the N-terminal modification and the aminoacid
            * in this position.
            */

            int i = 0;
            if (aSequence.charAt(i) == '[') {
                this.Nterm = new AminoAcid(null, aSequence.charAt(i + 1));
            } else {
                this.Nterm = new AminoAcid();
            }
            while (Character.isLowerCase(aSequence.charAt(i))) {
                i++;
            }
            this.Nterm.setAAletter(aSequence.charAt(i));

            /* N-Terminal taked*/

            /* To take the C-terminal modification and the aminoacid
            * in this position.
            */

            i = aSequence.length() - 1;
            if (aSequence.charAt(i) == ']') {
                this.Cterm = new AminoAcid(null, aSequence.charAt(i - 1));
            } else {
                this.Cterm = new AminoAcid();
            }
            while (Character.isLowerCase(aSequence.charAt(i))) {
                i--;
            }
            this.Cterm.setAAletter(aSequence.charAt(i));

            /* C-terminal taked*/

            if (aSequence.charAt(0) == '[') {
                aSequence = aSequence.substring(3);
            }

            if (aSequence.charAt(aSequence.length() - 1) == ']') {
                aSequence = aSequence.substring(0, aSequence.length() - 3);
            }

            for (; i < aSequence.length(); i++) {
                AminoAcid amino = null;
                char aatemp = aSequence.charAt(i);
                if (Character.isLowerCase(aatemp)) {
                    i++;
                    char aa = aSequence.charAt(i);
                    amino = new AminoAcid(aa, aatemp);
                } else {
                    amino = new AminoAcid(aatemp);
                }
                tmpList.add(amino);
            }
            return tmpList;
        }

        public double getChargeAtPH() {
            return chargeAtPH;
        }

        public void setChargeAtPH(double chargeAtPH) {
            this.chargeAtPH = chargeAtPH;
        }

        public double getPI() {
            return pI;
        }

        public void setPI(double pI) {
            this.pI = pI;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public void setMod(Character aa, int index) {
            this.seqList.get(index).setMod(aa);
        }

    }

    /**
     * @author ypriverol
     */
    private static class AminoAcid {
        Character AAletter = null;
        Character mod = null;

        public AminoAcid(char aa, char mod) {
            this.AAletter = new Character(aa);
            this.mod = new Character(mod);
        }

        public AminoAcid(char aa) {
            this.AAletter = new Character(aa);
        }

        public AminoAcid(Character aa, char mod) {
            this.AAletter = aa;
            this.mod = mod;
        }

        public AminoAcid() {
            this.AAletter = null;
            this.mod = null;
        }

        public Character getAAletter() {
            return AAletter;
        }

        public void setAAletter(Character AAletter) {
            this.AAletter = AAletter;
        }

        public Character getMod() {
            return mod;
        }

        public void setMod(Character mod) {
            this.mod = mod;
        }

    }


}
