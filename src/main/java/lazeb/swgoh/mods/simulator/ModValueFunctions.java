package lazeb.swgoh.mods.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class ModValueFunctions {

    /**
     * Construct a mod value function that factors in both current and future value.
     */
    static ModValueFunction getCurrentPlusPotentialValueFunction(ModValueFunction currentValueFunction, double futureDiscount) {
        return (speed, speedIncreases) -> {
            double currentValue = currentValueFunction.apply(speed, speedIncreases);
            // potential value: probability of speed slices * expected speed, and discounted for cost of mats
            // has to meet threshold of 17/3 or 18/4 to consider
            double avgSpeedIncrease = 0;
            if (speedIncreases == 4 && speed >= 18) {
                double probSpeedSlice = 0.68;
                avgSpeedIncrease = probSpeedSlice * Const.avgSpeedSlice();
            } else if (speedIncreases == 3 && speed >= 17) {
                double probSingleSpeedSlice = 0.42;
                double probDoubleSpeedSlice = 0.26;
                avgSpeedIncrease = probSingleSpeedSlice * Const.avgSpeedSlice() + probDoubleSpeedSlice * Const.avgSpeedSlice() * 2;
            }
            double addedValue = currentValueFunction.apply(speed + avgSpeedIncrease, speedIncreases) - currentValue;
            return currentValue + futureDiscount * addedValue;
        };
    }

    static void printModValues(String functionName, ModValueFunction modValueFunction) {
        class V {
            private int speed;
            private int increases;
            private double value;

            private V(int speed, int increases, double value) {
                this.speed = speed;
                this.increases = increases;
                this.value = value;
            }
        }
        List<V> all = new ArrayList<>();
        RangeIterator speeds = new RangeIterator(0, 29);
        System.out.println("Value function: " + functionName);
        System.out.println("| Speed | Increases |    Value    |");
        System.out.println("| ----- | --------- | ----------- |");
        while (speeds.hasNext()) {
            int speed = speeds.next();
            List<Integer> increases = new ArrayList<>();
            if (speed == 0) {
                increases.add(0);
            } else {
                if (speed <= 5)
                    increases.add(1);
                if (speed >= 6 && speed <= 11) {
                    increases.add(2);
                }
                if (speed >= 9 && speed <= 17) {
                    increases.add(3);
                }
                if (speed >= 12 && speed <= 23) {
                    increases.add(4);
                }
                if (speed >= 15 && speed <= 29) {
                    increases.add(5);
                }
            }
            for (int increase : increases) {
                all.add(new V(speed, increase, modValueFunction.apply((double) speed, increase)));
            }
        }
        all.sort(Comparator.comparingDouble(a -> a.value));
        for (V v : all) {
            System.out.println(String.format("|  %2d   |     %1d     |   %6.0f    |", v.speed, v.increases, v.value));
        }
    }
}