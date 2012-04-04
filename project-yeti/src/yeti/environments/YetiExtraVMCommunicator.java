package yeti.environments;

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
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import yeti.YetiInitializationException;
import yeti.environments.pharo.YetiPharoCallResult;

/**
 * Class that represents a connection to an external platform.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Nov 25, 2009
 *
 */
public class YetiExtraVMCommunicator extends YetiInitializer {

	public String platformName = "???";
	
	public Socket s=null;

	public String serverHostName = "localhost";

	public int portNumber = 0;

	public PrintStream toBoundPlatform = null;
	
	public BufferedReader fromBoundPlatform = null;
	
	public String modules = null;


	/**
	 * 
	 */
	public YetiExtraVMCommunicator() {

	}

	/* (non-Javadoc)
	 * @see yeti.environments.YetiInitializer#initialize(java.lang.String[])
	 */
	@Override
	public void initialize(String[] args) throws YetiInitializationException {

		// we parse options on the command-line
		for (String s:args) {
			// gets the HostName
			if (s.startsWith("-optHostName=")) {
				serverHostName = s.substring(13);
				continue;
			}
			// gets the port number
			try {
				if (s.startsWith("-optPortNumber=")) {
					portNumber = Integer.parseInt(s.substring(15));
					continue;
				}
			} catch(NumberFormatException e) {
				continue;
			}
			// we want to test these modules
			if (s.startsWith("-testModules=")) {
				modules=s.substring(13);
				continue;
			}
	
		}

		// we try to open the socket on indicated/default port numbers
		try {
			System.out.println("Trying to connect to: "+serverHostName+" on port: "+portNumber);
			
			s=new Socket(serverHostName, portNumber);
			System.out.println("connected!");
		} catch (UnknownHostException e) {
			System.err.println("YETI/"+platformName+": Unknown Host!");
			System.exit(0);
		} catch (IOException e) {
			System.err.println("YETI/"+platformName+": IO problem!");
			e.printStackTrace();
			System.exit(0);
		}
		try {
			toBoundPlatform = new PrintStream(s.getOutputStream());
			fromBoundPlatform = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			System.err.println("YETI/"+platformName+": IO problem!");
			e.printStackTrace();
			System.exit(0);
		}
		this.sendModuleList();
		this.receiveAllRoutinesList();

	}
	
	
	/**
	 * Sends a String to the bound program. Adds a '\n' at the end of the line.
	 * 
	 * @param s the String to send.
	 */
	public void sendToBoundProgram(String s){
		toBoundPlatform.println(s);
	}

	/**
	 * Receive a line from the bound program.
	 * 
	 * @return the line read, minus the '\n'.
	 */
	public String receiveFromBoundProgram(){
		try {
			return fromBoundPlatform.readLine();
		} catch (IOException e) {
			System.err.println("YETI/"+platformName+": IO problem!");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Sends the list of modules to the target platform.
	 * 
	 * @param s the module list.
	 */
	public void sendModuleList(){
		sendToBoundProgram(modules);
	}

	/**
	 * Receive all routines from the bound platform
	 * 
	 * @return the routines.
	 */
	public void receiveAllRoutinesList(){
		boolean finished = false;
		while (!finished) {
//			String routineSig = 
			System.out.println(this.receiveFromBoundProgram());
		}
	}
	

	
	
	/**
	 * Method used to make a call on the external platform.
	 * 
	 * @param moduleName the module in which the method is located
	 * @param routineName the routine to call
	 * @param resultName the name of the variable that will store the result
	 * @param targetName the target of the call
	 * @param argumentsNames the the names of the arguments
	 */
	public String call(String moduleName, String routineName, String resultName ,String targetName , String []argumentsNames){
		String args = "";
		
		// we initialize the list of arguments
		if (argumentsNames.length>0) {
			args = argumentsNames[0];
			boolean first = true;
			
			for (String name:argumentsNames) {
				if (first)
					first=false;
				else
					args = args+", "+name;

			}
		}
		
		// we effectively send the instructions here
		String returnVal= "call "+routineName+" from: "+moduleName+" on: "+targetName+" with: "+args+" and store in: "+targetName;
		toBoundPlatform.println(returnVal);
		return returnVal;
	}
	
	public YetiPharoCallResult getResult() {
		String resultType = this.receiveFromBoundProgram();
		if (resultType.startsWith("Success: ")) {
			return new YetiPharoCallResult(resultType.substring(9));
		} else {
			return new YetiPharoCallResult(true,resultType);
		}
		
	}
}
