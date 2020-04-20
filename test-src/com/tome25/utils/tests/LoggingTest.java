package com.tome25.utils.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.regex.Pattern;

import org.junit.Test;

import com.tome25.utils.logging.LogTracer;
import com.tome25.utils.logging.LoggingPrintStream;
import com.tome25.utils.logging.MultiPrintStream;
import com.tome25.utils.logging.OutputHandler;
import com.tome25.utils.logging.SplittingHandler;
import com.tome25.utils.logging.TracingFormatter;
import com.tome25.utils.logging.TracingMultiPrintStream;

public class LoggingTest {

	@Test
	public void multiPrintStreamTest() {
		// test the basics of the MultiPrintStream
		ByteArrayOutputStream baOut1 = new ByteArrayOutputStream();
		ByteArrayOutputStream baOut2 = new ByteArrayOutputStream();
		ByteArrayOutputStream baOut3 = new ByteArrayOutputStream();
		MultiPrintStream mOut = new MultiPrintStream(baOut1, baOut2);
		PrintStream pOut = new PrintStream(baOut3);
		println("Test Output", mOut, pOut);
		assertArrayEquals(baOut1.toByteArray(), baOut2.toByteArray());
		assertArrayEquals(baOut2.toByteArray(), baOut3.toByteArray());
		// test int output handling
		println(123.321, mOut, pOut);
		assertArrayEquals(baOut1.toByteArray(), baOut2.toByteArray());
		assertArrayEquals(baOut2.toByteArray(), baOut3.toByteArray());
	}

	@Test
	public void tracingMultiPrintStreamTest() {
		// test the TracingMultiPrintStream
		ByteArrayOutputStream baOut1 = new ByteArrayOutputStream();
		ByteArrayOutputStream baOut2 = new ByteArrayOutputStream();
		TracingMultiPrintStream tOut = new TracingMultiPrintStream(baOut1, baOut2);
		Pattern outputPattern = Pattern
				.compile("\\[[^\\[]*\\]\\s\\[main\\]\\s\\[LoggingTest\\]\\s\\[tracingMultiPrintStreamTest\\]:\\s.*\\n");
		tOut.println("Test");
		assertTrue(outputPattern.matcher(baOut1.toString()).matches());
		assertArrayEquals(baOut1.toByteArray(), baOut2.toByteArray());
		// close the TracingMultiPrintStream
		tOut.close();
	}

	@Test
	public void loggingPrintStreamTest() throws IOException {
		// test the LoggingPrintStream
		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		SimpleFormatter formatter = new SimpleFormatter();
		StreamHandler handler = new StreamHandler(baOut, formatter);
		Logger logger = Logger.getLogger("test");
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		LoggingPrintStream lOut = new LoggingPrintStream(logger);
		Pattern outputPattern = Pattern.compile(
				".*com\\.tome25\\.utils\\.tests\\.LoggingTest\\sloggingPrintStreamTest\\nINFORMATION:\\s.*\\n");
		lOut.println("Test String\"");
		handler.flush();
		assertTrue(outputPattern.matcher(baOut.toString()).matches());
		// close the LoggingPrintStream
		lOut.close();
	}

	@Test
	public void outputHandlerTest() {
		// test the level handling of the OutputHandler wit a set level of SEVERE
		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		SimpleFormatter formatter = new SimpleFormatter();
		OutputHandler severeHandler = new OutputHandler(baOut, Level.SEVERE, formatter);
		Logger logger = Logger.getLogger("test");
		logger.setUseParentHandlers(false);
		logger.addHandler(severeHandler);
		Pattern outputPattern = Pattern
				.compile(".*com\\.tome25\\.utils\\.tests\\.LoggingTest\\soutputHandlerTest\\n.*:\\s.*\\n");
		logger.info("INFO");
		severeHandler.flush();
		assertTrue(baOut.toByteArray().length == 0);
		logger.severe("SEVERE");
		severeHandler.flush();
		assertTrue(outputPattern.matcher(baOut.toString()).matches());
		// test the level handling of the OutputHandler wit a set level of ALL
		baOut = new ByteArrayOutputStream();
		OutputHandler allHandler = new OutputHandler(baOut, Level.ALL, formatter);
		logger.setLevel(Level.ALL);
		logger.removeHandler(severeHandler);
		logger.addHandler(allHandler);
		logger.finest("INFO");
		allHandler.flush();
		assertTrue(outputPattern.matcher(baOut.toString()).matches());
	}

	@Test
	public void splittingHandlerTest() {
		// test the SplittingHandler
		ByteArrayOutputStream baOut1 = new ByteArrayOutputStream();
		ByteArrayOutputStream baOut2 = new ByteArrayOutputStream();
		SimpleFormatter formatter = new SimpleFormatter();
		StreamHandler streamHandler1 = new StreamHandler(baOut1, formatter);
		StreamHandler streamHandler2 = new StreamHandler(baOut2, formatter);
		Logger logger1 = Logger.getLogger("output");
		Logger logger2 = Logger.getLogger("error");
		logger1.setUseParentHandlers(false);
		logger2.setUseParentHandlers(false);
		logger1.addHandler(streamHandler1);
		logger2.addHandler(streamHandler2);
		SplittingHandler handler = new SplittingHandler(logger1, logger2);
		Logger logger = Logger.getLogger("test");
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		Pattern outputPattern = Pattern
				.compile(".*com\\.tome25\\.utils\\.tests\\.LoggingTest\\ssplittingHandlerTest\\n.*:\\s.*\\n");
		logger.info("INFO");
		streamHandler1.flush();
		streamHandler2.flush();
		assertTrue(outputPattern.matcher(baOut1.toString()).matches());
		assertTrue(baOut2.toByteArray().length == 0);
		logger.severe("SEVERE");
		streamHandler1.flush();
		streamHandler2.flush();
		assertTrue(outputPattern.matcher(baOut2.toString()).matches());
	}

	@Test
	public void tracingFormatterTest() {
		// test the TracingFormatter
		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		TracingFormatter formatter = new TracingFormatter();
		StreamHandler handler = new StreamHandler(baOut, formatter);
		Logger logger = Logger.getLogger("test");
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		Pattern outputPattern = Pattern.compile(
				"\\[[^\\[]*\\]\\s\\[main\\]\\s\\[test/[^\\[]*\\]\\s\\[LoggingTest\\]\\s\\[tracingFormatterTest\\]:\\s.*\\n");
		logger.warning("WARNING");
		handler.flush();
		assertTrue(outputPattern.matcher(baOut.toString()).matches());
	}

	@Test
	public void logTracerTest() {
		// test LogTracer.traceOutput
		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		SimpleFormatter formatter = new SimpleFormatter();
		StreamHandler handler = new StreamHandler(baOut, formatter);
		Logger logger = Logger.getLogger("test");
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		LoggingPrintStream lOut = new LoggingPrintStream(logger);
		LogTracer.traceOutput(lOut);
		Pattern outputPattern = Pattern.compile(
				".*sun\\.nio\\.cs\\.StreamEncoder\\swriteBytes\\n.*\\[[^\\[]*\\]\\s\\[main\\]\\s\\[SYSOUT/[^\\[]*\\]\\s\\[LoggingTest\\]\\s\\[logTracerTest\\]:\\s.*\\n");
		System.out.println("test");
		handler.flush();
		System.err.println(baOut.toString());
		assertTrue(outputPattern.matcher(baOut.toString()).matches());
	}

	private void println(Object toPrint, PrintStream... printStreams) {
		for (PrintStream ps : printStreams) {
			ps.println(toPrint);
		}
	}

}
