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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import yeti.YetiCallContext;
import yeti.YetiCallException;
import yeti.YetiCard;
import yeti.YetiLog;
import yeti.YetiLogProcessor;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.environments.YetiSecurityException;


/**
 * Class that represents a routine in Java. A routine is supposed to be either a constructor or a  method.
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiJavaRoutine extends YetiRoutine {

	/**
	 * Result of the last call.
	 */
	protected YetiVariable lastCallResult=null;


	/**
	 * 
	 * Creates a Java routine.
	 * 
	 * @param name the name of the routine.
	 * @param openSlots the open slots for the routine.
	 * @param returnType the type of the returned value.
	 * @param originatingModule the module in which it was defined
	 */
	public YetiJavaRoutine(YetiName name, YetiType[] openSlots, YetiType returnType, YetiModule originatingModule) {
		super();
		this.name = name;
		this.openSlots = openSlots;
		this.returnType = returnType;
		this.originatingModule = originatingModule;
	}

	/* (non-Javadoc)
	 * 
	 * Checks the arguments. In the case of Java, arguments match (TODO: change that for generics).
	 * 
	 * @see yeti.YetiRoutine#checkArguments(yeti.YetiCard[])
	 */
	@Override
	public boolean checkArguments(YetiCard[] arg) {

		return true;
	}

	
	/* (non-Javadoc)
	 * Method used to perform the actual call
	 * 
	 * @see yeti.environments.java.YetiJavaRoutine#makeCall(yeti.YetiCard[])
	 */
	public Object makeCall(YetiCard []arg){
		String log = null;

		try {

			try {
				this.incnTimesCalled();
				makeEffectiveCall(arg);
				this.incnTimesCalledSuccessfully();
			} catch(YetiCallException e) {
				log = e.getLog();
				throw e.getOriginalThrowable();
			}

		} catch (IllegalArgumentException e) {
			YetiLog.printDebugLog(this.getSignature()+" IllegalArgumentException", this,true);
			for(YetiCard c: arg) {
				YetiLog.printDebugLog("YetiType: "+c.getType().toString()+", real type: "+c.getValue().getClass()+", value: "+c.getValue().toString(),this,true);
				//for (YetiType t: c.getType().directSuperTypes.values()) YetiLog.printDebugLog(t.getName()+" is supertype of "+ c.getType().toString(), this,true);
				//for (YetiType t: YetiType.allTypes.values()) YetiLog.printDebugLog("Type in the system: "+t.getName(), this,true);
			}
			// is not a bug
			this.incnTimesCalledSuccessfully();
			//e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			YetiLog.printDebugLog(this.getSignature()+" IllegalAccessException", this);
			// should never happen
			// e.printStackTrace();
			this.incnTimesCalledSuccessfully();
			return null;
		} catch (InvocationTargetException e) {

			// if we are here, we found a bug.
			// we first print the log
			YetiLog.printYetiLog("try {"+log+");} catch(Throwable t){}", this);
			// then print the exception
			if ((e.getCause() instanceof RuntimeException  || e.getCause() instanceof Error) && !isAcceptable(e.getCause())) {
				if (e.getCause() instanceof ThreadDeath) {
					YetiLog.printYetiLog("/**POSSIBLE BUG FOUND: TIMEOUT**/", this);
					this.incnTimesCalledUndecidable();
					YetiLog.printYetiThrowable(e.getCause(), new YetiCallContext(this,arg,e,"/**POSSIBLE BUG FOUND: TIMEOUT**/\n/** "+YetiLog.proc.getTraceFromThrowable(e.getCause())+"**/"));
				} else {
					if (e.getCause() instanceof YetiSecurityException) {
						YetiLog.printYetiLog("/**POSSIBLE BUG FOUND: "+e.getCause().getMessage()+" **/", this);
						this.incnTimesCalledUndecidable();
						YetiLog.printYetiThrowable(e.getCause(), new YetiCallContext(this,arg,e,"/**POSSIBLE BUG FOUND: "+e.getCause().getMessage()+"**/\n/** "+YetiLog.proc.getTraceFromThrowable(e.getCause())+"**/"));
					} else {
						if (YetiLog.isAccountableFailure(e.getCause())) {
							YetiLog.printYetiLog("/**BUG FOUND: RUNTIME EXCEPTION**/", this);
							this.incnTimesCalledUnsuccessfully();
							YetiLog.printYetiThrowable(e.getCause(), new YetiCallContext(this,arg,e,"/**BUG FOUND: RUNTIME EXCEPTION**/\n/** "+YetiLog.proc.getTraceFromThrowable(e.getCause())+"**/"));
						}
						else {
							YetiLog.printYetiLog("/**NORMAL EXCEPTION:**/", this);
							this.incnTimesCalledSuccessfully();
						}
					}
				}
			}
			else {
				YetiLog.printYetiLog("/**NORMAL EXCEPTION:**/", this);
				YetiLog.printYetiThrowable(e.getCause(), new YetiCallContext(this,arg,e,"/**NORMAL EXCEPTION:**/\n/** "+YetiLog.proc.getTraceFromThrowable(e.getCause())+"**/"),false);
				this.incnTimesCalledSuccessfully();
			}
			return null;
		} catch (Error e){
			// if we are here there was a serious error
			// we print it
			YetiLog.printYetiLog("try {"+log+");} catch(Throwable t){}", this);
			if (!YetiLog.isAccountableFailure(e.getCause())) {
				YetiLog.printYetiLog("/**BUG FOUND: ERROR**/", this);
				YetiLog.printYetiThrowable(e.getCause(), new YetiCallContext(this,arg,e,"/**BUG FOUND: ERROR**/\n/** "+YetiLog.proc.getTraceFromThrowable(e.getCause())+"**/"),true);
				this.incnTimesCalledUnsuccessfully();
			}
			else {
				YetiLog.printYetiLog("/**NORMAL EXCEPTION:**/", this);
				YetiLog.printYetiThrowable(e.getCause(), new YetiCallContext(this,arg,e,"/**NORMAL EXCEPTION:**/\n/** "+YetiLog.proc.getTraceFromThrowable(e.getCause())+"**/"),false);
				this.incnTimesCalledSuccessfully();
			}
			return null;
		}
		catch (Throwable e){
			// should never happen
			e.printStackTrace();
			return null;
		}
		return this.lastCallResult;
	}

	/**
	 * A hashmap of acceptable exception types.
	 */
	public HashMap <String, Class> acceptableExceptionTypes = new HashMap <String, Class>();

	/**
	 * @return  the types of exceptions that are considered "acceptable"
	 */
	public HashMap<String, Class> getAcceptableExceptionTypes() {
		return acceptableExceptionTypes;
	}

	/**
	 * Add an acceptable exception type of this routine.
	 * 
	 * @param s the name of the exception type.
	 */
	public void addAcceptableExceptionType(String s) {
		try {	
			YetiLog.printDebugLog("Added to "+this.name.getValue() + " the following acceptable exception " +s+" in "+Class.forName(s), this);
			acceptableExceptionTypes.put(s, Class.forName(s));
		} catch (ClassNotFoundException e) {
			// Ignored
			YetiLog.printDebugLog(e.getMessage(), this);
		}
	}

	/**
	 * This method returns true if and only if the throwable 
	 * is considered acceptable for the routine.
	 * 
	 * @param cause the Throwable to consider.
	 * @return true if the exception is to be expected, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	public boolean isAcceptable(Throwable cause) {
		// optimization
		if ((YetiJavaProperties.ignoreIllegalArgumentExceptions) 
				&& (cause instanceof IllegalArgumentException)) return true;
		if (acceptableExceptionTypes.isEmpty()) return false;
		// for all classes, we check that the Throwable is not an instance 
		// of a subclass of an acceptable Throwable

		YetiLog.printDebugLog("Class of the cause of the Throwable: "+cause.getClass().getName()+ "for method: "+this.getName().getValue(), this);
		for (Class c: acceptableExceptionTypes.values()) {
			YetiLog.printDebugLog("Matching: _"+c.getName()+ "_ with: _"+cause.getClass().getName(), this);

			if (c.getName().equals(cause.getClass().getName())) return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * A stub for sublasses.
	 * 
	 * @see yeti.YetiRoutine#makeEffectiveCall(yeti.YetiCard[])
	 */
	@Override
	public String makeEffectiveCall(YetiCard[] arg) throws Throwable {
		// by default this one does not do anything
		return null;
	}

}
