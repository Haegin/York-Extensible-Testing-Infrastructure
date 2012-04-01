package yeti.environments.java;

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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;

import yeti.Yeti;
import yeti.YetiCallContext;
import yeti.YetiLog;
import yeti.YetiLogProcessor;
import yeti.annotations.YetiTrace;
import yeti.environments.java.YetiBug;

/**
 * Class that represents a log processor for Java. <code>processLog</code>
 * generates test cases in each cell of the array.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 * 
 */
public class YetiJavaLogProcessor extends YetiLogProcessor {
    
    private YetiBugList listOfBugs = new YetiBugList();

	/**
	 * A constructor for the YetiJavaLogProcessor.
	 */
	public YetiJavaLogProcessor() {

	}

	/**
	 * Constructor of the YetiLogProcessor with an initial list of errors.
	 */
	public YetiJavaLogProcessor(HashMap<String, Object> listOfErrors) {
		super(listOfErrors);
	}
	
	public void printYetiBugs() {
	    for (YetiBug bug : listOfBugs.getBugs()) {
	        System.out.println(bug.toString());
	        System.out.println();
	    }
	    if (!listOfBugs.isEmpty()) {
    	    listOfBugs.saveToDB("Testing completed at " + new Date().toString());
	    }
	}
	
	public void printAndClassifyBugs() {
	    YetiBugClassifier classifier = new YetiDecisionTreeClassifier();
	    List<YetiBugList> classified = classifier.classifyThis(listOfBugs);
	    for (YetiBugList buglist : classified) {
	        System.out.println(buglist.getBugs().get(0).toString());
	        System.out.println();
	    }
	}
	
	/*
	 * (non-Javadoc) Adds a timestamp on the log
	 * 
	 * @see yeti.YetiLogProcessor#appendToCurrentLog(java.lang.String)
	 */
	@Override
	public void appendToCurrentLog(String newLog) {
		// substantific gains (2-3x) in execution time can be done by NOT adding
		// the timestamp
		// super.appendToCurrentLog(newLog);
		// TODO: add an option to add time stamping back through an option
		// super.appendToCurrentLog(newLog+" // time:"+(new Date()).getTime());
		super.appendToCurrentLog(newLog);
	}

	/*
	 * A nicer printer for Yeti and Java
	 * 
	 * (non-Javadoc)
	 * 
	 * @see yeti.YetiLogProcessor#appendFailureToCurrentLog(java.lang.String)
	 */
	@Override
    public void appendFailureToCurrentLog(String newLog) {
		String log = this.getCurrentLog();
		log = log + "\n" + "/**YETI EXCEPTION - START \n" + newLog
				+ "\nYETI EXCEPTION - END**/ ";
		this.setCurrentLog(log);
		YetiLog.printDebugLog("Appending to current log: " + newLog.toString(),
				YetiLog.class);

	}

	/**
	 * The number of errors in last Logs processed.
	 */
	private static int lastLogTotalSize = 0;

	/**
	 * Generates a Vector<String> that a test case for each cell.
	 * 
	 * @see yeti.YetiLogProcessor#processLogs()
	 */
	@Override
	public Vector<String> processLogs() {
		if (Yeti.pl.isRawLog() || Yeti.pl.isNoLogs()) {
			return new Vector<String>(this.testCases);
		}
		Vector<String> testCases = YetiJavaLogProcessor.sliceStatically(this
				.getCurrentLog());
		Vector<String> result = new Vector<String>();
		int i = 0;
		for (String tc : testCases) {
			i++;

			result.add(YetiLogProcessor
					.indent("/** Test case automatically generated by YETI **/\n@Test public void test_"
							+ i
							+ "() throws Exception {\n"
							+ YetiLogProcessor.indent(tc) + "\n}"));
		}
		result.add("/** Non-Unique bugs: " + lastNumberOfNonUniqueBugs
				+ ", Unique Bugs: " + result.size() + ", Logs size (locs): "
				+ lastLogTotalSize + "**/");
		return result;
	}

