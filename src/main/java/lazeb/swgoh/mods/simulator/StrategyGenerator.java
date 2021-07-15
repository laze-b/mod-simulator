package lazeb.swgoh.mods.simulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

class StrategyGenerator {

    private List<Strategy> strategies;
    private int currentStrategy = 0;

    StrategyGenerator() {
        generateAllStrategies();
    }

    StrategyGenerator(List<Strategy> strategies) {
        this.strategies = strategies;
    }

    private void generateAllStrategies() {
        strategies = new ArrayList<>();
        Supplier<Iterator<Integer>> grayInitialSecondariesSupplier = () -> new RangeIterator(0, 4);
        Supplier<Iterator<Integer>> greenInitialSecondariesSupplier = () -> new RangeIterator(1, 4);
        // Assume we always want to reveal all blue/purple secondaries
        Supplier<Iterator<Integer>> blueInitialSecondariesSupplier = () -> new RangeIterator(4, 4);
        Supplier<Iterator<Integer>> purpleInitialSecondariesSupplier = () -> new RangeIterator(4, 4);

        // make some assumptions on "good" target slice speeds to narrow the iterations
        Supplier<Iterator<Integer>> graySliceSpeedSupplier = () -> new RangeIterator(3, 6);
        Supplier<Iterator<Integer>> greenSliceSpeedSupplier = () -> new RangeIterator(5, 12);
        Supplier<Iterator<Integer>> blueSliceSpeedSupplier = () -> new RangeIterator(7, 14);
        Supplier<Iterator<Integer>> purpleSliceSpeedSupplier = () -> new RangeIterator(9, 14);

        // to limit the number of iterations, assume we're going for >= 15 speed
        Supplier<Iterator<Integer>> goldLevelSpeedSupplier = () -> new RangeIterator(12, 12);
        Supplier<Iterator<Integer>> minKeepSpeedSupplier = () -> new RangeIterator(12, 12);

        Iterator<Integer> grayInitialRange = grayInitialSecondariesSupplier.get();
        while (grayInitialRange.hasNext()) {
            int grayInitial = grayInitialRange.next();
            Iterator<Integer> greenInitialRange = greenInitialSecondariesSupplier.get();
            while (greenInitialRange.hasNext()) {
                int greenInitial = greenInitialRange.next();
                Iterator<Integer> blueInitialRange = blueInitialSecondariesSupplier.get();
                while (blueInitialRange.hasNext()) {
                    int blueInitial = blueInitialRange.next();
                    Iterator<Integer> purpleInitialRange = purpleInitialSecondariesSupplier.get();
                    while (purpleInitialRange.hasNext()) {
                        int purpleInitial = purpleInitialRange.next();
                        Iterator<Integer> graySliceRange = graySliceSpeedSupplier.get();
                        while (graySliceRange.hasNext()) {
                            int graySlice = graySliceRange.next();
                            Iterator<Integer> greenSliceRange = greenSliceSpeedSupplier.get();
                            while (greenSliceRange.hasNext()) {
                                int greenSlice = greenSliceRange.next();
                                Iterator<Integer> blueSliceRange = blueSliceSpeedSupplier.get();
                                while (blueSliceRange.hasNext()) {
                                    int blueSlice = blueSliceRange.next();
                                    Iterator<Integer> purpleSliceRange = purpleSliceSpeedSupplier.get();
                                    while (purpleSliceRange.hasNext()) {
                                        int purpleSlice = purpleSliceRange.next();
                                        Iterator<Integer> goldLevelRange = goldLevelSpeedSupplier.get();
                                        while (goldLevelRange.hasNext()) {
                                            int goldLevel = goldLevelRange.next();
                                            Iterator<Integer> minKeepRange = minKeepSpeedSupplier.get();
                                            while (minKeepRange.hasNext()) {
                                                int minKeep = minKeepRange.next();
                                                Strategy strategy = new Strategy(
                                                        grayInitial, greenInitial, blueInitial, purpleInitial,
                                                        graySlice, greenSlice, blueSlice, purpleSlice,
                                                        goldLevel, minKeep
                                                );
                                                if(strategy.validate()) {
                                                    strategies.add(strategy);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    int getCount() {
        return strategies.size();
    }

    Strategy getNextStrategy() {
        if(currentStrategy >= strategies.size()) {
            return null;
        } else {
            return strategies.get(currentStrategy++);
        }
    }
}
