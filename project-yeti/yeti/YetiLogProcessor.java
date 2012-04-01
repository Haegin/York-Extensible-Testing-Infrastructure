package yeti;

/**

 YETI - York Extensible Testing Infrastructure

 Copyright (c) 2009-2010, Manuel Oriol <manuel.oriol@gmail.com> - University of York
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by the University of York.
 4. Neither the name of the University of York nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 **/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

/**
 * Class that represents a processor for logs coming from Yeti.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 */
public abstract class YetiLogProcessor {

	/**
	 * The number of non-unique bugs in last logs.
	 */
	public static int lastNumberOfNonUniqueBugs = 0;

	/**
	 * A variable that is true if the test cases minimization should be
	 * aggressive (less accurate, but produces smaller test cases), or false if
	 * the processing should be less aggressive.
	 */
	public static boolean aggressiveTestCasesMinimization = true;

	/**
	 * The number of (non-unique) failures found so far.
	 */
	public int numberOfErrors = 0;

	/**
	 * The number of routine calls.
	 */
	public int numberOfCalls = 0;

	/**
	 * A field storing the logs.
	 */
	private Vector<String> logs = new Vector<String>();

	/**
	 * a variable to append to current logs.
	 */
	private String currentLog = "";

	/**
	 * a field to hold new errors.
	 */
	public static long numberOfNewErrors = 0;

	/**
	 * A list of traces for new relevant detected errors.
	 */
	public HashMap<String, Object> listOfNewErrors = new HashMap<String, Object>();

	/**
	 * A list of traces for relevant detected errors.
	 */
	private HashMap<String, Object> listOfErrors = new HashMap<String, Object>();

	/**
	 * Wrapping call to add a new trace in the list of errors.
	 * 
	 * @param trace
	 *            the trace to add.
	 * @param d
	 *            the date to add.
	 */
	public void putNewTrace(String trace, Date d) {
		int startIndex = trace.indexOf("\t");
		String s0 = trace;
		if (startIndex >= 0) {
			s0 = trace.substring(trace.indexOf("\t"));
		}
		numberOfNewErrors = listOfNewErrors.size();
		listOfErrors.put(s0, d);
		listOfNewErrors.put(trace, d);
		if (listOfNewErrors.size() > numberOfNewErrors)
			numberOfNewErrors++;
	}

	/**
	 * Wrapping call to add an old trace in the list of errors.
	 * 
	 * @param trace
	 *            the trace to add.
	 */
	public void putOldTrace(String trace) {
		listOfErrors.put(trace, 0);
	}

	/**
	 * Wrapping call to get the size of the list of errors.
	 * 
	 * @return the size of the list of errors.
	 */
	public int getListOfErrorsSize() {
		return listOfErrors.size();
	}

	/**
	 * Wrapping call to check whether the list of errors contains a trace.
	 * 
	 * @return true if the list contains the error.
	 */
	public boolean listOfErrorsContainsTrace(String trace) {
		return listOfErrors.containsKey(trace);
	}

	/**
	 * The number of errors in the listOfErrors that are actually acceptable
	 * errors.
	 */
	public int numberOfNonErrors = 0;

	/**
	 * The list of non-errors initially loaded.
	 */
	public HashMap<String, Object> listOfNonErrors = null;

	/**
	 * A simple routine to check whether a trace is in the list of non-errors.
	 * 
	 * @param trace
	 *            the trace to check
	 * @return true if it is in it.
	 */
	public boolean isInListOfNonErrors(String trace) {
		if (listOfNonErrors != null)
			return listOfNonErrors.containsKey(trace);
		else
			return false;
	}

	/**
	 * A simple routine to add a trace to in the list of non-errors.
	 * 
	 * @param trace
	 *            the trace to add
	 */
	public void addToListOfNonErrors(String trace) {
		if (listOfNonErrors == null) {
			listOfNonErrors = new HashMap<String, Object>();
		}
		String trimmedTrace = trace.substring(trace.indexOf('\t'));
		listOfNonErrors.put(trimmedTrace, 0);
		numberOfNonErrors++;
	}