	/**
	 * Generates the kill value for this line.
	 * 
	 * @param loc
	 *            the line of code to treat.
	 * @return the String value for the variable to kill.
	 */
	public static String kill(String loc) {
		if (loc.startsWith("try")) {
			loc = loc.substring(loc.indexOf("{") + 1, loc.indexOf("}"));
		}

		boolean isAssignment = (loc.indexOf("=") > 0);
		int indexOfSpace = loc.indexOf(" ");

		YetiLog.printDebugLog("loc: " + loc, YetiJavaLogProcessor.class);
		if (isAssignment) {
			YetiLog.printDebugLog(
					"kill: "
							+ loc.substring(indexOfSpace + 1, loc.indexOf("=")),
					YetiJavaLogProcessor.class);
			return loc.substring(indexOfSpace + 1, loc.indexOf("="));
		} else {
			YetiLog.printDebugLog("no kill", YetiJavaLogProcessor.class);
			return null;
		}
	}

	/**
	 * Generates the vector of variables that are used by this line of code.
	 * 
	 * @param loc
	 *            the line of code to treat.
	 * @return a vector containing all the variables that should be added to the
	 *         list of values that matter.
	 */
	public static Vector<String> gen(String loc) {
		if (loc.startsWith("try")) {
			loc = loc.substring(loc.indexOf("{") + 1, loc.indexOf("}"));
		}

		boolean isAssignment = (loc.indexOf("=") > 0);
		boolean isCreation = (loc.indexOf("new ") > 0);
		boolean isMethodCall = (loc.indexOf("(") > 0);
		boolean isComment = loc.startsWith("/**");

		// if this is a comment we return no gen
		if (isComment)
			return new Vector<String>();

		// we initialize the values
		String localLoc = loc;
		Vector<String> valuesThatMatter = new Vector<String>();
		YetiLog.printDebugLog("loc: " + loc, YetiJavaLogProcessor.class);

		// if it is not a creation method but it is a method call
		if (!isCreation && isMethodCall) {
			String target = target(loc);
			YetiLog.printDebugLog("target: " + target,
					YetiJavaLogProcessor.class);

			// we add it to the values that matter
			if (target.startsWith("v")
					|| !YetiLogProcessor.aggressiveTestCasesMinimization)
				valuesThatMatter.add(target);
		}
		// for all method calls we extract arguments
		if (isMethodCall) {
			int indexOfAfterOpenParenthesis = loc.indexOf("(") + 1;
			int indexOfCloseParenthesis = loc.lastIndexOf(")");
			localLoc = localLoc.substring(indexOfAfterOpenParenthesis,
					indexOfCloseParenthesis);

			// we add all arguments one after the other
			if (localLoc.length() > 0)
				for (String var : localLoc.split(",")) {
					YetiLog.printDebugLog("arg: " + var,
							YetiJavaLogProcessor.class);
					if (!var.equals("null")) {
						int indexOfParen = var.indexOf(")");
						if (indexOfParen >= 0)
							var = var.substring(indexOfParen + 1);
						valuesThatMatter.add(var);
					}
				}
		}
		// we return the result
		return valuesThatMatter;
	}

	/**
	 * A dimple method that extracts the target of a call in a log line.
	 * 
	 * @param loc
	 *            the line of code
	 * @return the target of the call if any
	 */
	public static String target(String loc) {
		String target;
		if (loc.startsWith("try")) {
			loc = loc.substring(loc.indexOf("{") + 1, loc.indexOf("}"));
		}

		boolean isAssignment = (loc.indexOf("=") > 0);
		boolean isMethodCall = (loc.indexOf("(") > 0);

		if (!isMethodCall)
			return null;
		// we find the target
		if (isAssignment) {
			String tmpLoc = loc.substring(0, loc.indexOf('('));
			target = tmpLoc.substring(tmpLoc.indexOf("=") + 1,
					tmpLoc.lastIndexOf('.'));
		} else {
			String tmpLoc = loc.substring(0, loc.indexOf('('));
			target = tmpLoc.substring(0, tmpLoc.lastIndexOf('.'));
		}
		return target;
	}

	/**
	 * Checks whether the line contains kills or gen that matter for the
	 * variables
	 * 
	 * @param loc
	 *            the line of code to consider
	 * @param varNames
	 *            the variable names
	 * @return <code>true</code> it it contains a gen or a kill,
	 *         <code>false</code> otherwise.
	 */
	public static boolean containsKillsOrGens(String loc,
			Vector<String> varNames) {
		Vector<String> gen0 = gen(loc);
		String kill0 = kill(loc);
		// String target0 = target (loc);

		// we iterate through all names
		for (String var : varNames) {
			if (kill0 != null)
				if (kill0.equals(var))
					return true;
			// if (target0!=null)
			// if (target0.equals(var)) return true;
			if (!YetiLogProcessor.aggressiveTestCasesMinimization) {
				for (String geni : gen0) {
					if (geni.equals(var))
						return true;
				}
			}

		}
		return false;

	}

