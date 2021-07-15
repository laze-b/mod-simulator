package lazeb.swgoh.mods.simulator;

interface Const {
    int energyNewMod = 16;
    int energyGraySlice = 102;
    int energyGreenSlice = 204;
    int energyBlueSlice = 356;
    int energyPurpleSlice = 508;

    // slice cost is cost to slice minus the credits gained from farming slicing mats
    int creditsGraySlice = 18000 - energyGraySlice * 100;
    int creditsGreenSlice = 36000 - energyGreenSlice * 100;
    int creditsBlueSlice = 63000 - energyBlueSlice * 100;
    int creditsPurpleSlice = 90000 - energyPurpleSlice * 100;
    // when we farm a new mod, we get a small amount of credits
    int creditsNewModFarm = 7500;
    int creditsSellLessThan5Dot = 4900;
    int creditsSellLevel1to6 = 9500;
    int creditsSellLevel9 = 14900;
    int creditsSellLevel12 = 33800;
    int creditsSellLevel15 = 97200;
    int creditsUpgradeLevel1to3 = 6900;
    int creditsUpgradeLevel3to6 = 11500;
    int creditsUpgradeLevel6to9 = 19500;
    int creditsUpgradeLevel9to12 = 48300;
    int creditsUpgradeLevel12to15 = 162200;
    int creditsStoreGold = 3910000;

    double pctLessThan5Dot = 0.20;
    double pctSpeedArrow = 0.01;
    double pctSpeedSecondary = 4.0/11;
    double pctSpeedSecondaryIncrease = 0.25;
    
    double pctGray = 0.641;
    double pctGreen = 0.192;
    double pctBlue = 0.103;
    double pctPurple = 0.041;
    double pctGold = 0.023;
    
    double pctSpeed3Initial = 0.304;
    double pctSpeed4Initial = 0.341;
    double pctSpeed5Initial = 0.355;
    double pctSpeed3Slice = 0.189;
    double pctSpeed4Slice = 0.351;
    double pctSpeed5Slice = 0.379;
    double pctSpeed6Slice = 0.081;

    static double avgSpeedSlice() {
        return 3 * pctSpeed3Slice + 4 * pctSpeed4Slice + 5 * pctSpeed5Slice + 6 * pctSpeed6Slice;
    }
}
