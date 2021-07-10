package lazeb.mods.simulator

import lazeb.swgoh.mods.simulator.Mod
import lazeb.swgoh.mods.simulator.Randomizer
import spock.lang.Specification
import spock.lang.Unroll

class ModSpec extends Specification {

    def "test mod < 5 dot"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomModDot() >> Mod.Dot.LESSTHANFIVE
            randomModColor() >> Mod.Color.GRAY
            randomModPrimary() >> new Mod.Primary(Mod.PrimaryStat.NOTSPEED)
            randomModSecondaries(Mod.Color.GRAY, Mod.PrimaryStat.NOTSPEED) >> [
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
            ]
        }
        Mod mod = new Mod(randomizer)

        expect:
        mod.dot == Mod.Dot.LESSTHANFIVE
        mod.color == Mod.Color.GRAY
        mod.primary.stat == Mod.PrimaryStat.NOTSPEED
        mod.level == 1
        mod.energySpent == 16
        mod.creditsSpent == 0
        mod.creditValueIfSold() == 4900

        when:
        mod.increaseLevel()

        then:
        thrown(IllegalStateException)

        when:
        mod.slice()

        then:
        thrown(IllegalStateException)
    }

    def "test 5 dot, speed arrow"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomModDot() >> Mod.Dot.FIVE
            randomModColor() >> Mod.Color.GRAY
            randomModPrimary() >> new Mod.Primary(Mod.PrimaryStat.SPEED)
            randomModSecondaries(Mod.Color.GRAY, Mod.PrimaryStat.SPEED) >> [
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
            ]
        }
        Mod mod = new Mod(randomizer)

        expect:
        mod.level == 1
        mod.creditValueIfSold() == 9500

        when:
        mod.increaseLevel()

        then:
        mod.level == 3
        mod.creditValueIfSold() == 9500

        when:
        mod.increaseLevel()

        then:
        mod.level == 6
        mod.creditValueIfSold() == 9500

        when:
        mod.increaseLevel()

        then:
        mod.level == 9
        mod.creditValueIfSold() == 14900

        when:
        mod.increaseLevel()

        then:
        mod.level == 12
        mod.creditValueIfSold() == 33800

        when:
        mod.increaseLevel()

        then:
        mod.dot == Mod.Dot.FIVE
        mod.color == Mod.Color.GRAY
        mod.primary.stat == Mod.PrimaryStat.SPEED
        mod.level == 15
        mod.energySpent == 16
        mod.creditsSpent == 248400
        mod.creditValueIfSold() == 97200

        when:
        mod.increaseLevel()

        then:
        thrown(IllegalStateException)
    }

    def "test 5 dot, gray, no speed primary, leveling and slicing"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomModDot() >> Mod.Dot.FIVE
            randomModColor() >> Mod.Color.GRAY
            randomModPrimary() >> new Mod.Primary(Mod.PrimaryStat.NOTSPEED)
            randomModSecondaries(Mod.Color.GRAY, Mod.PrimaryStat.NOTSPEED) >> [
                    new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
            ]
            randomSecondaryToReveal(_) >> 0
            randomSecondarySpeedIncrease() >> 4
        }
        Mod mod = new Mod(randomizer)

        expect:
        mod.level == 1
        mod.creditsSpent == 0
        !mod.secondaries[0].visible
        !mod.secondaries[1].visible
        !mod.secondaries[2].visible
        !mod.secondaries[3].visible
        mod.creditValueIfSold() == 9500

        when:
        mod.increaseLevel()

        then:
        mod.level == 3
        mod.creditsSpent == 6900
        mod.secondaries[0].visible
        !mod.secondaries[1].visible
        !mod.secondaries[2].visible
        !mod.secondaries[3].visible
        mod.creditValueIfSold() == 9500

        when:
        mod.increaseLevel()

        then:
        mod.level == 6
        mod.creditsSpent == 18400
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        !mod.secondaries[2].visible
        !mod.secondaries[3].visible
        mod.creditValueIfSold() == 9500

        when:
        mod.increaseLevel()

        then:
        mod.level == 9
        mod.creditsSpent == 37900
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        !mod.secondaries[3].visible
        mod.creditValueIfSold() == 14900

        when:
        mod.increaseLevel()

        then:
        mod.level == 12
        mod.creditsSpent == 86200
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible
        mod.creditValueIfSold() == 33800

        when:
        mod.increaseLevel()

        then:
        mod.color == Mod.Color.GRAY
        mod.level == 15
        mod.energySpent == 16
        mod.creditsSpent == 248400
        mod.secondaries[0].value == 5
        mod.creditValueIfSold() == 97200

        when:
        mod.increaseLevel()

        then:
        thrown(IllegalStateException)

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.color == Mod.Color.GREEN
        mod.energySpent == 118
        mod.creditsSpent == 266400
        mod.secondaries[0].value == 9

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 1
        mod.color == Mod.Color.BLUE
        mod.energySpent == 322
        mod.creditsSpent == 302400
        mod.secondaries[0].value == 9

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.color == Mod.Color.PURPLE
        mod.energySpent == 678
        mod.creditsSpent == 365400
        mod.secondaries[0].value == 13

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.color == Mod.Color.GOLD
        mod.energySpent == 1186
        mod.creditsSpent == 455400
        mod.secondaries[0].value == 17

        and:
        mod.dot == Mod.Dot.FIVE
        mod.primary.stat == Mod.PrimaryStat.NOTSPEED
        mod.level == 15
        mod.creditValueIfSold() == 97200

        when:
        mod.slice()

        then:
        thrown(IllegalStateException)
    }

    def "test 5 dot, green, no speed primary, leveling and slicing"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomModDot() >> Mod.Dot.FIVE
            randomModColor() >> Mod.Color.GREEN
            randomModPrimary() >> new Mod.Primary(Mod.PrimaryStat.NOTSPEED)
            randomModSecondaries(Mod.Color.GREEN, Mod.PrimaryStat.NOTSPEED) >> [
                    new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
            ]
            randomSecondaryToReveal(_) >> 0
            randomSecondarySpeedIncrease() >> 4
        }
        Mod mod = new Mod(randomizer)

        expect:
        mod.level == 1
        mod.creditsSpent == 0
        mod.secondaries[0].visible
        !mod.secondaries[1].visible
        !mod.secondaries[2].visible
        !mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.level == 3
        mod.creditsSpent == 6900
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        !mod.secondaries[2].visible
        !mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.level == 6
        mod.creditsSpent == 18400
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        !mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.level == 9
        mod.creditsSpent == 37900
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 12
        mod.creditsSpent == 86200
        mod.secondaries[0].visible
        mod.secondaries[0].value == 9
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.color == Mod.Color.GREEN
        mod.level == 15
        mod.energySpent == 16
        mod.creditsSpent == 248400
        mod.secondaries[0].value == 9

        when:
        mod.increaseLevel()

        then:
        thrown(IllegalStateException)

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 1
        mod.color == Mod.Color.BLUE
        mod.energySpent == 220
        mod.creditsSpent == 284400
        mod.secondaries[0].value == 9

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.color == Mod.Color.PURPLE
        mod.energySpent == 576
        mod.creditsSpent == 347400
        mod.secondaries[0].value == 13

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.color == Mod.Color.GOLD
        mod.energySpent == 1084
        mod.creditsSpent == 437400
        mod.secondaries[0].value == 17

        and:
        mod.dot == Mod.Dot.FIVE
        mod.primary.stat == Mod.PrimaryStat.NOTSPEED
        mod.level == 15

        when:
        mod.slice()

        then:
        thrown(IllegalStateException)
    }

    def "test 5 dot, blue, no speed primary, leveling and slicing"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomModDot() >> Mod.Dot.FIVE
            randomModColor() >> Mod.Color.BLUE
            randomModPrimary() >> new Mod.Primary(Mod.PrimaryStat.NOTSPEED)
            randomModSecondaries(Mod.Color.BLUE, Mod.PrimaryStat.NOTSPEED) >> [
                    new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
            ]
            randomSecondaryToReveal(_) >> 0
            randomSecondarySpeedIncrease() >> 4
        }
        Mod mod = new Mod(randomizer)

        expect:
        mod.level == 1
        mod.creditsSpent == 0
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        !mod.secondaries[2].visible
        !mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.level == 3
        mod.creditsSpent == 6900
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        !mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.level == 6
        mod.creditsSpent == 18400
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 9
        mod.creditsSpent == 37900
        mod.secondaries[0].visible
        mod.secondaries[0].value == 9
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 1
        mod.level == 12
        mod.creditsSpent == 86200
        mod.secondaries[0].visible
        mod.secondaries[0].value == 9
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.color == Mod.Color.BLUE
        mod.level == 15
        mod.energySpent == 16
        mod.creditsSpent == 248400
        mod.secondaries[0].value == 9

        when:
        mod.increaseLevel()

        then:
        thrown(IllegalStateException)

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.color == Mod.Color.PURPLE
        mod.energySpent == 372
        mod.creditsSpent == 311400
        mod.secondaries[0].value == 13

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 1
        mod.color == Mod.Color.GOLD
        mod.energySpent == 880
        mod.creditsSpent == 401400
        mod.secondaries[0].value == 13

        and:
        mod.dot == Mod.Dot.FIVE
        mod.primary.stat == Mod.PrimaryStat.NOTSPEED
        mod.level == 15

        when:
        mod.slice()

        then:
        thrown(IllegalStateException)
    }

    def "test 5 dot, purple, no speed primary, leveling and slicing"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomModDot() >> Mod.Dot.FIVE
            randomModColor() >> Mod.Color.PURPLE
            randomModPrimary() >> new Mod.Primary(Mod.PrimaryStat.NOTSPEED)
            randomModSecondaries(Mod.Color.PURPLE, Mod.PrimaryStat.NOTSPEED) >> [
                    new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
            ]
            randomSecondaryToReveal(_) >> 0
            randomSecondarySpeedIncrease() >> 4
        }
        Mod mod = new Mod(randomizer)

        expect:
        mod.level == 1
        mod.creditsSpent == 0
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        !mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.level == 3
        mod.creditsSpent == 6900
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 6
        mod.creditsSpent == 18400
        mod.secondaries[0].visible
        mod.secondaries[0].value == 9
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 1
        mod.level == 9
        mod.creditsSpent == 37900
        mod.secondaries[0].visible
        mod.secondaries[0].value == 9
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 12
        mod.creditsSpent == 86200
        mod.secondaries[0].visible
        mod.secondaries[0].value == 13
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.color == Mod.Color.PURPLE
        mod.level == 15
        mod.energySpent == 16
        mod.creditsSpent == 248400
        mod.secondaries[0].value == 13

        when:
        mod.increaseLevel()

        then:
        thrown(IllegalStateException)

        when:
        mod.slice()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.color == Mod.Color.GOLD
        mod.energySpent == 524
        mod.creditsSpent == 338400
        mod.secondaries[0].value == 17

        and:
        mod.dot == Mod.Dot.FIVE
        mod.primary.stat == Mod.PrimaryStat.NOTSPEED
        mod.level == 15

        when:
        mod.slice()

        then:
        thrown(IllegalStateException)
    }

    def "test 5 dot, gold, no speed primary, leveling and slicing"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomModDot() >> Mod.Dot.FIVE
            randomModColor() >> Mod.Color.GOLD
            randomModPrimary() >> new Mod.Primary(Mod.PrimaryStat.NOTSPEED)
            randomModSecondaries(Mod.Color.GOLD, Mod.PrimaryStat.NOTSPEED) >> [
                    new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true)
            ]
            randomSecondaryToReveal(_) >> 0
            randomSecondarySpeedIncrease() >> 4
        }
        Mod mod = new Mod(randomizer)

        expect:
        mod.level == 1
        mod.creditsSpent == 0
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 3
        mod.creditsSpent == 6900
        mod.secondaries[0].visible
        mod.secondaries[0].value == 9
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 6
        mod.creditsSpent == 18400
        mod.secondaries[0].visible
        mod.secondaries[0].value == 13
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 1
        mod.level == 9
        mod.creditsSpent == 37900
        mod.secondaries[0].visible
        mod.secondaries[0].value == 13
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 12
        mod.creditsSpent == 86200
        mod.secondaries[0].visible
        mod.secondaries[0].value == 17
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.color == Mod.Color.GOLD
        mod.level == 15
        mod.energySpent == 16
        mod.creditsSpent == 248400
        mod.secondaries[0].value == 17

        and:
        mod.dot == Mod.Dot.FIVE
        mod.primary.stat == Mod.PrimaryStat.NOTSPEED
        mod.level == 15

        when:
        mod.increaseLevel()

        then:
        thrown(IllegalStateException)

        when:
        mod.slice()

        then:
        thrown(IllegalStateException)
    }

    def "test 5 dot, gold from store, regular currency, no speed primary, leveling and slicing"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomSecondaryToReveal(_) >> 0
            randomSecondarySpeedIncrease() >> 4
        }
        Mod mod = new Mod(randomizer, Mod.Dot.FIVE, Mod.Color.GOLD, new Mod.Primary(Mod.PrimaryStat.NOTSPEED), [
                new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, true),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, true)
        ])

        expect:
        mod.level == 1
        mod.creditsSpent == 3910000
        mod.secondaries[0].visible
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 3
        mod.creditsSpent == 3916900
        mod.secondaries[0].visible
        mod.secondaries[0].value == 9
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 6
        mod.creditsSpent == 3928400
        mod.secondaries[0].visible
        mod.secondaries[0].value == 13
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 1
        mod.level == 9
        mod.creditsSpent == 3947900
        mod.secondaries[0].visible
        mod.secondaries[0].value == 13
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        1 * randomizer.randomSecondaryToIncrease() >> 0
        mod.level == 12
        mod.creditsSpent == 3996200
        mod.secondaries[0].visible
        mod.secondaries[0].value == 17
        mod.secondaries[1].visible
        mod.secondaries[2].visible
        mod.secondaries[3].visible

        when:
        mod.increaseLevel()

        then:
        mod.color == Mod.Color.GOLD
        mod.level == 15
        mod.energySpent == 0
        mod.creditsSpent == 4158400
        mod.secondaries[0].value == 17

        and:
        mod.dot == Mod.Dot.FIVE
        mod.primary.stat == Mod.PrimaryStat.NOTSPEED
        mod.level == 15

        when:
        mod.increaseLevel()

        then:
        thrown(IllegalStateException)

        when:
        mod.slice()

        then:
        thrown(IllegalStateException)
    }

    def "test visible speed"() {
        when:
        Mod mod1 = new Mod(Mock(Randomizer), Mod.Dot.FIVE, Mod.Color.GOLD, new Mod.Primary(Mod.PrimaryStat.NOTSPEED), [
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
        ])

        then:
        mod1.visibleSpeed() == 0

        when:
        Mod mod2 = new Mod(Mock(Randomizer), Mod.Dot.FIVE, Mod.Color.GOLD, new Mod.Primary(Mod.PrimaryStat.NOTSPEED), [
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, false),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
        ])

        then:
        mod2.visibleSpeed() == 0

        when:
        Mod mod3 = new Mod(Mock(Randomizer), Mod.Dot.FIVE, Mod.Color.GOLD, new Mod.Primary(Mod.PrimaryStat.NOTSPEED), [
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false),
                new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, true),
                new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, false)
        ])

        then:
        mod3.visibleSpeed() == 5
    }

    @Unroll
    def "potential secondary increases for #color, #levelCount"() {
        given:
        Randomizer randomizer = Mock(Randomizer) {
            randomModDot() >> Mod.Dot.FIVE
            randomModColor() >> color
            randomModPrimary() >> new Mod.Primary(Mod.PrimaryStat.NOTSPEED)
            randomModSecondaries(color, Mod.PrimaryStat.NOTSPEED) >> [
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, [Mod.Color.GOLD, Mod.Color.PURPLE, Mod.Color.BLUE, Mod.Color.GREEN].contains(color)),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, [Mod.Color.GOLD, Mod.Color.PURPLE, Mod.Color.BLUE].contains(color)),
                    new Mod.Secondary(Mod.SecondaryStat.SPEED, 5, [Mod.Color.GOLD, Mod.Color.PURPLE].contains(color)),
                    new Mod.Secondary(Mod.SecondaryStat.NOTSPEED, 0, [Mod.Color.GOLD].contains(color))
            ]
        }
        Mod mod = new Mod(randomizer)
        levelCount.times {
            mod.increaseLevel()
        }

        expect:
        mod.potentialSecondaryIncreasesFromLeveling() == potentialIncreases

        where:
        color            | levelCount | potentialIncreases
        Mod.Color.GRAY   | 0          | 0
        Mod.Color.GRAY   | 1          | 0
        Mod.Color.GRAY   | 2          | 0
        Mod.Color.GRAY   | 3          | 0
        Mod.Color.GRAY   | 4          | 0
        Mod.Color.GRAY   | 5          | 0
        Mod.Color.GREEN  | 0          | 1
        Mod.Color.GREEN  | 1          | 1
        Mod.Color.GREEN  | 2          | 1
        Mod.Color.GREEN  | 3          | 1
        Mod.Color.GREEN  | 4          | 0
        Mod.Color.GREEN  | 5          | 0
        Mod.Color.BLUE   | 0          | 2
        Mod.Color.BLUE   | 1          | 2
        Mod.Color.BLUE   | 2          | 2
        Mod.Color.BLUE   | 3          | 1
        Mod.Color.BLUE   | 4          | 0
        Mod.Color.BLUE   | 5          | 0
        Mod.Color.PURPLE | 0          | 3
        Mod.Color.PURPLE | 1          | 3
        Mod.Color.PURPLE | 2          | 2
        Mod.Color.PURPLE | 3          | 1
        Mod.Color.PURPLE | 4          | 0
        Mod.Color.PURPLE | 5          | 0
        Mod.Color.GOLD   | 0          | 4
        Mod.Color.GOLD   | 1          | 3
        Mod.Color.GOLD   | 2          | 2
        Mod.Color.GOLD   | 3          | 1
        Mod.Color.GOLD   | 4          | 0
        Mod.Color.GOLD   | 5          | 0
    }
}
