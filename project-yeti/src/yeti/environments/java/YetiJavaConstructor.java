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

import java.lang.reflect.Constructor;
import yeti.YetiCallException;
import yeti.YetiCard;
import yeti.YetiIdentifier;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiType;
import yeti.YetiVariable;

/**
 * Class that represents a constructor in Java.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 *
 */
public class YetiJavaConstructor extends YetiJavaRoutine {

	/**
	 * The actual constructor.  
	 */
	@SuppressWarnings("unchecked")
	protected Constructor c;



	/**
	 * Constructor to the constructor.
	 * 
	 * @param name the name of this constructor.
	 * @param openSlots the open slots for this constructor.
	 * @param returnType the returnType of this constructor.
	 * @param originatingModule the module in which it was defined.
	 * @param c the constructor itself.
	 */
	@SuppressWarnings("unchecked")
	public YetiJavaConstructor(YetiName name, YetiType[] openSlots, YetiType returnType, YetiModule originatingModule, Constructor c) {
		super(name, openSlots, returnType, originatingModule);
		this.c=c;

		for (Class cl: c.getExceptionTypes()) {
			this.addAcceptableExceptionType(cl.getName());			
		}
	}


	/* (non-Javadoc)
	 * Returns the actual name of the constructor.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return c.getName();
	}



	/**
	 * Makes the effective call (lets return the exceptions and Errors).
	 * 
	 * @param arg the arguments of the call.
	 * @return the logs.
	 * @throws YetiCallException the wrapped exception. 
	 */

	public String makeEffectiveCall(YetiCard[] arg)
	throws YetiCallException {
		String log;
		lastCallResult=null;
		Object []initargs=new Object[arg.length];

		// we start by unboxing the arguments boxed into the cards
		YetiIdentifier id=YetiIdentifier.getFreshIdentifier();
		log = returnType.toString() + " " + id.getValue() + "=new "+returnType.getName()+"(";
		for (int i=0;i<arg.length; i++){
			// if we should replace it by a null value, we do it
			if (YetiVariable.PROBABILITY_TO_USE_NULL_VALUE>Math.random()&&!(((YetiJavaSpecificType)arg[i].getType()).isSimpleType())) {
				initargs[i]=null;
				arg[i]=new YetiCard(arg[i].getType());
				log=log+"("+arg[i].getType()+")null";
			} else {
				// note that we use getValue to get the actual value
				initargs[i]=arg[i].getValue();
				// we use toString() to make it pretty-print.
				log=log+"("+arg[i].getType()+")"+arg[i].toString();
			}
			if (i<arg.length-1){
				log=log+",";
			}
		}
		// we try to make the call
		Object o = null;
		try {
			o = c.newInstance(initargs);
		} catch (Throwable t) {
			throw new YetiCallException(log,t);
		}
		// if it succeeds we create the new variable
		this.lastCallResult=new YetiVariable(id, returnType, o);
		log=log+");";
		// print the log
		YetiLog.printYetiLog(log, this);
		YetiLog.printDebugLog(log, this);
		return log;
	}

	/**
	 * Creates a method call from the routine and the arguments
	 * 
	 * @param arguments the arguments of the call
	 * @return the text of the test case
	 */
	public String toStringWithArguments(YetiCard[] arguments) {
		String longRoutineName = this.getName().getValue();
		String routineName = longRoutineName.substring(0,longRoutineName.lastIndexOf("_"));
		String prefix = "";
		// we first set up the prefix with some arguments
		if (arguments.length>0) {
			prefix = arguments[0].toStringPrefix();
		}
		// we thena dd the arguments
		String testCaseBody= prefix+((prefix.length()==0)?"":"\n")+"new "+routineName+"(";
		if (arguments.length>0) {
			testCaseBody = testCaseBody+arguments[0].toStringVariable();
			for (int i=1;i<arguments.length;i++) {
				if (!testCaseBody.contains(arguments[i].toStringVariable()+"=")) {
					prefix = arguments[i].toStringPrefix();
				} else
					prefix = "";
				testCaseBody=prefix+((prefix.length()==0)?"":"\n")+testCaseBody+","+arguments[i].toStringVariable();
			}

		}
		testCaseBody=testCaseBody+");\n";
		return testCaseBody;
	}

}
