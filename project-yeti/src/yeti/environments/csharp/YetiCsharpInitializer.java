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
import yeti.YetiInitializationException;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiType;
import yeti.environments.YetiInitializer;
import yeti.environments.csharp.YetiServerSocket;
import yeti.environments.csharp.YetiCsharpSpecificType;


/**
 * Class that represents the initialiser for Csharp.
 * 
 * @author Sotirios Tassis (st552@cs.york.ac.uk)
 * @date Jul 21, 2009
 *
 */

public class YetiCsharpInitializer extends YetiInitializer {

	private ArrayList<String> strTypes;
	private ArrayList<String> cons,meths,inters;
	public static boolean initflag=false;
	
	public YetiCsharpInitializer()
	{
		strTypes=new ArrayList<String>();
		cons=new ArrayList<String>();
		meths=new ArrayList<String>();
		inters=new ArrayList<String>();
	
	}
	/**
	 * A simple helper routine that ignores the parameter String.
	 * 
	 * @param s the string to be ignored.
	 */
	public void ignore(String s){
		
	}
	
	/* (non-Javadoc)
	 * Initializes the Java environment.
	 * 
	 * @see yeti.environments.YetiInitializer#initialize(java.lang.String[])
	 */
	@SuppressWarnings("static-access")
	@Override
	public void initialize(String[] args) throws YetiInitializationException {
		
		YetiServerSocket soc;		
		soc = new YetiServerSocket();
		
		
		
		// we go through all arguments
		String modulesToTest="";
		String yetiPath="";
		for(int i=0; i<args.length; i++) 
		{
			if (args[i].equals("-csharp")) 
				ignore(args[i]);
			else 
			{
				
				for (String s0: args) {
					if (s0.startsWith("-testModules=")) 
					{
						String s1=s0.substring(13);
						modulesToTest=s1;
						
					}
					
					if(s0.startsWith("-yetiPath="))
					{
					
						String s1=s0.substring(10);
						yetiPath=s1;
					}
				}
			}
		}
		
		if(YetiCsharpInitializer.initflag) throw new YetiInitializationException("C#ReflexiveLayer Unable To Start");		
		String info= yetiPath+"="+modulesToTest;
		// we initialize primitive types first
		// the primitives have the type names that C# has		
		YetiCsharpSpecificType.initPrimitiveTypes();
			YetiServerSocket.sendData(info);
			
			@SuppressWarnings("unused")			
			ArrayList<String> a = YetiServerSocket.getData();
			YetiServerSocket.sendData("reached read point");
			a = YetiServerSocket.getData();
			strTypes = soc.getData();
			cons = soc.getData();
			meths = soc.getData();
			inters = soc.getData();
	
		//For each type we do what the Prefetching Loader does	
		for(String s: strTypes)
		{			
			String[] st = s.split(":");
			YetiType type=new YetiCsharpSpecificType(st[0].trim());
			YetiType.allTypes.put(type.getName(), type);
			YetiLog.printDebugLog("adding "+type.getName()+" to yeti types ", this);
			//If the base class is not object we add it to the subtypes 
			//of that base type
			String parent = st[1].trim();
			if (!parent.equalsIgnoreCase("Object") && YetiType.allTypes.containsKey(parent)){
				YetiLog.printDebugLog("linking "+type.getName()+" to "+parent, this);
				YetiType.allTypes.get(parent).allSubtypes.put(st[0].trim(), type);
			}
							
			// we create the YetiModule out of the type
			// which exists in st[2] each time
			if(!(YetiModule.allModules.containsKey(st[2].trim())))
			{
				YetiLog.printDebugLog("Added module: "+st[2].trim(),this);
				YetiModule mod = this.makeModuleFromClass(st[2].trim());
				YetiModule.allModules.put(st[2].trim(), mod);
			}
			
		}
		// we link an interface with its type to the parent interfaces
		for (String i: inters ) 
		{
			String[] st = i.split(":");			
			YetiType type=new YetiCsharpSpecificType(st[0].trim());
			if (YetiType.allTypes.containsKey(st[0])){
				YetiLog.printDebugLog("linking "+type.getName()+" to "+st[0], this);
				YetiType.allTypes.get(st[0]).allSubtypes.put(st[1].trim(), type);
			}
		}
		// Here we add the constructors of the assemblies
		// Each constructor is put to the Module and Type (i.e. class)
		// that it belongs to and we do that by getting the types/modules from
		// the fields that store them in Java (allTypes,allModules)
		for(String cs: cons)
		{
			String[] st = cs.split(":");
			YetiType t = YetiType.allTypes.get(st[0].trim());
			YetiModule m = YetiModule.allModules.get(st[2].trim());
			addConstructors(cs, t, m);
		}
		
		// Here we add the methods of the assemblies
		// Each method is put to the Module and Type(i.e. class)
		// that it belongs to and we do that by getting the types/modules from
		// the fields that store them in Java (allTypes,allModules)
		for(String ms: meths)
		{
			String[] st = ms.split(":");
			YetiType t = YetiType.allTypes.get(st[0].trim());
			YetiModule m = YetiModule.allModules.get(st[5].trim());
			addMethods(ms,t,m);
		}
		
	}
	
	
	private void addConstructors(String c, YetiType type, YetiModule mod) {
		String[] st = c.split(":");
		String[] pars = st[1].split(";");
		boolean usable = true;
		YetiType []paramTypes=null;
		int numberPars=0; //The number of parameters the constructor has
		//Checks if there are any parameters or not
		if("".equals(pars[0]))
			paramTypes=new YetiType[numberPars];				
		else 
		{
			numberPars=pars.length;
			paramTypes=new YetiType[numberPars];
			
		}
		// for all types we box the types.
		for (int i=0; i<numberPars; i++){
			
			if (YetiType.allTypes.containsKey(pars[i].trim())){				
				paramTypes[i]=YetiType.allTypes.get(pars[i].trim());						
			} else {
				usable = false;
			}
		}
		
		// if we don't know a type from the constructor we don't add it
		if (usable){
			YetiLog.printDebugLog("adding constructor to "+type.getName()+" in module "+mod.getModuleName(), this);
			YetiCsharpConstructor construct = new YetiCsharpConstructor(YetiName.getFreshNameFrom(st[0]), paramTypes , type, mod,type.getName());		
			// add it as a creation routine for the type
			type.addCreationRoutine(construct);
			// add the constructor as a routines to test
			mod.addRoutineInModule(construct);
		}
	}
	
	
	private void addMethods(String c, YetiType type, YetiModule mod) {
		String[] st = c.split(":");
		String[] pars = st[2].split(";"); //We further split the parameters
		boolean usable = true;
		YetiType []paramTypes;
		int numberParameters=-1;
		//Check if there are any parameters or not
		if("".equals(pars[0].trim())) numberParameters=0;
		else numberParameters=pars.length;
		
		boolean isStatic = false;
		if("True".equals(st[4].trim())) isStatic = true;
		else isStatic = false;
		// if the method is static we do not introduce a slot for the target.
		int offset = 0;
		if (isStatic){
			
			paramTypes=new YetiType[numberParameters];
		} else {
			paramTypes=new YetiType[numberParameters+1];
			offset = 1;
			if (YetiType.allTypes.containsKey(st[0].trim())){
				paramTypes[0]=YetiType.allTypes.get(st[0].trim());						
			} else {
				usable = false;
			}
		}
		
		// for all types we box the types.
		for (int i=0; i<numberParameters; i++){
			if (YetiType.allTypes.containsKey(pars[i].trim())){
				paramTypes[i+offset]=YetiType.allTypes.get(pars[i].trim());						
			} else {
				usable = false;
			}
		}
		
		// if we don't know a type from the constructor we don't add it
		if (usable){
			YetiLog.printDebugLog("adding method "+st[1]+" in module "+st[5], this);
			// add it as a creation routine for the return type
			YetiType returnType = YetiType.allTypes.get(st[3].trim());
			if (returnType==null)
				returnType = new YetiCsharpSpecificType(st[3].trim());
			YetiCsharpMethod method = new YetiCsharpMethod(YetiName.getFreshNameFrom(st[1]), paramTypes , returnType, mod,st[1].trim(),isStatic,st[0].trim());
			returnType.addCreationRoutine(method);
			// add the constructor as a routines to test
			mod.addRoutineInModule(method);
		}
	}
	
	
	public YetiModule makeModuleFromClass(String c){
		YetiModule mod=new YetiCsharpModule(c);
		
		return mod;
	}
}
