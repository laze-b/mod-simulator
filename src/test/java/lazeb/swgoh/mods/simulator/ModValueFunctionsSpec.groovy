package lazeb.swgoh.mods.simulator

import spock.lang.Specification
import spock.lang.Unroll

class ModValueFunctionsSpec extends Specification {

    @Unroll
    def "speed: #speed, increases: #increases current + potential speed value"() {
        given:
        ModValueFunction f = ModValueFunctions.getCurrentPlusPotentialValueFunction(
                { speed, speedIncreases -> Math.pow(speed - 9, 3) },
                0.75
        )

        expect:
        f.apply((double) speed, increases) >= lowerBound
        f.apply((double) speed, increases) <= upperBound

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
        17    | 3         | 1453.66    | 1453.67
        18    | 4         | 1465.12    | 1465.13
        21    | 5         | 1728.0     | 1728.01
        19    | 4         | 1882.34    | 1882.35
        22    | 5         | 2197.0     | 2197.01
        20    | 4         | 2372.87    | 2372.88
        23    | 5         | 2744.0     | 2744.01
        21    | 4         | 2942.73    | 2942.74
        24    | 5         | 3375.0     | 3375.01
        22    | 4         | 3597.9     | 3597.91
        25    | 5         | 4096.0     | 4096.01
        23    | 4         | 4344.38    | 4344.39
        26    | 5         | 4913.0     | 4913.01
        27    | 5         | 5832.0     | 5832.01
        28    | 5         | 6859.0     | 6859.01
        29    | 5         | 8000.0     | 8000.01
    }

    def "printer"() {
        given:
        ModValueFunctions.printModValues("speed + 5 - increases", { s, si -> s + 5 - si })

        expect:
        true
    }
}
