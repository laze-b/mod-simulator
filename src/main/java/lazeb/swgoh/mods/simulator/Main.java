package lazeb.swgoh.mods.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
//        runSmokeTest();
        ModValueFunction modValueFunction = ModValueFunctions.getCurrentPlusPotentialValueFunction(
                (speed, speedIncreases) -> Math.pow(2, speed / 2.0),
                .75
        );
        runMultiPassSimulation("2^(speed/2) + potential speed", modValueFunction);
        System.out.println("Simulation took " + (System.currentTimeMillis() - time)/60000 + " minutes");
    }

    private static void runSmokeTest() {
        List<Results> results = new ArrayList<>();
        List<Strategy> strategies = new ArrayList<>();
        strategies.add(new Strategy(
                2, 4, 4, 4,
                5, 8, 10, 12, 12,
                12
        ));
        // maw
        strategies.add(new Strategy(
                2, 4, 4, 4,
                5, 8, 12, 14, 12,
                12
        ));
        for (Strategy strategy : strategies) {
            results.add(new Simulator(strategy, new Randomizer(),
                    ModValueFunctions.getCurrentPlusPotentialValueFunction(
                            (speed, speedIncreases) -> Math.pow(2, speed / 2.0),
                            .5)
            ).simulate(2500));
        }
        printResults(results, "Results:");

        /*

         Range of scores for different years (100 iterations) to get an idea of accuracy

         100:  .29
         150:  .21
         200:  .19
         300:  .17
         400:  .13
         500:  .12
         1000: .08
         2500: .04
         */
    }

    private static void runMultiPassSimulation(String functionDescription, ModValueFunction modValueFunction) {
        Map<Integer, Strategy> winnerMap = new TreeMap<>();
        for (int i = 3; i <= 15; i += 2) {
            winnerMap.put(i, runMultiPassSimulation(i, modValueFunction));
        }
        ModValueFunctions.printModValues(functionDescription, modValueFunction);
        printWinnerMap(winnerMap);
    }

    private static void printWinnerMap(Map<Integer, Strategy> winnerMap) {
        System.out.println("--------------");
        System.out.println("Final results:");
        System.out.println("| Min speed goal | Gray secondary reveals | Other secondary reveals | Min gray slice speed | Min green slice speed | Min blue slice speed | Min purple slice speed |");
        System.out.println("| -------------- | ---------------------- | ----------------------- | -------------------- | --------------------- | -------------------- | ---------------------- |");
        for(int minSpeed: winnerMap.keySet()) {
            Strategy winner = winnerMap.get(minSpeed);
            System.out.println(String.format(
                    "| %2d             | %2d                     | all                     | %2d                   | %2d                    | %2d                   | %2d                     |",
                    minSpeed, winner.grayInitialSecondaries, winner.graySliceSpeed, winner.greenSliceSpeed, winner.blueSliceSpeed, winner.purpleSliceSpeed
            ));
        }
    }

    /**
     * Run through strategies, narrowing the field and increasing precision with each pass until we have a set of
     * winning candidates. Note that this will not necessarily identify the "best" candidate due to random error, it
     * should provide a representative set of the most effective farming strategies near the top.
     */
    private static Strategy runMultiPassSimulation(int minSpeed, ModValueFunction modValueFunction) {
        // first pass: run all strategies for 100 years, keep the top 10%
        StrategyGenerator strategyGenerator = new StrategyGenerator(minSpeed);
        System.out.println("First pass strategies evaluating: " + strategyGenerator.getCount());
        List<Results> results = simulateStrategies(100, strategyGenerator, modValueFunction);
        printResults(results, "First pass with minSpeed = " + minSpeed);

        // 2nd pass: run all strategies for 500 years, keep the top 10%
        List<Strategy> strategies = results.stream().limit(strategyGenerator.getCount() / 10).flatMap(r -> Stream.of(r.strategy)).collect(Collectors.toList());
        strategyGenerator = new StrategyGenerator(strategies);
        System.out.println("Second pass strategies evaluating: " + strategyGenerator.getCount());
        results = simulateStrategies(500, strategyGenerator, modValueFunction);
        printResults(results, "Second pass with minSpeed = " + minSpeed);

        // 3rd pass: run all strategies for 1600 years, keep the top 10%
        strategies = results.stream().limit(strategyGenerator.getCount() / 10).flatMap(r -> Stream.of(r.strategy)).collect(Collectors.toList());
        strategyGenerator = new StrategyGenerator(strategies);
        System.out.println("Final pass strategies evaluating: " + strategyGenerator.getCount());
        results = simulateStrategies(2500, strategyGenerator, modValueFunction);
        printResults(results, "Final pass with minSpeed = " + minSpeed);
        return results.get(0).strategy;
    }

    private static void printResults(List<Results> results, String name) {
        System.out.println(name);
        results.stream().limit(25).forEach(System.out::println);
    }

    private static List<Results> simulateStrategies(int numYears, StrategyGenerator strategyGenerator, ModValueFunction modValueFunction) {
        ExecutorService es = Executors.newFixedThreadPool(8);

        List<Future<Results>> futures = new ArrayList<>();
        Strategy nextStrategy = strategyGenerator.getNextStrategy();
        while (nextStrategy != null) {
            Strategy strategy = nextStrategy;
            futures.add(
                    es.submit(
                            () -> new Simulator(strategy, new Randomizer(), modValueFunction).simulate(numYears)
                    ));
            nextStrategy = strategyGenerator.getNextStrategy();
        }
        es.shutdown();

        try {
            List<Results> results = new ArrayList<>();
            int evaluated = 0;
            for (Future<Results> future : futures) {
                results.add(future.get());
                evaluated++;
                if (evaluated % 100 == 0) {
                    System.out.println("Evaluated: " + evaluated);
                }
            }
            return results.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        } catch (InterruptedException e) {
            System.out.println("Simulation timed out.");
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
