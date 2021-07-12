package lazeb.swgoh.mods.simulator

import spock.lang.Specification

class RandomizerSpec extends Specification {

    def "random secondaries"() {
        given:
        Randomizer randomizer = new Randomizer()

        when:
        List<Mod.Secondary> secondaries = randomizer.randomModSecondaries(color, primaryStat)

        then:
        secondaries.size() == 4

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
}