	/**
	 * Slices the code of the test case statically and conservatively.
	 * 
	 * Does not make any assumption on command-query separation.
	 * 
	 * @param log
	 *            the log to slice
	 * @return a vector with all generated test cases.
	 */
	public static Vector<String> sliceStatically(String log) {
		Vector<String> testCases = new Vector<String>();

		// we split the lines of code
		String[] linesOfTest = log.split("\n");

		// for logging purposes
		lastLogTotalSize = linesOfTest.length;

		// for logging purposes, we want to know how many errors we found
		int numberOfErrorsParsed = 0;

		// we make the list of errors
		HashMap<String, Integer> listOfErrors = new HashMap<String, Integer>();
		// we look up for all errors
		for (int i = 0; i < linesOfTest.length; i++) {
			String exceptionTrace = "";
			if (linesOfTest[i].startsWith("/**BUG")
					|| linesOfTest[i].startsWith("/**POSSIBLE BUG")) {
				// we aggregate the results and give some output
				int k = i + 1;

				// just in case the trace is unfinished
				if (k >= linesOfTest.length)
					continue;

				// logging purposes
				numberOfErrorsParsed++;

				// the exception starts with a comment
				if (linesOfTest[k].startsWith("/**")) {
					k += 2;
					// will be used to filter the yeti exception stack
					boolean isInYetiExceptions = false;

					// we continue until the end of the exception trace
					while (k < linesOfTest.length
							&& !(linesOfTest[k].contains("**/"))) {
						// if we arrive to the reflexive call, we cut
						if (!isInYetiExceptions
								&& linesOfTest[k].contains("sun.reflect.")) {
							isInYetiExceptions = true;
						}
						if (!isInYetiExceptions)
							exceptionTrace = exceptionTrace + "\n"
									+ linesOfTest[k++];
						else
							k++;
					}
				}
				// we add the error if it is unique
				if (!listOfErrors.containsKey(exceptionTrace)
						&& Yeti.testModule.isThrowableInModule(exceptionTrace)) {
					YetiLog.printDebugLog("Exception trace added: "
							+ exceptionTrace, YetiJavaLogProcessor.class);
					listOfErrors.put(exceptionTrace, i - 1);
				}
			}
		}

		// for logging purposes:
		lastNumberOfNonUniqueBugs = numberOfErrorsParsed;

		// for each error:
		for (int i : listOfErrors.values()) {
			int finalLength = 0;
			String failingCall = linesOfTest[i];
			if (failingCall.startsWith("try")) {
				failingCall = failingCall.substring(
						failingCall.indexOf("{") + 1, failingCall.indexOf("}"));
			}

			String currentTestCase = failingCall + "\n" + linesOfTest[i + 1];
			Vector<String> variables = gen(linesOfTest[i]);
			boolean ignoreNext = false;
			// for all lines previously executed:
			for (int j = i - 1; j >= 0; j--) {
				// if there is no active variable we stop here
				if (variables.isEmpty())
					break;

				// if there is an error, we ignore the call
				if (ignoreNext) {
					if (linesOfTest[j + 1].startsWith("/**"))
						ignoreNext = false;
					continue;
				}
				if (linesOfTest[j].endsWith("**/")) {
					ignoreNext = true;
					continue;
				}

				// if the line contains meaningful kills or gen
				// then we include it in the trace
				// Note we cannot take an aggressive stance on command-query
				// separation with Java
				if (containsKillsOrGens(linesOfTest[j], variables)) {
					String kill0 = kill(linesOfTest[j]);
					// we remove the kill
					if (kill0 != null)
						for (int k = 0; k < variables.size(); k++) {
							if (variables.get(k).equals(kill0)) {
								variables.remove(k--);
							}
						}
					// we add the gens
					variables.addAll(gen(linesOfTest[j]));
					// we add the line to the test case
					currentTestCase = linesOfTest[j] + "\n" + currentTestCase;
					finalLength++;
				}
			}
			// we aggregate the results and give some output
			String exceptionTrace = "";
			int k = i + 2;
			// the exception starts with a comment
			if (linesOfTest[k].startsWith("/**")) {
				// will be used to filter the yeti exception stack
				boolean isInYetiExceptions = false;

				// we continue until the end of the exception trace
				while (k < linesOfTest.length
						&& !linesOfTest[k].contains("**/")) {
					// if we arrive to the reflexive call, we cut
					if (!isInYetiExceptions
							&& linesOfTest[k].contains("sun.reflect.")) {
						isInYetiExceptions = true;
					}
					if (!isInYetiExceptions)
						exceptionTrace = exceptionTrace + "\n"
								+ linesOfTest[k++];
					else
						k++;
				}
				// we add the comment at the end
				exceptionTrace = exceptionTrace + "\n" + linesOfTest[k++];
			}
			currentTestCase = currentTestCase + exceptionTrace;
			currentTestCase = currentTestCase + "\n/** original locs: " + i
					+ " minimal locs: " + (finalLength + 1) + "**/";
			testCases.add(currentTestCase);
		}

		YetiLog.printDebugLog("Number of Errors: " + listOfErrors.size()
				+ " Number of test cases: " + testCases.size(),
				YetiJavaLogProcessor.class);
		// testCases.add("Number of Errors: "+listOfErrors.size()+" Number of test cases: "+testCases.size());

		return testCases;

	}

