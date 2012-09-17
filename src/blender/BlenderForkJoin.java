package blender;

import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import util.UnimplementedExercise;

/**
 * Fork-join implementation of image blending
 */

/*
 1.	This time we will start from the ThreadPool solution. Copy either your or 
  	the given solution to the class below.

 2.	In BlenderForkJoin, transform the computation task to not implement Runnable, but
	extend RecursiveAction (the code in process() will now break).

 3. In process(), create a ForkJoinPool (using its constructor) instead of an
	ExecutorService.

 4.	Instead of creating many tasks, create only one RecursiveAction task and pass 
	it to the pool's execute().

 5.	Now, there is no parallelism. Introduce parallelism by forking the computation in
	compute(). If the size of the task is greater than some threshold, split the task by 
	creating two or more new tasks and pass them to invokeAll().

 5. Remove the UnimplementedExercise interface and test the implementation. 
 	The performance should be similar to that of the thread pool version.
 */

public class BlenderForkJoin extends Blender implements UnimplementedExercise {

	public BlenderForkJoin(BufferedImage img1, BufferedImage img2,
			int[] imageBuffer, MemoryImageSource imageSource) {
		super(img1, img2, imageBuffer, imageSource);

	}

	@Override
	public void process() {
		// TODO: implement this method
		return;
	}
}
