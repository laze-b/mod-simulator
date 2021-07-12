package lazeb.swgoh.mods.simulator

import spock.lang.Specification

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
        results.getWeightedSpeedValue() > 2.9 && results.getWeightedSpeedValue() < 3.1

        when:
        results.addMod(getMod(25), true)

        then:
        results.getWeightedSpeedValue() > 6.9 && results.getWeightedSpeedValue() < 7.1

    }

    private Mod getMod(int speed) {
        return new Mod(randomizer, Mod.Dot.FIVE, Mod.Color.GOLD, new Mod.Primary(Mod.PrimaryStat.NOTSPEED), [
                new Mod.Secondary(Mod.SecondaryStat.SPEED, speed, true)
        ])
    }
}
