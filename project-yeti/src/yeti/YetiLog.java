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

/**
 * Class that manages logging in Yeti.  The generated test cases are also part of the logging mechanism. 
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiLog {

	/**
	 * The pocessor to use in these logs. 
	 */
	public static YetiLogProcessor proc = null;

	/**
	 * The total number of calls.
	 */
	public static long numberOfCalls =0;

	/**
	 * The total number of errors.
	 */
	public static long numberOfErrors =0;

	/**
	 * The array of classes on which the debug messages should be shown. 
	 * example of classes to debug:	
	 * <code>public static String []enabledDebugClasses={"yeti.YetiType", "yeti.environments.java.YetiJavaMethod"};</code>
	 */
	//public static String []enabledDebugClasses={"yeti.YetiType", "yeti.environments.java.YetiJavaMethod"};
	//public static String []enabledDebugClasses={"yeti.environments.java.YetiJavaPrefetchingLoader"};
	public static String []enabledDebugClasses={"yeti.strategies.GA.YetiStrategyOptimizer"};

	/**
	 * Method used to print debugging messages.
	 * 
	 * @param message the debugging message to use.
	 * @param objectInWhichCalled the caller or the class of the caller in case it is in a static method.
	 */
	@SuppressWarnings("rawtypes")
	public static void printDebugLog(String message, Object objectInWhichCalled){
		String className;

		// if it is a class we check directly for this class
		if (objectInWhichCalled instanceof Class)
			className = ((Class)objectInWhichCalled).getName();
		// else we get the class of the object passed as a parameter
		else
			className = objectInWhichCalled.getClass().getName();

		// we check that the class is in the classes to print
		boolean isPrintable = false;
		for(String s : enabledDebugClasses){
			if (className.equals(s)) {
				isPrintable = true; 
				break;
			}
		}
		if (isPrintable) 
			// we print the message with maximum information so it is easier 
			// to know where it comes from 
			System.err.println("YETI DEBUG:"+className+": "+message);
	}

	/**
	 * Method used to print debugging messages. Prints the message if isTemporary is true.
	 * 
	 * @param message the debugging message to use.
	 * @param objectInWhichCalled the caller or the class of the caller in case it is in a static method.
	 * @param isTemporary print the message anyway if true.
	 */
	@SuppressWarnings("rawtypes")
	public static void printDebugLog(String message, Object objectInWhichCalled, boolean isTemporary) {
		if (isTemporary)
			if (objectInWhichCalled instanceof Class)
				System.err.println("YETI DEBUG:TMP:"+((Class)objectInWhichCalled).getName()+": "+message);
			else
				System.err.println("YETI DEBUG:TMP:"+objectInWhichCalled.getClass().getName()+": "+message);
	}

	/**
	 * This method is used to store or print Yeti logs.
	 * 
	 * @param message the log to add/print
	 * @param objectInWhichCalled object in which the method was called
	 */
	public static synchronized void printYetiLog(String message, Object objectInWhichCalled){
		numberOfCalls++;
		if (Yeti.pl.isNoLogs())
			proc.printMessageNoLogs(message);
		else {
			if (Yeti.pl.isRawLog())
				proc.printMessageRawLogs(message);
			else
				proc.appendToCurrentLog(message);
		}
	}
	
	/**
	 * Method used to store or print log of a Throwable (Exception or Error).
	 * 
	 * @param t the Throwable to print.
	 * @param objectInWhichCalled the object in which this was called.
	 */
	public static synchronized void printYetiThrowable(Throwable t, YetiCallContext context){
		printYetiThrowable(t, context,true);
	}


	/**
	 * Method used to store or print log of a Throwable (Exception or Error).
	 * 
	 * @param t the Throwable to print.
	 * @param objectInWhichCalled the object in which this was called.
	 * @param isFailure
	 */
	public static synchronized void printYetiThrowable(Throwable t, YetiCallContext context, boolean isFailure){
		if (isFailure) numberOfErrors++;
		if (Yeti.pl.isNoLogs())
			proc.printThrowableNoLogs(t, context, isFailure);
		else {
			if (Yeti.pl.isRawLog()){
				proc.printThrowableRawLogs(t, context,isFailure);
			} else {
				YetiLog.printDebugLog("Will throw Logs in "+proc.getClass().getName(), YetiLog.class);
				proc.printThrowableLogs(t, context, isFailure);
			}
		}
	}


	public static synchronized boolean isAccountableFailure(Throwable t) {
		return proc.isAccountableFailure(t);
	}

	/**
	 * Resets the logs.
	 */
	public static void reset() {
		proc = null;
		numberOfCalls =0;
		numberOfErrors =0;
		// this one is statically set anyway...
		//		enabledDebugClasses={}; 
		
	}

}
