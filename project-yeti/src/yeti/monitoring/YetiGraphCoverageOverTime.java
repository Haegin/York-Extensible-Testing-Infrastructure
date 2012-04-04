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

import java.util.Date;
import yeti.YetiNoCoverageException;


/**
 * Class that represents the a graph that shows the coverage.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 30, 2010
 *
 */
@SuppressWarnings("serial")
public class YetiGraphCoverageOverTime extends YetiGraph {
	/**
	 * The log processor associated with this component
	 */
	public YetiCoverageIndicator indicator;

	/**
	 * The number of ms between updates.
	 */
	public long nMSBetweenUpdates = 0;

	/**
	 * A constructor for this GUI.
	 * 
	 * @param lp the processor that we wrap inside it.
	 * @param nMilliseconds number of milliseconds second between failures.
	 */
	public YetiGraphCoverageOverTime(YetiCoverageIndicator bi, long nMSBetweenUpdates) {
		super("Coverage, steps of "+nMSBetweenUpdates+" milliseconds");
		try {
			this.name = bi.getCoverageKind()+", steps of "+nMSBetweenUpdates+" milliseconds";
		} catch (YetiNoCoverageException e) {
			// should never happen
		}
		this.nMSBetweenUpdates=nMSBetweenUpdates;
		this.indicator=bi;
	}


	/**
	 * true if this GUI has been initialized.
	 */
	public boolean called = false;

	/**
	 * The value of the last time that was shown.
	 */
	public long lastInstantShown = 0;

	/**
	 * The value of the starting time.
	 */
	public long firstInstant = 0;

	/* (non-Javadoc)
	 * Taking a sample of the 
	 * 
	 * @see yeti.monitoring.YetiSamplable#sample()
	 */
	public void sample() {
		// if this is the first time we call it
		if (!called) {
			// we start by setting the value of the first second
			firstInstant = (long) (new Date().getTime());
			called = true;
			// and add the value
			try {
				addValue(0,indicator.getCoverage());
			} catch (YetiNoCoverageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// otherwise, we remove the offset and add the offsetted value
			lastInstantShown = new Date().getTime();
			try {
				addValue(((double)(lastInstantShown-firstInstant)/nMSBetweenUpdates),indicator.getCoverage());
			} catch (YetiNoCoverageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}	
}
