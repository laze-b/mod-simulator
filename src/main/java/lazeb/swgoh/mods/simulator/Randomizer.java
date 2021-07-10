package lazeb.swgoh.mods.simulator;

import lazeb.swgoh.mods.simulator.Mod.Secondary;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class Randomizer {

    private SplittableRandom random = new SplittableRandom();

    public Mod.Dot randomModDot() {
        return random.nextDouble() < Const.pctLessThan5Dot ? Mod.Dot.LESSTHANFIVE : Mod.Dot.FIVE;
    }

    public Mod.Color randomModColor() {
        double randomColor = random.nextDouble();
        if (randomColor < Const.pctGray) {
            return Mod.Color.GRAY;
        } else if (randomColor < Const.pctGray + Const.pctGreen) {
            return Mod.Color.GREEN;
        } else if (randomColor < Const.pctGray + Const.pctGreen + Const.pctBlue) {
            return Mod.Color.BLUE;
        } else if (randomColor < Const.pctGray + Const.pctGreen + Const.pctBlue + Const.pctPurple) {
            return Mod.Color.PURPLE;
        } else {
            return Mod.Color.GOLD;
        }
    }

    public Mod.Primary randomModPrimary() {
        return random.nextDouble() < Const.pctSpeedArrow ?
                new Mod.Primary(Mod.PrimaryStat.SPEED) :
                new Mod.Primary(Mod.PrimaryStat.NOTSPEED);
    }

    public List<Secondary> randomModSecondaries(Mod.Color color, Mod.PrimaryStat primaryStat) {
        List<Secondary> initialSecondaries = new ArrayList<>();
        if (primaryStat != Mod.PrimaryStat.SPEED && random.nextDouble() < Const.pctSpeedSecondary) {
            double initialSpeed = random.nextDouble();
            if (initialSpeed < Const.pctSpeed3Initial) {
                initialSecondaries.add(new Secondary(Mod.SecondaryStat.SPEED, 3, false));
            } else if (initialSpeed < Const.pctSpeed3Initial + Const.pctSpeed4Initial) {
                initialSecondaries.add(new Secondary(Mod.SecondaryStat.SPEED, 4, false));
            } else {
                initialSecondaries.add(new Secondary(Mod.SecondaryStat.SPEED, 5, false));
            }
        }
        while (initialSecondaries.size() < 4) {
            initialSecondaries.add(new Secondary(Mod.SecondaryStat.NOTSPEED, 0, false));
        }
        List<Secondary> finalSecondaries = new ArrayList<>();
        int secondariesToReveal = getInitialSecondariesShowing(color);
        for (int i = 0; i < secondariesToReveal; i++) {
            Secondary secondary = initialSecondaries.remove(randomSecondaryToReveal(initialSecondaries.size()));
            secondary.setVisible(true);
            finalSecondaries.add(secondary);
        }
        finalSecondaries.addAll(initialSecondaries);
        return finalSecondaries;
    }

    private int getInitialSecondariesShowing(Mod.Color color) {
        switch (color) {
            case GRAY:
                return 0;
            case GREEN:
                return 1;
            case BLUE:
                return 2;
            case PURPLE:
                return 3;
            case GOLD:
                return 4;
            default:
                throw new IllegalArgumentException("Invalid color: $color");
        }
    }

    public int randomSecondarySpeedIncrease() {
        double value = random.nextDouble();
        if (value < Const.pctSpeed3Slice) {
            return 3;
        } else if (value < Const.pctSpeed3Slice + Const.pctSpeed4Slice) {
            return 4;
        } else if (value < Const.pctSpeed3Slice + Const.pctSpeed4Slice + Const.pctSpeed5Slice) {
            return 5;
        } else {
            return 6;
        }
    }

    public int randomSecondaryToReveal(int hiddenCount) {
        return random.nextInt(hiddenCount);
    }

    public int randomSecondaryToIncrease() {
        return random.nextInt(4);
    }
}
