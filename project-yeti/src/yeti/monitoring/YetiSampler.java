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

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * Class that represents a sampling class. 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Sep 4, 2009
 *
 */
public class YetiSampler implements Runnable {

	/**
	 * A vector of instances to sample.
	 */
	ArrayList<YetiSamplable> samplables = new ArrayList<YetiSamplable>();	

	/**
	 * A boolean variable to stop the thread in case it is not needed anymore.
	 */
	public boolean isToUpdate = true;
	
	/**
	 * A simple setter for is to update.
	 * 
	 * @param isToUpdate the new value. Put false to stop sampling.
	 */
	public void setToUpdate(boolean isToUpdate) {
		this.isToUpdate = isToUpdate;
	}

	/**
	 * The timeout between updates. 
	 */
	public long nMSBetweenUpdates;

	/**
	 * Add the samplable to the list of samplables.
	 * 
	 * @param s the samplable to add.
	 */
	public void addSamplable(YetiSamplable s) {
		samplables.add(s);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		long startSampledPoint = new Date().getTime();
		long currentSamplePoint = 0;
		long waitTime = 0;
		long i = 0;
		
		// sampling loop
		while (isToUpdate) {
			// we take samples on all samplables
			for (YetiSamplable u: samplables) {
				u.sample();
			}
			i++;
			// we take the current time
			currentSamplePoint=new Date().getTime();
			// the time to wait is calculated theoretically
			waitTime=(startSampledPoint+nMSBetweenUpdates*i)-currentSamplePoint;
			try {
				// we check that the time to wait is positive
				// otherwise, we iterate directly.
				if (waitTime>=0) {
					Thread.sleep(waitTime);
				}
			} catch (InterruptedException e) {
				// Should never happen
				// e.printStackTrace();
			}

		}
	}

	/**
	 * Simple constructor for the sampler.
	 * 
	 * @param nMSBetweenUpdates the number of milliseconds between two updates.
	 */
	public YetiSampler(long nMSBetweenUpdates) {
		super();
		this.nMSBetweenUpdates = nMSBetweenUpdates;
	}
	

}
