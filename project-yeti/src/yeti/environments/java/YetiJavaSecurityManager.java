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
 * @author  Manuel Oriol (manuel@cs.york.ac.uk), Muneeb Waseem Kh (mwk500@york.ac.uk)
 * @date  Jun 28, 2010
 */
public class YetiJavaSecurityManager extends SecurityManager {

	/*
	* We prevent the system under test from any file handling operations
	*/
	@Override
	public void checkPermission(Permission perm) {
		// if we are in the thread group of worker threads
		if (this.getThreadGroup()==YetiJavaTestManager.workersGroup) {
			// if we are trying to access a file permission
			if (perm instanceof FilePermission) {
				String action = perm.getActions();
				// if any of those is in the permission requested, we throw the exception
				if ((action.indexOf("write")>=0) || (action.indexOf("execute")>=0) || (action.indexOf("delete")>=0)) {
					YetiLog.printDebugLog("Yeti did not grant permission: "+ perm, this);
					throw new YetiSecurityException("Yeti did not grant the following file permission: "+perm.toString());
				}
			}
		}
	}
	
	/*
	* We prevent the system under test from exiting the VM
	*/
	@Override
	public void checkExit(int status)
	{
		if (this.getThreadGroup()==YetiJavaTestManager.workersGroup) {
			super.checkExit(status);
			YetiLog.printDebugLog("Yeti did not grant exit permission", this);
			throw new YetiSecurityException("Yeti did not grant exit permission");
		}
	}
	
	
	

}
