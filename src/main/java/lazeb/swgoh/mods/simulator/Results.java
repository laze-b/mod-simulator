package lazeb.swgoh.mods.simulator;

public class Results implements Comparable<Results> {

    private long farmedMods;
    private long storeMods;
    private long keptMods;
    private long speed20plus;
    private long speed15to19;
    private long speed10to14;
    private long speed0to9;
    private long speedArrows;

    private final int numMonths;
    private final Resources resources;

    final Strategy strategy;

    Results(int numMonths, Strategy strategy, Resources resources) {
        this.numMonths = numMonths;
        this.strategy = strategy;
        this.resources = resources;
    }

    @Override
    public int compareTo(Results o) {
        return Double.compare(getScore(), o.getScore());
    }

    /**
     * This is our optimization function. How well a given simulation did is based on maxing out this metric.
     */
    private double getScore() {
        return getSpeed15Plus() * 1.0 / numMonths;
    }

    private long getSpeed15Plus() {
        return speed15to19 + speed20plus;
    }

    void addMod(Mod mod, boolean keep) {
        if(mod.isFromStore()) {
            storeMods++;
        } else {
            farmedMods++;
        }
        if (keep) {
            keptMods++;
            if (mod.getPrimary().getStat() == Mod.PrimaryStat.SPEED) {
                speedArrows++;
            } else if (mod.visibleSpeed() < 10) {
                speed0to9++;
            } else if (mod.visibleSpeed() < 15) {
                speed10to14++;
            } else if (mod.visibleSpeed() < 20) {
                speed15to19++;
            } else {
                speed20plus++;
            }
        }
    }

    void prettyPrint() {
        System.out.println(strategy);
        System.out.printf("Total mods farmed: %,d%n", farmedMods);
        System.out.printf("Total mods bought: %,d%n", storeMods);
        System.out.printf("Total mods kept:   %,d (%.2f percent)%n", keptMods, keptMods * 100.0 / (farmedMods + storeMods));
        System.out.println("--------------------------------");
        System.out.println("Monthly averages");
        System.out.printf("Mods farmed:  %5.3f%n", farmedMods * 1.0 / numMonths);
        System.out.printf("Mods bought:  %5.3f%n", storeMods * 1.0 / numMonths);
        System.out.printf("Mods kept:    %5.3f%n", keptMods * 1.0 / numMonths);
        System.out.printf("20+ speed:    %5.3f%n", speed20plus * 1.0 / numMonths);
        System.out.printf("15-19 speed:  %5.3f%n", speed15to19 * 1.0 / numMonths);
        System.out.printf("10-14 speed:  %5.3f%n", speed10to14 * 1.0 / numMonths);
        System.out.printf("0-9 speed:    %5.3f%n", speed0to9 * 1.0 / numMonths);
        System.out.printf("Speed arrows: %5.3f%n", speedArrows * 1.0 / numMonths);
        System.out.println("--------------------------------");
        System.out.printf("Overall score: %5.3f%n", getScore());
        System.out.println("--------------------------------");
    }

    @Override
    public String toString() {
        return "Results{" +
                "score=" + String.format("%5.2f", getScore()) +
                ", strategy=" + strategy +
                ", farmedMods=" + farmedMods +
                ", storeMods=" + storeMods +
                ", keptMods=" + keptMods +
                ", speed20plus=" + speed20plus +
                ", speed15to19=" + speed15to19 +
                ", speed10to14=" + speed10to14 +
                ", speed0to9=" + speed0to9 +
                ", speedArrows=" + speedArrows +
                ", numMonths=" + numMonths +
                ", resources=" + resources +
                '}';
    }
}
