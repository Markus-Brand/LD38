package mbeb.opengldefault.util;

import java.util.Iterator;

/**
 * An Iterator<Integer> that counts up to a specified value
 */
public class IntIterator implements Iterator<Integer> {
	
	/** the maximum to count to */
	private int max;
	/** how far this Iterator is already */
	private int counter;
	
	/**
	 * create a new up-counting iterator that counts "amount" - times: from 0 to amount-1
	 * @param amount
	 */
	public IntIterator(int amount) {
		this.max = amount;
		this.counter = 0;
	}
	
	@Override
	public boolean hasNext() {
		return counter < max;
	}
	
	@Override
	public Integer next() {
		return counter++;
	}
}
