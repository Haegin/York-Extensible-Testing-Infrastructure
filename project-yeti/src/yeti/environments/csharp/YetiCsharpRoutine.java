package yeti.environments.csharp;

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

import yeti.YetiCallContext;
import yeti.YetiCallException;
import yeti.YetiCard;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.Yeti;


/**
 * Class that represents... 
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Aug 09, 2009
 *
 */
public class YetiCsharpRoutine extends YetiRoutine {

	
	/**
	 * Result of the last call.
	 */
	protected YetiVariable lastCallResult=null;	
	protected boolean firstFaultFlag=false;

	/**
	 * 
	 * Creates a Csharp routine.
	 * 
	 * @param name the name of the routine.
	 * @param openSlots the open slots for the routine.
	 * @param returnType the type of the returned value.
	 * @param originatingModule the module in which it was defined
	 */
	public YetiCsharpRoutine(YetiName name, YetiType[] openSlots, YetiType returnType, YetiModule originatingModule) {
		super();
		this.name = name;
		this.openSlots = openSlots;
		this.returnType = returnType;
		this.originatingModule = originatingModule;
	}

	/* (non-Javadoc)
	 * 
	 * Checks the arguments. In the case of Java, arguments match (TODO: change that for generics).
	 * 
	 * @see yeti.YetiRoutine#checkArguments(yeti.YetiCard[])
	 */
	@Override
	public boolean checkArguments(YetiCard[] arg) {
			
		return true;
	}

	/* (non-Javadoc)
	 * Method used to perform the actual call
	 * 
	 * @see yeti.environments.java.YetiCsharpRoutine#makeCall(yeti.YetiCard[])
	 */
	public Object makeCall(YetiCard []arg){
		//String log = null;
		super.makeCall(arg);
		

			try {
				makeEffectiveCall(arg);
			} catch(YetiCallException e) {
				String temp = e.getLog();
				YetiLog.printDebugLog(temp, this);
				String[] results = temp.split("><");
				//log = results[0];			
				String reasonException="";
				int tmp=0;
				if(results[1]!=null)
				{					
					reasonException = results[1];
					tmp = results[1].indexOf("PRECONDITION");
				}
											
				if(tmp==-1)
				{	
					//if it is not precondition break then possible bug
					String[] exception = reasonException.split("@");
					//exception[2] has the Buggy Log
					//exception[1] has the Exception Message and StackTrace
					//YetiLog.printDebugLog(temp, this, true);
					String exceMessage = /*exception[2].trim()+*/"\t"+exception[1].trim();
					//System.out.println("BUG FOUND: ERROR");
					//System.out.println(exceMessage);
					if(!this.firstFaultFlag)
					{
						double d = ((new Date()).getTime()-Yeti.st);
					System.out.println("Time: "+d+ "\ttest case: "+Yeti.testCaseCount);
					//System.out.println("%%%%%%%\n"+exceMessage);
					this.firstFaultFlag=true;
					}
					
					YetiLog.printYetiLog(exception[2].trim(),this);
					YetiLog.printYetiLog("/**BUG FOUND: ERROR**/", this);
					Exception e0 = new Exception(exceMessage);
					YetiLog.printYetiThrowable(e0, new YetiCallContext(this,arg,e0,"/** BUG FOUND: **/\n/** "+e0.getMessage()+" **/"));

				}
				else
				{
					//precondition break is a normal exception
					String[] exception = reasonException.split("@");
					//exception[1] has the Buggy Log
					//exception[0] has the Exception Message and StackTrace
					//String exceMessage = exception[1].trim();
					YetiLog.printDebugLog(temp, this);
					//System.out.println("/**NORMAL EXCEPTION:**/");
					//System.out.println(exceMessage);
					YetiLog.printYetiLog("/**NORMAL EXCEPTION:**/", this);
					Exception e0 = new Exception(exception[1].trim());
					YetiLog.printYetiThrowable(e0, new YetiCallContext(this,arg,e0,"/**NORMAL EXCEPTION:**/\n/** "+e0.getMessage()+" **/"),false);
				}
			} catch (Throwable e) {
				
			}

		
		return this.lastCallResult;
	}
	
	
	/* (non-Javadoc)
	 * A stub for sublasses.
	 * 
	 * @see yeti.YetiRoutine#makeEffectiveCall(yeti.YetiCard[])
	 */
	@Override
	public String makeEffectiveCall(YetiCard[] arg) throws Throwable {
		// by default this one does not do anything
		return null;
	}

}
