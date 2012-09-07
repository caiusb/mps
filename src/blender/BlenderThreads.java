package blender;

import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

/**
 * Parallel implementation of Blender with using the Thread class.
 */

/*
 * Parallelize the implementation in PrimesThreads using threads in a similar
 * fashion to the previous exercise. Allow the InterruptedException to propagate
 * to run() and catch it there.
 * 
 * No detailed steps this time :)
 */

public class BlenderThreads extends Blender {

	public BlenderThreads(BufferedImage img1, BufferedImage img2,
			int[] imageBuffer, MemoryImageSource imageSource) {
		super(img1, img2, imageBuffer, imageSource);

	}

	@Override
	public void process() {
		int noOfCores = Runtime.getRuntime().availableProcessors();
		int chunk = height/noOfCores;
		BlenderPartialComputationThread[] threads = new BlenderPartialComputationThread[noOfCores];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new BlenderPartialComputationThread(i*chunk,(i+1)*chunk);
			threads[i].start();
		}
		
		for (int i = 0; i < threads.length; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
			}
	}

	class BlenderPartialComputationThread extends Thread {

		private int loRow;
		private int hiRow;

		public BlenderPartialComputationThread(int loRow, int hiRow) {
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
