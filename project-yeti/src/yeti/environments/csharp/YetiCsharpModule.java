package yeti.environments.csharp;

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

import yeti.YetiModule;


/**
 * Class that represents a Csharp module (typically in Csharp either a class or an assembly).
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jul 20, 2009
 *
 */

public class YetiCsharpModule extends YetiModule {
	
	/**
	 * In this implementation we consider it to be a class name.
	 * 
	 * @param className the name of the class of this module.
	 */
	public YetiCsharpModule(String className){
		super(className);
	}
	
	/**
	 * Checks that the trace contains refers to the module(s) in its trace.
	 * 
	 * @param trace the trace to check.
	 * @return true if the throwable is relevant.
	 */
	public boolean isThrowableInModule(String trace) {
		String moduleName = this.getModuleName();
		int endExe = moduleName.indexOf(".exe");
		int endDLL = moduleName.indexOf(".dll");
		int end = trace.length();
		
		// if it is neither a dll nor a exe we return false
		if ((endExe<0) && (endDLL<0)) return false;
		
		// if it is one of those, we will cut it
		if (endExe>=0) end = endExe;
		if (endDLL>=0) end = endDLL;
		
		// we check that the trace contains the module name
		return (trace.indexOf(moduleName.substring(0,end))>=0);
	}

	
}
