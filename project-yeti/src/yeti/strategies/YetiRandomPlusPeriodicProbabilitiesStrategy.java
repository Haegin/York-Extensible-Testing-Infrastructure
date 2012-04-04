package yeti.strategies;
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

import yeti.YetiModule;
import yeti.YetiRoutine;
import yeti.YetiStrategy;
import yeti.YetiVariable;
import yeti.environments.YetiTestManager;

public class YetiRandomPlusPeriodicProbabilitiesStrategy extends YetiRandomPlusStrategy {

	static int routinesCalledSinceLastNullValueChangePeriod = 500;
	static int routinesCalledSinceLastNewGenChangePeriod = 250;
	static int routinesCalledSinceLastInterestingValuesChangePeriod = 1000;


	int routinesCalledSinceLastNullValueChange = 0;
	int routinesCalledSinceLastNewGenChange = 0;
	int routinesCalledSinceLastInterestingValuesChange = 0;

	public YetiRoutine getNextRoutine(YetiModule module) {
		routinesCalledSinceLastNullValueChange++;
		routinesCalledSinceLastNewGenChange++;
		routinesCalledSinceLastInterestingValuesChange++;

		if (routinesCalledSinceLastNullValueChange>routinesCalledSinceLastNullValueChangePeriod) {
			if (0==YetiVariable.PROBABILITY_TO_USE_NULL_VALUE) {
				YetiVariable.PROBABILITY_TO_USE_NULL_VALUE=1.0;						
			} else {
				YetiVariable.PROBABILITY_TO_USE_NULL_VALUE=YetiVariable.PROBABILITY_TO_USE_NULL_VALUE-.01;											
			}

			routinesCalledSinceLastNullValueChange=0;
		}
		if (routinesCalledSinceLastNewGenChange>routinesCalledSinceLastNewGenChangePeriod) {
			if (0==YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY) {
				YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY=1.0;						
			} else {
				YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY=YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY-.01;											
			}
			routinesCalledSinceLastNewGenChange=0;
		}
		if (routinesCalledSinceLastInterestingValuesChange>routinesCalledSinceLastInterestingValuesChangePeriod) {
			if (0==YetiRandomPlusStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY) {
				YetiRandomPlusStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY=1.0;						
			} else {
				YetiRandomPlusStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY=YetiRandomPlusStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY-.01;											
			}
			routinesCalledSinceLastInterestingValuesChange=0;
		}

		return super.getNextRoutine(module);
	}

	public YetiRandomPlusPeriodicProbabilitiesStrategy(YetiTestManager ytm) {
		super(ytm);
	}

	@Override
	public String getName() {
		return "Random+ Periodic Strategy";
	}

	
}
