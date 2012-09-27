package pi.solutions;

import java.util.Random;

import pi.LiveValue;
import pi.PiApproximation;

public class PiLiveSync implements PiApproximation, LiveValue {

	long globalInside, globalTotal;

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

	public synchronized double liveValue() {
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
			Random rand = new java.util.Random();

			long inside = 0;
			for (long i = 0; i < iterations; i++) {
				double x = rand.nextDouble();
				double y = rand.nextDouble();
				double lenght = x * x + y * y;
				if (lenght < 1.0) {
					inside++;
					synchronized (PiLiveSync.this) {
						globalInside++;
						globalTotal++;
					}
				} else {
					synchronized (PiLiveSync.this) {
						globalTotal++;
					}
				}
			}

			pi = inside * 4.0 / iterations;
		}

		public double result() {
			return pi;
		}
	}
}
