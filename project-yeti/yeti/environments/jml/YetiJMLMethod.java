package yeti.environments.jml;

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

import java.lang.reflect.Method;

import yeti.YetiCard;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiType;
import yeti.environments.java.YetiJavaMethod;

/**
 * Class that represents a Java method annotated with JML.
 * @author  Vasileios Dimitriadis (vd508@cs.york.ac.uk, vdimitr@hotmail.com)
 * @date  20 Jul 2009
 */
public class YetiJMLMethod extends YetiJMLRoutine {
	
	/**
	 * The wrapped Java method
	 */
	private YetiJavaMethod javaMethod = null;
	
	/**
	 * Constructor to define a JML method. By default the method is non-static.
	 * 
	 * @param name the name of the method.
	 * @param openSlots the open slots of the method.
	 * @param returnType the return type of the method.
	 * @param originatingModule the module in which it was defined.
	 * @param method the method implementation.
	 */
	public YetiJMLMethod(YetiName name, YetiType[] openSlots, YetiType returnType, YetiModule originatingModule, Method method) {
		super(name, openSlots, returnType, originatingModule);
		javaMethod = new YetiJavaMethod(name, openSlots, returnType, originatingModule, method);
		acceptableExceptionTypes = javaMethod.getAcceptableExceptionTypes();
	}

	/**
	 * Constructor to define a JML method
	 * 
	 * @param name the name of the method.
	 * @param openSlots the open slots of the method.
	 * @param returnType the return type of the method.
	 * @param originatingModule the module in which it was defined.
	 * @param method the method implementation.
	 * @param isStatic the parameter that says whether the method is static or not.
	 */
	public YetiJMLMethod(YetiName name, YetiType[] openSlots, YetiType returnType, YetiModule originatingModule, Method method, boolean isStatic) {
		super(name, openSlots, returnType, originatingModule);
		javaMethod = new YetiJavaMethod(name, openSlots, returnType, originatingModule, method, isStatic);
		acceptableExceptionTypes = javaMethod.getAcceptableExceptionTypes();
	}
	
	@Override
	public String makeEffectiveCall(YetiCard[] yetiCards) throws Throwable {
		return javaMethod.makeEffectiveCall(yetiCards);
	}
	
	@Override
	public String toString() {
		return javaMethod.toString();
	}
	
	/**
	 * Creates a method call from the routine and the arguments
	 * 
	 * @param arguments the arguments of the call
	 * @return the text of the test case
	 */
	public String toStringWithArguments(YetiCard[] arguments) {
		return javaMethod.toStringWithArguments(arguments);
	}
}
