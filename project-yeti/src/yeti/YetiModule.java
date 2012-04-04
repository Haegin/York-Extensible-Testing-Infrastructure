package yeti;

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

import java.util.HashMap;

import yeti.monitoring.YetiCoverageIndicator;

/**
 * Class that represents a unit of testing. Typically for Java this would be a class or a package, for C a header file. A module contains a list of routines to test. The strategy will iterate through them.
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiModule implements YetiCoverageIndicator {

	/**
	 * The name of the module
	 */
	protected String moduleName;

	/**
	 * The modules from which it was combined.
	 */
	private YetiModule []combiningModules = null;

	/**
	 * Gets the modules from which it was combined.
	 * @return  the array containing all modules, null if not composed.
	 */
	public YetiModule[] getCombiningModules() {
		return combiningModules;
	}

	/**
	 * Sets the modules that were combined to obtain this one. 
	 * @param combiningModules  the array of combined modules.
	 */
	public void setCombiningModules(YetiModule[] combiningModules) {
		this.combiningModules = combiningModules;
	}

	/**
	 * A HashMap of all existing modules.
	 */
	public static HashMap <String, YetiModule> allModules =new HashMap<String,YetiModule>();

	/**
	 * A HashMap of all routines in this module.
	 */
	public HashMap <String, YetiRoutine> routinesInModule =new HashMap<String,YetiRoutine>();

	/**
	 * A simple Constructor.
	 * 
	 * @param moduleName takes the name of the module as a parameter.
	 */
	public YetiModule(String moduleName) {
		super();
		this.moduleName = moduleName;
	}

	/**
	 * Add a routine to the list of routine in module.
	 * 
	 * @param routine the routine to add.
	 */
	public void addRoutineInModule(YetiRoutine routine){
		routinesInModule.put(routine.name.toString(),routine);
	}

	/**
	 * Return a routine from this module with a given name.
	 * 
	 * @param name the name of the routine asked
	 * @return the routine selected
	 */
	public YetiRoutine getRoutineFromModuleWithName(String name){
		return routinesInModule.get(name);
	}


	/**
	 * Adds a module to the general list of modules.
	 * 
	 * @param module the module to add.
	 */
	public static void addModuleToAllModules(YetiModule module){
		allModules.put(module.getModuleName(),module);
	}

	/**
	 * Get a routine at random.
	 * 
	 * @return the routine selected.
	 */
	public YetiRoutine getRoutineAtRandom(){
		double d=Math.random();
		int i=(int) Math.floor(d*(routinesInModule.size()));

		return (YetiRoutine)(routinesInModule.values().toArray()[i]);
	}


	/**
	 * Getter for the module name.
	 * @return  the module name.
	 */
	public String getModuleName(){
		return moduleName;
	}

	/**
	 * Setter for the module name.
	 * @param name  the name to set.
	 */
	public void setModuleName(String name){
		moduleName=name;
	}

	/**
	 * Method used to to combine two modules into one.
	 * 
	 * @param modules the array of modules to combine.
	 * @return a module that combined all modules.
	 *
	 * @remarks if the modules array contain only one module, then it is returned as it is
	 */
	public static YetiModule combineModules(YetiModule []modules) {
		if (modules.length == 1)
		{
			return modules[0];
		}
		else
		{
			YetiModule result = new YetiModule(YetiName.getFreshNameFrom("__yeti_test_module").value);
			result.setCombiningModules(modules);
			for (YetiModule mod0: modules) {
				for (YetiRoutine rout0: mod0.routinesInModule.values())
					result.addRoutineInModule(rout0);
			}
			YetiModule.addModuleToAllModules(result);
			return result;
		}
	}

	/**
	 * Checks that the trace contains refers to the module(s) in its trace.
	 * 
	 * @param trace the trace to check.
	 * @return true if the throwable is relevant.
	 */
	public boolean isThrowableInModule(String trace) {
		YetiModule []combModules = this.combiningModules;
		if (combModules!=null) {
			for (YetiModule mod: combModules) {
				YetiLog.printDebugLog(mod.getModuleName(), this);
				if (mod.isThrowableInModule(trace)) return true;
			}
		}
		return false;
	}

	/**
	 * Checks that the modules contain the moduleName
	 * 
	 * @param moduleName the name of the module to check
	 * @return true if the module contains it.
	 */
	public boolean containsModuleName(String moduleName) {
		YetiModule []combModules = this.combiningModules;
		if (combModules!=null) {
			for (YetiModule mod: combModules) {
				YetiLog.printDebugLog(mod.getModuleName(), this);
				if (mod.getModuleName().equals(moduleName)) return true;
			}
		} 
		return moduleName.equals(this.getModuleName());
	}

	/**
	 * This method either returns a value for the coverage or an exception if not supported.
	 * 
	 * @return the coverage (between 0.0 and 1.0).
	 * @throws YetiNoCoverageException in case it does not support the coverage.
	 */	
	public double getCoverage() throws YetiNoCoverageException {
		return (((double)getNumberOfCoveredBranches())/(double)getNumberOfBranches())*100;
		
	}

	/** 
	 * This method returns the type of coverage.
	 * 
	 * @see yeti.monitoring.YetiCoverageIndicator#getCoverageKind()
	 */
	public String getCoverageKind() throws YetiNoCoverageException {
		if (combiningModules!=null) {
			String kind = null;
			for (YetiModule ym: combiningModules) {
				if (kind==null) {
					kind =	ym.getCoverageKind();
				} else {
					if (!kind.equals(ym.getCoverageKind()))
						throw new YetiNoCoverageException(this.moduleName);
				}
			}
			return kind;
		}
		throw new YetiNoCoverageException(this.moduleName);
	}

	/* (non-Javadoc)
	 * @see yeti.monitoring.YetiCoverageIndicator#getNumberOfBranches()
	 */
	public long getNumberOfBranches() throws YetiNoCoverageException {
		if (combiningModules!=null) {
			long numberOfBranches = 0;
			for (YetiModule ym: combiningModules) {
				numberOfBranches += ym.getNumberOfBranches();
			}
			YetiLog.printDebugLog("/"+numberOfBranches+" total branches", this);
			return numberOfBranches;
		}
		throw new YetiNoCoverageException(this.moduleName);	
	}

	/* (non-Javadoc)
	 * @see yeti.monitoring.YetiCoverageIndicator#getNumberOfCoveredBranches()
	 */
	public long getNumberOfCoveredBranches() throws YetiNoCoverageException {
		if (combiningModules!=null) {
			long numberOfCoveredBranches = 0;
			for (YetiModule ym: combiningModules) {
				numberOfCoveredBranches += ym.getNumberOfCoveredBranches();
			}
			YetiLog.printDebugLog(numberOfCoveredBranches+" branches covered", this);
			return numberOfCoveredBranches;
		}
		throw new YetiNoCoverageException(this.moduleName);	
	}

	/**
	 * Resets all modules.
	 */
	public static void reset() {
		allModules =new HashMap<String,YetiModule>();
	}

}
