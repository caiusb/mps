package pi;

/***
 * 
 * This class uses an approximation algorithm to compute pi. Since the area of a
 * circle is pi*r^2, one can find an approximate value of pi by approximating
 * the area of a circle with radius r. <br/>
 * 
 * Imagine a circle inscribed in a square with side length 2.0. Thus, the radius
 * of the circle is 1.0. The area of a circle of radius r is: pi * r * r. So,
 * the area of our circle of radius 1 is: pi * 1.0 * 1.0 = pi Hence, pi has the
 * same value as the area of our circle. <br/>
 * 
 * We can approximate the area of the circle by generating random points in our
 * square of side length 2.0 and counting how many of them are within our
 * circle, i.e., their distance from the center is less than 1.0.<br/>
 * 
 * So, the area of our circle is the ratio between the number of inscribed
 * points and the total number of points, times 4. Thus, pi is the same ratio,
 * times 4.<br/>
 * 
 * For simplicity, we only generate points within one quadrant of the square but
 * this does not affect the ratio.<br/>
 */

public class PiSequential implements PiApproximation {

	@Override
	public double computePi(long iterations) {
		long inside = 0;
		for (int j = 0; j < iterations; j++) {
			double x = Math.random();
			double y = Math.random();

			double lenght = x * x + y * y;
			if (lenght < 1.0)
				inside++;
		}
		return

		((double) inside) / iterations * 4;
	}
}