	/**
	 * A simple routine to a trace to in the list of errors.
	 * 
	 * @param trace
	 *            the trace to add
	 */
	public void addToListOfErrors(String trace) {
		String trimmedTrace = trace.substring(trace.indexOf('\t'));
		listOfErrors.put(trimmedTrace, 0);
		numberOfErrors++;
	}

	/**
	 * Constructor of the YetiLogProcessor.
	 */
	public YetiLogProcessor() {
		super();
	}

	/**
	 * Constructor of the YetiLogProcessor with an initial list of errors.
	 */
	@SuppressWarnings("unchecked")
	public YetiLogProcessor(HashMap<String, Object> listOfErrors) {
		super();
		if (listOfErrors != null) {
			this.listOfErrors = listOfErrors;
			this.numberOfNonErrors = listOfErrors.size();
			this.listOfNonErrors = (HashMap<String, Object>) (listOfErrors
					.clone());
			YetiLog.printDebugLog("NumberOfNonErrors = "
					+ this.numberOfNonErrors, this);
		}
	}

	/**
	 * Add the parameter at the end of the currentLog.
	 * 
	 * @param newLog
	 *            the log to add.
	 */
	public void appendToCurrentLog(String newLog) {

		currentLog = this.currentLog + "\n" + newLog;
		this.numberOfCalls++;
	}

	/**
	 * Add the parameter at the end of the currentLog.
	 * 
	 * @param newLog
	 *            the log to add.
	 */
	public void appendFailureToCurrentLog(String newLog) {
		currentLog = this.currentLog + "\n" + "/**Error:Start: " + newLog
				+ "**/\n/**End:Error**/";
		this.numberOfErrors++;
	}

	/**
	 * Simple setter for the current logs.
	 * 
	 * @param currentLog
	 *            the logs to set.
	 */
	public void setCurrentLog(String currentLog) {
		this.currentLog = currentLog;
	}

	/**
	 * Adds the currentLog to the logs and reinitializes currentLog.
	 */
	public void newSerieOfLog() {
		logs.add(currentLog);
		currentLog = "";
	}

	/**
	 * Process the logs currently stored.
	 * 
	 * @return the processed logs, what can be given to end-user.
	 */
	public abstract Vector<String> processLogs();

	/**
	 * Getter for the currentLog variable.
	 * 
	 * @return The value of currentLog
	 */
	public String getCurrentLog() {
		return currentLog;
	}

	/**
	 * Getter for logs.
	 * 
	 * @return the value of the older logs.
	 */
	public Vector<String> getLogs() {
		return logs;
	}

	/**
	 * Printer for raw logs
	 * 
	 * @parameter message the message log to print.
	 */
	public void printMessageRawLogs(String message) {
		System.err.println("YETI LOG: " + message);
	}

	/**
	 * Printer for throwables in raw logs
	 * 
	 * @parameter t the throwable log to print.
	 */
	public void printThrowableRawLogs(Throwable t, YetiCallContext context) {
		System.err.println("YETI EXCEPTION - START ");
		if (t != null)
			t.printStackTrace(System.err);
		else
			System.err.println("Thread killed by Yeti!");
		System.err.println("YETI EXCEPTION - END ");

	}

	/**
	 * Printer for throwables in raw logs
	 * 
	 * @parameter t the throwable log to print.
	 * @param isFailure
	 *            if it is actually a real failure.
	 */
	public void printThrowableRawLogs(Throwable t, YetiCallContext context,
			boolean isFailure) {
		if (isFailure)
			printThrowableRawLogs(t, context);
	}

	/**
	 * Printer for no logs
	 * 
	 * @parameter message the message log not to print.
	 */
	public void printMessageNoLogs(String message) {
	}

