/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.manager.benchmark;

import io.valkyrja.container.data.ContainerData;
import io.valkyrja.container.manager.ChildContainer;
import io.valkyrja.container.manager.Container;
import io.valkyrja.container.manager.NativeChildContainer;
import io.valkyrja.container.manager.contract.ContainerContract;

/**
 * Rough benchmark comparing {@link NativeChildContainer} (direct field access) against
 * {@link ChildContainer} (contract-only with map copy).
 *
 * <p>This is a manual warm-up benchmark using {@link System#nanoTime()}. It is not as rigorous as
 * JMH (no dead-code elimination protection, no forking) but gives a reliable directional signal
 * for the two main cost centres:
 *
 * <ul>
 *   <li><b>Construction</b> — ChildContainer copies singletons + deferredCallbacks maps;
 *       NativeChildContainer allocates only empty maps.
 *   <li><b>Singleton lookup (parent cached)</b> — NativeChildContainer does a direct HashMap field
 *       read; ChildContainer calls isSingletonInstance via interface dispatch.
 *   <li><b>Singleton lookup (child binding)</b> — both create in child; cost is equivalent.
 *   <li><b>Full request simulation</b> — construction + 3 setSingleton overrides + 1 getSingleton.
 * </ul>
 *
 * <p>For production-grade numbers, migrate this to a JMH benchmark module.
 */
public class ChildContainerBenchmark {

    private static final int WARMUP     = 50_000;
    private static final int ITERATIONS = 500_000;

    public static void main(String[] args) {
        Container parent = buildParent();
        ContainerData parentData = (ContainerData) parent.getData();

        System.out.println("Warming up...");
        for (int i = 0; i < WARMUP; i++) {
            runChild(parent);
            runPortable(parent, parentData);
        }

        System.out.println("Benchmarking construction (" + ITERATIONS + " iterations each)...\n");
        benchmarkConstruction(parent, parentData);

        System.out.println("\nBenchmarking singleton lookup — parent cached...\n");
        benchmarkParentCachedLookup(parent, parentData);

        System.out.println("\nBenchmarking full request simulation...\n");
        benchmarkFullRequest(parent, parentData);
    }

    // --- Scenarios ---

    private static void benchmarkConstruction(Container parent, ContainerData parentData) {
        long childNs = time(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                new NativeChildContainer(parent);
            }
        });

        long portableNs = time(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                new ChildContainer(parent, parentData);
            }
        });

        report("Construction", childNs, portableNs);
    }

    private static void benchmarkParentCachedLookup(Container parent, ContainerData parentData) {
        // Ensure BenchmarkService is resolved in parent before benchmarking
        NativeChildContainer warmChild = new NativeChildContainer(parent);
        ChildContainer warmPortable = new ChildContainer(parent, parentData);

        long childNs = time(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                NativeChildContainer c = new NativeChildContainer(parent);
                c.getSingleton(BenchmarkService.class);
            }
        });

        long portableNs = time(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                ChildContainer c = new ChildContainer(parent, parentData);
                c.getSingleton(BenchmarkService.class);
            }
        });

        // Suppress unused warning
        warmChild.has(BenchmarkService.class);
        warmPortable.has(BenchmarkService.class);

        report("Singleton lookup (parent cached)", childNs, portableNs);
    }

    private static void benchmarkFullRequest(Container parent, ContainerData parentData) {
        long childNs = time(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                runChild(parent);
            }
        });

        long portableNs = time(() -> {
            for (int i = 0; i < ITERATIONS; i++) {
                runPortable(parent, parentData);
            }
        });

        report("Full request simulation", childNs, portableNs);
    }

    // --- Helpers ---

    private static void runChild(Container parent) {
        NativeChildContainer container = new NativeChildContainer(parent);
        container.setSingleton(ContainerContract.class, container);
        container.getSingleton(BenchmarkService.class);
    }

    private static void runPortable(Container parent, ContainerData parentData) {
        ChildContainer container = new ChildContainer(parent, parentData);
        container.setSingleton(ContainerContract.class, container);
        container.getSingleton(BenchmarkService.class);
    }

    private static Container buildParent() {
        Container parent = new Container();
        // Register and resolve a singleton so it is in parent's instances map
        parent.bindSingleton(BenchmarkService.class, (c, args) -> new BenchmarkService());
        parent.getSingleton(BenchmarkService.class); // force-resolve into instances
        return parent;
    }

    private static long time(Runnable r) {
        long start = System.nanoTime();
        r.run();
        return System.nanoTime() - start;
    }

    private static void report(String label, long childNs, long portableNs) {
        double childMs = childNs / 1_000_000.0;
        double portableMs = portableNs / 1_000_000.0;
        double childNsOp = (double) childNs / ITERATIONS;
        double portableNsOp = (double) portableNs / ITERATIONS;
        double ratio = (double) portableNs / childNs;

        System.out.printf("%-40s%n", label);
        System.out.printf(
                "  NativeChildContainer          %8.2f ms  (%6.2f ns/op)%n", childMs, childNsOp);
        System.out.printf(
                "  ChildContainer  %8.2f ms  (%6.2f ns/op)%n", portableMs, portableNsOp);
        System.out.printf("  Ratio (child/native)    %.2fx%n%n", ratio);
    }

    // Minimal service used as the benchmark resolution target
    public static class BenchmarkService {}
}
