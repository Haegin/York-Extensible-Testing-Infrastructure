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

import java.io.IOException;
import java.util.ArrayList;


import yeti.ImpossibleToMakeConstructorException;
//import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiRoutine;
import yeti.YetiStrategy;
import yeti.environments.YetiTestManager;


/**
 * Class that represents a test manager for Csharp
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Aug 10, 2009
 *
 */
public class YetiCsharpTestManager extends YetiTestManager {


	/**
	 * Number of worker threads started by the infrastructure
	 */
	public static long nThreadsStarted = 0;

	/**
	 * Number of worker threads stopped by the infrastructure
	 */
	public static long nThreadsStopped = 0;

	/**
	 * A thread group to which all workers belong.
	 */
	public static ThreadGroup workersGroup = new ThreadGroup("workers");
	/* (non-Javadoc)
	 * 
	 * We make the call.
	 * 
	 * @see yeti.environments.YetiTestManager#makeNextCall(yeti.YetiModule, yeti.YetiStrategy)
	 */

	@Override
	public void makeNextCall(YetiModule mod, YetiStrategy strategy) {


		YetiRoutine r = strategy.getNextRoutine(mod);

		// if there is a routine...
		if (r != null) {
			// we make the actual call
			try {
				r.makeCall(strategy.getAllCards(r));
			} catch (ImpossibleToMakeConstructorException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}


	}


	/* (non-Javadoc)
	 * Will stop the caller thread.
	 * 
	 * @see yeti.environments.YetiTestManager#stopTesting()
	 */
	@Override
	public void stopTesting() {
		// we call the parent
		super.stopTesting();

		YetiServerSocket.sendData("! STOP TESTING !");
		@SuppressWarnings("unused")
		ArrayList<String> a = YetiServerSocket.getData();
		try {
			YetiServerSocket.clientSocket.close();
			YetiServerSocket.s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}



	}

}
