package yeti.environments.commandline;

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

import javax.swing.JOptionPane;

import yeti.Yeti;
import yeti.YetiInitializationException;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.environments.YetiInitializer;
import yeti.environments.YetiLoader;
import yeti.environments.java.YetiJavaPrefetchingLoader;
import yeti.environments.java.YetiJavaSpecificType;

 


/**
 * Class that represents an initializer for command-lines 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jul 23, 2010
 *
 */
public class YetiCLInitializer extends YetiInitializer {

	/**
	 * timeout million seconds for each command
	 * */
	static int timeout = 1000;
	
	/**
	 * A simple helper routine that ignores the parameter String.
	 * 
	 * @param s the string to be ignored.
	 */
	public void ignore(String s){

	}
	/* (non-Javadoc)
	 * @see yeti.environments.YetiInitializer#initialize(java.lang.String[])
	 */
	@Override
	public void initialize(String[] args) throws YetiInitializationException {
		
		YetiCLSpecificType.initPrimitiveTypes();

		// we go through all arguments
		for(int i=0; i<args.length; i++) {
			if (args[i].equals("-cl")) 
				ignore(args[i]);
			else {
				// we want to test these modules
				String []modulesToTest=null;
				for (String s0: args) {
					if (s0.startsWith("-testModules=")) {
						String s1=s0.substring(13);
						yeti.YetiLog.printDebugLog("Has found modules to test: "+s1, this);
						modulesToTest=s1.split(":");
						//break; //two options need to process 
						continue;
					}
					
					// get the timeout if user specified in launching option
					if(s0.startsWith("-msCalltimeout")){
						int size = s0.length();
						timeout=(Integer.parseInt(s0.substring(15, size)));
						if (timeout<=0) {
							Yeti.printHelp();
							return;
						}
						continue;
					}
				}

				// we iterate through the modules
				// if the module does not exist we load it
				for(String moduleToTest : modulesToTest) {
					YetiModule yetiModuleToTest = YetiModule.allModules.get(moduleToTest);
					if(yetiModuleToTest==null) {
						yeti.YetiLog.printDebugLog("Has found module to test to create: "+moduleToTest, this);
						YetiCLModule ycm = new YetiCLModule(moduleToTest);
						YetiModule.allModules.put(moduleToTest, ycm);
					} 
				}


			}
		}
	}

}