	/**
	 * Printer for raw logs
	 * 
	 * @parameter message the message log to print.
	 */
	@Override
    public void printMessageRawLogs(String message) {
		System.err.println("YETI LOG: " + message);
	}

	/**
	 * Printer for throwables in raw logs
	 * 
	 * @parameter t the throwable log to print.
	 */
	@Override
    public void printThrowableRawLogs(Throwable t, YetiCallContext context) {
		System.err.print("YETI EXCEPTION - START ");
		String exceptionTrace = getTraceFromThrowable(t);

		// if the trace is actually relevant for the considered module...
		if (Yeti.testModule.isThrowableInModule(exceptionTrace)) {
			// we print the exception trace
			String s0;
			if (exceptionTrace.indexOf('\t') >= 0)
				s0 = exceptionTrace.substring(exceptionTrace.indexOf('\t'));
			else
				s0 = exceptionTrace;
			if (!this.listOfErrorsContainsTrace(s0)) {
				this.putNewTrace(exceptionTrace, new Date());
				this.addTestCase(context.generateTestCase());
			}
		} else
			System.err.println("- NOT IN TESTED MODULE" + exceptionTrace);
		System.err.println("YETI EXCEPTION - END ");

	}

	/**
	 * The list of test cases.
	 */
	public ArrayList<String> testCases = new ArrayList<String>();

	/**
	 * Adds a test case to the set of processed test cases
	 * 
	 * @param generatedTestCase
	 */
	public void addTestCase(String generatedTestCase) {
		testCases.add(generatedTestCase);
		YetiLog.printDebugLog("Generated test case: \n" + generatedTestCase,
				this);
	}

	/**
	 * Printer for throwables in no logs. If a failure boolean was passed the
	 * superclass will call this if the boolean was true.
	 * 
	 * @parameter t the throwable log not to print.
	 */
	@Override
    public void printThrowableNoLogs(Throwable t, YetiCallContext context) {
		String exceptionTrace = getTraceFromThrowable(t);
		if (Yeti.testModule.isThrowableInModule(exceptionTrace)) {
			String s0;
			if (exceptionTrace.indexOf('\t') >= 0)
				s0 = exceptionTrace.substring(exceptionTrace.indexOf('\t'));
			else
				s0 = exceptionTrace;
			if (!this.listOfErrorsContainsTrace(s0)) {
				this.putNewTrace(exceptionTrace, new Date());
				this.addTestCase(context.generateTestCase());
				System.out.println("Exception " + this.getListOfErrorsSize()
						+ "\n" + t.toString() + "\n" + s0);
			}
		}
	}

