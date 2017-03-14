package mbeb.opengldefault.rendering.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parse the content of a simple YAML file
 */
public class YAMLParser {

	private File file;
	private YAMLNode root = null;

	public YAMLParser(File file) {
		this.file = file;
	}

	public YAMLNode parse() {
		if (root == null) {
			try {
				Iterator<String> lines = Files.lines(file.toPath()).filter((String s) -> !s.startsWith("#")).iterator();
				root = parse(new PeekableIterator<>(lines), new YAMLNode("root", null), 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return root;
	}

	/**
	 *
	 * @param lines
	 * @param depth all lines with indentation of <code>depth</code> are my children
	 * @return
	 */
	private YAMLNode parse(PeekableIterator<String> lines, YAMLNode me, int depth) {
		while (lines.hasNext()) {
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

	public static class YAMLNode {
		private final String name;
		private final String data;
		private List<YAMLNode> children = null;

		public YAMLNode(String name, String data) {
			this.name = name;
			this.data = data;
		}

		public String getName() {
			return name;
		}

		public List<YAMLNode> getChildren() {
			if (children == null) {
				children = new ArrayList<>();
			}
			return children;
		}

		public void addChild(YAMLNode newChild){
			getChildren().add(newChild);
		}

		public String getData() {
			return data;
		}
	}

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
