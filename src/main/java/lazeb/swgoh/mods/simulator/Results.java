package lazeb.swgoh.mods.simulator;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class Results implements Comparable<Results> {

    private long farmedMods;
    private long storeMods;
    private long keptFarmedMods;
    private long keptStoreMods;
    // [key=speed, value=[key=speedIncreases, value=numMods]]
    private Map<Integer, Map<Integer, Integer>> speedMap = new TreeMap<>();
    private long speedArrows;

    private final int numMonths;
    private final Resources resources;
    // function that takes in speedMap and returns valuation
    private final ModValueFunction modValueFunction;

    final Strategy strategy;

    Results(int numMonths, Strategy strategy, Resources resources, ModValueFunction modValueFunction) {
        this.numMonths = numMonths;
        this.strategy = strategy;
        this.resources = resources;
        this.modValueFunction = modValueFunction;
    }

    @Override
    public int compareTo(Results o) {
        return Double.compare(getScore(), o.getScore());
    }

    /**
     * This is our optimization function. How well a given simulation did is based on maxing out this metric.
     */
    double getScore() {
        double score = 0;
        for (int speed : speedMap.keySet()) {
            Map<Integer, Integer> speedIncreaseMap = speedMap.get(speed);
            for (int speedIncreases : speedIncreaseMap.keySet()) {
                int count = speedIncreaseMap.get(speedIncreases);
                score += count * modValueFunction.apply((double) speed, speedIncreases);
            }
        }
        return score / numMonths;
    }

    void addMod(Mod mod, boolean keep) {
        if (mod.isFromStore()) {
            storeMods++;
        } else {
            farmedMods++;
        }
        if (keep) {
            if (mod.isFromStore()) {
                keptStoreMods++;
            } else {
                keptFarmedMods++;
            }
            if (mod.getPrimary().getStat() == Mod.PrimaryStat.SPEED) {
                speedArrows++;
            } else {
                Optional<Mod.Secondary> speedSecondary = mod.visibleSpeedSecondary();
                int speed = speedSecondary.map(Mod.Secondary::getValue).orElse(0);
                int speedIncreases = speedSecondary.map(Mod.Secondary::getCount).orElse(0);
                Map<Integer, Integer> speedHitMap = speedMap.computeIfAbsent(speed, k -> new TreeMap<>());
                speedMap.put(speed, speedHitMap);
                int numMods = speedHitMap.computeIfAbsent(speedIncreases, k -> 0);
                speedHitMap.put(speedIncreases, numMods + 1);
            }
        }
    }

    @Override
    public String toString() {
        return "Results{" +
                "score=" + String.format("%5.2f", getScore()) +
                ", strategy=" + strategy +
                ", farmedMods=" + farmedMods +
                ", storeMods=" + storeMods +
                ", keptFarmedMods=" + keptFarmedMods +
                ", keptStoreMods=" + keptStoreMods +
                ", speedMap=" + speedMap +
                ", speedArrows=" + speedArrows +
                ", numMonths=" + numMonths +
                ", resources=" + resources +
                '}';
    }
}
