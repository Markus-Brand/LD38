package mbeb.opengldefault.logging;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * class for logging, asserting, erroring, filing
 *
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public class Log {

	/** Class Name Tag */
	private static final String TAG = "Log";
	/** current log mode */
	static LogMode logMode;
	/** Log file for logging in file */
	static File logFile;
	/** File writer for logging in file */
	static PrintWriter writer;

	/**
	 * Inits Debug width given debug mode
	 *
	 * @param mode
	 *            (File, Console Nonsole...)
	 */
	public static void initDebug(final LogMode mode) {
		logMode = mode;
		if (logMode == LogMode.LOGFILE) {

			// if the directory does not exist, create it
			final File loggingDirectory = new File("log");
			if (!loggingDirectory.exists()) {
				loggingDirectory.mkdir();
			}

			final DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss");
			final Date today = Calendar.getInstance().getTime(); //what happens when the date changes while this is running?
			final String log = dateFormat.format(today);
			logFile = new File("log/" + log + ".log");
			try {
				writer = new PrintWriter(logFile);
			} catch(final FileNotFoundException e1) {
				e1.printStackTrace();
			}
			try {
				if (!logFile.createNewFile()) {
					Log.error(TAG, "Cannot create logging File");
				}
			} catch(final IOException e) {
				Log.error(TAG, "Error creating logging file", e);
			}
		}
	}

	/**
	 * closes log file when closing window.
	 */
	public static void closeLogFile() {
		if (logMode == LogMode.LOGFILE) {
			writer.close();
		}
	}

	/**
	 * log message
	 *
	 * @param additionalInformation
	 *            the object (mostly Strings) being logged
	 */
	public static void log(final String tag, final Object additionalInformation) {
		if (logMode == LogMode.NONE) {
			return;
		}
		final String log = constructErrorMessage(additionalInformation, "LOG: ", tag);
		if (logMode == LogMode.CONSOLE) {
			System.out.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * error message
	 *
	 * @param tag
	 *            of the calling glass
	 * @param additionalInformation
	 */
	public static void error(final String tag, final Object additionalInformation) {
		error(tag, additionalInformation, null);
	}

	/**
	 * error message
	 *
	 * @param tag
	 *            of the calling glass
	 * @param additionalInformation
	 * @param throwable
	 */
	public static void error(final String tag, final Object additionalInformation, final Throwable throwable) {
		if (logMode == LogMode.NONE) {
			return;
		}
		final Object toLog = (throwable == null ? additionalInformation : (additionalInformation.toString() + " (" + throwable.getLocalizedMessage() + ")"));
		final String log = constructErrorMessage(toLog, "ERROR: ", tag);
		if (logMode == LogMode.CONSOLE) {
			System.err.println(log);
			if (throwable != null) {
				throwable.printStackTrace(System.err);
			}
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * @param additionalInformation
	 * @param info
	 *            (for example "LOG: ")
	 * @param tag
	 *            of the calling glass
	 * @return error message a la: <br>
	 *         [MM/dd/yyyy HH:mm:ss] <i>info</i> In class <i>tag</i>: <i>additionalInformation</i>
	 */
	private static String constructErrorMessage(final Object additionalInformation, final String info, final String tag) {
		final String message = additionalInformation.toString();
		final DateFormat df = new SimpleDateFormat("[MM/dd/yyyy HH:mm:ss] ");
		final Date today = Calendar.getInstance().getTime();
		String log = df.format(today);
		log += info;
		if (tag != null) {
			log += "In class " + tag + ": ";
		}
		log += message;
		return log;
	}

	/**
	 * Assert that condition is true
	 *
	 * @param tag
	 *            of the calling glass
	 * @param condition
	 * @param obj
	 * @throws AssertionError
	 */
	public static void assertTrue(final String tag, final boolean condition, final Object obj) {
		if (logMode == LogMode.NONE || condition) {
			return;
		}
		final String log = constructErrorMessage(obj, "ASSERTION: ", tag);
		if (logMode == LogMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
		throw new AssertionError(log);//TODO think about using error method or leaving it bee
	}

	/**
	 * Assert that condition is false
	 *
	 * @param tag
	 *            of the calling glass
	 * @param condition
	 * @param obj
	 * @throws AssertionError
	 */
	public static void assertFalse(final String tag, final boolean condition, final Object obj) {
		assertTrue(tag, !condition, obj);
	}

	/**
	 * Assert that testObject equals referenceObject
	 *
	 * @param tag
	 *            of the calling glass
	 * @param testObject
	 * @param referenceObject
	 * @param obj
	 * @throws AssertionError
	 */
	public static void assertEqual(final String tag, final Object testObject, final Object referenceObject, final Object obj) {
		assertTrue(tag, testObject.equals(referenceObject), obj);
	}

	/**
	 * assert that testObject isn't equal referenceObject
	 *
	 * @param tag
	 *            of the calling glass
	 * @param testObject
	 * @param referenceObject
	 * @param obj
	 * @throws AssertionError
	 */
	public static void assertNotEqual(final String tag, final Object testObject, final Object referenceObject, final Object additionalInformation) {
		assertTrue(tag, !testObject.equals(referenceObject), additionalInformation);
	}

	/**
	 * assert that <i>object</i> != null
	 *
	 * @param tag
	 *            of the calling glass
	 * @param object
	 *            under test
	 * @param additionalInformation
	 * @throws AssertionError
	 */
	public static void assertNotNull(final String tag, final Object object, final Object additionalInformation) {
		assertTrue(tag, object != null, additionalInformation);
	}
}
