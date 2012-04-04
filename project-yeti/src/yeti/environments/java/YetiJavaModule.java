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

import java.lang.reflect.Method;

import yeti.YetiModule;
import yeti.YetiNoCoverageException;
import yeti.monitoring.YetiCoverageIndicator;


/**
 * Class that represents a Java module (typically in Java either a class or a package).
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 *
 */
public class YetiJavaModule extends YetiModule {
	
	private Class c = null;
	
	/**
	 * In this implementation we consider it to be a class name.
	 * 
	 * @param className the name of the class of this module.
	 */
	public YetiJavaModule(String className, Class c){
		super(className);
		this.c=c;
	}


	/**
	 * Checks that the throwable trace contains the name (or one of the names) of the module(s) in its trace.
	 * 
	 * @param throwableTrace the trace to check.
	 * @return true if the throwable is relevant.
	 */
	public boolean isThrowableInModule(String throwableTrace) {
		// we remove the beginning of the trace
		String trace = throwableTrace.substring(throwableTrace.lastIndexOf('\t')+1);
		trace = trace.substring(trace.indexOf(' ')+1);
		// we return true if the trace contains the module name
		if (trace.startsWith(this.getModuleName()+"."))
			return true;
		return false;
	}


	/* (non-Javadoc)
	 * @see yeti.monitoring.YetiBranchCoverageIndicator#__yeti_get_coverage()
	 */
	@SuppressWarnings("unchecked")
	public double getCoverage() throws YetiNoCoverageException {
		if (c!=null) {
			Method m;
			try {
				double d;
				Class []cc = {};
				Object []oo= {};
				m = c.getMethod("__yeti_get_coverage", cc);			
				d = (Double) (m.invoke(null, oo));
				return d;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				throw new YetiNoCoverageException(this.moduleName);
			}
		}
		return 0;
	}
	
	/** 
	 * This method returns the type of coverage.
	 * 
	 * @see yeti.monitoring.YetiCoverageIndicator#getCoverageKind()
	 */
	public String getCoverageKind() throws YetiNoCoverageException {
		return "Branch coverage";
	}
	/* (non-Javadoc)
	 * @see yeti.monitoring.YetiCoverageIndicator#getNumberOfBranches()
	 */
	public long getNumberOfBranches() throws YetiNoCoverageException {
		if (c!=null) {
			Method m;
			try {
				Class []cc = {};
				Object []oo= {};
				m = c.getMethod("__yeti_get_n_branches", cc);			
				return (Integer) (m.invoke(null, oo));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				throw new YetiNoCoverageException(this.moduleName);
			}
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see yeti.monitoring.YetiCoverageIndicator#getNumberOfCoveredBranches()
	 */
	public long getNumberOfCoveredBranches() throws YetiNoCoverageException {
		if (c!=null) {
			Method m;
			try {
				Class []cc = {};
				Object []oo= {};
				m = c.getMethod("__yeti_get_covered_branches", cc);			
				return (Integer) (m.invoke(null, oo));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				throw new YetiNoCoverageException(this.moduleName);
			}
		}
		return 0;
	}


}
