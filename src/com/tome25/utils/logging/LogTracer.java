package com.tome25.utils.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 
 * A utility class to improve logging.
 * 
 * @author ToMe25
 *
 */
public class LogTracer {

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and a File Output Stream to the log file.
	 * 
	 * @param log the file to log the System Error Stream to.
	 */
	public static void traceOutputs(String log) {
		traceOutputs(new File(log));
	}

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and a File Output Stream to their respective file.
	 * 
	 * @param errorLog  the file to log the System Error Stream to.
	 * @param outputLog the file to log the System Output Stream to.
	 */
	public static void traceOutputs(String errorLog, String outputLog) {
		traceOutputs(new File(errorLog), new File(outputLog));
	}

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and a File Output Stream to the log file.
	 * 
	 * @param log the file to log the System Error Stream to.
	 */
	public static void traceOutputs(File log) {
		traceOutputs(log, log);
	}

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and a File Output Stream to their respective file.
	 * 
	 * @param errorLog  the file to log the System Error Stream to.
	 * @param outputLog the file to log the System Output Stream to.
	 */
	public static void traceOutputs(File errorLog, File outputLog) {
		if (!errorLog.getParentFile().exists()) {
			errorLog.getParentFile().mkdirs();
		}
		if (!outputLog.getParentFile().exists()) {
			outputLog.getParentFile().mkdirs();
		}
		try {
			traceError(new FileOutputStream(errorLog));
			traceOutput(new FileOutputStream(outputLog));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets System Output and Error Stream to a TracingMultiPrintStream with the old
	 * Stream and the Output Streams from additionalStreams.
	 * 
	 * @param additionalStreams the Output Streams to add to System.out
	 */
	public static void traceOutputs(OutputStream... additionalStreams) {
		traceError(additionalStreams);
		traceOutput(additionalStreams);
	}

	/**
	 * Sets System Error Stream to a TracingMultiPrintStream with the old System
	 * Error Stream and the Output Streams from additionalStreams.
	 * 
	 * @param additionalStreams the Output Streams to add to System.err
	 */
	public static void traceError(OutputStream... additionalStreams) {
		OutputStream[] errors = Arrays.copyOf(additionalStreams, additionalStreams.length + 1);
		errors[additionalStreams.length] = System.err;
		System.setErr(new TracingMultiPrintStream(errors));
	}

	/**
	 * Sets System Output to a TracingMultiPrintStream with the old System Output
	 * and the Output Streams from additionalStreams.
	 * 
	 * @param additionalStreams the Output Streams to add to System.out
	 */
	public static void traceOutput(OutputStream... additionalStreams) {
		OutputStream[] outputs = Arrays.copyOf(additionalStreams, additionalStreams.length + 1);
		outputs[additionalStreams.length] = System.out;
		System.setOut(new TracingMultiPrintStream(outputs));
	}

}
