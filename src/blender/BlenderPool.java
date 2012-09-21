package blender;

import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

public class BlenderPool extends Blender {

	public BlenderPool(BufferedImage img1, BufferedImage img2,
			int[] imageBuffer, MemoryImageSource imageSource) {
		super(img1, img2, imageBuffer, imageSource);

	}

	@Override
	public void process() {
		int noOfCores = Runtime.getRuntime().availableProcessors();
		int noOfTasks = noOfCores;
		int chunkSize = height / noOfTasks;
		ExecutorService threadPool = Executors.newFixedThreadPool(noOfCores);

		for (int i = 0; i < noOfTasks; i++)
			threadPool.execute(new BlenderPartialComputationTask(i * chunkSize,
					(i + 1) * chunkSize));

		threadPool.shutdown();
		try {
			while (!threadPool.awaitTermination(500, TimeUnit.MILLISECONDS))
				;
		} catch (InterruptedException e) {
			System.out.println("WTF???!!");
		}
	}

	class BlenderPartialComputationTask implements Runnable {

		private int loRow;
		private int hiRow;

		public BlenderPartialComputationTask(int loRow, int hiRow) {
			this.loRow = loRow;
			this.hiRow = hiRow;

		}

		@Override
		public void run() {
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

					imageBuffer[row * width + col] = new java.awt.Color(r3, g3,
							b3).getRGB();
				}

				imageSource.newPixels(0, row, width, 1, true);

			}
		}
	}
}
