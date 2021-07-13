package lazeb.swgoh.mods.simulator

import spock.lang.Specification
import spock.lang.Unroll

class ResultsSpec extends Specification {

    Randomizer randomizer = Stub(Randomizer)


    def "test flat speed cutoff"() {
        given:
        Results results = new Results(
                10,
                new Strategy(0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0),
                new Resources(100, 100000)
        )

        when:
        results.addMod(getMod(5), false)

        then:
        results.getSpeedGreaterThanEqual(1) == 0

        when:
        results.addMod(getMod(6), true)

        then:
        results.getSpeedGreaterThanEqual(5) == 1

        when:
        results.addMod(getMod(5), true)

        then:
        results.getSpeedGreaterThanEqual(5) == 2
    }

    def "test weighted speed value"() {
        given:
        Results results = new Results(
                10,
                new Strategy(0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0),
                new Resources(100, 100000)
        )

        when:
        results.addMod(getMod(15), false)

        then:
        results.getWeightedSpeedValue() == 0

        when:
        results.addMod(getMod(15), true)

        then:
        results.getWeightedSpeedValue() == 1.0

        when:
        results.addMod(getMod(20), true)

        then:
        results.getWeightedSpeedValue() == 4.0

        when:
        results.addMod(getMod(25), true)

        then:
        results.getWeightedSpeedValue() == 13.0

    }

    def "test weighted speed + potential value"() {
        given:
        Results results = new Results(
                10,
                new Strategy(0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0),
                new Resources(100, 100000)
        )

        when:
        results.addMod(getMod(15, 5), false)

        then:
        results.getWeightedSpeedPlusPotentialValue() == 0

        when:
        results.addMod(getMod(15, 5), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() == 1.0

        when:
        results.addMod(getMod(15, 5), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() == 2.0

        when:
        results.addMod(getMod(15, 3), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() > 4.83 && results.getWeightedSpeedPlusPotentialValue() < 4.84

        when:
        results.addMod(getMod(20, 4), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() > 9.31 && results.getWeightedSpeedPlusPotentialValue() < 9.32

        when:
        results.addMod(getMod(25, 5), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() > 18.31 && results.getWeightedSpeedPlusPotentialValue() < 18.32
    }

    @Unroll
    def "speed: #speed current speed value"() {
        expect:
        Results.getCurrentSpeedValue(speed) >= lowerBound
        Results.getCurrentSpeedValue(speed) <= upperBound

        where:
        speed | lowerBound | upperBound
        14    | 0          | 0
        15    | 1          | 1
        16    | 1.07       | 1.08
        17    | 1.32       | 1.33
        18    | 1.72       | 1.72
        19    | 2.28       | 2.29
        20    | 3.00       | 3.01
        21    | 3.88       | 3.88
        22    | 4.92       | 4.92
        23    | 6.12       | 6.12
        24    | 7.48       | 7.48
        25    | 9.00       | 9.00
        26    | 10.68      | 10.68
        27    | 12.52      | 12.52
        28    | 14.52      | 14.52
        29    | 16.68      | 16.68
    }

    @Unroll
    def "speed: #speed, increases: #increases current + potential speed value"() {
        expect:
        Results.getCurrentAndPotentialSpeedValue(speed, increases) >= lowerBound
        Results.getCurrentAndPotentialSpeedValue(speed, increases) <= upperBound

        where:
        speed | increases | lowerBound | upperBound
        14    | 3         | 0.0        | 0
        14    | 4         | 0.0        | 0
        15    | 5         | 1.0        | 1.01
        16    | 5         | 1.08       | 1.09
        17    | 5         | 1.32       | 1.33
        15    | 4         | 1.39       | 1.4
        16    | 4         | 1.59       | 1.6
        18    | 5         | 1.72       | 1.73
        17    | 4         | 2.0        | 2.01
        19    | 5         | 2.28       | 2.29
        18    | 4         | 2.62       | 2.63
        15    | 3         | 2.83       | 2.84
        20    | 5         | 3.0        | 3.01
        16    | 3         | 3.32       | 3.33
        19    | 4         | 3.44       | 3.45
        21    | 5         | 3.88       | 3.89
        17    | 3         | 4.01       | 4.02
        20    | 4         | 4.47       | 4.48
        22    | 5         | 4.92       | 4.93
        21    | 4         | 5.71       | 5.72
        23    | 5         | 6.12       | 6.13
        22    | 4         | 7.16       | 7.17
        24    | 5         | 7.48       | 7.49
        23    | 4         | 8.82       | 8.83
        25    | 5         | 9.0        | 9.01
        26    | 5         | 10.68      | 10.69
        27    | 5         | 12.52      | 12.53
        28    | 5         | 14.52      | 14.53
        29    | 5         | 16.68      | 16.69
    }

    def "print values"() {
        given:
        printCurrentPotentialValues()

        expect:
        true
    }

    private void printCurrentPotentialValues() {
        def speeds = [
                [15, 5],
                [16, 5],
                [17, 5],
                [15, 4],
                [18, 5],
                [16, 4],
                [19, 5],
                [17, 4],
                [15, 3],
                [20, 5],
                [18, 4],
                [16, 3],
                [21, 5],
                [19, 4],
                [17, 3],
                [22, 5],
                [20, 4],
                [23, 5],
                [21, 4],
                [24, 5],
                [22, 4],
                [25, 5],
                [23, 4],
                [26, 5],
                [27, 5],
                [28, 5],
                [29, 5]
        ]
        for (def vals : speeds) {
            double v = Results.getCurrentAndPotentialSpeedValue(vals[0], vals[1])
            vals.add((v * 100).trunc() / 100)
            vals.add((v * 100 + 1).trunc() / 100)
        }
        speeds.sort { a, b -> a[3] <=> b[3] }
        for (def vals : speeds) {
            println "${vals[0]} | ${vals[1]} | ${vals[2]} | ${vals[3]}"
        }
    }

    private Mod getMod(int speed, int increases = 1) {
        Mod.Secondary secondary = new Mod.Secondary(Mod.SecondaryStat.SPEED, speed, true)
        secondary.count = increases
        return new Mod(randomizer, Mod.Dot.FIVE, Mod.Color.GOLD, new Mod.Primary(Mod.PrimaryStat.NOTSPEED), [secondary])
    }
}
