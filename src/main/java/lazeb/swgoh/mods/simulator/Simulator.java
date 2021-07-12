package lazeb.swgoh.mods.simulator;

import java.util.Arrays;

/**
 * Run a simulation against a particular strategy.
 *
 * The simulator runs as a closed system based on a given budget for mod energy and credits. No other outside
 * resources are taken into account.
 *
 * Each day during the simulation, if there is available energy then farm a new mod and level/slice it according
 * to the following sequence:
 *
 * 1. If not 5 dot, sell immediately
 * 2. If a 5 dot speed arrow, level it to 15 and keep it
 * 3. Level remaining mods to show the desired number of secondaries
 * 4. Continue leveling to 15 if speed is showing and we have a chance of meeting the target slice speed for that color
 *    (or in the case of gold mods, the target leveling speed)
 * 5. Slice until we don't meet the target slice speed for the color the mod is at
 * 6. Sell if we don't meet the minimum keep speed
 * 7. Repeat for a large number of trials
 */
class Simulator {

    private final Strategy strategy;
    private final Randomizer randomizer;

    Simulator(Strategy strategy, Randomizer randomizer) {
        this.strategy = strategy;
        this.randomizer = randomizer;
    }

    Results simulate(int numYears) {
        int numMonths = 12 * numYears;
        int numDays = 365 * numYears;
        long dailyEnergyBudget = (240 + strategy.modEnergyDailyRefreshes * 120);
        Resources resources = new Resources(dailyEnergyBudget, strategy.modCreditDailyBudget);
        Results results = new Results(numMonths, strategy, resources);

        for (int i = 0; i < numDays; i++) {
            resources.addDailyResources();
            while(resources.getEnergyBalance() > 0 && resources.getCreditBalance()  > 0) {
                Mod mod = new Mod(randomizer);
                processMod(results, resources, mod);
            }
            while(resources.getCreditBalance() >= Const.creditsStoreGold) {
                Mod mod = createStoreMod();
                processMod(results, resources, mod);
            }
        }

        return results;
    }

    private void processMod(Results results, Resources resources, Mod mod) {
        levelAndSlice(mod);
        boolean keep = keep(mod);
        resources.substractModInvestment(mod, keep);
        results.addMod(mod, keep);
    }

    private boolean keep(Mod mod) {
        return mod.getDot() == Mod.Dot.FIVE &&
                (
                        mod.getPrimary().getStat() == Mod.PrimaryStat.SPEED ||
                                mod.visibleSpeed() >= strategy.minKeepSpeed
                );
    }

    private Mod createStoreMod() {
        return new Mod(randomizer, Mod.Dot.FIVE, Mod.Color.GOLD, new Mod.Primary(Mod.PrimaryStat.NOTSPEED), Arrays.asList(
                new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, true),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true)
        ));
    }

    private void levelAndSlice(Mod mod) {
        if (mod.getDot() == Mod.Dot.FIVE) {
            if (mod.getPrimary().getStat() == Mod.PrimaryStat.SPEED) {
                for (int i = 1; i <= 5; i++) {
                    mod.increaseLevel();
                }
            } else {
                level(mod);
                if (mod.getLevel() == 15) {
                    slice(mod);
                }
            }
        }
    }

    private void level(Mod mod) {
        if (mod.getColor() == Mod.Color.GRAY) {
            for (int i = 1; i <= strategy.grayInitialSecondaries; i++) {
                mod.increaseLevel();
            }
        } else if (mod.getColor() == Mod.Color.GREEN) {
            for (int i = 1; i <= strategy.greenInitialSecondaries - 1; i++) {
                mod.increaseLevel();
            }
        } else if (mod.getColor() == Mod.Color.BLUE) {
            for (int i = 1; i <= strategy.blueInitialSecondaries - 2; i++) {
                mod.increaseLevel();
            }
        } else if (mod.getColor() == Mod.Color.PURPLE) {
            for (int i = 1; i <= strategy.purpleInitialSecondaries - 3; i++) {
                mod.increaseLevel();
            }
        }

        while (mod.getLevel() < 15 && getModPotentialSpeedFromLeveling(mod) >= getTargetLevelingSpeed(mod)) {
            mod.increaseLevel();
        }
    }

    private int getTargetLevelingSpeed(Mod mod) {
        if (mod.getColor() == Mod.Color.GRAY) {
            return strategy.graySliceSpeed;
        } else if (mod.getColor() == Mod.Color.GREEN) {
            return strategy.greenSliceSpeed;
        } else if (mod.getColor() == Mod.Color.BLUE) {
            return strategy.blueSliceSpeed;
        } else if (mod.getColor() == Mod.Color.PURPLE) {
            return strategy.purpleSliceSpeed;
        } else if (mod.getColor() == Mod.Color.GOLD) {
            return strategy.goldTargetLevel;
        } else {
            throw new IllegalArgumentException("Invalid mod color: " + mod);
        }
    }

    private int getModPotentialSpeedFromLeveling(Mod mod) {
        return mod.visibleSpeed() + mod.potentialSecondaryIncreasesFromLeveling() * 6;
    }

    private void slice(Mod mod) {
        while (mod.getColor() != Mod.Color.GOLD && mod.visibleSpeed() >= getSliceSpeed(mod.getColor())) {
            mod.slice();
        }
    }

    private int getSliceSpeed(Mod.Color color) {
        if (color == Mod.Color.GRAY) {
            return strategy.graySliceSpeed;
        } else if (color == Mod.Color.GREEN) {
            return strategy.greenSliceSpeed;
        } else if (color == Mod.Color.BLUE) {
            return strategy.blueSliceSpeed;
        } else if (color == Mod.Color.PURPLE) {
            return strategy.purpleSliceSpeed;
        } else {
            throw new IllegalArgumentException("Cannot get slice speed for color: " + color);
        }
    }
}
