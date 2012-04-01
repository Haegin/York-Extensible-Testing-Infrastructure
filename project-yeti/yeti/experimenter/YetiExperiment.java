package yeti.experimenter;

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

import java.io.PrintStream;

/**

YETI - York Extensible Testing Infrastructure

Copyright (c) 2009-2011, Manuel Oriol <manuel.oriol@gmail.com> - University of York
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

THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

**/ 

import yeti.Yeti;
import yeti.YetiReport;

/**
 * Class that represents an experiment with YETI
 *
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Apr 7, 2011
 *
 */
public abstract class YetiExperiment {
	
	/**
	 * The Printstream in which print the results.
	 */
	PrintStream ps;
	
	/**
	 * The static options for this experiment.
	 */
	String []staticOptions;
	
	/**
	 * True if only printing the calls.
	 */
	boolean onlyPrint=false;
	
	/**
	 * Simple constructor for YetiExperiments.
	 * 
	 * @param ps the printStream in which print the results.
	 * @param staticOptions the static options to call YETI.
	 * @param onlyPrint true to only print the calls on the command-line.
	 */
	public YetiExperiment(PrintStream ps, String []staticOptions, boolean onlyPrint) {
		this.ps=ps;
		this.staticOptions=staticOptions;
		this.onlyPrint = onlyPrint;
		
	}
	
	/**
	 * The method to chain the calls.
	 */
	public abstract void make();
	
	/**
	 * Make an individual call to YETI.
	 * 
	 * @param yetiPath the yetiPath to provide.
	 * @param modules the modules to test.
	 */
	public void makeCall(String yetiPath, String modules) {
		// we first initialize the arguments.
		String []args = new String[staticOptions.length+2];
		for (int i = 0;i<staticOptions.length;i++) {
			args[i]=staticOptions[i];
		}
		args[staticOptions.length]="-yetiPath="+yetiPath;
		args[staticOptions.length+1]="-testModules="+modules;
		// We then make the call
		if (!onlyPrint) {
			Yeti.YetiRun(args);
			Yeti.reset();
		} else {
			String call = "java -ea yeti.Yeti ";
			for (String opt: args) {
				call=call+" "+opt;
			}
			System.out.println(call);
		}
	}

}
