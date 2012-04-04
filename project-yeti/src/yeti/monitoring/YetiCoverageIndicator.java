package yeti.monitoring;

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

import yeti.YetiNoCoverageException;

/**
 * Class that represents an interface for obtaining the coverage of the tests.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 30, 2010
 *
 */
public interface YetiCoverageIndicator {
	/**
	 * This method either returns a value for the coverage or an exception if not supported.
	 * 
	 * @return the coverage (between 0.0 and 1.0).
	 * @throws YetiNoCoverageException in case it does not support the coverage.
	 */	
	public abstract double getCoverage() throws YetiNoCoverageException;

	/** 
	 * This method returns the type of coverage.
	 *
	 * @return the kind of coverage.
	 * @throws YetiNoCoverageException in case it does not support the coverage.
	 */
	public abstract String getCoverageKind() throws YetiNoCoverageException;

	/**
	 * This method returns a value for the total number of branches or an exception if not supported.
	 * 
	 * @return the number of branches.
	 * @throws YetiNoCoverageException in case it does not support branch coverage.
	 */	
	public abstract long getNumberOfBranches() throws YetiNoCoverageException;

	/**
	 * This method returns a value for the number of covered branches or an exception if not supported.
	 * 
	 * @return the number of branches.
	 * @throws YetiNoCoverageException in case it does not support branch coverage.
	 */	
	public abstract long getNumberOfCoveredBranches() throws YetiNoCoverageException;
}
