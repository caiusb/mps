package pi;

import java.util.Random;

import pi.PiThreads.PiApproximationThread;
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
 * 
 *  In this exercise, we make the Pi example slightly more complex. 
 	Let's say we want a client of our class to be able to observe the most 
 	up-to-date approximation of pi as the computation progresses. 

	Steps:

 1. We start from the threads version of this exercise. Replace the computePi
  	below with the computePi and the companion inner class from either 
  	your or the given threads solution.

 2. Gather approximate results from the threads in a shared data structure.
	Make it possible for other concurrent threads to inspect the most up to date 
	value by calling the liveValue() method.
	
 3. Run PiTest. You might notice that the live approximation is slightly skewed. 
    This is not only because the computation is not over, but also because of 
    race conditions. Do not add synchronization here. The next exercise (PiLiveSync) 
    will ask you to introduce synchronization.
*/

public class PiLive implements PiApproximation, LiveValue, UnimplementedExercise {
	
	private PiApproximationThread[] threads;

	@Override
	public double computePi(long iterations) {
		int noOfCores = Runtime.getRuntime().availableProcessors();
		threads = new PiApproximationThread[noOfCores];
		
		long partialIterations = iterations/noOfCores;
		for (int i=0; i<noOfCores; i++) {
			threads[i] = new PiApproximationThread(partialIterations);
			threads[i].start();
		}
		
		for (int i=0; i<noOfCores; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
			}
		
		double partialResult = 0;
		for (int i=0; i<noOfCores; i++)
			partialResult += threads[i].getResult();
		
		return partialResult/(double)noOfCores;
	}

	class PiApproximationThread extends Thread {
		
		private double partialPi = 0;
		private final long iterations;
		
		public PiApproximationThread(long iterations) {
			this.iterations = iterations;
		}
		
		public double getResult() {
			return partialPi;
		}
		
		@Override
		public void run() {
			long inside = 0;
			Random randomNumberGenerator = new Random();
			for (int j = 0; j < iterations; j++) {
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
	 * Return the most up-to-date Pi approximation. This method can be invoked at any time
	 * from outside the PiLive object.
	 */
	public double liveValue() {
		// TODO: implement
		return 100.0;
	}
}
