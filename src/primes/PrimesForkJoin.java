package primes;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Fork-join style implementation for primes computation
 */

/*
 * Steps:
 * 
 * 1. Create an inner class that extends RecursiveAction and move the
 * computation to the compute() method of that task class.
 * 
 * 2. In computePrimes(), create a new ForkJoinPool (using its constructor;
 * instead of an ExecutorService, as before)
 * 
 * 3. Initialize a RecursiveAction task and pass it to the pool's invoke().
 * 
 * 4. Now, the program should work but there is no parallelism. Introduce
 * parallelism by forking the computation in compute(). If the size of the task
 * is greater than some threshold, split the task by creating two or more new
 * tasks and pass them to invokeAll().
 * 
 * 5. Remove the UnimplementedExercise interface and test the implementation.
 * The performance should be similar to that of the thread pool version.
 */

public class PrimesForkJoin extends PrimesComputation {
	
	@Override
	public Boolean[] computePrimes(int upto) {
		Boolean[] results = new Boolean[upto];
		ForkJoinPool pool = new ForkJoinPool();
		PrimesTask initialTask = new PrimesTask(0, upto, results);
		pool.invoke(initialTask);
		return results;
	}

	@SuppressWarnings("serial")
	private class PrimesTask extends RecursiveAction {

		private int upto;
		private int from;
		private Boolean[] results;
		private int threshold = 10000;

		public PrimesTask(int from, int upto, Boolean results[]) {
			this.from = from;
			this.upto = upto;
			this.results = results;
		}

		@Override
		protected void compute() {
			if (upto - from <= threshold)
				for (int x = from; x < upto; x++)
					results[x] = isPrime(x);
			else {
				int middle = (from + upto) / 2;
				PrimesTask firstHalf = new PrimesTask(from, middle, results);
				PrimesTask secondHalf = new PrimesTask(middle, upto,
						results);
				firstHalf.fork();
				secondHalf.compute();
				firstHalf.join();
			}
		}
	}

}
