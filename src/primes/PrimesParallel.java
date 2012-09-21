package primes;

import extra166y.Ops.IntToObject;
import extra166y.ParallelArray;

/**
 * ParallelArray version
 */

/*
 * Steps:
 * 
 * 1. Replace the array of boolean results with a ParallelArray<Boolean> by
 * using the factory method ParallelArray.create(). e.g.
 * ParallelArray.create(size, Boolean.class, ParallelArray.defaultExecutor());
 * 
 * 2. Use the replaceWithMappedIndex() parallel operation and pass it an
 * IntToObject<Boolean> operator. Similar to the sequential "for" loop, the
 * parallel operation will populate the ParallelArray with whether the value at
 * a certain index is prime or not. Implement the operator accordingly.
 * 
 * 3. Return ParallelArray's inner array by using getArray().
 * 
 * 4. Remove the UnimplementedExercise interface and test using Driver.main().
 * The performance should be worse than the thread pool and fork-join versions.
 * This is because ParallelArray uses much more fine grained tasks and, even
 * with its very efficient work distribution algorithm, it cannot beat a manual
 * implementation. The up side is very readable code and easy adaptation to new
 * hardware.
 */

public class PrimesParallel extends PrimesComputation {
	@Override
	public Boolean[] computePrimes(int upto) {

		ParallelArray<Boolean> array = ParallelArray.create(upto,
				Boolean.class, ParallelArray.defaultExecutor());

		array.replaceWithMappedIndex(new IntToObject<Boolean>() {

			@Override
			public Boolean op(int x) {
				return isPrime(x);
			}
		});

		return array.getArray();
	}
}
