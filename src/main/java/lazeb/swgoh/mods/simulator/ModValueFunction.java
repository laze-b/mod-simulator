package lazeb.swgoh.mods.simulator;

import java.util.function.BiFunction;

/**
 * Interface defining how a mod is valued.
 * <p/>
 * Inputs: speed, speedIncreases
 * <br>
 * Output: mod value
 */
@FunctionalInterface
interface ModValueFunction extends BiFunction<Double, Integer, Double> {
}
