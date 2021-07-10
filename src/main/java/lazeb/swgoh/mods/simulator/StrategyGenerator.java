package lazeb.swgoh.mods.simulator;

import java.util.ArrayList;
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
        Supplier<RangeIterator> grayInitialSecondariesSupplier = () -> new RangeIterator(0, 4);
        Supplier<RangeIterator> greenInitialSecondariesSupplier = () -> new RangeIterator(1, 4);
        // Assume we always want to reveal all blue/purple secondaries
        Supplier<RangeIterator> blueInitialSecondariesSupplier = () -> new RangeIterator(4, 4);
        Supplier<RangeIterator> purpleInitialSecondariesSupplier = () -> new RangeIterator(4, 4);

        // make some assumptions on "good" target slice speeds to narrow the iterations
        Supplier<RangeIterator> graySliceSpeedSupplier = () -> new RangeIterator(3, 5);
        Supplier<RangeIterator> greenSliceSpeedSupplier = () -> new RangeIterator(5, 12);
        Supplier<RangeIterator> blueSliceSpeedSupplier = () -> new RangeIterator(7, 14);
        Supplier<RangeIterator> purpleSliceSpeedSupplier = () -> new RangeIterator(9, 14);

        // to limit the number of iterations, assume we're going for >= 15 speed
        Supplier<RangeIterator> goldLevelSpeedSupplier = () -> new RangeIterator(15, 15);
        Supplier<RangeIterator> minKeepSpeedSupplier = () -> new RangeIterator(15, 15);

        RangeIterator grayInitialRange = grayInitialSecondariesSupplier.get();
        while (grayInitialRange.hasNext()) {
            int grayInitial = grayInitialRange.next();
            RangeIterator greenInitialRange = greenInitialSecondariesSupplier.get();
            while (greenInitialRange.hasNext()) {
                int greenInitial = greenInitialRange.next();
                RangeIterator blueInitialRange = blueInitialSecondariesSupplier.get();
                while (blueInitialRange.hasNext()) {
                    int blueInitial = blueInitialRange.next();
                    RangeIterator purpleInitialRange = purpleInitialSecondariesSupplier.get();
                    while (purpleInitialRange.hasNext()) {
                        int purpleInitial = purpleInitialRange.next();
                        RangeIterator graySliceRange = graySliceSpeedSupplier.get();
                        while (graySliceRange.hasNext()) {
                            int graySlice = graySliceRange.next();
                            RangeIterator greenSliceRange = greenSliceSpeedSupplier.get();
                            while (greenSliceRange.hasNext()) {
                                int greenSlice = greenSliceRange.next();
                                RangeIterator blueSliceRange = blueSliceSpeedSupplier.get();
                                while (blueSliceRange.hasNext()) {
                                    int blueSlice = blueSliceRange.next();
                                    RangeIterator purpleSliceRange = purpleSliceSpeedSupplier.get();
                                    while (purpleSliceRange.hasNext()) {
                                        int purpleSlice = purpleSliceRange.next();
                                        RangeIterator goldLevelRange = goldLevelSpeedSupplier.get();
                                        while (goldLevelRange.hasNext()) {
                                            int goldLevel = goldLevelRange.next();
                                            RangeIterator minKeepRange = minKeepSpeedSupplier.get();
                                            while (minKeepRange.hasNext()) {
                                                int minKeep = minKeepRange.next();
                                                Strategy strategy = new Strategy(
                                                        grayInitial, greenInitial, blueInitial, purpleInitial,
                                                        graySlice, greenSlice, blueSlice, purpleSlice,
                                                        goldLevel, minKeep
                                                );
                                                try {
                                                    strategy.validate();
                                                    strategies.add(strategy);
                                                } catch (AssertionError e) {
                                                    // invalid strategy
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