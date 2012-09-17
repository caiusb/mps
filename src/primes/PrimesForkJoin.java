package primes;

import util.UnimplementedExercise;

/**
 * Fork-join style implementation for primes computation
 */

/*
	Steps:

 1. Create an inner class that extends RecursiveAction and move the
	computation to the compute() method of that task class.

 2. In computePrimes(), create a new ForkJoinPool (using its constructor;
	instead of an ExecutorService, as before)

 3. Initialize a RecursiveAction task and pass it to the pool's invoke().

 4. Now, the program should work but there is no parallelism. Introduce
	parallelism by forking the computation in compute(). If the size of the task
	is greater than some threshold, split the task by creating two or more new
	tasks and pass them to invokeAll().

 5. Remove the UnimplementedExercise interface and test the implementation.
	The performance should be similar to that of the thread pool version.
 */

public class PrimesForkJoin extends PrimesComputation implements
		UnimplementedExercise {
	@Override
	public Boolean[] computePrimes(int upto) {
		Boolean[] results = new Boolean[upto];
		for (int x = 0; x < results.length; x++)
			results[x] = isPrime(x);
		return results;
	}

}
