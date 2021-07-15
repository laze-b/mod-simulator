package lazeb.swgoh.mods.simulator

import spock.lang.Specification

class RandomizerSpec extends Specification {

    Randomizer randomizer = new Randomizer()

    def "random mod dot has the right distribution"() {
        when:
        List<Mod.Dot> dots = []
        int iterations = 1_000_000
        iterations.times {
            dots << randomizer.randomModDot()
        }
        double pct5Dot = dots.findAll { it == Mod.Dot.FIVE }.size() * 1.0 / dots.size()

        then:
        pct5Dot > 0.79
        pct5Dot < 0.81
    }

    def "random mod color has the right distribution"() {
        when:
        List<Mod.Color> colors = []
        int iterations = 1_000_000
        iterations.times {
            colors << randomizer.randomModColor()
        }
        double pctGray = colors.findAll { it == Mod.Color.GRAY }.size() * 1.0 / colors.size()
        double pctGreen = colors.findAll { it == Mod.Color.GREEN }.size() * 1.0 / colors.size()
        double pctBlue = colors.findAll { it == Mod.Color.BLUE }.size() * 1.0 / colors.size()
        double pctPurple = colors.findAll { it == Mod.Color.PURPLE }.size() * 1.0 / colors.size()
        double pctGold = colors.findAll { it == Mod.Color.GOLD }.size() * 1.0 / colors.size()

        then:
        pctGray > 0.64
        pctGray < 0.65
        pctGreen > 0.18
        pctGreen < 0.20
        pctBlue > 0.09
        pctBlue < 0.11
        pctPurple > 0.03
        pctPurple < 0.05
        pctGold > 0.01
        pctGold < 0.03
    }

    def "random mod primary has the right distribution"() {
        when:
        List<Mod.PrimaryStat> primaries = []
        int iterations = 1_000_000
        iterations.times {
            primaries << randomizer.randomModPrimary().stat
        }
        double pctSpeed = primaries.findAll { it == Mod.PrimaryStat.SPEED }.size() * 1.0 / primaries.size()

        then:
        pctSpeed > 0.005
        pctSpeed < 0.015
    }

    def "random secondaries have the right number visible per color"() {
        when:
        List<Mod.Secondary> secondaries = randomizer.randomModSecondaries(color, primaryStat)

        then:
        secondaries.size() == 4
        secondaries.findAll { it.visible }.size() == visible

        where:
        color            | primaryStat              | visible
        Mod.Color.GRAY   | Mod.PrimaryStat.NOTSPEED | 0
        Mod.Color.GREEN  | Mod.PrimaryStat.NOTSPEED | 1
        Mod.Color.BLUE   | Mod.PrimaryStat.NOTSPEED | 2
        Mod.Color.PURPLE | Mod.PrimaryStat.NOTSPEED | 3
        Mod.Color.GOLD   | Mod.PrimaryStat.NOTSPEED | 4
        Mod.Color.GRAY   | Mod.PrimaryStat.SPEED    | 0
        Mod.Color.GREEN  | Mod.PrimaryStat.SPEED    | 1
        Mod.Color.BLUE   | Mod.PrimaryStat.SPEED    | 2
        Mod.Color.PURPLE | Mod.PrimaryStat.SPEED    | 3
        Mod.Color.GOLD   | Mod.PrimaryStat.SPEED    | 4
    }

