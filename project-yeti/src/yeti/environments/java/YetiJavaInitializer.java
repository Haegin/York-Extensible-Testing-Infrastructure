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

import java.io.FilePermission;
import java.security.Permission;

import javax.swing.JOptionPane;

import yeti.YetiInitializationException;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.environments.YetiInitializer;
import yeti.environments.YetiLoader;
import yeti.environments.YetiSecurityException;


/**
 * Class that represents the initialiser for Java.
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiJavaInitializer extends YetiInitializer {

	/**
	 * The custom class loader that is going to be used.
	 */
	protected YetiLoader loader = null;

	/**
	 * Default constructor 
	 * @param loader the class loader to use.
	 */
	public YetiJavaInitializer(YetiLoader loader) {
		this.loader = loader;
	}

	/**
	 * A simple helper routine that ignores the parameter String.
	 * 
	 * @param s the string to be ignored.
	 */
	public void ignore(String s){

	}

	/* (non-Javadoc)
	 * Initializes the Java environment.
	 * 
	 * @see yeti.environments.YetiInitializer#initialize(java.lang.String[])
	 */
	@Override
	public void initialize(String []args) throws YetiInitializationException {
		// we initialize primitive types first
		YetiJavaSpecificType.initPrimitiveTypes();

		// we try to load classes that will certainly be used
		try {
			loader.loadClass("java.lang.Object");
			loader.loadClass("java.lang.String");
		} catch (ClassNotFoundException e1) {
			// should never happen
			e1.printStackTrace();
		}

		// we go through all arguments
		for(int i=0; i<args.length; i++) {
			if (args[i].equals("-java")) 
				ignore(args[i]);
			else {

				try {

					// we load all classes in path and String
					loader.loadAllClassesInPath();

					loader.loadClass("java.lang.String");

				} catch (ClassNotFoundException e) {
					// Should not happen, but... we ignore it...
					YetiLog.printDebugLog(e.toString(), this, true);
					// e.printStackTrace();
				}catch (Throwable t) {}
				// we want to test these modules
				String []modulesToTest=null;
				for (String s0: args) {
					if (s0.startsWith("-testModules=")) {
						String s1=s0.substring(13);
						modulesToTest=s1.split(":");
						break;
					}
				}

				// we iterate through the modules
				// if the module does not exist we load it
				for(String moduleToTest : modulesToTest) {
					YetiModule yetiModuleToTest = YetiModule.allModules.get(moduleToTest);
					if(yetiModuleToTest==null) {
						try {
							loader.loadClass(moduleToTest);
						} catch (ClassNotFoundException e) {
							// could happen, but... we ignore it...
							YetiLog.printDebugLog(e.toString(), this, true);
							// e.printStackTrace();
						} catch (Throwable t) {}
					} 
				}


			}
		}
		
		System.setSecurityManager(new YetiJavaSecurityManager());

	}

	@Override
	public void addModule(String s) {

		try {
			loader.loadClass(s);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "could not find class");
		}
	}

}
