package yeti.environments.pharo;

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

import yeti.YetiCallContext;
import yeti.YetiCard;
import yeti.YetiIdentifier;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.YetiVariable;

/**
 * Class that represents... 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Nov 25, 2009
 *
 */
public class YetiPharoRoutine extends YetiRoutine {

	YetiPharoCommunicator com = null;
	
	/**
	 * 
	 */
	public YetiPharoRoutine(YetiPharoCommunicator com, YetiName name, YetiModule module, YetiType []openslots ) {
		this.com = com;
		this.openSlots=openslots;
		this.originatingModule = module;
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see yeti.YetiRoutine#checkArguments(yeti.YetiCard[])
	 */
	@Override
	public boolean checkArguments(YetiCard[] arg) {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see yeti.YetiRoutine#makeEffectiveCall(yeti.YetiCard[])
	 */
	@Override
	public String makeEffectiveCall(YetiCard[] arg) throws Throwable {
		YetiIdentifier id = YetiIdentifier.getFreshIdentifier();
		String []argNames = new String[arg.length-1];
		// we transfer arguments
		for (int i = 0; i<arg.length-1; i++) {
			argNames[i]=arg[i+1].getIdentity().toString();
		}
		
		// we make the call
		String log = com.call(originatingModule.getModuleName(), name.toString(), id.getValue(), arg[0].getIdentity().getValue(), argNames);
		// we get the results
		YetiPharoCallResult res = com.getResult();
		
		// if the result is not an exception
		if (!res.isException) {
			if (!YetiType.allTypes.containsKey(res.resultType)) {
				YetiType.allTypes.put(res.resultType, new YetiType(res.resultType));
			}
			
			new YetiVariable( id, YetiType.allTypes.get(res.resultType), null);
		} else {
			// if it is an exception
			Exception e = new Exception(res.exceptionTrace);
			YetiLog.printYetiThrowable(e, new YetiCallContext(this,arg,e,"/** BUG FOUND: **/\n/** "+res.exceptionTrace+" **/"));
		}
		return log;
	}

}
