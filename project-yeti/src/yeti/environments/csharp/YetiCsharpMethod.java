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

import java.util.ArrayList;

import yeti.YetiCallException;
import yeti.YetiCard;
import yeti.YetiIdentifier;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.environments.csharp.YetiCsharpRoutine;
import yeti.Yeti;



/**
 * Class that represents a method of a .NET compatible language.
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jun 22, 2009
 *
 */
public class YetiCsharpMethod extends YetiCsharpRoutine {
	

	/**
	 * The actual method to call.
	 */
	private String m;
	private String originatingClass;

	/**
	 * Checks whether this method is a static method or not. 
	 */
	public boolean isStatic = false;
	//shows when a call was successful on the CsharpReflexiveLayer
	public boolean successCall=true;


	/**
	 * Constructor to define a Csharp method
	 * 
	 * @param name the name of the method.
	 * @param openSlots the open slots of the method.
	 * @param returnType the return type of the method.
	 * @param originatingModule the module in which it was defined.
	 * @param m the method implementation.
	 * @param isStatic the parameter that says whether the method is static or not.
	 */
	public YetiCsharpMethod(YetiName name, YetiType[] openSlots, YetiType returnType, YetiModule originatingModule, String m, boolean isStatic, String cls) {
		super(name, openSlots, returnType, originatingModule);
		this.m=m;
		this.originatingClass = cls;
		this.isStatic=isStatic;
		
	}


	/* (non-Javadoc)
	 * Returns the name of the method for pretty-print.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return m;
	}

	/**
	 * Makes the effective call (lets return the exceptions and Errors).
	 * 
	 * @param arg the arguments of the call.
	 * @return the logs.
	 * @throws YetiCallException the wrapped exception. 
	 */
	public String makeEffectiveCall(YetiCard[] arg) throws YetiCallException {
		String log="";
		String log1="";
		this.lastCallResult=null;
		Yeti.testCaseCount++;
		String prefix,target="";
		boolean isValue= false;
		String msg="Method:";

		// if the method is static, we need to adjust the arguments
		// there is no target in the open slots.
		if (isStatic){					
			prefix = this.originatingClass;			
		}else{
			
			prefix = arg[0].getIdentity().toString();
		}
		
		// we start generating the log as well as the identifier to use to store the 
		// result if there is one.
		YetiIdentifier id=null;
		if (!("Void".equals(returnType.getName()))) {
			// if there is a result to be expected
			YetiLog.printDebugLog("return type is "+returnType.getName(), this);
			id=YetiIdentifier.getFreshIdentifier();
			//constructing the message to send to the CsharpreflexiveLayer
			msg+=id+":"+originatingClass+":"+returnType.getName()+":";
			msg+=m+":";
			
			boolean isSimpleReturnType=false;
			
			if (this.m.startsWith("__yetiValue_"))			
					isSimpleReturnType=true;						
			
			if (isSimpleReturnType) {
				isValue=true;
				log1 = this.returnType.toString()+" "+ id.getValue();
			} else
				log = this.returnType.toString()+" "+ id.getValue() + "="+ prefix +"."+ m+"(";			

			
		} else {
			// otherwise		
			msg+="void"+":"+originatingClass+":"+returnType.getName()+":";
			msg+=m+":";
			log = prefix + "."+m +"(";
		}

		//we specify which identifier has the target object
		int offset=1;
		if (isStatic){
			offset=0;
			target="null";
		} 
		else target = arg[0].getIdentity().toString();		
		
		// we adjust the number of arguments according 
		// to the fact that they are static or not.
		for (int i = offset;i<arg.length; i++){
			// if we should replace it by a null value, we do it
			if (YetiVariable.PROBABILITY_TO_USE_NULL_VALUE>Math.random()&&!(((YetiCsharpSpecificType)arg[i].getType()).isSimpleType())) {		
				msg+="null";
				if(i<arg.length -1) msg+= ";";
				arg[i]=new YetiCard(arg[i].getType());
				log=log+"null";
			} else {
				msg+=arg[i].getIdentity();
                if(i<arg.length -1) msg+= ";";				
				log=log+arg[i].toString();
			}
			if (i<arg.length-1){
				log=log+",";
			}

		}
		
		log=log+");";
		msg+=":"+log+":"+target;
		
		
		if ("Void".equals(this.returnType.getName()))
    		returnType=YetiType.allTypes.get(returnType.getName());
    	
    	
    	
		String valuestring="";	
		YetiLog.printDebugLog(msg,this);
			//sending the call to the other part
            YetiServerSocket.sendData(msg);
           //Receiving results
            ArrayList<String> a = YetiServerSocket.getData();
            String s=a.get(0);
            if (s.indexOf("FAIL!")>=0){ 
            	//we throw the exception of an not successful call
            	successCall = false;
            	//storing the exception message and stack in msg variable
            	// from a ArrayList
            	msg="";
            	for (String s0: a)
            		msg=msg+s0+"\n";
            	YetiLog.printDebugLog("The LOG: "+log, this);
            	//we throw the exception of a not successful call
            	YetiLog.printDebugLog(log+"><"+msg, this);
            	throw new YetiCallException(log+"><"+msg,new Throwable());
            	
            } else{            	
            	YetiLog.printDebugLog(log+"><"+msg, this);
            	String[] helps = s.split(":");
     			if(helps.length>=2)
    			{
     				String[] h = helps[1].split("@"); 
    				valuestring = h[0].trim();
    			}
            }
            YetiLog.printDebugLog(log+"><"+msg, this);                            	        	        	        	              
        	
        	if (id!=null && successCall){
        		//if the call is successful we store to the pool the id
            	//the value for now is not the valid one because of syncronization
            	//problem with the working threads and the non-asynchronous socket
            	//communication 
        		
        		this.lastCallResult=new YetiVariable(id, returnType, valuestring);
        	}        	                	    
        	if(isValue) log=log1+" = "+valuestring+";";
        	// finally we print the log.
        		YetiLog.printDebugLog("The LOG: "+log, this);        		
        		YetiLog.printYetiLog(log, this); 

		return log;
	}	


}
