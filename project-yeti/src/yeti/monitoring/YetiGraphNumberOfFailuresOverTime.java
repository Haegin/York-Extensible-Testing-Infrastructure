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

import javax.swing.JFrame;

import yeti.YetiLog;
import yeti.YetiLogProcessor;

/**
 * Class that allows the wrapping of a YetiLogProcessor and shows the number of failures over time.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jul 27, 2009
 *
 */
public class YetiGraphNumberOfFailuresOverTime extends YetiGraph {

	/**
	 * The log processor associated with this component
	 */
	public YetiLogProcessor lp=null;

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
	public YetiGraphNumberOfFailuresOverTime(YetiLogProcessor lp, long nMSBetweenUpdates) {
		super("Number of Failures, steps of "+nMSBetweenUpdates+" milliseconds");
		this.nMSBetweenUpdates=nMSBetweenUpdates;
		this.lp=lp;
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
	 * the value of the starting time.
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
			addValue(0,YetiLog.numberOfErrors);
		} else {
			// otherwise, we remove the offset and add the offsetted value
			lastInstantShown = new Date().getTime();
			addValue(((double)(lastInstantShown-firstInstant))/nMSBetweenUpdates,YetiLog.numberOfErrors);				

		}
	}	

}
