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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.environments.java.YetiJavaPrefetchingLoader;

/**
 * 
 * Class that represents the custom class loader to load classes of the program.
 * It creates Yeti routines that form constructors and methods annotated with JML.
 * 
 * If the classes are not annotated with JML they are treated as normal Java classes.
 * 
 * @author Vasileios Dimitriadis (vd508@cs.york.ac.uk, vdimitr@hotmail.com)
 * @date 20 Jul 2009
 *
 */
public class YetiJMLPrefetchingLoader extends YetiJavaPrefetchingLoader {
	
	/**
	 * This character denotes that the 
	 */
	private static final char JML_SPECIFIC_INDICATOR = '$';
	
	/**
	 * Constructor that creates a new loader for loading JML annotated Java classes.
	 * 
	 * @param path the classpath to load classes.
	 */
	public YetiJMLPrefetchingLoader(String path) {
		super(path);
	}
	
	/**
	 * Create a Yeti routine for the JML annotated constructor to test
	 * 
	 * @param clazz the originating class.
	 * @param paramTypes the types of the parameters.
	 * @param type the type of the created object.
	 * @param mod the module to which we should add it.
	 * @param m the constructor.
	 * @return the Yeti routine for the constructor of the class c
	 */
	@SuppressWarnings("unchecked")
	protected YetiRoutine generateRoutineFromConstructor(Class clazz, YetiType[] paramTypes, YetiType type, YetiModule mod, Constructor con) {
		return new YetiJMLConstructor(YetiName.getFreshNameFrom(clazz.getName()), paramTypes , type, mod, con);
	}
	
	/**
	 * Create a Yeti routine for the JML annotated method to test
	 * 
	 * @param module the module to which add the method.
	 * @param method the method to add.
	 * @param paramTypes the types of the parameters.
	 * @param returnType the type returned by the method 
	 * @return a Yeti routine for this method
	 */
	protected YetiRoutine generateRoutineFromMethod(YetiModule module, Method method, YetiType[] paramTypes, YetiType returnType) {
		return new YetiJMLMethod(YetiName.getFreshNameFrom(method.getName()), paramTypes , returnType, module, method);
	}
	
	/**
	 * Add a method to the module if it is usable.
	 * 
	 * @param module the module to which add the method.
	 * @param method the method to add.
	 * @param usable True if it should be added.
	 * @param paramTypes the types of the parameters.
	 */
	public void addMethodToModuleIfUsable(YetiModule module, Method method, boolean usable, YetiType[] paramTypes) {
		if(!isJMLSpecific(method)) {
			super.addMethodToModuleIfUsable(module, method, usable, paramTypes);
		}
	}
	
	/**
	 * Check whether the method to add is a method generated from JML
	 * 
	 * @param method The method to test
	 * @return True if the method contains '$'
	 */
	protected boolean isJMLSpecific(Method method) {
		// if it is jml-specific, skip the method
		return method.getName().indexOf(JML_SPECIFIC_INDICATOR) >= 0; 
	}
}
