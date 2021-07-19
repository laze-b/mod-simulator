# mod-simulator
A simulator for mod farming strategies in SWGOH

# Running the project
The main loop is located in [Main.java](src/main/java/lazeb/swgoh/mods/simulator/Main.java). It can be executed with Maven using:

./mvnw clean install exec:java

Note that it may take a while to execute since the simulation runs millions of trials.

# How it works
The simulator runs as a closed system based on a given budget for mod energy and credits as well as a farming strategy. 
No other outside resources are taken into account.

Each day during the simulation, if there is available energy then farm a new mod and level/slice it according to the following sequence:
1. If not 5 dot, sell immediately
2. If a 5 dot speed arrow, level it to 15 and keep it
3. Level remaining mods to show the desired number of secondaries
4. Continue leveling to 15 if speed is showing and we have a chance of meeting the target slice speed for that color
   (or in the case of gold mods, the target leveling speed)
5. Slice until we don't meet the target slice speed for the color the mod is at
6. Sell if we don't meet the minimum keep speed
7. Repeat for a large number of trials

If there are enough credits left, a gold 5 dot mod with a secondary speed of 5 will be purchased from the store and upgraded following the same rules.  

# Farming strategies
See [Strategy.java](src/main/java/lazeb/swgoh/mods/simulator/Strategy.java) for the allowed parameters

# Optimization function
See the getScore method in [Results.java](src/main/java/lazeb/swgoh/mods/simulator/Results.java). Our goal is to find the strategy that maximizes this function.

# Game probabilities
Assumptions about underlying game probabilities can be found in [Const.java](src/main/java/lazeb/swgoh/mods/simulator/Const.java)

# Results
Current testing recommends the following strategies for the given min speed targets. Anything under this speed should be sold. A different
strategy per shape may be used depending on your speed targets (e.g. crit damage triangles may have a lower min speed goal). 
Current results for value function: 2^(speed/2) + potential speed

| Speed | Increases |    Value    |
| ----- | --------- | ----------- |
|   0   |     0     |        1    |
|   1   |     1     |        1    |
|   2   |     1     |        2    |
|   3   |     1     |        3    |
|   4   |     1     |        4    |
|   5   |     1     |        6    |
|   6   |     2     |        8    |
|   7   |     2     |       11    |
|   8   |     2     |       16    |
|   9   |     2     |       23    |
|   9   |     3     |       23    |
|  10   |     2     |       32    |
|  10   |     3     |       32    |
|  11   |     2     |       45    |
|  11   |     3     |       45    |
|  12   |     3     |       64    |
|  12   |     4     |       64    |
|  13   |     3     |       91    |
|  13   |     4     |       91    |
|  14   |     3     |      128    |
|  14   |     4     |      128    |
|  15   |     3     |      181    |
|  15   |     4     |      181    |
|  15   |     5     |      181    |
|  16   |     3     |      256    |
|  16   |     4     |      256    |
|  16   |     5     |      256    |
|  17   |     4     |      362    |
|  17   |     5     |      362    |
|  18   |     5     |      512    |
|  19   |     5     |      724    |
|  20   |     5     |     1024    |
|  18   |     4     |     1199    |
|  17   |     3     |     1211    |
|  21   |     5     |     1448    |
|  19   |     4     |     1696    |
|  22   |     5     |     2048    |
|  20   |     4     |     2398    |
|  23   |     5     |     2896    |
|  21   |     4     |     3391    |
|  24   |     5     |     4096    |
|  22   |     4     |     4796    |
|  25   |     5     |     5793    |
|  23   |     4     |     6782    |
|  26   |     5     |     8192    |
|  27   |     5     |    11585    |
|  28   |     5     |    16384    |
|  29   |     5     |    23170    |


Simulation results:

| Min speed goal | Gray secondary reveals | Other secondary reveals | Min gray slice speed | Min green slice speed | Min blue slice speed | Min purple slice speed |
| -------------- | ---------------------- | ----------------------- | -------------------- | --------------------- | -------------------- | ---------------------- |
|  3             |  3                     | all                     |  5                   |  9                    | 10                   | 13                     |
|  5             |  2                     | all                     |  5                   |  8                    | 10                   | 13                     |
|  7             |  1                     | all                     |  5                   |  8                    | 10                   | 13                     |
|  9             |  1                     | all                     |  5                   |  8                    | 10                   | 13                     |
| 11             |  2                     | all                     |  5                   |  8                    | 10                   | 12                     |
| 13             |  3                     | all                     |  5                   |  8                    | 10                   | 12                     |
| 15             |  3                     | all                     |  5                   |  8                    | 10                   | 12                     |