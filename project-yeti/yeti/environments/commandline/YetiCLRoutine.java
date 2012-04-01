package yeti.environments.commandline;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import yeti.YetiCallContext;
import yeti.YetiCard;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.environments.commandline.YetiCLInitializer;
import yeti.environments.commandline.YetiCLRoutine.TestingSession;

/**
 * Class that represents... 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jul 23, 2010
 *
 */
public class YetiCLRoutine extends YetiRoutine {

	String cmdLine = "";
	// timeout for each commmand line process
	int timeout = YetiCLInitializer.timeout;
	
	/**
	 * 
	 * Creates a Java routine.
	 * 
	 * @param name the name of the routine.
	 * @param openSlots the open slots for the routine.
	 * @param originatingModule the module in which it was defined
	 */
	public YetiCLRoutine(YetiName name, String cmdLine, YetiType[] openSlots, YetiModule originatingModule) {
		super();
		this.name = name;
		this.cmdLine = cmdLine;
		this.openSlots = openSlots;
		this.originatingModule = originatingModule;
	}
	
	
	/* (non-Javadoc)
	 * @see yeti.YetiRoutine#checkArguments(yeti.YetiCard[])
	 */
	@Override
	public boolean checkArguments(YetiCard[] arg) {
		return true;
	}

	/* (non-Javadoc)
	 * @see yeti.YetiRoutine#makeEffectiveCall(yeti.YetiCard[])
	 */
	@Override
	public String makeEffectiveCall(YetiCard[] arg) throws Throwable {
		String cmd = this.cmdLine;
		
		for (YetiCard arg0: arg) {
			//cmd = cmd + " " + arg0.toString();
			cmd = cmd + " " + arg0.getValue();
		}
		
		/*
		Process p = Runtime.getRuntime().exec(cmd);
		int result = p.waitFor();
		if (result!=0) {
			YetiLog.printYetiThrowable(new Exception("returned value: "+result), this);
		} else {
			YetiLog.printYetiLog(cmd, this);
		}
		*/
		
		//executes the command line process in another thread
		TestingSession test = new TestingSession(cmd);
		test.start();
		test.join(timeout);  // join the thread in timeout million seconds
		
		if(test.isAlive()){  // thread still alive when time out
			test.stop();
			Exception e0 = new Exception("testing time out");
			YetiLog.printYetiThrowable(e0, new YetiCallContext(this,arg,e0,"/** BUG FOUND: **/\n/** "+e0.getStackTrace()+" **/"));
		}else{				
			int result = test.getTestResult();  // get the exit value of the command line process
			if (result!=0) {  	               // process exit abnormally
				Exception e0 = new Exception("returned value: "+result);
				YetiLog.printYetiThrowable(e0, new YetiCallContext(this,arg,e0,"/** BUG FOUND: **/\n/** "+e0.getStackTrace()+" **/"));
			} else {
				YetiLog.printYetiLog(cmd, this);
			}
		}
	
		return cmd;
	}
	
	/**
	 * inner class presents a thread in which the command line process is executed
	 * */
	class TestingSession extends Thread{
		
		String cmd;
		int result = -1;
		
		public TestingSession(String cmd){
			this.cmd = cmd;
		}
		
		/**
		 * get the process exit value of the command line
		 * */
		public int getTestResult(){
			return result;
		}

		public void run(){
			Process p;
			try {
				// execute the command
				p = Runtime.getRuntime().exec(cmd);
				
				// get the output and err stream of the process in case it is blocked by them
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));	
				BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String received = in.readLine();
				while(received != null){
					System.out.println(received);
					received = in.readLine();
				}
				received = err.readLine();
				while(received != null){
					System.err.println(received);
					received = err.readLine();
				}
				
				// wait the execution of the process and get the exit value 
				result = p.waitFor();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
