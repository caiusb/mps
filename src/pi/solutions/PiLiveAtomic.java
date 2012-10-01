package pi.solutions;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import pi.PiApproximation;

public class PiLiveAtomic implements PiApproximation {

	AtomicLong globalInside = new AtomicLong();
	AtomicLong globalTotal = new AtomicLong();
	
	public static final int UPDATE_EACH = 100;

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
		return globalInside.intValue() * 4.0 / globalTotal.intValue();
	}

	class PiApproximationThread extends Thread {
		private double pi;
		private final long iterations;

		public PiApproximationThread(long iterations) {
			this.iterations = iterations;
		}

		public double result() {
			return pi;
		}

		@Override
		public void run() {
			Random rand = new java.util.Random();

			int inside = 0;
			for (int i = 0; i < iterations / UPDATE_EACH; i++) {
				int insideJ = 0;
				for (int j = 0; j < UPDATE_EACH; j++) {
					double x = rand.nextDouble();
					double y = rand.nextDouble();
					double lenght = x * x + y * y;
					if (lenght < 1.0) {
						insideJ++;
					}
				}
				inside += insideJ;
				globalInside.addAndGet(insideJ);
				globalTotal.addAndGet(UPDATE_EACH);
			}
			pi = inside * 4.0 / iterations;
		}
	}
}
