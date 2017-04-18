package mbeb.opengldefault.rendering.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mbeb.opengldefault.logging.Log;

/**
 * Parse the content of a simple YAML file
 */
public class YAMLParser {

	private static final String TAG = "YAMLParser";

	private File file;
	private YAMLNode root = null;

	public YAMLParser(File file) {
		this.file = file;
	}

	/**
	 * parse the yaml file if not happened already
	 * 
	 * @return the YAML root-node
	 */
	public YAMLNode getRoot() {
		if (root == null) {
			try {
				Iterator<String> lines = Files.lines(file.toPath()).filter((String s) -> !s.startsWith("#")).iterator();
				root = parse(new PeekableIterator<>(lines), new YAMLNode("root", null), 0);
			} catch(IOException e) {
				Log.error(TAG, "Cannot find file " + file.getName(), e);
			}
		}
		return root;
	}

	/**
	 * parse the content of a Node
	 * 
	 * @param lines
	 *            the Content iterator
	 * @param depth
	 *            all lines with indentation of <code>depth</code> are my children
	 * @return me
	 */
	private YAMLNode parse(PeekableIterator<String> lines, YAMLNode me, int depth) {
		while(lines.hasNext()) {
			String nextLine = lines.peek();

			if (indentation(nextLine) == depth) {
				String[] childDataArray = lines.next().split(":");
				String childName = childDataArray[0].trim();
				String childData = (childDataArray.length < 2) ? null : childDataArray[1].trim();
				me.addChild(parse(lines, new YAMLNode(childName, childData), depth + 2));
			} else {
				return me;
			}
		}
		return me;
	}

	/**
	 * @param line
	 * @return the number of leading spaces of the given String
	 */
	private int indentation(String line) {
		int indent = 0;
		for (char c : line.toCharArray()) {
			if (c == ' ') {
				indent++;
			} else {
				return indent;
			}
		}
		return indent;
	}

	/**
	 * An Object inside a YAML-File (composite)
	 */
	public static class YAMLNode {
		private final String name;
		private final String data;
		private Map<String, YAMLNode> children = null;

		public YAMLNode(String name, String data) {
			this.name = name;
			this.data = data;
		}

		public String getName() {
			return name;
		}

		public Map<String, YAMLNode> getChildren() {
			if (children == null) {
				children = new HashMap<>();
			}
			return children;
		}

		public void addChild(YAMLNode newChild) {
			getChildren().put(newChild.getName(), newChild);
		}

		public String getData() {
			return data;
		}

		public YAMLNode getChildByName(String name) {
			return children.get(name);
		}
	}

	/**
	 * An Iterator<T> that allows peeking of the next()-value
	 */
	private static class PeekableIterator<T> {
		private T next = null;
		private Iterator<T> it;

		public PeekableIterator(Iterator<T> it) {
			this.it = it;
		}

		public T next() {
			if (next != null) {
				T result = next;
				next = null;
				return result;
			} else {
				return it.next();
			}
		}

		public boolean hasNext() {
			return next != null || it.hasNext();
		}

		public T peek() {
			if (next == null) {
				next = it.next();
			}
			return next;
		}
	}
}
