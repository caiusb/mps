package pi;

public interface LiveValue {
	/**
	 * Return the most up-to-date approximation. This method can be invoked
	 * at any time from outside the implementing object.
	 */
	public double liveValue();
}
