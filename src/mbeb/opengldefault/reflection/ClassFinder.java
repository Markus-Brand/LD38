package mbeb.opengldefault.reflection;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Used for finding all Classes in a package
 * 
 * @author Markus
 */
public class ClassFinder {

	/** Character used as Package Seperator */
	private static final char PACKAGE_SEPARATOR = '.';

	/** Character used as Directory Seperator */
	private static final char DIRECTORY_SEPARATOR = '/';

	/** Suffix of the file type that will be searched */
	private static final String CLASS_FILE_SUFFIX = ".class";

	/** Error if the package does not exist */
	private static final String BAD_PACKAGE_ERROR =
			"Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

	public static List<Class<?>> findAllClassesInPackage(String scannedPackage) {
		String scannedPath = scannedPackage.replace(PACKAGE_SEPARATOR, DIRECTORY_SEPARATOR);
		URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
		if (scannedUrl == null) {
			throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
		}
		File scannedDir = new File(scannedUrl.getFile());
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File file : scannedDir.listFiles()) {
			classes.addAll(find(file, scannedPackage));
		}
		return classes;
	}

	/**
	 * Method that will be used recursively to find all classes
	 * 
	 * @param file
	 *            the current file or directory
	 * @param scannedPackage
	 *            current package
	 * @return
	 */
	private static List<Class<?>> find(File file, String scannedPackage) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		String resource = scannedPackage + PACKAGE_SEPARATOR + file.getName();
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				classes.addAll(find(child, resource));
			}
		} else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
			int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
			String className = resource.substring(0, endIndex);
			try {
				classes.add(Class.forName(className));
			} catch(ClassNotFoundException ignore) {}
		}
		return classes;
	}

}
