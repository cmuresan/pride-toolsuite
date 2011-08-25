package uk.ac.ebi.pride.data.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * IsoelectricPointUtils is used to calculate the theoretical isoelectric point of a peptide
 * <p/>
 * At the moment we only support one method, and the peptide PTMs are not taken into account
 *
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

        private HashMap Cterm_pI_Bjell = new HashMap(); // pk at CTerm position Bjell
        private HashMap Nterm_pI_Bjell = new HashMap(); // pk at the NTerm position
        private HashMap sideGroup_pI_Bjell = new HashMap(); // in oder position


        private HashMap Cterm_pI_Skoog = new HashMap();
        private HashMap Nterm_pI_Skoog = new HashMap();
        private HashMap sideGroup_pI_Skoog = new HashMap();


        private HashMap Cterm_pI_expasy = new HashMap();
        private HashMap Nterm_pI_expasy = new HashMap();
        private HashMap sideGroup_pI_expasy = new HashMap();


        private HashMap Cterm_pI_calibrated = new HashMap(); // pk set from the Proteomics 2008,8, 4898-4906
        private HashMap Nterm_pI_calibrated = new HashMap();
        private HashMap sideGroup_pI_calibrated = new HashMap();

        private HashMap AA_pI_mod = new HashMap();
        private HashMap sideGroup_pI = new HashMap();

        private double phStart = 0.0D;
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

            HashMap AApI_n; // Variables for pk Values in N-Term position (Depend of the method)
            HashMap AApI_c; // Variables for pk Values in C-Term position (Depend of the method)
            HashMap AApI_side; // Variables for pk Values in other position (Depend of the method)
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
                HashMap tempmap = AApI_side;
                tempmap.remove("D");  // When the SequenceAA is methylated the function delete the
                tempmap.remove("E");  // Aspartic and Glutamic AA contributions in the any position.
                AApI_side = new HashMap(); // of the SequenceAA less in the N-term and C-term position.
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
        private double getpI(HashMap AApI_n, HashMap AApI_c, HashMap AApI_side, double PH) {

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
                        if (usephosphoNterm == true) {
                            mod = String.valueOf("p"); // modification
                        } else if (useoxidation == true) {
                            mod = String.valueOf("o"); // modification
                        }
                    } else {
                        mod = String.valueOf(this.seq.charAt(t - 1));
                        if (!(mod.equals("o")) && !(mod.equals("p"))) {
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
            this.AA_pI_mod = new HashMap();
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

        public void setPhospAAdep(boolean useAAdep) {
            this.PhosphoAADep = true;
        }

        public void setSTYPkA(double ST1, double ST2, double Y1, double Y2) {
            this.STpKa1 = ST1;
            this.STpKa2 = ST2;
            this.YpKa1 = Y1;
            this.YpKa2 = Y2;
        }

        public void setUserPka(double PKA1, double PKA2) {
            this.pKa1 = PKA1;
            this.pKa2 = PKA2;
        }

        public void setpKaSet(String setname) {
            if (setname.equals("scansite")) {
                this.pKa1 = 2.12D;
                this.pKa2 = 7.21D;
            } else if (setname.equals("promost")) {
                this.pKa1 = 1.2D;
                this.pKa2 = 6.5D;
            } else {
                this.pKa1 = 1.2D;
                this.pKa2 = 6.9D;
            }
        }

        public void insertMod(String aminoacid, double pka, double mw_mono, double mw_ave) {
            this.AA_pI_mod.put(aminoacid, Double.valueOf(pka));
        }

        public void setPhosfoNumber(int n) {
            this.numberOfPhosfoptides = n;
        }

        private void fillMaps() {

            this.Cterm_pI_Bjell.put("A", Double.valueOf(2.35D));
            this.Cterm_pI_Bjell.put("R", Double.valueOf(2.17D));
            this.Cterm_pI_Bjell.put("N", Double.valueOf(2.02D));
            this.Cterm_pI_Bjell.put("D", Double.valueOf(2.09D));
            this.Cterm_pI_Bjell.put("C", Double.valueOf(1.71D));
            this.Cterm_pI_Bjell.put("E", Double.valueOf(2.19D));
            this.Cterm_pI_Bjell.put("Q", Double.valueOf(2.17D));
            this.Cterm_pI_Bjell.put("G", Double.valueOf(2.34D));
            this.Cterm_pI_Bjell.put("H", Double.valueOf(1.82D));
            this.Cterm_pI_Bjell.put("I", Double.valueOf(2.36D));
            this.Cterm_pI_Bjell.put("L", Double.valueOf(2.36D));
            this.Cterm_pI_Bjell.put("K", Double.valueOf(2.18D));
            this.Cterm_pI_Bjell.put("M", Double.valueOf(2.28D));
            this.Cterm_pI_Bjell.put("F", Double.valueOf(1.83D));
            this.Cterm_pI_Bjell.put("P", Double.valueOf(1.99D));
            this.Cterm_pI_Bjell.put("S", Double.valueOf(2.21D));
            this.Cterm_pI_Bjell.put("T", Double.valueOf(2.63D));
            this.Cterm_pI_Bjell.put("W", Double.valueOf(2.38D));
            this.Cterm_pI_Bjell.put("Y", Double.valueOf(2.2D));
            this.Cterm_pI_Bjell.put("V", Double.valueOf(2.32D));

            this.Nterm_pI_Bjell.put("A", Double.valueOf(7.5D));
            this.Nterm_pI_Bjell.put("R", Double.valueOf(6.76D));
            this.Nterm_pI_Bjell.put("N", Double.valueOf(7.22D));
            this.Nterm_pI_Bjell.put("D", Double.valueOf(7.7D));
            this.Nterm_pI_Bjell.put("C", Double.valueOf(8.119999999999999D));
            this.Nterm_pI_Bjell.put("E", Double.valueOf(7.19D));
            this.Nterm_pI_Bjell.put("Q", Double.valueOf(6.73D));
            this.Nterm_pI_Bjell.put("G", Double.valueOf(7.5D));
            this.Nterm_pI_Bjell.put("H", Double.valueOf(7.18D));
            this.Nterm_pI_Bjell.put("I", Double.valueOf(7.48D));
            this.Nterm_pI_Bjell.put("L", Double.valueOf(7.46D));
            this.Nterm_pI_Bjell.put("K", Double.valueOf(6.67D));
            this.Nterm_pI_Bjell.put("M", Double.valueOf(6.98D));
            this.Nterm_pI_Bjell.put("F", Double.valueOf(6.96D));
            this.Nterm_pI_Bjell.put("P", Double.valueOf(8.359999999999999D));
            this.Nterm_pI_Bjell.put("S", Double.valueOf(6.86D));
            this.Nterm_pI_Bjell.put("T", Double.valueOf(7.02D));
            this.Nterm_pI_Bjell.put("W", Double.valueOf(7.11D));
            this.Nterm_pI_Bjell.put("Y", Double.valueOf(6.83D));
            this.Nterm_pI_Bjell.put("V", Double.valueOf(7.44D));

            this.sideGroup_pI_Bjell.put("R", Double.valueOf(-12.5D));
            this.sideGroup_pI_Bjell.put("D", Double.valueOf(4.07D));
            this.sideGroup_pI_Bjell.put("C", Double.valueOf(8.279999999999999D));
            this.sideGroup_pI_Bjell.put("E", Double.valueOf(4.45D));
            this.sideGroup_pI_Bjell.put("H", Double.valueOf(-6.08D));
            this.sideGroup_pI_Bjell.put("K", Double.valueOf(-9.800000000000001D));
            this.sideGroup_pI_Bjell.put("Y", Double.valueOf(9.84D));

            this.Cterm_pI_Skoog.put("A", Double.valueOf(2.35D));
            this.Cterm_pI_Skoog.put("R", Double.valueOf(2.17D));
            this.Cterm_pI_Skoog.put("N", Double.valueOf(2.02D));
            this.Cterm_pI_Skoog.put("D", Double.valueOf(2.09D));
            this.Cterm_pI_Skoog.put("C", Double.valueOf(1.71D));
            this.Cterm_pI_Skoog.put("E", Double.valueOf(2.19D));
            this.Cterm_pI_Skoog.put("Q", Double.valueOf(2.17D));
            this.Cterm_pI_Skoog.put("G", Double.valueOf(2.34D));
            this.Cterm_pI_Skoog.put("H", Double.valueOf(1.82D));
            this.Cterm_pI_Skoog.put("I", Double.valueOf(2.36D));
            this.Cterm_pI_Skoog.put("L", Double.valueOf(2.36D));
            this.Cterm_pI_Skoog.put("K", Double.valueOf(2.18D));
            this.Cterm_pI_Skoog.put("M", Double.valueOf(2.28D));
            this.Cterm_pI_Skoog.put("F", Double.valueOf(1.83D));
            this.Cterm_pI_Skoog.put("P", Double.valueOf(1.99D));
            this.Cterm_pI_Skoog.put("S", Double.valueOf(2.21D));
            this.Cterm_pI_Skoog.put("T", Double.valueOf(2.63D));
            this.Cterm_pI_Skoog.put("W", Double.valueOf(2.38D));
            this.Cterm_pI_Skoog.put("Y", Double.valueOf(2.2D));
            this.Cterm_pI_Skoog.put("V", Double.valueOf(2.32D));

            this.Nterm_pI_Skoog.put("A", Double.valueOf(9.69D));
            this.Nterm_pI_Skoog.put("R", Double.valueOf(9.039999999999999D));
            this.Nterm_pI_Skoog.put("N", Double.valueOf(8.800000000000001D));
            this.Nterm_pI_Skoog.put("D", Double.valueOf(9.82D));
            this.Nterm_pI_Skoog.put("C", Double.valueOf(10.779999999999999D));
            this.Nterm_pI_Skoog.put("E", Double.valueOf(9.76D));
            this.Nterm_pI_Skoog.put("Q", Double.valueOf(9.130000000000001D));
            this.Nterm_pI_Skoog.put("G", Double.valueOf(9.6D));
            this.Nterm_pI_Skoog.put("H", Double.valueOf(9.17D));
            this.Nterm_pI_Skoog.put("I", Double.valueOf(9.68D));
            this.Nterm_pI_Skoog.put("L", Double.valueOf(9.6D));
            this.Nterm_pI_Skoog.put("K", Double.valueOf(8.949999999999999D));
            this.Nterm_pI_Skoog.put("M", Double.valueOf(9.210000000000001D));
            this.Nterm_pI_Skoog.put("F", Double.valueOf(9.130000000000001D));
            this.Nterm_pI_Skoog.put("P", Double.valueOf(10.6D));
            this.Nterm_pI_Skoog.put("S", Double.valueOf(9.15D));
            this.Nterm_pI_Skoog.put("T", Double.valueOf(10.43D));
            this.Nterm_pI_Skoog.put("W", Double.valueOf(9.390000000000001D));
            this.Nterm_pI_Skoog.put("Y", Double.valueOf(9.109999999999999D));
            this.Nterm_pI_Skoog.put("V", Double.valueOf(9.619999999999999D));
            this.sideGroup_pI_Skoog.put("R", Double.valueOf(-12.48D));
            this.sideGroup_pI_Skoog.put("D", Double.valueOf(3.86D));
            this.sideGroup_pI_Skoog.put("C", Double.valueOf(8.33D));
            this.sideGroup_pI_Skoog.put("E", Double.valueOf(4.25D));
            this.sideGroup_pI_Skoog.put("H", Integer.valueOf(-6));
            this.sideGroup_pI_Skoog.put("K", Double.valueOf(-10.529999999999999D));
            this.sideGroup_pI_Skoog.put("Y", Double.valueOf(10.07D));

            this.Cterm_pI_expasy.put("A", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("R", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("N", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("D", Double.valueOf(4.55D));
            this.Cterm_pI_expasy.put("C", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("E", Double.valueOf(4.75D));
            this.Cterm_pI_expasy.put("Q", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("G", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("H", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("I", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("L", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("K", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("M", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("F", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("P", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("S", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("T", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("W", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("Y", Double.valueOf(3.55D));
            this.Cterm_pI_expasy.put("V", Double.valueOf(3.55D));

            this.Nterm_pI_expasy.put("A", Double.valueOf(7.59D));
            this.Nterm_pI_expasy.put("R", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("N", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("D", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("C", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("E", Double.valueOf(7.7D));
            this.Nterm_pI_expasy.put("Q", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("G", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("H", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("I", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("L", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("K", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("M", Double.valueOf(7.0D));
            this.Nterm_pI_expasy.put("F", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("P", Double.valueOf(8.359999999999999D));
            this.Nterm_pI_expasy.put("S", Double.valueOf(6.93D));
            this.Nterm_pI_expasy.put("T", Double.valueOf(6.82D));
            this.Nterm_pI_expasy.put("W", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("Y", Double.valueOf(7.5D));
            this.Nterm_pI_expasy.put("V", Double.valueOf(7.44D));

            this.sideGroup_pI_expasy.put("R", Double.valueOf(-12.0D));
            this.sideGroup_pI_expasy.put("D", Double.valueOf(4.05D));
            this.sideGroup_pI_expasy.put("C", Double.valueOf(9.0D));
            this.sideGroup_pI_expasy.put("E", Double.valueOf(4.45D));
            this.sideGroup_pI_expasy.put("H", Double.valueOf(-5.98D));
            this.sideGroup_pI_expasy.put("K", Double.valueOf(-10.0D));
            this.sideGroup_pI_expasy.put("Y", Double.valueOf(10.0D));

            this.Cterm_pI_calibrated.put("A", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("R", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("N", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("D", Double.valueOf(4.55D));
            this.Cterm_pI_calibrated.put("C", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("E", Double.valueOf(4.75D));
            this.Cterm_pI_calibrated.put("Q", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("G", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("H", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("I", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("L", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("K", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("M", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("F", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("P", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("S", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("T", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("W", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("Y", Double.valueOf(3.55D));
            this.Cterm_pI_calibrated.put("V", Double.valueOf(3.55D));

            this.Nterm_pI_calibrated.put("A", Double.valueOf(7.59D));
            this.Nterm_pI_calibrated.put("R", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("N", Double.valueOf(6.7D));
            this.Nterm_pI_calibrated.put("D", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("C", Double.valueOf(6.5D));
            this.Nterm_pI_calibrated.put("E", Double.valueOf(7.7D));
            this.Nterm_pI_calibrated.put("Q", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("G", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("H", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("I", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("L", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("K", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("M", Double.valueOf(7.0D));
            this.Nterm_pI_calibrated.put("F", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("P", Double.valueOf(8.359999999999999D));
            this.Nterm_pI_calibrated.put("S", Double.valueOf(6.93D));
            this.Nterm_pI_calibrated.put("T", Double.valueOf(6.82D));
            this.Nterm_pI_calibrated.put("W", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("Y", Double.valueOf(7.5D));
            this.Nterm_pI_calibrated.put("V", Double.valueOf(7.44D));

            this.sideGroup_pI_calibrated.put("R", Double.valueOf(-12.0D));
            this.sideGroup_pI_calibrated.put("D", Double.valueOf(4.05D));
            this.sideGroup_pI_calibrated.put("C", Double.valueOf(9.0D));
            this.sideGroup_pI_calibrated.put("E", Double.valueOf(4.45D));
            this.sideGroup_pI_calibrated.put("H", Double.valueOf(-5.98D));
            this.sideGroup_pI_calibrated.put("K", Double.valueOf(-10.0D));
            this.sideGroup_pI_calibrated.put("Y", Double.valueOf(10.0D));
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

        public void setCterm(AminoAcid Cterm) {
            this.Cterm = Cterm;
        }

        public AminoAcid getNterm() {
            return Nterm;
        }

        public void setNterm(AminoAcid Nterm) {
            this.Nterm = Nterm;
        }

        public ArrayList<AminoAcid> getSeqList() {
            return seqList;
        }

        public void setSeqList(ArrayList<AminoAcid> seqList) {
            this.seqList = seqList;
        }

        public void setSeqList(String aSequence) {
            this.seqList = parseSequence(aSequence);
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
            this.AAletter = new Character(aa);
            this.mod = new Character(mod);
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
