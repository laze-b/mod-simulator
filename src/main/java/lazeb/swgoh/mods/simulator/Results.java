package lazeb.swgoh.mods.simulator;

public class Results implements Comparable<Results> {
    long energyBalance;
    long creditBalance;

    long farmedMods;
    long storeMods;
    long keptMods;
    long speed0to4;
    long speed5to9;
    long speed10to14;
    long speed15to19;
    long speed20plus;
    long speedArrows;

    private final int numMonths;
    private final int numWeeks;
    final Strategy strategy;

    Results(int numMonths, int numWeeks, Strategy strategy) {
        this.numMonths = numMonths;
        this.numWeeks = numWeeks;
        this.strategy = strategy;
    }

    @Override
    public int compareTo(Results o) {
        return Long.compare(getScore(), o.getScore());
    }

    /**
     * This is our optimization function. How well a given simulation did is based on maxing out this metric.
     */
    private long getScore() {
        return speed15to19 + speed20plus;
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
        System.out.printf("5-9 speed:    %5.3f%n", speed5to9 * 1.0 / numMonths);
        System.out.printf("<5 speed:     %5.3f%n", speed0to4 * 1.0 / numMonths);
        System.out.printf("Speed arrows: %5.3f%n", speedArrows * 1.0 / numMonths);
        System.out.println("--------------------------------");
        System.out.println("Weekly costs");
        System.out.printf("Remaining energy:  %,d%n", energyBalance / numWeeks);
        System.out.printf("Remaining credits: %,d%n", creditBalance / numWeeks);
        System.out.println("--------------------------------");
        System.out.printf("Overall score: %5.3f%n", getScore() * 1.0 / numMonths);
        System.out.println("--------------------------------");
    }

    @Override
    public String toString() {
        return "Results{" +
                "energyBalance=" + energyBalance +
                ", creditBalance=" + creditBalance +
                ", farmedMods=" + farmedMods +
                ", storeMods=" + storeMods +
                ", keptMods=" + keptMods +
                ", speed0to4=" + speed0to4 +
                ", speed5to9=" + speed5to9 +
                ", speed10to14=" + speed10to14 +
                ", speed15to19=" + speed15to19 +
                ", speed20plus=" + speed20plus +
                ", speedArrows=" + speedArrows +
                ", numMonths=" + numMonths +
                ", numWeeks=" + numWeeks +
                ", strategy=" + strategy +
                '}';
    }
}
