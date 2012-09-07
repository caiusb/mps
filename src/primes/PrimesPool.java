package primes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import primes.PrimesThreads.PrimesApproximationThread;
import util.UnimplementedExercise;

/*
 The goal of this exercise is to parallelize the primes 
 computation using a thread pool instead of an array 
 of manually managed Threads

 Steps:

 1. Copy your implementation of computePrimes() from your 
 PrimesThreads class (the previous exercise) if it worked well.
 Otherwise, copy the implementation from primes.solutions.PrimesThreads

 2. Transform the computation thread class to not extend Thread, 
 but implement Runnable (the code in computePrimes() will break).

 3. Create a new ExecutorService by invoking Executors.newFixedThreadPool()

 4. Instead of creating an array of threads, create a number of tasks (grater than 
 the number of threads) and pass them to ExecutorService's execute() method.

 5. Instead of joining Threads, shutdown() the thread pool and awaitTermination(60, TimeUnit.SECONDS).

 6. Remove the UnimplementedExercise interface and test using the Driver.
 */

public class PrimesPool extends PrimesComputation {

	@Override
	public Boolean[] computePrimes(int upto) {
		int noOfCores = Runtime.getRuntime().availableProcessors();
		int chunkSize = 2000;
		int tasks = upto / chunkSize;
		Boolean[] results = new Boolean[upto];

		ExecutorService threadPool = Executors.newFixedThreadPool(noOfCores);

		for (int i = 0; i < tasks; i++)
			threadPool.execute(new PrimesApproximationTask(results, i
					* chunkSize, (i + 1) * chunkSize));

		threadPool.shutdown();
		try {
			while (!threadPool.awaitTermination(500, TimeUnit.MILLISECONDS))
				;
		} catch (InterruptedException e) {
		}

		return results;
	}

	class PrimesApproximationTask implements Runnable {

		private Boolean[] results;
		private int lo;
		private int hi;

		public PrimesApproximationTask(Boolean[] results, int lo, int hi) {
			this.results = results;
			this.lo = lo;
			this.hi = hi;

		}

		@Override
		public void run() {
			for (int x = lo; x < hi; x++)
				results[x] = isPrime(x);
		}
	}
}
