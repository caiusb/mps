package primes;

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

public class PrimesPool extends PrimesComputation implements
		UnimplementedExercise {

	@Override
	public Boolean[] computePrimes(int upto) {
		// TODO: implement this method
		return null;
	}

}