	/**
	 * Printer for throwables in logs
	 * 
	 * @parameter t the throwable log to print.
	 */
	@Override
    public void printThrowableLogs(Throwable t, YetiCallContext context) {
		YetiLog.printDebugLog("Logs printed", this);
		String exceptionTrace = getTraceFromThrowable(t);
		YetiLog.printDebugLog(exceptionTrace, this);
		
	    // New YetiBugList stuff
		// HJM YETIBUG - ADD TO LIST
		if (Yeti.testModule.isThrowableInModule(exceptionTrace)) {
    	    YetiBug bug = new YetiBug(context.getRoutine().getOriginatingModule().getModuleName(), context.getRoutine().getName().getValue());
    	    bug.setArgs(context.getArguments());
    	    bug.setThrowable(t);
    	    bug.setUnitTest(context.generateTestCase());
    	    
    	    listOfBugs.addBug(bug);
		}
	    
	    // Old stuff
		// if the trace is actually relevant for the considered module...
		if (Yeti.testModule.isThrowableInModule(exceptionTrace)) {
			String s0;
			if (exceptionTrace.indexOf('\t') >= 0)
				s0 = exceptionTrace.substring(exceptionTrace.indexOf('\t'));
			else
				s0 = exceptionTrace;
			if (!this.listOfErrorsContainsTrace(s0)) {
				this.putNewTrace(exceptionTrace, new Date());
				this.addTestCase(context.generateTestCase());
				System.out.println("Exception " + this.getListOfErrorsSize()
						+ "\n" + t.toString() + "\n" + s0);
			}
		}
		this.appendFailureToCurrentLog(exceptionTrace);
	}

	/**
	 * Return true if the error is a real error.
	 * 
	 * @param t
	 *            the Throwable that might be a real error
	 * @return true if this is a real error.
	 */
	@Override
    public boolean isAccountableFailure(Throwable t) {

		String exceptionTrace = getTraceFromThrowable(t);

		// if the trace is actually relevant for the considered module...
		if (Yeti.testModule.isThrowableInModule(exceptionTrace)
				&& exceptionTrace.indexOf('\t') >= 0) {
			String s0;
			if (exceptionTrace.indexOf('\t') >= 0)
				s0 = exceptionTrace.substring(exceptionTrace.indexOf('\t'));
			else
				s0 = exceptionTrace;
			if (this.isInListOfNonErrors(s0)) {
				return false;
			} else {
				return true;
			}
		}
		YetiLog.printDebugLog(exceptionTrace, this);
		return false;

	}

	/**
	 * A routine that extracts a trace from a throwable.
	 * 
	 * @param t
	 *            the throwable to get the trace from.
	 * @return the corresponding String
	 */
	@Override
    public String getTraceFromThrowable(Throwable t) {
		OutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		if (t != null)
			t.printStackTrace(ps);
		String throwableLog = os.toString();
		// we split the lines of code
		String[] linesOfTest = throwableLog.split("\n");
		// we continue until the end of the exception trace
		int k = 0;
		String exceptionTrace = null;
		while (k < linesOfTest.length) {

			// if we arrive to the reflexive call, we cut
			if (linesOfTest[k].contains("sun.reflect.")) {
				break;
			}
			if (exceptionTrace == null) {
				exceptionTrace = linesOfTest[k++];
			} else {
				exceptionTrace = exceptionTrace + "\n" + linesOfTest[k++];
			}
		}
		YetiLog.printDebugLog("Trace returned:\n" + exceptionTrace, this);
		return exceptionTrace;
	}

