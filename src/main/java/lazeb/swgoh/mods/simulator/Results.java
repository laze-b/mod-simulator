package lazeb.swgoh.mods.simulator;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
//        return getWeightedSpeedValue() / numMonths;
        return getWeightedSpeedPlusPotentialValue() / numMonths;
    }

    /**
     * Simple value function - count of speed over threshold.
     */
    long getSpeedGreaterThanEqual(int speed) {
        Set<Integer> speeds = speedMap.keySet().stream().filter(k -> k >= speed).collect(Collectors.toSet());
        int count = 0;
        for (int nextSpeed : speeds) {
            count += speedMap.get(nextSpeed).values().stream().mapToInt(i -> i).sum();
        }
        return count;
    }

    /**
     * More complex value function taking individual speeds into account.
     */
    double getWeightedSpeedValue() {
        double score = 0;
        for (int speed : speedMap.keySet()) {
            int count = speedMap.get(speed).values().stream().mapToInt(i -> i).sum();
            score += count * getSpeedValue(speed, strategy.minKeepSpeed);
        }
        return score;
    }

    /**
     * Even more complex value function taking speed + speed potential into account.
     */
    double getWeightedSpeedPlusPotentialValue() {
        double score = 0;
        for (int speed : speedMap.keySet()) {
            Map<Integer, Integer> speedIncreaseMap = speedMap.get(speed);
            for (int speedIncreases : speedIncreaseMap.keySet()) {
                int count = speedIncreaseMap.get(speedIncreases);
                score += count * getCurrentAndPotentialSpeedValue(speed, speedIncreases, strategy.minKeepSpeed);
            }
        }
        return score;
    }

    private static double getSpeedValue(double speed, int minSpeedWithValue) {
        if (speed < minSpeedWithValue) {
            return 0;
        } else {
            return Math.pow(speed - minSpeedWithValue + 1, 3);
        }
    }

    private static double getCurrentAndPotentialSpeedValue(int speed, int speedIncreases, int minSpeedWithValue) {
        double value = getSpeedValue(speed, minSpeedWithValue);
        // potential value: probability of speed slices * expected speed, and discounted for cost of mats
        // has to meet threshold of 17/3 or 18/4 to consider
        double addedValue = 0;
        double discount = 0.75;
        if(speedIncreases == 4 && speed >= 18) {
            double probSpeedSlice = 0.68;
            double avgSpeedIncrease = probSpeedSlice * Const.avgSpeedSlice();
            addedValue = getSpeedValue(speed + avgSpeedIncrease, minSpeedWithValue) - value;
        } else if (speedIncreases == 3 && speed >= 17) {
            double probSingleSpeedSlice = 0.42;
            double probDoubleSpeedSlice = 0.26;
            double avgSpeedIncrease = probSingleSpeedSlice * Const.avgSpeedSlice() + probDoubleSpeedSlice * Const.avgSpeedSlice() * 2;
            addedValue = getSpeedValue(speed + avgSpeedIncrease, minSpeedWithValue) - value;
        }
        return value + discount * addedValue;
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
