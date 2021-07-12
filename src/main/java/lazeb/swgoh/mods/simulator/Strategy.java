package lazeb.swgoh.mods.simulator;

class Strategy {

    // Number of secondaries to reveal per color (first decision point)
    final int grayInitialSecondaries; // [0-4]
    final int greenInitialSecondaries; // [1-4]
    final int blueInitialSecondaries; // [2-4]
    final int purpleInitialSecondaries; // [3-4]

    // amount of speed needed by color to slice a mod
    final int graySliceSpeed; // [3-6]
    final int greenSliceSpeed; // [3-12]
    final int blueSliceSpeed; // [3-18]
    final int purpleSliceSpeed; // [3-24]

    // minimum amount of speed to target when leveling a gold mod (will stop if at any point this can't be reached)
    final int goldTargetLevel; // [3-29]

    // the minimum speed on a mod in order to keep it, otherwise it will be sold
    final int minKeepSpeed; // [3-29]

    // tweak these to match what you want to invest daily
    final int modEnergyDailyRefreshes = 3;
    final int modCreditDailyBudget = 1_000_000;

    Strategy(int grayInitialSecondaries, int greenInitialSecondaries, int blueInitialSecondaries, int purpleInitialSecondaries, int graySliceSpeed, int greenSliceSpeed, int blueSliceSpeed, int purpleSliceSpeed, int goldTargetLevel, int minKeepSpeed) {
        this.grayInitialSecondaries = grayInitialSecondaries;
        this.greenInitialSecondaries = greenInitialSecondaries;
        this.blueInitialSecondaries = blueInitialSecondaries;
        this.purpleInitialSecondaries = purpleInitialSecondaries;
        this.graySliceSpeed = graySliceSpeed;
        this.greenSliceSpeed = greenSliceSpeed;
        this.blueSliceSpeed = blueSliceSpeed;
        this.purpleSliceSpeed = purpleSliceSpeed;
        this.goldTargetLevel = goldTargetLevel;
        this.minKeepSpeed = minKeepSpeed;
    }

    boolean validate() {
        try {
            assertBetween(0, 4, grayInitialSecondaries);
            assertBetween(1, 4, greenInitialSecondaries);
            assertBetween(2, 4, blueInitialSecondaries);
            assertBetween(3, 4, purpleInitialSecondaries);
            assertBetween(3, 6, graySliceSpeed);
            assertBetween(3, 12, greenSliceSpeed);
            assertBetween(3, 18, blueSliceSpeed);
            assertBetween(3, 24, purpleSliceSpeed);
            assertBetween(3, 29, goldTargetLevel);
            assertBetween(3, 29, minKeepSpeed);
            assertTrue(graySliceSpeed + 24 >= minKeepSpeed);
            assertTrue(graySliceSpeed + 6 >= greenSliceSpeed);
            assertTrue(graySliceSpeed <= greenSliceSpeed);
            assertTrue(greenSliceSpeed + 18 >= minKeepSpeed);
            assertTrue(greenSliceSpeed + 6 >= blueSliceSpeed);
            assertTrue(greenSliceSpeed <= blueSliceSpeed);
            assertTrue(blueSliceSpeed + 12 >= minKeepSpeed);
            assertTrue(blueSliceSpeed + 6 >= purpleSliceSpeed);
            assertTrue(blueSliceSpeed <= purpleSliceSpeed);
            assertTrue(purpleSliceSpeed + 6 >= minKeepSpeed);
            assertTrue(goldTargetLevel + 24 >= minKeepSpeed);
            // never slice these, so don't level either
            if (graySliceSpeed == 6) {
                assertTrue(grayInitialSecondaries == 0);
            } else {
                assertTrue(grayInitialSecondaries > 0);
            }
            if (greenSliceSpeed == 12) {
                // never slice, so don't level either
                assertTrue(greenInitialSecondaries == 1);
            }
            if (blueSliceSpeed == 18) {
                // never slice, so don't level either
                assertTrue(blueInitialSecondaries == 2);
            }
            if (purpleSliceSpeed == 24) {
                // never slice, so don't level either
                assertTrue(purpleInitialSecondaries == 3);
            }
            assertTrue(modEnergyDailyRefreshes >= 0);
            assertTrue(modCreditDailyBudget >= 0);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    private void assertTrue(boolean b) {
        if (!b) {
            throw new AssertionError("Invalid strategy: " + this);
        }
    }

    private void assertBetween(int a, int b, int val) {
        assertTrue(val >= a && val <= b);
    }

    @Override
    public String toString() {
        return "Strategy{" +
                "grayInitialSecondaries=" + grayInitialSecondaries +
                ", greenInitialSecondaries=" + greenInitialSecondaries +
                ", blueInitialSecondaries=" + blueInitialSecondaries +
                ", purpleInitialSecondaries=" + purpleInitialSecondaries +
                ", graySliceSpeed=" + graySliceSpeed +
                ", greenSliceSpeed=" + greenSliceSpeed +
                ", blueSliceSpeed=" + blueSliceSpeed +
                ", purpleSliceSpeed=" + purpleSliceSpeed +
                ", goldTargetLevel=" + goldTargetLevel +
                ", minKeepSpeed=" + minKeepSpeed +
                ", modEnergyDailyRefreshes=" + modEnergyDailyRefreshes +
                ", modCreditDailyBudget=" + modCreditDailyBudget +
                '}';
    }
}