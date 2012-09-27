package pi.solutions;

import java.util.Random;

import pi.LiveValue;
import pi.PiApproximation;

/**
 * Parallel implementation of {@link PiApproximation} that also reports the
 * approximation so far using the {@link #liveValue()} method.
 * 
 * The liveValue() method can be invoked at any time *during* the computation to
 * get the latest approximation.
 * 
 */

public class PiLive implements PiApproximation, LiveValue {

	// update the live value each UPDATE_EACH cycles
	public static final int UPDATE_EACH = 100;

	long globalInside, globalTotal;

	@Override
	public double computePi(long iterations) throws InterruptedException {
		int noProcessors = 2;// Runtime.getRuntime().availableProcessors();
		PiApproximationThread[] threads = new PiApproximationThread[noProcessors];

		for (int i = 0; i < noProcessors; i++) {
			threads[i] = new PiApproximationThread(iterations / noProcessors);
			threads[i].start();
		}

		double pi = 0;

		for (int i = 0; i < noProcessors; i++) {
			threads[i].join();
			pi += threads[i].result() / noProcessors;
		}
		return pi;
	}

	public double liveValue() {
		return globalInside * 4.0 / globalTotal;
	}

	class PiApproximationThread extends Thread {
		private double pi;
		private final long iterations;

		public PiApproximationThread(long iterations) {
			this.iterations = iterations;
		}

		@Override
		public void run() {
			long inside = 0;
			Random rand = new Random();

			for (int i = 0; i < iterations; i++) {
				double x = rand.nextDouble();
				double y = rand.nextDouble();
				double lenght = x * x + y * y;
				if (lenght < 1.0) {
					globalInside++;
					inside++;
				}
				globalTotal++;
			}
			pi = inside * 4.0 / iterations;
		}

		public double result() {
			return pi;
		}
	}
}
