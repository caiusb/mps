package pi;

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
 Steps:
	
 1. Copy your or the given solution to PiLive to the class below.

 2. Fix the races using an Atomic construct.
 */

public class PiLiveAtomic implements PiApproximation, UnimplementedExercise {

	@Override
	public double computePi(long iterations) {
		// TODO: implement
		return 100.0;
	}

	public double liveValue() {
		// TODO: implement
		return 100.0;
	}
}
