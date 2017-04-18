package mbeb.opengldefault.util;

import java.util.*;

/**
 * a Map with a limited amount of pages that implements lazy outpaging via FIFO
 * @param <P> the class of the page
 * @param <V> the class of the objects that get paged (values)
 */
public class PagingMap<P, V> {
	
	/**
	 * different strategies on how to get space for a new object that needs a page
	 */
	public enum PageGatherStrategy {
		/**
		 * prefer creating a new page over unbinding soft-bound objects
		 */
		FILL_FREE,
		/**
		 * prefer unbinding a soft-bound object over creating a new page
		 */
		REPLACE_SOFT
	}
	
	/**
	 * comparable to a Map.Entry, only that it can be created on the fly
	 */
	private class BindingEntry {
		private final P page;
		private final Node node;
		private BindingEntry(P page, Node node) {
			this.page = page;
			this.node = node;
		}
	}
	
	/**
	 * an in-paged value together with a mark whether it is bound or can be removed from the mapping
	 */
	private class Node {
		private final V value;
		private boolean bound;
		public Node(V value) {
			this.value = value;
			bound = true;
		}
	}
	
	//the paging data structure
	private final Map<P, Node> bindings = new HashMap<>();
	private final Queue<BindingEntry> freePages = new LinkedList<>();
	private final Map<V, P> reverseBindings = new HashMap<>();
	
	//page creation
	private final Iterator<P> universeCreator;
	private final PageGatherStrategy pageGatherStrategy;
	
	/**
	 * create a new PagingMap
	 * @param universeCreator an iterator that creates the Page-objects
	 * @param strategy the strategy for acquiring free pages
	 */
	public PagingMap(Iterator<P> universeCreator, PageGatherStrategy strategy) {
		this.universeCreator = universeCreator;
		this.pageGatherStrategy = strategy;
	}
	
	/**
	 * tries to find a Page that is empty.
	 * @return a Page that is free (no object bound to it)
	 * @throws RuntimeException when there are no free pages left anymore
	 */
	private P findEmptyPage() {
		P page = null;
		if (pageGatherStrategy == PageGatherStrategy.REPLACE_SOFT) {
			page = findEmptyPageFromExisting();
			if (page == null) {
				page = createNewEmptyPage();
			}
		} else if (pageGatherStrategy == PageGatherStrategy.FILL_FREE){
			page = createNewEmptyPage();
			if (page == null) {
				page = findEmptyPageFromExisting();
			}
		}
		
		if (page == null) {
			throw new RuntimeException("No free pages left!");
		}
		return page;
	}
	
	/**
	 * find a page that is currently empty
	 * @return an empty page, or null if no one was empty
	 */
	private P findEmptyPageFromExisting() {
		P page = null;
		while (page == null && !freePages.isEmpty()) {
			BindingEntry freePage = freePages.remove();
			if (!freePage.node.bound) {
				page = freePage.page;
			}
		}
		return page;
	}
	
	/**
	 * create a new page object
	 * @return a new page object, or null if the universe is depleted
	 */
	private P createNewEmptyPage() {
		if (!universeCreator.hasNext()) {
			return null;
		}
		return universeCreator.next();
	}
	
	/**
	 * get the page that an object is bound to. This method solves soft page faults.
	 * @param value the object that may be bound
	 * @return this objects page (if it was still (at least soft) bound), or null
	 */
	public P getPageOf(V value) {
		P reverse = reverseBindings.get(value);
		if (reverse != null) {
			Node valueNode = bindings.get(reverse);
			if (valueNode.value == value) {
				//possible soft page fault
				valueNode.bound = true;
				return reverse;
			} else {
				//page fault
				reverseBindings.put(value, null);
				return null;
			}
		} else {
			//was never paged
			return null;
		}
	}
	
	/**
	 * make sure that an object is bound to a page
	 * @param value the object to bind if not bound already
	 * @return the page that this object is bound to
	 */
	public P bind(V value) {
		P oldPage = getPageOf(value);
		if (oldPage != null) {
			return oldPage;
		}
		P newPage = findEmptyPage();
		bindings.put(newPage, new Node(value));
		reverseBindings.put(value, newPage);
		return newPage;
	}
	
	/**
	 * mark an object as unbindable - it may be unbound later to make space
	 * @param value the object that is no longer needed
	 * @return the Page it was bound to, or null if it wasn't
	 */
	public P unbind(V value) {
		P page = reverseBindings.get(value);
		if (page == null) {
			return null;
		}
		Node valueNode = bindings.get(page);
		if (valueNode.value != value) {
			//page fault
			reverseBindings.put(value, null);
			return null;
		}
		if (valueNode.bound) {
			valueNode.bound = false;
			freePages.add(new BindingEntry(page, valueNode));
		}
		return page;
	}

	/**
	 * unbind a value totally
	 * @param value the object that is no longer needed
	 * @return the Page it was bound to, or null if it wasn't
	 */
	public P forceUnbind(V value) {
		P page = unbind(value);
		reverseBindings.put(value, null);
		return page;
	}
}
