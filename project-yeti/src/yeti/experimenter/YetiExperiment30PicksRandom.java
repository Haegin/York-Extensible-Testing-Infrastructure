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

/**
 * Class that represents an experiment with YETI where we pick 30 classes at random from a .yexp 
 * file and test for a 1000000 tests each.
 *
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Apr 7, 2011
 *
 */
public class YetiExperiment30PicksRandom extends YetiExperiment {

	/**
	 * The archive explorer where the archive is located.
	 */
	public YetiTestArchiveExplorer archive;

	/**
	 * Simple constructor for the YetiExperiment30PicksRandom
	 * 
	 * @param ps the PrintStream where to store results.
	 * @param staticOptions the options that are standard.
	 * @param arch the archive explorer.
	 * @param onlyPrint true if only to print the commands. 
	 */
	public YetiExperiment30PicksRandom(PrintStream ps, String[] staticOptions, YetiTestArchiveExplorer arch, boolean onlyPrint) {
		super(ps, staticOptions,onlyPrint);
		archive = arch;
	}

	@Override
	public void make() {
		int archiveSize = archive.allModules.size();
		YetiTestArchiveModule []archiveModules = new YetiTestArchiveModule[30];

		for (int i = 0; i<30; i++) {
			archiveModules[i]= archive.allModules.get((int) (Math.random()*archiveSize));
		}
		archive = null;
		for (int i = 0; i<30; i++) {
			this.makeCall(archiveModules[i].getClasspath(), archiveModules[i].getClassName());
		}
	}

	/**
	 * Main method allowing to launch experiments.
	 * 
	 * @param args
	 */
	public static void main(String []args) {
		String yexpFile = args[0];
		boolean onlyPrint = false;
		if (args.length>1&&args[1].equals("-onlyPrint"))
			onlyPrint = true;
		YetiTestArchiveExplorer expl = new YetiTestArchiveExplorer(".");
		try {
			expl.loadFromFile(yexpFile);

			//PrintStream ps = new PrintStream("results.csv");
			PrintStream ps = null;
			String []staticOptions = {"-Java","-nTests=100000","-nologs","-approximate","-compactReport=results.csv"};
			YetiExperiment30PicksRandom exp = new YetiExperiment30PicksRandom(ps, staticOptions, expl,onlyPrint);
			exp.make();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