	/**
	 * Printer for throwables in no logs
	 * 
	 * @parameter t the throwable log not to print.
	 */
	public void printThrowableNoLogs(Throwable t, YetiCallContext context) {

	}

	/**
	 * Printer for throwables in no logs
	 * 
	 * @parameter t the throwable log not to print.
	 * @param isFailure
	 *            if it is actually a real failure.
	 */
	public void printThrowableNoLogs(Throwable t, YetiCallContext context,
			boolean isFailure) {
		if (isFailure)
			this.printThrowableNoLogs(t, context);
	}

	/**
	 * Printer for throwables in logs
	 * 
	 * @parameter t the throwable log to print.
	 */
	public void printThrowableLogs(Throwable t, YetiCallContext context) {

	}

	/**
	 * Printer for throwables in logs
	 * 
	 * @parameter t the throwable log to print.
	 * @param isFailure
	 *            if it is actually a real failure.
	 */
	public void printThrowableLogs(Throwable t, YetiCallContext context,
			boolean isFailure) {
		if (isFailure)
			this.printThrowableLogs(t, context);
	}

	/**
	 * Getter for the number of non-errors;
	 * 
	 * @return the number of non errors;
	 */
	public int getNumberOfNonErrors() {
		if (this.listOfNonErrors == null)
			return 0;
		return this.listOfNonErrors.size();
	}

	/**
	 * Getter for the number of unique faults.
	 * 
	 * @return the number of non errors;
	 */
	public int getNumberOfUniqueFaults() {
		return this.listOfErrors.size() - this.numberOfNonErrors;
	}

	/**
	 * A simple getter for the list of errors
	 * 
	 * @return the list of errors.
	 */
	public HashMap<String, Object> getListOfErrors() {
		return this.listOfErrors;
	}

	/**
	 * Return true if the error is a real error.
	 * 
	 * @param t
	 *            the Throwable that might be a real error
	 * @return true if this is a real error.
	 */
	public boolean isAccountableFailure(Throwable t) {
		return true;
	}