	/**
	 * Proper generation of JUnit test-cases.
	 * 
	 * @param processedTestCases
	 *            the processed test cases
	 */
	@Override
    public String generateUnitTestFile(Vector<String> processedTestCases,
			String unitTestFileName) {

		String packageName = null;
		String className = "";

		if (unitTestFileName == null) {
			unitTestFileName = "T";
		}
		String testFileName = unitTestFileName;

		// if the user specified the filename with a .java, we remove it.
		if (unitTestFileName.endsWith(".java")) {
			testFileName = testFileName.substring(0, testFileName.length() - 5);
		}

		// if we store the file into a different location, we remove the
		// directory name
		if (testFileName.contains("/")) {
			testFileName = testFileName
					.substring(testFileName.lastIndexOf("/") + 1);
		}

		YetiLog.printDebugLog("Will store in " + testFileName, this);

		// if it is in a package, we separate the package name from the class
		// name
		if (testFileName.contains(".")) {
			int indexOfLastDot = testFileName.lastIndexOf(".");
			className = testFileName.substring(indexOfLastDot + 1);
			packageName = testFileName.substring(0, indexOfLastDot);
		} else {
			className = testFileName;
		}

		String testCase = "";
		// we then build the test case
		if (packageName != null) {
			testCase = "package "
					+ packageName
					+ ";\n\nimport static org.junit.Assert.*;\nimport java.io.*;\nimport org.junit.Test;\n\n/** Class automatically generated by YETI **/\npublic class "
					+ className + " {";
		} else {
			testCase = "import static org.junit.Assert.*;\nimport java.io.*;\nimport org.junit.Test;\n\n/** Class automatically generated by YETI **/\npublic class "
					+ className + " {";
		}
		testCase = testCase
				+ super.generateUnitTestFile(processedTestCases,
						unitTestFileName) + "\n\n}";
		return testCase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yeti.YetiLogProcessor#generateUnitTestFileName(java.util.Vector,
	 * java.lang.String)
	 */
	@Override
	public String generateUnitTestFileName(Vector<String> processedTestCases,
			String unitTestFileName) {

		String testFileName = unitTestFileName;

		// if the user specified the filename with a .java, we remove it.
		if (unitTestFileName.endsWith(".java")) {
			testFileName = testFileName.substring(0, testFileName.length() - 5);
		}

		String directoryName = null;
		// if we store the file into a different location, we remove the
		// directory name
		if (testFileName.contains("/")) {
			int indexOfLastSlash = testFileName.lastIndexOf("/");
			directoryName = testFileName.substring(0, indexOfLastSlash);
			testFileName = testFileName.substring(indexOfLastSlash + 1);
		}

		String className;
		// if it is in a package, we separate the package name from the class
		// name
		if (testFileName.contains(".")) {
			int indexOfLastDot = testFileName.lastIndexOf(".");
			className = testFileName.substring(indexOfLastDot + 1);
		} else {
			className = testFileName;
		}

		// we add .java to the file name
		YetiLog.printDebugLog("fileName is: " + className, Yeti.class);
		String fullName = className + ".java";

		// if it is supposed to go to a directory, we add it too
		if (directoryName != null) {
			fullName = directoryName + System.getProperty("file.separator")
					+ fullName;
		}

		return fullName;

	}

	@Override
	public ArrayList<String> readTracesFromFile(String fileName) {
		ArrayList<String> result = new ArrayList<String>();
		YetiLog.printDebugLog("Filename is " + fileName, this);

		// if we have a class file, we will execute methods instead
		if (fileName.endsWith(".class")) {
			String className = null;
			if (fileName.contains(System.getProperty("file.separator"))) {
				className = fileName.substring(fileName.lastIndexOf(System
						.getProperty("file.separator")) + 1, fileName
						.lastIndexOf("."));
			} else {
				className = fileName.substring(0, fileName.lastIndexOf("."));

			}
			try {
				YetiLog.printDebugLog("Loading: " + className, this);
				// we first load the class
				@SuppressWarnings("rawtypes")
				Class c = ClassLoader.getSystemClassLoader().loadClass(
						className);
				for (Method m : c.getDeclaredMethods()) {
					// if the annotation is there
					if (m.isAnnotationPresent(YetiTrace.class)) {
						// we create a target for the call
						Object o = m.getDeclaringClass().getConstructor()
								.newInstance();
						YetiLog.printDebugLog("Target of test call is: " + o,
								this);
						YetiLog.printDebugLog("Calling: \n" + m.getName(), this);

						try {
							// we invoke the method with no arguments
							m.invoke(o);
						} catch (Throwable t) {
							// if there was a Throwable thrown, we add the trace
							// to the list of
							// returned non-errors
							Throwable t0 = t.getCause();
							String s = this.getTraceFromThrowable(t0);
							s = s.substring(0, s.lastIndexOf("\n"));
							if (Yeti.testModule.isThrowableInModule(s)
									&& s.indexOf('\t') >= 0) {
								result.add(s);
								YetiLog.printDebugLog("Added trace: \n" + s,
										this);
							}
						}
					}
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Could not find class " + className);
				e.printStackTrace();
			} catch (SecurityException e) {
				System.out
						.println("Does not have the right privileges when importing traces in "
								+ className);
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				System.out
						.println("Cannot execute method when importing traces in "
								+ className);
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				System.out
						.println("Should not happen: the trace methods should have no arguments in "
								+ className);
				e.printStackTrace();
			} catch (InstantiationException e) {
				System.out.println("Should not happen: the class " + className
						+ " should be instanciable with no argument");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.out
						.println("Does not have the right privileges when importing traces in "
								+ className);
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				System.out.println("Should not happen: the class " + className
						+ " should be instanciable with no argument"
						+ className);
				e.printStackTrace();
			}
			return result;
		}
		// if the file is a regular text file, we use the generic importer
		return super.readTracesFromFile(fileName);
	}

}
