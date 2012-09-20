package blender;

import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import util.UnimplementedExercise;

/**
 * ParallelArray implementation.
 */

/*
Steps:

 1.	In this step, the aim is to convert the outer "for" loop to a ParallelArray 
	operation. Unfortunately, currently ParallelArray does not have an operator that can
	run over a 2D array. Neither does it have an operator that can run a for in parallel
	without an underlying data structure. Hopefully, these shortcomings will be solved by 
	time ParallelArray will be included in Java8 next year. In the mean time, we're 
	going to trick it into working. Create a ParallelLongArray using the static factory 
	method create(). We use this as an empty shell so that we can run a parallel 
	operation. Next, replace the outer loop of the algorithm with a 
	replaceWithMappedIndex() operation  over this array. The operator needs to return a
	value. We're not going to use that value in any way, so return whichever long constant
	you like.

 2. Remove the UnimplementedExercise annotation and run the application and
	see how it performs. Again, it will be slighly slower than the fork-join and 
	thread pool versions.
*/

public class BlenderParallel extends Blender implements UnimplementedExercise {
	
	public BlenderParallel(BufferedImage img1, BufferedImage img2,
			int[] imageBuffer, MemoryImageSource imageSource) {
		super(img1, img2, imageBuffer, imageSource);
	}

	@Override
	public void process() {
		int[] rgbim1 = new int[width];
		int[] rgbim2 = new int[width];

		for (int row = 0; row < height; row++) {
			img1.getRGB(0, row, width, 1, rgbim1, 0, width);
			img2.getRGB(0, row, width, 1, rgbim2, 0, width);

			for (int col = 0; col < width; col++) {
				int rgb1 = rgbim1[col];
				int r1 = (rgb1 >> 16) & 255;
				int g1 = (rgb1 >> 8) & 255;
				int b1 = rgb1 & 255;

				int rgb2 = rgbim2[col];
				int r2 = (rgb2 >> 16) & 255;
				int g2 = (rgb2 >> 8) & 255;
				int b2 = rgb2 & 255;

				int r3 = (int) (r1 * weight + r2 * (1.0 - weight));
				int g3 = (int) (g1 * weight + g2 * (1.0 - weight));
				int b3 = (int) (b1 * weight + b2 * (1.0 - weight));

				imageBuffer[row * width + col] = new java.awt.Color(r3, g3, b3)
						.getRGB();
			}

			imageSource.newPixels(0, row, width, 1, true);
		}
	}
}
