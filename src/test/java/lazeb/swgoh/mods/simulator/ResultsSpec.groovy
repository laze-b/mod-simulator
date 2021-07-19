package lazeb.swgoh.mods.simulator

import spock.lang.Specification
import spock.lang.Unroll

class ResultsSpec extends Specification {

    Randomizer randomizer = Stub(Randomizer)

    def "compareTo"() {
        given:
        Results results = getResults()
        results.addMod(getMod(5, 1), true)

        Results results2 = getResults()
        results2.addMod(getMod(5, 1), true)

        Results results3 = getResults()
        results3.addMod(getMod(3, 1), true)

        expect:
        results <=> results2 == 0
        results <=> results3 == 1
        results3 <=> results == -1
    }

    def "test value calculations"() {
        given:
        Results results = new Results(
                10,
                new Strategy(0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0),
                new Resources(100, 100000),
                { speed, speedIncreases -> speed + speedIncreases }
        )

        when:
        results.addMod(getMod(5, 1), false)

        then:
        results.getScore() == 0

        when:
        results.addMod(getMod(6, 2), true)

        then:
        results.getScore() == 0.8

        when:
        results.addMod(getMod(9, 2), true)
        results.addMod(getMod(9, 3), true)

        then:
        results.getScore() == 3.1
    }

    private Results getResults() {
        return new Results(
                10,
                new Strategy(0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0),
                new Resources(100, 100000),
                { speed, speedIncreases -> speed + speedIncreases }
        )
    }

    private Mod getMod(int speed, int increases = 1) {
        Mod.Secondary secondary = new Mod.Secondary(Mod.SecondaryStat.SPEED, speed, true)
        secondary.count = increases
        return new Mod(randomizer, Mod.Dot.FIVE, Mod.Color.GOLD, new Mod.Primary(Mod.PrimaryStat.NOTSPEED), [secondary])
    }
}
