package lazeb.swgoh.mods.simulator;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Results implements Comparable<Results> {

    private long farmedMods;
    private long storeMods;
    private long keptMods;
    private Map<Integer, Integer> speedMap = new TreeMap<>();
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
    double getScore() {
//        return getSpeedGreaterThanEqual(15) * 1.0 / numMonths;
        return getWeightedSpeedValue() / numMonths;
    }

    long getSpeedGreaterThanEqual(int speed) {
        Set<Integer> keys = speedMap.keySet().stream().filter(k -> k >= speed).collect(Collectors.toSet());
        int count = 0;
        for(int key : keys) {
            count += speedMap.get(key);
        }
        return count;
    }

    double getWeightedSpeedValue() {
        double score = 0;
        for(int speed : speedMap.keySet()) {
            if(speed >= 15) {
                int count = speedMap.get(speed);
                // fitted function so speed 15=1, 20=2, 25=4
                score += count * (1 + 0.08 * Math.pow(speed - 15, 1.57));
            }
        }
        return score;
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
            } else {
                int speed = mod.visibleSpeed();
                int count = speedMap.computeIfAbsent(speed, k -> 0);
                speedMap.put(speed, count + 1);
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
                ", keptMods=" + keptMods +
                ", speedMap=" + speedMap +
                ", speedArrows=" + speedArrows +
                ", numMonths=" + numMonths +
                ", resources=" + resources +
                '}';
    }
}
