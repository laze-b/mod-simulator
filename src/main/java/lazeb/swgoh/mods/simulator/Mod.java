package lazeb.swgoh.mods.simulator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Mod {

    public enum Dot {
        LESSTHANFIVE, FIVE
    }

    public enum PrimaryStat {
        SPEED, NOTSPEED
    }

    public enum SecondaryStat {
        SPEED, NOTSPEED
    }

    public enum Color {
        GRAY, GREEN, BLUE, PURPLE, GOLD
    }

    public static class Primary {
        private final PrimaryStat stat;

        public Primary(PrimaryStat stat) {
            this.stat = stat;
        }

        public PrimaryStat getStat() {
            return stat;
        }
    }

    public static class Secondary {
        private final SecondaryStat stat;
        private int value;
        private boolean visible;

        public Secondary(SecondaryStat stat, int value, boolean visible) {
            this.stat = stat;
            this.value = value;
            this.visible = visible;
        }

        public SecondaryStat getStat() {
            return stat;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
    }

    private final Color originalColor;

    private final Randomizer randomizer;

    private Dot dot;
    private Color color;

    private Primary primary;
    private List<Secondary> secondaries;

    private int level = 1;
    private int energySpent;
    private int creditsSpent;

    /**
     * Create a mod from energy.
     */
    public Mod(Randomizer randomizer) {
        this.randomizer = randomizer;
        this.energySpent = Const.energyNewMod;
        Color c = randomizer.randomModColor();
        Primary p = randomizer.randomModPrimary();
        this.originalColor = c;
        this.creditsSpent = -7500;
        init(randomizer.randomModDot(), c, p, randomizer.randomModSecondaries(c, p.stat));
    }

    /**
     * Create a mod from the store.
     */
    public Mod(Randomizer randomizer, Dot dot, Color color, Primary primary, List<Secondary> secondaries) {
        this.randomizer = randomizer;
        if(color != Color.GOLD) {
            throw new IllegalArgumentException("Only gold mods supported from store");
        }
        this.creditsSpent = Const.creditsStoreGold;
        this.originalColor = color;
        init(dot, color, primary, secondaries);
    }

    private void init(Dot dot, Color color, Primary primary, List<Secondary> secondaries) {
        this.dot = dot;
        this.color = color;
        this.primary = primary;
        this.secondaries = secondaries;
    }

    public Color getOriginalColor() {
        return originalColor;
    }

    public Dot getDot() {
        return dot;
    }

    public Color getColor() {
        return color;
    }

    public Primary getPrimary() {
        return primary;
    }

    public List<Secondary> getSecondaries() {
        return secondaries;
    }

    public int getLevel() {
        return level;
    }

    public int getEnergySpent() {
        return energySpent;
    }

    public int getCreditsSpent() {
        return creditsSpent;
    }

    public void increaseLevel() {
        if(dot != Dot.FIVE) {
            throw new IllegalStateException("Leveling only supported on 5 dot mods");
        } else if(level == 1) {
            level = 3;
            creditsSpent += Const.creditsUpgradeLevel1to3;
            if(Color.GRAY == color || Color.GREEN == color || Color.BLUE == color || Color.PURPLE == color) {
                revealSecondary();
            } else {
                increaseSecondary();
            }
        } else if(level == 3) {
            level = 6;
            creditsSpent += Const.creditsUpgradeLevel3to6;
            if(Color.GRAY == color || Color.GREEN == color || Color.BLUE == color) {
                revealSecondary();
            } else {
                increaseSecondary();
            }
        } else if(level == 6) {
            level = 9;
            creditsSpent += Const.creditsUpgradeLevel6to9;
            if(Color.GRAY == color || Color.GREEN == color) {
                revealSecondary();
            } else {
                increaseSecondary();
            }
        } else if(level == 9) {
            level = 12;
            creditsSpent += Const.creditsUpgradeLevel9to12;
            if(Color.GRAY == color) {
                revealSecondary();
            } else {
                increaseSecondary();
            }
        } else if(level == 12) {
            level = 15;
            creditsSpent += Const.creditsUpgradeLevel12to15;
        } else {
            throw new IllegalStateException("Unsupported mod level: " + level);
        }
    }

    private void revealSecondary() {
        List<Secondary> hidden = secondaries.stream().filter(s -> !s.visible).collect(Collectors.toList());
        if(hidden.isEmpty()) {
            throw new IllegalStateException("Cannot reveal secondary since all are visible");
        } else {
            hidden.get(randomizer.randomSecondaryToReveal(hidden.size())).visible = true;
        }
    }

    private void increaseSecondary() {
        if(secondaries.stream().anyMatch(s -> !s.visible)) {
            throw new IllegalStateException("Cannot increase secondary since some are hidden");
        } else {
            Secondary secondaryToIncrease = secondaries.get(randomizer.randomSecondaryToIncrease());
            if(secondaryToIncrease.stat == SecondaryStat.SPEED) {
                secondaryToIncrease.value += randomizer.randomSecondarySpeedIncrease();
            }
        }
    }

    public void slice() {
        if(dot != Dot.FIVE) {
            throw new IllegalStateException("Slicing only supported on 5 dot mods");
        } else if(level < 15) {
            throw new IllegalStateException("Cannot slice mod at level: " + level);
        } else {
            if (color == Color.GRAY) {
                energySpent += Const.energyGraySlice;
                creditsSpent += Const.creditsGraySlice;
                color = Color.GREEN;
            } else if (color == Color.GREEN) {
                energySpent += Const.energyGreenSlice;
                creditsSpent += Const.creditsGreenSlice;
                color = Color.BLUE;
            } else if (color == Color.BLUE) {
                energySpent += Const.energyBlueSlice;
                creditsSpent += Const.creditsBlueSlice;
                color = Color.PURPLE;
            } else if (color == Color.PURPLE) {
                energySpent += Const.energyPurpleSlice;
                creditsSpent += Const.creditsPurpleSlice;
                color = Color.GOLD;
            } else {
                throw new IllegalStateException("Slicing not supported for color: " + color);
            }
            increaseSecondary();
        }
    }

    public int creditValueIfSold() {
        if(dot == Dot.LESSTHANFIVE) {
            return Const.creditsSellLessThan5Dot;
        } else if(level == 1 || level == 3 || level == 6) {
            return Const.creditsSellLevel1to6;
        } else if(level == 9) {
            return Const.creditsSellLevel9;
        } else if(level == 12) {
            return Const.creditsSellLevel12;
        } else if(level == 15) {
            return Const.creditsSellLevel15;
        } else {
            throw new IllegalStateException("Unable to compute credit sale value");
        }
    }

    public int visibleSpeed() {
        Optional<Secondary> speedSecondary = secondaries.stream().filter(s -> s.stat == SecondaryStat.SPEED && s.visible).findAny();
        return speedSecondary.map(secondary -> secondary.value).orElse(0);
    }

    public int potentialSecondaryIncreasesFromLeveling() {
        if (color == Color.GRAY || level >= 12) {
            return  0;
        } else {
            int l = level == 1 ? 0 : level;
            int increasesLeft = (12 - l) / 3;
            if (color == Color.GREEN) {
                return Math.min(increasesLeft, 1);
            } else if (color == Color.BLUE) {
                return Math.min(increasesLeft, 2);
            } else if (color == Color.PURPLE) {
                return Math.min(increasesLeft, 3);
            } else if (color == Color.GOLD) {
                return Math.min(increasesLeft, 4);
            } else {
                throw new IllegalArgumentException("Cannot get secondary potential for mod " + this);
            }
        }
    }
}
