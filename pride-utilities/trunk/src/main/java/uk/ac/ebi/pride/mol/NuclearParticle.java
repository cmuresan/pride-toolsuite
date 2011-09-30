package uk.ac.ebi.pride.mol;

/**
 * NuclearParticle is an enum which stores all the nuclear particles.
 *
 * User: rwang
 * Date: 10-Aug-2010
 * Time: 09:48:18
 */
public enum NuclearParticle implements Mass {
    //todo: check the monomass and avgmass values.
    PROTON ("proton", "H", 1, 1.007276470, 1.007276470);

    private final String name;
    private final String formula;
    private final double charge;
    private final double monoMass;
    private final double avgMass;

    private NuclearParticle(String name, String formula, double charge,
                            double monoMass, double avgMass) {
        this.name = name;
        this.formula = formula;
        this.charge = charge;
        this.monoMass = monoMass;
        this.avgMass = avgMass;
    }

    public String getName() {
        return name;
    }

    public String getFormula() {
        return formula;
    }

    public double getCharge() {
        return charge;
    }

    public double getMonoMass() {
        return monoMass;
    }

    public double getAvgMass() {
        return avgMass;
    }
}