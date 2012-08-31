package primes;

import org.junit.Test;

import util.StopWatch;
import util.UnimplementedExercise;

/**
 * A driver for testing classes that extend PrimeComputation.
 */

public class PrimesTest {
	final static int UP_TO = 7000000;
	static long referenceRuntime = 0;
	
	@Test
	public void testSequential() throws Exception {
		test("Sequential version", new PrimesSequential());
	}

	private void test(String version, PrimesComputation p)
			throws Exception {
		test(version, p, UP_TO, false);
	}

	private void test(String version, PrimesComputation p, int upto,
			boolean resetReference) throws Exception {
		System.out.println(version);
		System.out.println("-----------------------------");

		if (p instanceof UnimplementedExercise) {
			System.out.println("UNIMPLEMENTED - when finished "
					+ "implementing this exercise, "
					+ "remove UnimplementedExercise from "
					+ "the list of implemented interfaces.\n");
			return;
		}

		// warm-up
		p.computePrimes(upto);
		p.computePrimes(upto);

		StopWatch.start();
		Boolean[] results = p.computePrimes(upto);
		StopWatch.stop();
		long numberOfPrimes = 0;
		for (boolean isP : results)
			if (isP)
				numberOfPrimes++;
		System.out.println("#      primes: " + numberOfPrimes);
		if (p instanceof LiveResults)
			System.out.println("# live primes: "
					+ ((LiveResults<?>) p).resultsCount());
		StopWatch.log("Time: ");
		if (resetReference || referenceRuntime == 0)
			referenceRuntime = StopWatch.getRuntime();
		else
			System.out.printf("Speed-up: %.2f\n", referenceRuntime / 1.0
					/ StopWatch.getRuntime());
		System.out.println();
	}
}