package primes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* 
 Steps:

 1. Copy your or the given solution to PrimesLive to the class below. We keep the 
 original so that we can compare results.

 2. Fix the races by replacing the array index with an AtomicInteger.
 */

public class PrimesLiveAtomic extends PrimesComputation implements
		LiveResults<Integer[]> {

	private Integer[] liveResults;
	private AtomicInteger count = new AtomicInteger();

	@Override
	public Boolean[] computePrimes(int upto) {
		liveResults = new Integer[upto];
		count.set(0);

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
			for (int x = lo; x < hi; x++) {
				results[x] = isPrime(x);
				if (results[x])
					liveResults[count.getAndIncrement()] = x;
			}
		}
	}

	/**
	 * Returns an array of prime numbers. Each getResults() invocation returns
	 * the same array with increasingly larger number of cells replaced by prime
	 * numbers, as the computation progresses. This method can be invoked at any
	 * time from outside the PrimesLive object.
	 */
	@Override
	public Integer[] getPrimes() {
		return liveResults;
	}

	/**
	 * Returns the number of primes that have been computed so far. This method
	 * can be invoked at any time from outside the PrimesLive object.
	 */
	@Override
	public int primesCount() {
		return count.get();
	}

}
