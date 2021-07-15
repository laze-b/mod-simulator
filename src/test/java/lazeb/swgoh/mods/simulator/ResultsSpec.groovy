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
        results.getWeightedSpeedValue() == 216

        when:
        results.addMod(getMod(20), true)

        then:
        results.getWeightedSpeedValue() == 1547

        when:
        results.addMod(getMod(25), true)

        then:
        results.getWeightedSpeedValue() == 5643

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
        results.getWeightedSpeedPlusPotentialValue() == 4096

        when:
        results.addMod(getMod(15, 5), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() == 8192

        when:
        results.addMod(getMod(15, 3), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() == 12288

        when:
        results.addMod(getMod(20, 4), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() > 25049 && results.getWeightedSpeedPlusPotentialValue() < 25050

        when:
        results.addMod(getMod(25, 5), true)

        then:
        results.getWeightedSpeedPlusPotentialValue() > 42625 && results.getWeightedSpeedPlusPotentialValue() < 42626
    }

    @Unroll
    def "speed: #speed value"() {
        expect:
        Results.getSpeedValue(speed, 10) >= lowerBound
        Results.getSpeedValue(speed, 10) <= upperBound

        where:
        speed | lowerBound | upperBound
        9     | 0          | 0
        10    | 1          | 1
        11    | 8          | 8
        12    | 27         | 27
        13    | 64         | 64
        14    | 125        | 125
        15    | 216        | 216
        16    | 343        | 343
        17    | 512        | 512
        18    | 729        | 729
        19    | 1000       | 1000
        20    | 1331       | 1331
        21    | 1728       | 1728
        22    | 2197       | 2197
        23    | 2744       | 2744
        24    | 3375       | 3375
        25    | 4096       | 4096
        26    | 4913       | 4913
        27    | 5832       | 5832
        28    | 6859       | 6859
        29    | 8000       | 8000
    }

    @Unroll
    def "speed: #speed, increases: #increases current + potential speed value"() {
        expect:
        Results.getCurrentAndPotentialSpeedValue(speed, increases, 10) >= lowerBound
        Results.getCurrentAndPotentialSpeedValue(speed, increases, 10) <= upperBound

        where:
        speed | increases | lowerBound | upperBound
        10    | 2         | 1.0        | 1.01
        10    | 3         | 1.0        | 1.01
        11    | 2         | 8.0        | 8.01
        11    | 3         | 8.0        | 8.01
        12    | 3         | 27.0       | 27.01
        12    | 4         | 27.0       | 27.01
        13    | 3         | 64.0       | 64.01
        13    | 4         | 64.0       | 64.01
        14    | 3         | 125.0      | 125.01
        14    | 4         | 125.0      | 125.01
        15    | 3         | 216.0      | 216.01
        15    | 4         | 216.0      | 216.01
        15    | 5         | 216.0      | 216.01
        16    | 3         | 343.0      | 343.01
        16    | 4         | 343.0      | 343.01
        16    | 5         | 343.0      | 343.01
        17    | 4         | 512.0      | 512.01
        17    | 5         | 512.0      | 512.01
        18    | 5         | 729.0      | 729.01
        19    | 5         | 1000.0     | 1000.01
        20    | 5         | 1331.0     | 1331.01
        18    | 4         | 1497.78    | 1497.79
        17    | 3         | 1499.95    | 1499.96
        21    | 5         | 1728.0     | 1728.01
        19    | 4         | 1920.67    | 1920.68
        22    | 5         | 2197.0     | 2197.01
        20    | 4         | 2417.32    | 2417.33
        23    | 5         | 2744.0     | 2744.01
        21    | 4         | 2993.74    | 2993.75
        24    | 5         | 3375.0     | 3375.01
        22    | 4         | 3655.93    | 3655.94
        25    | 5         | 4096.0     | 4096.01
        23    | 4         | 4409.9     | 4409.91
        26    | 5         | 4913.0     | 4913.01
        27    | 5         | 5832.0     | 5832.01
        28    | 5         | 6859.0     | 6859.01
        29    | 5         | 8000.0     | 8000.01

    }

    def "print values"() {
        given:
        printCurrentPotentialValues()

        expect:
        true
    }

    private void printCurrentPotentialValues() {
        def speeds = [
                [10, 2],
                [10, 3],
                [11, 2],
                [11, 3],
                [12, 3],
                [12, 4],
                [13, 3],
                [13, 4],
                [14, 3],
                [14, 4],
                [15, 3],
                [15, 4],
                [15, 5],
                [16, 3],
                [16, 4],
                [16, 5],
                [17, 3],
                [17, 4],
                [17, 5],
                [18, 4],
                [18, 5],
                [19, 4],
                [19, 5],
                [20, 4],
                [20, 5],
                [21, 4],
                [21, 5],
                [22, 4],
                [22, 5],
                [23, 4],
                [23, 5],
                [24, 5],
                [25, 5],
                [26, 5],
                [27, 5],
                [28, 5],
                [29, 5]
        ]
        for (def vals : speeds) {
            double v = Results.getCurrentAndPotentialSpeedValue(vals[0], vals[1], 10)
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
