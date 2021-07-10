package lazeb.swgoh.mods.simulator;

class Strategy {

    // tweak these to match what you want to invest daily
    final int modEnergyDailyRefreshes = 3;
    final int modCreditDailyBudget = 1_000_000;

    // Number of secondaries to reveal per color (first decision point)
    final int grayInitialSecondaries; // [0-4]
    final int greenInitialSecondaries; // [1-4]
    final int blueInitialSecondaries; // [2-4]
    final int purpleInitialSecondaries; // [3-4]

    // amount of speed needed by color to slice a mod
    final int graySliceSpeed; // [3-5]
    final int greenSliceSpeed; // [3-12]
    final int blueSliceSpeed; // [3-18]
    final int purpleSliceSpeed; // [3-24]

    // minimum amount of speed to target when leveling a gold mod (will stop if at any point this can't be reached)
    final int goldTargetLevel; // [3-29]

    // the minimum speed on a mod in order to keep it, otherwise it will be sold
    final int minKeepSpeed; // [3-29]

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

    void validate() {
        assert modEnergyDailyRefreshes >= 0;
        assertBetween(0, 4, grayInitialSecondaries);
        assertBetween(1, 4, greenInitialSecondaries);
        assertBetween(2, 4, blueInitialSecondaries);
        assertBetween(3, 4, purpleInitialSecondaries);
        assertBetween(3, 5, graySliceSpeed);
        assertBetween(3, 12, greenSliceSpeed);
        assertBetween(3, 18, blueSliceSpeed);
        assertBetween(3, 24, purpleSliceSpeed);
        assertBetween(3, 29, goldTargetLevel);
        assertBetween(3, 29, minKeepSpeed);
        assert graySliceSpeed + 24 >= minKeepSpeed;
        assert graySliceSpeed + 6 >= greenSliceSpeed;
        assert graySliceSpeed <= greenSliceSpeed;
        assert greenSliceSpeed + 18 >= minKeepSpeed;
        assert greenSliceSpeed + 6 >= blueSliceSpeed;
        assert greenSliceSpeed <= blueSliceSpeed;
        assert blueSliceSpeed + 12 >= minKeepSpeed;
        assert blueSliceSpeed + 6 >= purpleSliceSpeed;
        assert blueSliceSpeed <= purpleSliceSpeed;
        assert purpleSliceSpeed + 6 >= minKeepSpeed;
        assert goldTargetLevel + 24 >= minKeepSpeed;
    }

    private void assertBetween(int a, int b, int val) {
        assert val >= a && val <= b;
    }


    @Override
    public String toString() {
        return "Strategy{" +
                "modEnergyDailyRefreshes=" + modEnergyDailyRefreshes +
                ", modCreditDailyBudget=" + modCreditDailyBudget +
                ", grayInitialSecondaries=" + grayInitialSecondaries +
                ", greenInitialSecondaries=" + greenInitialSecondaries +
                ", blueInitialSecondaries=" + blueInitialSecondaries +
                ", purpleInitialSecondaries=" + purpleInitialSecondaries +
                ", graySliceSpeed=" + graySliceSpeed +
                ", greenSliceSpeed=" + greenSliceSpeed +
                ", blueSliceSpeed=" + blueSliceSpeed +
                ", purpleSliceSpeed=" + purpleSliceSpeed +
                ", goldLevelSpeed=" + goldTargetLevel +
                ", minKeepSpeed=" + minKeepSpeed +
                '}';
    }


}