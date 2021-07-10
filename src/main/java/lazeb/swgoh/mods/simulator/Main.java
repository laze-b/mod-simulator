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

    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        runMultiPassSimulation();
        System.out.println("Simulation took " + (System.currentTimeMillis() - time) + "ms");
    }

    /**
     * Run through strategies, narrowing the field and increasing precision with each pass until we have a set of
     * winning candidates.
     */
    private static void runMultiPassSimulation() throws ExecutionException {
        // first pass: run all strategies for 100 years, keep the top 10%
        StrategyGenerator strategyGenerator = new StrategyGenerator();
        System.out.println("First pass strategies evaluating: " + strategyGenerator.getCount());
        List<Results> results = simulateStrategies(100,strategyGenerator.getCount() / 10, strategyGenerator);
        printWinners(results, "First pass top 10: ");

        // 2nd pass: run all strategies for 500 years, keep the top 10%
        List<Strategy> strategies = results.stream().flatMap(r -> Stream.of(r.strategy)).collect(Collectors.toList());
        strategyGenerator = new StrategyGenerator(strategies);
        System.out.println("Second pass strategies evaluating: " + strategyGenerator.getCount());
        results = simulateStrategies(500,strategyGenerator.getCount() / 10, strategyGenerator);
        printWinners(results, "Second pass top 10: ");

        // 3rd pass: run all strategies for 2500 years, keep the top 10%
        strategies = results.stream().flatMap(r -> Stream.of(r.strategy)).collect(Collectors.toList());
        strategyGenerator = new StrategyGenerator(strategies);
        System.out.println("Third pass strategies evaluating: " + strategyGenerator.getCount());
        results = simulateStrategies(2500,strategyGenerator.getCount() / 10, strategyGenerator);
        printWinners(results, "Third pass top 10: ");

        results.get(0).prettyPrint();
    }

    private static void printWinners(List<Results> results, String name) {
        System.out.println(name);
        for(int i=0; i<10; i++) {
            System.out.println(results.get(i));
        }
    }

    private static List<Results> simulateStrategies(int numYears, int numToKeep, StrategyGenerator strategyGenerator) throws ExecutionException {
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
                if(evaluated % 100 == 0) {
                    System.out.println("Evaluated: " + evaluated);
                }
            }
            return results.stream().sorted(Comparator.reverseOrder()).limit(numToKeep).collect(Collectors.toList());
        } catch (InterruptedException e) {
            System.out.println("Simulation timed out.");
            throw new RuntimeException(e);
        }
    }
}
