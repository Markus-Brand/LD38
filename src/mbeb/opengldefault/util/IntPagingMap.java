package mbeb.opengldefault.util;

import java.util.Iterator;

/**
 * A PagingMap with integers as pages
 */
public class IntPagingMap<V> extends PagingMap<Integer, V> {
	/**
	 * create a new PagingMap that fastly prefers using new pages over unbinding objects
	 *
	 * @param universeCreator an iterator that creates the Page-objects
	 */
	public IntPagingMap(Iterator<Integer> universeCreator) {
		super(universeCreator, PageGatherStrategy.DEPLETE_UNIVERSE);
	}
	
	/**
	 * create a new PagingMap that simply counts up page numbers
	 * @param amount the maximum amount of pages
	 */
	public IntPagingMap(int amount) {
		this(new IntIterator(amount));
	}
}
