package blender;

import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Fork-join implementation of image blending
 */

/*
 * 1. This time we will start from the ThreadPool solution. Copy either your or
 * the given solution to the class below.
 * 
 * 2. In BlenderForkJoin, transform the computation task to not implement
 * Runnable, but extend RecursiveAction (the code in process() will now break).
 * 
 * 3. In process(), create a ForkJoinPool (using its constructor) instead of an
 * ExecutorService.
 * 
 * 4. Instead of creating many tasks, create only one RecursiveAction task and
 * pass it to the pool's execute().
 * 
 * 5. Now, there is no parallelism. Introduce parallelism by forking the
 * computation in compute(). If the size of the task is greater than some
 * threshold, split the task by creating two or more new tasks and pass them to
 * invokeAll().
 * 
 * 5. Remove the UnimplementedExercise interface and test the implementation.
 * The performance should be similar to that of the thread pool version.
 */

public class BlenderForkJoin extends Blender {

	public BlenderForkJoin(BufferedImage img1, BufferedImage img2,
			int[] imageBuffer, MemoryImageSource imageSource) {
		super(img1, img2, imageBuffer, imageSource);

	}

	@Override
	public void process() {
		ForkJoinPool pool = new ForkJoinPool();
		BlenderPartialComputationTask task = new BlenderPartialComputationTask(
				0, height);
		pool.invoke(task);
	}

	@SuppressWarnings("serial")
	private class BlenderPartialComputationTask extends RecursiveAction {

		private int loRow;
		private int hiRow;
		private int threshold = 100;

		public BlenderPartialComputationTask(int loRow, int hiRow) {
			this.loRow = loRow;
			this.hiRow = hiRow;

		}

		@Override
		public void compute() {
			if (hiRow - loRow < threshold) {
				int[] rgbim1 = new int[width];
				int[] rgbim2 = new int[width];

				for (int row = loRow; row < hiRow; row++) {
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

						imageBuffer[row * width + col] = new java.awt.Color(r3,
								g3, b3).getRGB();
					}

					imageSource.newPixels(0, row, width, 1, true);

				}
			} else {
				int middle = (hiRow + loRow) / 2;
				BlenderPartialComputationTask first = new BlenderPartialComputationTask(
						loRow, middle);
				BlenderPartialComputationTask second = new BlenderPartialComputationTask(
						middle, hiRow);
				
				first.fork();
				second.compute();
				first.join();
			}
		}
	}
}
