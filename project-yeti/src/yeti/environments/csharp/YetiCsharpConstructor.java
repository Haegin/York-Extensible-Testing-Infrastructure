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
import yeti.Yeti;



/**
 * Class that represents a constructor of a .NET compatible language.
 *
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jul 21, 2009
 *
 */
public class YetiCsharpConstructor extends YetiCsharpRoutine {

    /**
     * The name of the constructor. 
     */
   
    private String c;
  //shows when a call was successful on the CsharpReflexiveLayer
    public boolean successCall=true;



    /**
     * Constructor to the constructor.
     *
     * @param name the name of this constructor.
     * @param openSlots the open slots for this constructor.
     * @param returnType the returnType of this constructor.
     * @param originatingModule the module in which it was defined.
     * @param c the constructor itself.
     */
   
    public YetiCsharpConstructor(YetiName name, YetiType[] openSlots, YetiType returnType, YetiModule originatingModule, String c) {
        super(name, openSlots, returnType, originatingModule);
        this.c=c; //make it a string
    }
   

    /* (non-Javadoc)
     * Returns the actual name of the constructor.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return c;
    }


    /**
     * Makes the effective call (lets return the exceptions and Errors).
     *
     * @param arg the arguments of the call.
     * @return the logs.
     * @throws YetiCallException the wrapped exception.
     */

    public String makeEffectiveCall(YetiCard[] arg)
    throws YetiCallException {
        String log;
        String msg="";
        this.lastCallResult=null;
        Yeti.testCaseCount++;
        //Object []initargs=new Object[arg.length];
       
        msg+="Constructor:";
        // we start by unboxing the arguments boxed into the cards
        YetiIdentifier id=YetiIdentifier.getFreshIdentifier();
        msg+=id+":"+c+":";
        
        log = returnType.toString() + " " + id.getValue() + "=new "+returnType.getName()+"(";
        for (int i=0;i<arg.length; i++){
            // if we should replace it by a null value, we do it
        	
            if (YetiVariable.PROBABILITY_TO_USE_NULL_VALUE>Math.random()&& !(((YetiCsharpSpecificType)arg[i].getType()).isSimpleType())) {                
                msg+="null";
				arg[i]=new YetiCard(arg[i].getType());
                if(i<arg.length -1) msg+= ";";
                log=log+"null";
                
            } else {
                // note that we use getValue to get the actual value
                //initargs[i]=arg[i].getValue();
                msg+=arg[i].getIdentity();
                if(i<arg.length -1) msg+= ";";
                // we use toString() to make it pretty-print.
                log=log+arg[i].toString();
            }
            if (i<arg.length-1){
                log=log+",";
            }
        }
        
        log=log+");";
        msg+=":"+log;
        String valuestring="";       
        	YetiLog.printDebugLog(msg,this);	
            YetiServerSocket.sendData(msg);
           
            ArrayList<String> a = YetiServerSocket.getData();
            String s=a.get(0);
            if (s.indexOf("FAIL!")>=0){
            	successCall = false;              	
            	msg="";
            	for (String s0: a)
            		msg=msg+s0+"\n";
            	YetiLog.printDebugLog("The LOG: "+log, this);
            	//we throw the exception of an not successful call   
            	//YetiLog.printDebugLog(log+"><"+msg, this, true);
            	throw new YetiCallException(log+"><"+msg,new Throwable());
            	
            } else{
            	YetiLog.printDebugLog(log+"><"+msg+"^^^^"+s, this);
            	String[] helps = s.split(":");
     			if(helps.length>=2)
    			{
    				valuestring = helps[1].trim();
    			}
            }


        	
        	//if the call is successful we store to the pool the id
        	//the value for now is not the valid one because of syncronization
        	//problem with the working threads and the non-asynchronous socket
        	//communication
        	if(successCall)
        	{          		        		
        		this.lastCallResult=new YetiVariable(id, returnType, valuestring);
        	}
        	
        	// print the log
    		YetiLog.printDebugLog("The LOG: "+log, this);        		
        	YetiLog.printYetiLog(log, this);        		                            
        
        return log;
    }     

}
