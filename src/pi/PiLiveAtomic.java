package pi;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Parallel implementation of {@link PiApproximation} that also reports the
 * approximation so far using the {@link #liveValue()} method.
 * 
 * The liveValue() method can be invoked at any time *during* the computation to
 * get the latest approximation.
 * 
 */

/*
 * Steps:
 * 
 * 1. Copy your or the given solution to PiLive to the class below.
 * 
 * 2. Fix the races using an Atomic construct.
 */

public class PiLiveAtomic implements PiApproximation, LiveValue {

	// update the live value each UPDATE_EACH cycles
	//public static final int UPDATE_EACH = 100;

	AtomicLong globalInside = new AtomicLong(0);
	AtomicLong globalTotal = new AtomicLong(0);

	@Override
	public double computePi(long iterations) throws InterruptedException {
		int noProcessors = Runtime.getRuntime().availableProcessors();
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
		return globalInside.get() * 4.0 / globalTotal.get();
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
					globalInside.incrementAndGet();
					inside++;
				}
				globalTotal.incrementAndGet();
			}
			pi = inside * 4.0 / iterations;
		}

		public double result() {
			return pi;
		}
	}
}
