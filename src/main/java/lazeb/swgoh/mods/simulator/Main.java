package lazeb.swgoh.mods.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        runMultiPassSimulation();
        System.out.println("Simulation took " + (System.currentTimeMillis() - time) + "ms");
    }

    private static void runSmokeTest() {
        List<Results> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(new Simulator(new Strategy(
                    2, 4, 4, 4,
                    5, 7, 9, 9, 15,
                    15
            ), new Randomizer()).simulate(2500));
        }
        results.sort(Comparator.reverseOrder());
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

    /**
     * Run through strategies, narrowing the field and increasing precision with each pass until we have a set of
     * winning candidates. Note that this will not necessarily identify the "best" candidate due to random error, it
     * should provide a representative set of the most effective farming strategies near the top.
     */
    private static void runMultiPassSimulation() {
        // first pass: run all strategies for 100 years, keep the top 10%
        StrategyGenerator strategyGenerator = new StrategyGenerator();
        System.out.println("First pass strategies evaluating: " + strategyGenerator.getCount());
        List<Results> results = simulateStrategies(100, strategyGenerator);
        printResults(results, "First pass: ");

        // 2nd pass: run all strategies for 500 years, keep the top 10%
        List<Strategy> strategies = results.stream().limit(strategyGenerator.getCount() / 10).flatMap(r -> Stream.of(r.strategy)).collect(Collectors.toList());
        strategyGenerator = new StrategyGenerator(strategies);
        System.out.println("Second pass strategies evaluating: " + strategyGenerator.getCount());
        results = simulateStrategies(500, strategyGenerator);
        printResults(results, "Second pass: ");

        // 3rd pass: run all strategies for 2500 years, keep the top 10%
        strategies = results.stream().limit(strategyGenerator.getCount() / 10).flatMap(r -> Stream.of(r.strategy)).collect(Collectors.toList());
        strategyGenerator = new StrategyGenerator(strategies);
        System.out.println("Third pass strategies evaluating: " + strategyGenerator.getCount());
        results = simulateStrategies(2500, strategyGenerator);
        printResults(results, "Third pass: ");

        System.out.println("Winner:");
        results.get(0).prettyPrint();
    }

    private static void printResults(List<Results> results, String name) {
        System.out.println(name);
        for (Results result : results) {
            System.out.println(result);
        }
    }

    private static List<Results> simulateStrategies(int numYears, StrategyGenerator strategyGenerator) {
        ExecutorService es = Executors.newFixedThreadPool(8);

        List<Future<Results>> futures = new ArrayList<>();
        Strategy nextStrategy = strategyGenerator.getNextStrategy();
        while (nextStrategy != null) {
            Strategy strategy = nextStrategy;
            futures.add(
                    es.submit(
                            () -> new Simulator(strategy, new Randomizer()).simulate(numYears)
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
