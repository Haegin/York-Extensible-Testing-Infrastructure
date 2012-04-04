package yeti.environments.kermeta;

/**

KYETI - Kermeta binding for YETI.

Copyright 2011 (c) IRISA / INRIA / Université de Rennes 1 (Triskell team) / INSA de Rennes
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

import yeti.YetiLogProcessor;
import yeti.environments.YetiInitializer;
import yeti.environments.YetiProgrammingLanguageProperties;
import yeti.environments.YetiTestManager;

/**
 * Class that represents the Kermeta specific properties 
 * 
 * @author Erwan Bousse (erwan.bousse@gmail.com)
 * @date juil. 2011
 */
public class YetiKermetaProperties extends YetiProgrammingLanguageProperties {

	/**
	 * The initialiser.
	 */
	protected YetiInitializer initializer = null;
	
	/**
	 * The test manager.
	 */
	protected YetiTestManager testManager = null;
	
	/**
	 * The logProcessor.
	 */
	protected YetiLogProcessor logProcessor = null;
	
	/**
	 * A simple constructor for the Java properties.
	 * 
	 * @param initializer the initializer for the Java session.
	 * @param testManager the test manager for the session.
	 * @param logProcessor the log processor for the session.
	 */
	public YetiKermetaProperties(YetiInitializer initializer, YetiTestManager testManager, YetiLogProcessor logProcessor) {
		this.initializer = initializer;
		this.testManager = testManager;
		this.logProcessor = logProcessor;
	}
	
	/* (non-Javadoc)
	 * 
	 * Returns an initializer.
	 * 
	 * @see yeti.environments.YetiProgrammingLanguageProperties#getInitializer()
	 */
	@Override
	public YetiInitializer getInitializer() {
		
		return initializer;
	}

	/* (non-Javadoc)
	 * Returns the test manager
	 * 
	 * @see yeti.environments.YetiProgrammingLanguageProperties#getTestManager()
	 */
	@Override
	public YetiTestManager getTestManager() {
		return testManager;
	}

	/* (non-Javadoc)
	 * Returns the log processor
	 * 
	 * @see yeti.environments.YetiProgrammingLanguageProperties#getTestManager()
	 */
	@Override
	public YetiLogProcessor getLogProcessor() {
		return logProcessor;
	}

}
