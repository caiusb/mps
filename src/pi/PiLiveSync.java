package pi;

import java.util.Random;

import util.UnimplementedExercise;

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
 * 1. Copy your PiLive solution to the class below.
 * 
 * 2. Fix the races using synchronization.
 */

public class PiLiveSync implements PiApproximation, UnimplementedExercise {

	private PiApproximationThread[] threads;
	private int noOfCores;

	@Override
	public double computePi(long iterations) {
		noOfCores = Runtime.getRuntime().availableProcessors();
		threads = new PiApproximationThread[noOfCores];

		long partialIterations = iterations / noOfCores;
		for (int i = 0; i < noOfCores; i++) {
			threads[i] = new PiApproximationThread(partialIterations);
			threads[i].start();
		}

		for (int i = 0; i < noOfCores; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
			}

		double partialResult = 0;
		for (int i = 0; i < noOfCores; i++)
			partialResult += threads[i].getResult();

		return partialResult / (double) noOfCores;
	}

	class PiApproximationThread extends Thread {

		private double partialPi = 0;
		private final long iterations;
		private long inside = 0;
		private int soFar = 0;

		public PiApproximationThread(long iterations) {
			this.iterations = iterations;
		}

		public double getResult() {
			return partialPi;
		}

		public double getPartial() {
			return ((double) inside) / soFar * 4;
		}

		@Override
		public void run() {
			inside = 0;
			Random randomNumberGenerator = new Random();
			for (soFar = 0; soFar < iterations; soFar++) {
				double x = randomNumberGenerator.nextDouble();
				double y = randomNumberGenerator.nextDouble();
				double lenght = x * x + y * y;
				if (lenght < 1.0)
					inside++;
			}
			partialPi = ((double) inside) / iterations * 4;
		}
	}

	/**
	 * Return the most up-to-date Pi approximation. This method can be invoked
	 * at any time from outside the PiLive object.
	 */
	public double liveValue() {
		double partialResult = 0;
		for (PiApproximationThread thread : threads) {
			partialResult += thread.getPartial();
		}

		return partialResult / noOfCores;
	}
}