	/**
	 * Method that reads exception traces from a file where they were stored.
	 * The exceptions are supposed to be of the format:<br>
	 * "Trace ...\n..."
	 * 
	 * The method also supports comments inserted in the form of a line starting
	 * with "//"
	 * 
	 * @param fileName
	 *            the file to read
	 * @return an ArrayList containing all decoded traces
	 */
	public ArrayList<String> readTracesFromFile(String fileName) {
		ArrayList<String> result = new ArrayList<String>();
		BufferedReader br = null;
		// we first check that the file exists
		if (new File(fileName).exists()) {
			try {
				// we create a reader to read line by line the traces
				br = new BufferedReader(new FileReader(fileName));
				boolean isValid = true;

				// we read line by line
				String currentLine = br.readLine();
				while (isValid) {
					if (currentLine == null)
						break;
					// we have to remove comments
					if (!currentLine.startsWith("//")) {
						// the first line of the exception trace should start
						// with "Trace "
						if (currentLine.startsWith("Trace ")) {
							currentLine = br.readLine();
							String trace = null;
							// we read the trace itself
							while ((currentLine != null)
									&& !currentLine.startsWith("Trace ")) {
								if (currentLine.startsWith("\t")) {
									if (trace == null) {
										trace = currentLine;
									} else {
										trace = trace + "\n" + currentLine;
									}
								}
								currentLine = br.readLine();
							}
							// once read we add it to the result
							result.add(trace);
							YetiLog.printDebugLog("Imported trace:\n" + trace,
									Yeti.class);
						} else {
							isValid = false;
						}
					} else {
						// in case the file started by a comment
						currentLine = br.readLine();
					}
				}
			} catch (FileNotFoundException e) {
				// Should never happen unless somebody removed it between our
				// two tests!!!
				// e.printStackTrace();
			} catch (IOException e) {
				// Should never happen either
				// e.printStackTrace();
			}
		} else {
			// just in case the file cannot be open, we print a message
			System.err.println("Trying to read exception trace from "
					+ fileName + ": file not found, continuing with execution");
		}
		// we close the streams when we are finished
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				// Should never happen
				// e.printStackTrace();
			}
		}
		System.out
				.println("/** Yeti imported " + result.size() + " traces **/");
		return result;

	}

	/**
	 * A simple method to read all traces from a list of files.
	 * 
	 * @param traceInputFiles
	 *            an array containing all file names.
	 */
	public void readTraces(String[] traceInputFiles) {
		if (traceInputFiles != null) {
			// for each file to use, we read the traces and add them to our
			// initial list
			for (String fileName : traceInputFiles) {
				for (String trace : this.readTracesFromFile(fileName)) {
					this.addToListOfNonErrors(trace);
					this.addToListOfErrors(trace);
				}
			}

		}
	}

	/**
	 * Outputs traces into a file passed as an argument. This method is coded to
	 * work with method readTracesFromFile
	 * 
	 * @param listOfErrors
	 *            the list of errors to output.
	 * @param fileName
	 *            the name of the file in which to write them.
	 */
	public void outputTracesToFile(HashMap<String, Object> listOfErrors,
			String fileName, int nNonErrors) {
		try {
			// we open the file
			PrintStream ps = new PrintStream(fileName);

			// we get our values
			Iterator<String> traces = listOfErrors.keySet().iterator();
			Iterator<Object> dates = listOfErrors.values().iterator();

			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
					Locale.US);
			// we print all the traces in the file
			for (int i = 0; i < listOfErrors.size(); i++) {
				Object traceDate = dates.next();
				if (traceDate instanceof Date) {
					ps.println("Trace " + (i + 1 + nNonErrors)
							+ " discovered on " + df.format(((Date) traceDate))
							+ ":\n" + traces.next());
				} else {
					ps.println("Trace " + (i + 1 + nNonErrors)
							+ " discovered on " + traceDate.toString() + ":\n"
							+ traces.next());
				}
			}

			// we close the stream
			ps.close();
		} catch (FileNotFoundException e) {
			System.err.println("Trying to write exception trace to " + fileName
					+ ": cannot open or create file");
		}

	}

	/**
	 * Resets this class.
	 */
	public static void reset() {
		lastNumberOfNonUniqueBugs = 0;
	}

	/**
	 * Specialize this to generate proper language-bound test cases.
	 * 
	 * @param processedTestCases
	 *            the processed test cases
	 */
	public String generateUnitTestFile(Vector<String> processedTestCases,
			String unitTestFileName) {
		String aggregate = "";
		for (String tc : processedTestCases) {
			aggregate = aggregate + "\n\n" + tc;
		}
		return aggregate;
	}

	/**
	 * Returns the file name under which store the generated unit tests.
	 * 
	 * @param processedTestCases
	 *            the test cases.
	 * @param unitTestFileName
	 *            the file name specified on the command-line.
	 * @return a file name under which the unit tests should be stored.
	 */
	public String generateUnitTestFileName(Vector<String> processedTestCases,
			String unitTestFileName) {
		return unitTestFileName;
	}

	/**
	 * Adds 3 spaces at the beginning of each line of the string.
	 * 
	 * @param stringToIndent
	 *            the part of the test case to indent.
	 * @return the indenter test case.
	 */
	public static String indent(String stringToIndent) {
		String[] lines = stringToIndent.split("\n");
		String result = "";
		for (String s : lines) {
			result = result + "   " + s + "\n";
		}

		return result;
	}

	/**
	 * A utility function for the stack trace.
	 * 
	 * @param stackTrace
	 *            the stackTrace
	 * @return a combined String
	 */
	public String getTraceFromThrowable(Throwable t) {
		String result = "\n";
		StackTraceElement[] stackTrace = t.getStackTrace();
		for (StackTraceElement e : stackTrace) {
			result += e.toString() + "\n   ";
		}
		return result.substring(0, result.length());
	}

}
