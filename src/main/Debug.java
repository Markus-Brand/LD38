package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Debug {

	static DebugMode debugMode; //current debug mode
	static File logFile; //Log file
	static PrintWriter writer; //File writer

	/**
	 * Inits Debug width given debug mode
	 *
	 * @param mode
	 */
	static void initDebug(DebugMode mode) {
		debugMode = mode;
		if (debugMode == DebugMode.LOGFILE) {

			// if the directory does not exist, create it
			File theDir = new File("log");
			if (!theDir.exists()) {
				theDir.mkdir();
			}

			DateFormat df = new SimpleDateFormat("MM_dd_yyyy__HH_mm_ss");
			Date today = Calendar.getInstance().getTime();
			String log = df.format(today);
			logFile = new File("log/" + log + ".log");
			try {
				writer = new PrintWriter(logFile);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * closes log file when closing window.
	 */
	static void closeLogFile() {
		if (debugMode == DebugMode.LOGFILE) {
			writer.close();
		}
	}

	/**
	 * log message
	 *
	 * @param message
	 *            the message being logged
	 */
	static void log(String message) {
		if (debugMode == DebugMode.NONE) {
			return;
		}
		DateFormat df = new SimpleDateFormat("[MM/dd/yyyy HH:mm:ss] ");
		Date today = Calendar.getInstance().getTime();
		String log = df.format(today);
		log += "LOG: ";
		log += message;
		if (debugMode == DebugMode.CONSOLE) {
			System.out.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * error message
	 *
	 * @param message
	 *            the message being logged in error mode
	 */
	static void error(String message) {
		if (debugMode == DebugMode.NONE) {
			return;
		}
		DateFormat df = new SimpleDateFormat("[MM/dd/yyyy HH:mm:ss] ");
		Date today = Calendar.getInstance().getTime();
		String log = df.format(today);
		log += "ERR: ";
		log += message;
		if (debugMode == DebugMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * Assert if condition is true
	 *
	 * @param condition
	 * @param message
	 */
	static void assertIfTrue(boolean condition, String message) {
		if (debugMode == DebugMode.NONE || !condition) {
			return;
		}
		DateFormat df = new SimpleDateFormat("[MM/dd/yyyy HH:mm:ss] ");
		Date today = Calendar.getInstance().getTime();
		String log = df.format(today);
		log += "ASSERTION: ";
		log += message;
		if (debugMode == DebugMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * Assert if condition is false
	 *
	 * @param condition
	 * @param message
	 */
	static void assertIfFalse(boolean condition, String message) {
		if (debugMode == DebugMode.NONE || condition) {
			return;
		}
		DateFormat df = new SimpleDateFormat("[MM/dd/yyyy HH:mm:ss] ");
		Date today = Calendar.getInstance().getTime();
		String log = df.format(today);
		log += "ASSERTION: ";
		log += message;
		if (debugMode == DebugMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * Assert if o1 equals o2
	 *
	 * @param o1
	 * @param o2
	 * @param message
	 */
	static void assertIfEquals(Object o1, Object o2, String message) {
		if (debugMode == DebugMode.NONE || !o1.equals(o2)) {
			return;
		}
		DateFormat df = new SimpleDateFormat("[MM/dd/yyyy HH:mm:ss] ");
		Date today = Calendar.getInstance().getTime();
		String log = df.format(today);
		log += "ASSERTION: ";
		log += message;
		if (debugMode == DebugMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}

	/**
	 * Assert if o1 doesn't equal o2
	 *
	 * @param o1
	 * @param o2
	 * @param message
	 */
	static void assertIfNotEquals(Object o1, Object o2, String message) {
		if (debugMode == DebugMode.NONE || o1.equals(o2)) {
			return;
		}
		DateFormat df = new SimpleDateFormat("[MM/dd/yyyy HH:mm:ss] ");
		Date today = Calendar.getInstance().getTime();
		String log = df.format(today);
		log += "ASSERTION: ";
		log += message;
		if (debugMode == DebugMode.CONSOLE) {
			System.err.println(log);
		} else {
			writer.println(log);
			writer.flush();
		}
	}
}
