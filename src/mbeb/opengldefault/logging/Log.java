package mbeb.opengldefault.logging;

import java.io.*;
import java.text.*;
import java.util.*;

public class Log {

	/** Class Name Tag */
	private static final String TAG = "Log";

	static LogMode logMode; //current log mode
	static File logFile; //Log file
	static PrintWriter writer; //File writer

	/**
	 * Inits Debug width given debug mode
	 *
	 * @param mode
	 */
	public static void initDebug(final LogMode mode) {
		logMode = mode;
		if (logMode == LogMode.LOGFILE) {

			// if the directory does not exist, create it
			final File theDir = new File("log");
			if (!theDir.exists()) {
				theDir.mkdir();
			}

			final DateFormat df = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss");
			final Date today = Calendar.getInstance().getTime();
			final String log = df.format(today);
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
	 * @param obj
	 *            the object (mostly Strings) being logged
	 */
	public static void log(final String tag, final Object obj) {
		if (logMode == LogMode.NONE) {
			return;
		}
		final String log = constructErrorMessage(obj, "LOG: ", tag);
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
	 * @param obj
	 */
	public static void error(final String tag, final Object obj) {
		error(tag, obj, null);
	}

	/**
	 * error message
	 *
	 * @param tag
	 * @param obj
	 * @param t
	 */
	public static void error(final String tag, final Object obj, final Throwable t) {
		if (logMode == LogMode.NONE) {
			return;
		}
		final Object toLog = (t == null ? obj : (obj.toString() + " (" + t.getLocalizedMessage() + ")"));
		final String log = constructErrorMessage(toLog, "ERR: ", tag);
		if (logMode == LogMode.CONSOLE) {
			System.err.println(log);
			if (t != null) {
				t.printStackTrace(System.err);
			}
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	private static String constructErrorMessage(final Object obj, final String info, final String tag) {
		final String message = obj.toString();
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
	 * Assert that condition is false
	 *
	 * @param tag
	 * @param condition
	 * @param obj
	 */
	public static void assertFalse(final String tag, final boolean condition, final Object obj) {
		if (logMode == LogMode.NONE || !condition) {
			return;
		}
		final String log = constructErrorMessage(obj, "ASSERTION: ", tag);
		if (logMode == LogMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * Assert that condition is true
	 *
	 * @param tag
	 * @param condition
	 * @param obj
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
	}

	/**
	 * Assert if o1 equals o2
	 *
	 * @param tag
	 * @param o1
	 * @param o2
	 * @param obj
	 */
	public static void assertIfEquals(final String tag, final Object o1, final Object o2, final Object obj) {
		if (logMode == LogMode.NONE || !o1.equals(o2)) {
			return;
		}
		final String log = constructErrorMessage(obj, "ASSERTION: ", tag);
		if (logMode == LogMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * Assert if o1 doesn't equal o2
	 *
	 * @param tag
	 * @param o1
	 * @param o2
	 * @param obj
	 */
	public static void assertIfNotEquals(final String tag, final Object o1, final Object o2, final Object obj) {
		if (logMode == LogMode.NONE || o1.equals(o2)) {
			return;
		}
		final String log = constructErrorMessage(obj, "ASSERTION: ", tag);
		if (logMode == LogMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}
}
