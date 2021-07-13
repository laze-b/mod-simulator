package lazeb.swgoh.mods.simulator;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Results implements Comparable<Results> {

    private long farmedMods;
    private long storeMods;
    private long keptMods;
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
            // fitted function so speed 15=1, 20=2, 25=4
            score += count * getCurrentSpeedValue(speed);
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
                score += count * getCurrentAndPotentialSpeedValue(speed, speedIncreases);
            }
        }
        return score;
    }

    private static double getCurrentSpeedValue(int speed) {
        // current value: fitted function so speed 15=1, 20=3, 25=6
        return speed < 15 ? 0 : 1 + 0.08 * Math.pow(speed - 15, 2);
    }

    private static double getCurrentAndPotentialSpeedValue(int speed, int speedIncreases) {
        // potential value: same as current, but scaled down
        int potentialSpeed = speed + 4 * (4 - speedIncreases) + 2 * (5 - speedIncreases);
        double value = 0;
        if(speed >= 15) {
            value += getCurrentSpeedValue(speed);
            if(potentialSpeed > speed)
                value += 0.3 * getCurrentSpeedValue(potentialSpeed);
        }
        return value;
    }

    void addMod(Mod mod, boolean keep) {
        if (mod.isFromStore()) {
            storeMods++;
        } else {
            farmedMods++;
        }
        if (keep) {
            keptMods++;
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
                ", keptMods=" + keptMods +
                ", speedMap=" + speedMap +
                ", speedArrows=" + speedArrows +
                ", numMonths=" + numMonths +
                ", resources=" + resources +
                '}';
    }
}
