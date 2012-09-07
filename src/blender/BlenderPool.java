package blender;

import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import util.UnimplementedExercise;

/*
 The goal of this exercise is to parallelize the blender 
 computation using a thread pool instead of an array 
 of manually managed Threads

 1. Copy your implementation of process() from your BlenderThreads 
 	class (the previous exercise) if it worked well.
 	Otherwise, copy the implementation from blender.solutions.BlenderThreads

 2. Convert the Thread-based implementation to a thread pool one 
	  in a manner similar to the previous exercise.

 3. Remove the UnimplementedExercise interface and test.
*/

public class BlenderPool extends Blender implements UnimplementedExercise {

	public BlenderPool(BufferedImage img1, BufferedImage img2,
			int[] imageBuffer, MemoryImageSource imageSource) {
		super(img1, img2, imageBuffer, imageSource);

	}

	@Override
	public void process() {
		// TODO: implement this method
		return;
	}
}
