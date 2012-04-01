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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import yeti.Yeti;

/**
 * Class that represents an experiment on one class only.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date May 3, 2011
 *
 */
public class YetiExperimentOneClass extends YetiExperiment {
	
	/**
	 * A String representing the classpath
	 */
	String classPath;

	/**
	 * A string representing the name of the class to test
	 */
	String className;

	/**
	 * Simple constructor for the YetiExperiment30PicksRandom
	 * 
	 * @param ps the PrintStream where to store results.
	 * @param staticOptions the options that are standard.
	 * @param arch the archive explorer.
	 * @param onlyPrint true if only to print the commands. 
	 */
	public YetiExperimentOneClass(PrintStream ps, String[] staticOptions,boolean onlyPrint, String classPath, String className) {
		super(ps, staticOptions,onlyPrint);
		this.className = className;
		this.classPath = classPath;
	}

	@Override
	public void make() {
			for (int i = 0; i<101; i++) {
				for (int j = 0; j<101; j++) {
					this.makeCall(classPath, className, i,j);
		
				}
			}
	}

	/**
	 * Make an individual call to YETI.
	 * 
	 * @param yetiPath the yetiPath to provide.
	 * @param modules the modules to test.
	 */
	public void makeCall(String yetiPath, String modules, int nullValues, int newValues) {
		// we first initialize the arguments.
		String []args = new String[staticOptions.length+4];
		for (int i = 0;i<staticOptions.length;i++) {
			args[i]=staticOptions[i];
		}
		args[staticOptions.length]="-yetiPath="+yetiPath;
		args[staticOptions.length+1]="-testModules="+modules;
		args[staticOptions.length+2]="-newInstanceInjectionProbability="+newValues;
		args[staticOptions.length+3]="-probabilityToUseNullValue="+nullValues;
		
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
	/**
	 * Main method allowing to launch experiments.
	 * 
	 * Example of use: java yeti.experimenter.YetiExperimentOneClass . java.lang.String -onlyPrint
	 * 
	 * @param args
	 */
	public static void main(String []args) {
		String classPath = args[0];
		String className = args[1];
		
		boolean onlyPrint = false;
		if (args.length>2&&args[2].equals("-onlyPrint"))
			onlyPrint = true;
		YetiTestArchiveExplorer expl = new YetiTestArchiveExplorer(".");
		PrintStream ps = null;
		String []staticOptions = {"-Java","-nTests=100000","-nologs","-approximate","-compactReport=results.csv"};
		YetiExperimentOneClass exp = new YetiExperimentOneClass(ps, staticOptions,onlyPrint, classPath, className);
		exp.make();

	}


}
