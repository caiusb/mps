package primes;

import util.UnimplementedExercise;

/*
 In this exercise, we make the Primes example slightly more complex. 
 Let's say we want to gather the prime numbers in an array, each element of 
 the array storing one prime number. Also, we want this array to be updated live, 
 as the computation progresses.

 1.	We start from the thread pool version of this exercise. Replace the computePrimes
  	below with the computePrimes and the companion inner class from either 
  	your or the given PrimesPool solution. 

 2.	Create an int array field in PrimesLive. This array will keep the live results.
 	Also, create an int field to keep the current number of found primes.

 3. PrimesLive implements LiveResults. Implement the two methods of the interface.

 4. Make the run() method of the computation task also add elements to the live 
 	array as they are computed.

 5. Remove the UnimplementedExercise interface, then run the test. In previous MPs, you've 
 	seen that  the test code also displays the number of primes found. This is a simple yet
 	effective way of checking the different versions of the algorithm run correctly.
 	In this last case, we also display the number of elements in the live array at the 
 	end of the computation. Does it have the expected value? Why?
 	Do not add synchronization here. The next exercise (PrimesLiveSync) 
    will ask you to introduce synchronization.
 */

public class PrimesLive extends PrimesComputation implements
		LiveResults<Integer[]>, UnimplementedExercise {

	@Override
	public Boolean[] computePrimes(int upto) {
		// TODO: implement this method
		return null;
	}

	/**
	 * Returns an array of prime numbers. Each getResults() invocation returns
	 * the same array with increasingly larger number of cells replaced by prime numbers,
	 * as the computation progresses.
	 * This method can be invoked at any time from outside the PrimesLive object.
	 */
	@Override
	public Integer[] getPrimes() {
		// TODO implement this method
		return null;
	}

	/**
	 * Returns the number of primes that have been computed so far.
	 * This method can be invoked at any time from outside the PrimesLive object.
	 */
	@Override
	public int primesCount() {
		// TODO implement this method
		return 0;
	}

}
