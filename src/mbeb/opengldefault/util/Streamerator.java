package mbeb.opengldefault.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * utility functions to use the fast Iterators of java with the comfort functions of Streams
 */
public class Streamerator {

	private Streamerator() {
		//should never be instantiated
	}

	/**
	 * create a new Iterator that iterates over the elements of an array
	 * 
	 * @param data
	 *            the array to wrap
	 * @param <T>
	 *            array type
	 * @return an iterator view of an array
	 */
	public static <T> Iterator<T> ofArray(final T[] data) {
		return new Iterator<T>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				return index < data.length;
			}

			@Override
			public T next() {
				return data[index++];
			}
		};
	}

	/**
	 * lazily apply a mapping function to all the elements of an iterator
	 * 
	 * @param mapped
	 *            an iterator to map
	 * @param mapper
	 *            the function to apply lazily to all elements of the iterator
	 * @param <T>
	 *            source type
	 * @param <V>
	 *            result type
	 * @return another iterator of the mapped values
	 */
	public static <T, V> Iterator<V> map(final Iterator<T> mapped, final Function<T, V> mapper) {
		return new Iterator<V>() {
			@Override
			public boolean hasNext() {
				return mapped.hasNext();
			}

			@Override
			public V next() {
				return mapper.apply(mapped.next());
			}
		};
	}

	/**
	 * collect an iterator to a list
	 * 
	 * @param iterator
	 *            the (not infinite) iterator to collect
	 * @param <T>
	 *            the type of the data
	 * @return a list containing all the elements of the iterator
	 */
	public static <T> List<T> asList(Iterator<T> iterator) {
		List<T> list = new ArrayList<>();
		iterator.forEachRemaining(list::add);
		return list;
	}
}