    def "random secondaries do not have speed when the primary has speed"() {
        when:
        List<Mod.Secondary> secondaries1 = randomizer.randomModSecondaries(Mod.Color.GRAY, Mod.PrimaryStat.SPEED)
        List<Mod.Secondary> secondaries2 = randomizer.randomModSecondaries(Mod.Color.GREEN, Mod.PrimaryStat.SPEED)
        List<Mod.Secondary> secondaries3 = randomizer.randomModSecondaries(Mod.Color.BLUE, Mod.PrimaryStat.SPEED)
        List<Mod.Secondary> secondaries4 = randomizer.randomModSecondaries(Mod.Color.PURPLE, Mod.PrimaryStat.SPEED)
        List<Mod.Secondary> secondaries5 = randomizer.randomModSecondaries(Mod.Color.GOLD, Mod.PrimaryStat.SPEED)

        then:
        secondaries1.size() == 4
        secondaries1.findAll { it.stat == Mod.SecondaryStat.SPEED }.isEmpty()
        secondaries2.size() == 4
        secondaries2.findAll { it.stat == Mod.SecondaryStat.SPEED }.isEmpty()
        secondaries3.size() == 4
        secondaries3.findAll { it.stat == Mod.SecondaryStat.SPEED }.isEmpty()
        secondaries4.size() == 4
        secondaries4.findAll { it.stat == Mod.SecondaryStat.SPEED }.isEmpty()
        secondaries5.size() == 4
        secondaries5.findAll { it.stat == Mod.SecondaryStat.SPEED }.isEmpty()

        where:
        iterations << (1..10000)
    }

    def "random secondaries have the right distribution of speed"() {
        when:
        int iterations = 1_000_000
        List<List<Mod.Secondary>> secondariesList = []
        iterations.times {
            secondariesList << randomizer.randomModSecondaries(color, Mod.PrimaryStat.NOTSPEED)
        }
        int withSpeed = secondariesList.findAll { secondaries -> secondaries.find { s -> s.stat == Mod.SecondaryStat.SPEED } }.size()
        int withVisibleSpeed = secondariesList.findAll { secondaries -> secondaries.find { s -> s.stat == Mod.SecondaryStat.SPEED && s.visible } }.size()
        int with3Speed = secondariesList.findAll { secondaries -> secondaries.find { s -> s.stat == Mod.SecondaryStat.SPEED && s.value == 3 } }.size()
        int with4Speed = secondariesList.findAll { secondaries -> secondaries.find { s -> s.stat == Mod.SecondaryStat.SPEED && s.value == 4 } }.size()
        int with5Speed = secondariesList.findAll { secondaries -> secondaries.find { s -> s.stat == Mod.SecondaryStat.SPEED && s.value == 5 } }.size()
        double pctWithSpeed = withSpeed * 1.0 / iterations
        double pctSpeedVisible = withVisibleSpeed * 1.0 / withSpeed
        double pctSpeed3 = with3Speed * 1.0 / withSpeed
        double pctSpeed4 = with4Speed * 1.0 / withSpeed
        double pctSpeed5 = with5Speed * 1.0 / withSpeed

        then:
        pctWithSpeed > 0.36
        pctWithSpeed < 0.37
        pctSpeedVisible >= pctSpeedVisibleLower
        pctSpeedVisible <= pctSpeedVisibleUpper
        pctSpeed3 > 0.30
        pctSpeed3 < 0.31
        pctSpeed4 > 0.34
        pctSpeed4 < 0.35
        pctSpeed5 > 0.35
        pctSpeed5 < 0.36

        where:
        color            | pctSpeedVisibleLower | pctSpeedVisibleUpper
        Mod.Color.GRAY   | 0                    | 0
        Mod.Color.GREEN  | 0.245                | 0.255
        Mod.Color.BLUE   | 0.45                 | 0.55
        Mod.Color.PURPLE | 0.745                | 0.755
        Mod.Color.GOLD   | 1                    | 1
    }

    def "random secondary speed increase has the right distribution"() {
        when:
        List<Integer> speeds = []
        int iterations = 1_000_000
        iterations.times {
            speeds << randomizer.randomSecondarySpeedIncrease()
        }
        double pct3Speed = speeds.findAll { it == 3 }.size() * 1.0 / speeds.size()
        double pct4Speed = speeds.findAll { it == 4 }.size() * 1.0 / speeds.size()
        double pct5Speed = speeds.findAll { it == 5 }.size() * 1.0 / speeds.size()
        double pct6Speed = speeds.findAll { it == 6 }.size() * 1.0 / speeds.size()

        then:
        pct3Speed > 0.18
        pct3Speed < 0.20
        pct4Speed > 0.34
        pct4Speed < 0.36
        pct5Speed > 0.37
        pct5Speed < 0.39
        pct6Speed > 0.07
        pct6Speed < 0.09
    }
}
