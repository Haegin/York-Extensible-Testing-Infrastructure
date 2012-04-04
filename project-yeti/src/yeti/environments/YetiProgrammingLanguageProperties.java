package yeti.environments;

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

import yeti.YetiLogProcessor;


/**
 * Class that represents the properties of a language for this session. 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 *
 */
public abstract class YetiProgrammingLanguageProperties {
	

	/**
	 * Should we have proper logs?
	 */
	private boolean isNoLogs =false;

	/**
	 * Simple getter on the logs.
	 * 
	 * @return false if there is logs, true if there is no log.
	 */
	public boolean isNoLogs() {
		return isNoLogs;
	}

	/**
	 * Simple setter on the no logs.
	 * 
	 * @param isNoLogs true to remove logs, false otherwise.
	 */
	public void setNoLogs(boolean isNoLogs) {
		this.isNoLogs = isNoLogs;
	}

	/**
	 * Should we get the logs rather than the test case
	 */
	private boolean isRawLog = false;
	
	/**
	 * Returns if it is a raw log (unprocessed)
	 * 
	 * @return true if this is raw logs, false otherwise.
	 */
	public boolean isRawLog() {
		return isRawLog;
	}

	/**
	 * Sets it as rawLogs
	 * 
	 * @param isRawLog true if it is going to be a raw log
	 */
	public void setRawLog(boolean isRawLog) {
		this.isRawLog = isRawLog;
	}
	/**
	 * Returns the test manager for this language.
	 * 
	 * @return the test manager for this language.
	 */
	public abstract YetiTestManager getTestManager();
	
	/**
	 * Returns the initialiser for this session and this language
	 * 
	 * @return the initialiser for this language and this session.
	 */
	public abstract YetiInitializer getInitializer();

	/**
	 * Returns the log processor for this session and this language
	 * 
	 * @return the log processor for this language and this session.
	 */
	public abstract YetiLogProcessor getLogProcessor();
}
