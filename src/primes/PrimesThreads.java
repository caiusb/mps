package primes;


/*
 The goal is to parallelize the sequential implementation using threads.

 In a manner similar to the previous exercise, split the computation of 
 the primes between multiple threads.

 Steps:

 1. Create an inner class that extends Thread
 2. Transfer the algorithm to the inner class's run()
 3. Adapt the thread to only compute its proportional part of the workload
 	- hint: for simplicity, don't create a boolean array in each thread;
 	simply pass the shared outer one
 4. Remove the UnimplementedExercise interface
 5. Test using the Driver
 */

public class PrimesThreads extends PrimesComputation {
	
	@Override
	public Boolean[] computePrimes(int upto) {
		int noOfCores = Runtime.getRuntime().availableProcessors();
		int chunk = upto / noOfCores;
		Boolean[] results = new Boolean[upto];
		PrimesApproximationThread[] thread = new PrimesApproximationThread[noOfCores];

		for (int i=0; i<noOfCores; i++) {
			thread[i] = new PrimesApproximationThread(results, chunk*i, chunk*(i+1));
			thread[i].start();
		}
		
		for (int i=0; i<noOfCores; i++)
			try {
				thread[i].join();
			} catch (InterruptedException e) {
			}			
		
		return results;
	}
	
	class PrimesApproximationThread extends Thread {
		
		private Boolean[] results;
		private int lo;
		private int hi;

		public PrimesApproximationThread(Boolean[] results, int lo, int hi) {
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