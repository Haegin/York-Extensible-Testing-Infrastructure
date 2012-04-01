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

/**
 * Class that represents a routine in Yeti 
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public abstract class YetiRoutine {

	/**
	 * The name of the routine.
	 */
	protected YetiName name;

	/**
	 * The openslots of the routine.
	 */
	protected YetiType []openSlots;

	/**
	 * The return type of the routine.
	 */
	protected YetiType returnType;

	/**
	 * The module in which the routine is defined.
	 */
	protected YetiModule originatingModule;	

	/**
	 * Adds the routine as a creator for its return type.
	 */
	public void addRoutineAsCreatorForType(){
		returnType.addCreationRoutine(this);
	}

	/**
	 * Just to verify that arguments fit the actual routine.
	 * 
	 * @param arg the arguments to check
	 * @return true if the arguments fit, false otherwise.
	 */
	public abstract boolean checkArguments(YetiCard []arg);


	/**
	 * Tracks how many calls were made to this routine.
	 */
	public int nTimesCalled = 0;

	/**
	 * Simple getter for the number of times this routine was called.
	 * @return  the number of times this routine was called.
	 */
	public int getnTimesCalled() {
		return nTimesCalled;
	}

	/**
	 * Simple Setter for the number of times this routine was called.
	 * @param nTimesCalled  the number of time this routine was called.
	 */
	public void setnTimesCalled(int nTimesCalled) {
		this.nTimesCalled = nTimesCalled;
	}
	/**
	 * Simple incrementer for the number of times this routine was called.
	 * 
	 */
	public void incnTimesCalled() {
		this.nTimesCalled++;
	}

	/**
	 * Tracks how many successful calls were made to this routine.
	 */
	public int nTimesCalledSuccessfully = 0;

	/**
	 * Simple getter for the number of times this routine was called successfully.
	 * @return  the number of times this routine was called successfully.
	 */
	public int getnTimesCalledSuccessfully() {
		return nTimesCalledSuccessfully;
	}

	/**
	 * Simple Setter for the number of times this routine was called successfully.
	 * @param nTimesCalledSuccessfully  the number of time this routine was called successfully.
	 */
	public void setnTimesCalledSuccessfully(int nTimesCalledSuccessfully) {
		this.nTimesCalledSuccessfully = nTimesCalledSuccessfully;
	}
	/**
	 * Simple incrementer for the number of times this routine was called.
	 * 
	 */
	public void incnTimesCalledSuccessfully() {
		this.nTimesCalledSuccessfully++;
	}

	/**
	 * Tracks how many unsuccessfully calls were made to this routine.
	 */
	public int nTimesCalledUnsuccessfully = 0;

	/**
	 * Simple getter for the number of times this routine was called unsuccessfully.
	 * @return  the number of times this routine was called unsuccessfully.
	 */
	public int getnTimesCalledUnsuccessfully() {
		return nTimesCalledUnsuccessfully;
	}

	/**
	 * Simple Setter for the number of times this routine was called unsuccessfully.
	 * @param nTimesCalledSuccessfully  the number of time this routine was called unsuccessfully.
	 */
	public void setnTimesCalledUnsuccessfully(int nTimesCalledUnsuccessfully) {
		this.nTimesCalledUnsuccessfully = nTimesCalledUnsuccessfully;
	}
	/**
	 * Simple incrementer for the number of times this routine was called unsuccessfully.
	 * 
	 */
	public void incnTimesCalledUnsuccessfully() {
		this.nTimesCalledUnsuccessfully++;
	}
	
	/**
	 * Tracks how many undecidable calls were made to this routine.
	 */
	public int nTimesCalledUndecidable = 0;

	/**
	 * Simple getter for the number of times this routine was called undecidable.
	 * @return  the number of times this routine was called undecidable.
	 */
	public int getnTimesCalledUndecidable() {
		return nTimesCalledUndecidable;
	}

	/**
	 * Simple Setter for the number of times this routine was called undecidable.
	 * @param nTimesCalledSuccessfully  the number of time this routine was called undecidable.
	 */
	public void setnTimesCalledUndecidable(int nTimesCalledUndecidable) {
		this.nTimesCalledUndecidable = nTimesCalledUndecidable;
	}
	/**
	 * Simple incrementer for the number of times this routine was called undecidable.
	 * 
	 */
	public void incnTimesCalledUndecidable() {
		this.nTimesCalledUndecidable++;
	}
	
	
	/**
	 * 
	 * Make a call of this routine if arguments fit the routine.
	 * Can be used to code a specific oracle.
	 * 
	 * @param arg the arguments.
	 * @return the result of the call.
	 */
	public Object makeCall(YetiCard []arg) {
		this.incnTimesCalled();
		return null;
	}

	/**
	 * Makes the effective call (lets return the exceptions and Errors).
	 * 
	 * @param arg the arguments of the call.
	 * @return the logs
	 * @throws Throwable an exception to tell "make_call" what was the actual error. 
	 */
	public abstract String makeEffectiveCall(YetiCard[] arg) throws Throwable;

	/**
	 * Getter for the name of the routine.
	 * @return  the name of the routine.
	 */
	public YetiName getName() {
		return name;
	}

	/**
	 * Returns the signature of the routine.
	 * 
	 * @return the signature of the routine.
	 */
	public String getSignature() {
		String arguments = null;

		// we aggregate the arguments types.
		for (YetiType type: this.openSlots) {
			if (arguments==null) {
				arguments=type.toString();
			} else {
				arguments = arguments+" x "+type.toString();
			}
		}
		// if no argument the chain is empty
		if (arguments==null) {
			arguments="";
		}

		// we return the signature
		return (this.toString()+": ("+arguments+")->"+this.returnType.toString());
	}

	/**
	 * Setter of the name of the routine.
	 * @param name  the name to set.
	 */
	public void setName(YetiName name) {
		this.name = name;
	}

	/**
	 * Getter of the open slots of the routine.
	 * @return  the open slots of the routine.
	 */
	public YetiType[] getOpenSlots() {
		return openSlots;
	}

	/**
	 * Setter of the open slots.
	 * @param openSlots  the slots that are open on the routine.
	 */
	public void setOpenSlots(YetiType[] openSlots) {
		this.openSlots = openSlots;
	}

	/**
	 * Getter of the module where the routine is defined.
	 * @return  the module where the routine is defined.
	 */
	public YetiModule getOriginatingModule() {
		return originatingModule;
	}

	/**
	 * Setter of the defining module of the routine.
	 * @param originatingModule  the module where the routine is defined.
	 */
	public void setOriginatingModule(YetiModule originatingModule) {
		this.originatingModule = originatingModule;
	}

	/**
	 * Get the return type of the routine.
	 * @return  the return type of the routine.
	 */
	public YetiType getReturnType() {
		return returnType;
	}

	/**
	 * Setter for the return type of the routine.
	 * @param returnType  the return type to set.
	 */
	public void setReturnType(YetiType returnType) {
		this.returnType = returnType;
	}

	/**
	 * Creates a method call from the routine and the arguments
	 * 
	 * @param arguments the arguments of the call
	 * @return the text of the test case
	 */
	public String toStringWithArguments(YetiCard[] arguments) {
		String longRoutineName = this.getName().getValue();
		String routineName = longRoutineName.substring(0,longRoutineName.lastIndexOf("_"));
		String prefix = arguments[0].toStringPrefix();
		String testCaseBody=prefix+((prefix.length()==0)?"":"\n")+routineName+"("+arguments[0].toStringVariable();
		for (int i=1;i<arguments.length;i++) {
			prefix = arguments[i].toStringPrefix();
			testCaseBody=prefix+((prefix.length()==0)?"":"\n")+testCaseBody+","+arguments[i].toStringVariable();
		}
		testCaseBody=testCaseBody+");\n";
		return testCaseBody;
	}

}
