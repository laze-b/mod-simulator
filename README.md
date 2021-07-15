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
strategy per shape may be used depending on your speed targets (e.g. crit damage triangles may have a lower min speed goal). This was computed using a mod
value function of (speed over min)^3 + 0.75 * speed_potential.

| Min speed goal | Gray secondary reveals | Other secondary reveals | Min gray slice speed | Min green slice speed | Min blue slice speed | Min purple slice speed |  
| -------------- | ---------------------- | ----------------------- | -------------------- | --------------------- | -------------------- | ---------------------- |
| 10             | 3                      | all                     | 5                    | 8                     | 9                    | 12                     |
| 12             | 2                      | all                     | 5                    | 8                     | 10                   | 13                     |
| 14             | 2                      | all                     | 5                    | 8                     | 10                   | 13                     |