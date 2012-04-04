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
import java.util.Vector;

/**
 * Class that represents types in Yeti.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 */
public class YetiType {

	/**
	 * True if there is a maximum to the number of instances by default.
	 */
	public static boolean TYPES_HAVEMAXIMUM_NUMBER_OF_INSTANCES = true;

	/**
	 * The default maximum to the number of instances.
	 */
	public static int DEFAULT_MAXIMUM_NUMBER_OF_INSTANCES = 1000;

	/**
	 * True if there is a maximum to the number of instances specific to this
	 * type.
	 */
	public boolean hasSpecificMaximumNumberOfDirectInstances = false;

	/**
	 * The maximum to the number of instances of this type.
	 */
	public int specificMaximumNumberOfDirectInstances;

	/**
	 * The name of the type. Note that this can be a class name, a type with a
	 * generic instantiation, or an interface.
	 */
	protected String name;

	/**
	 * Structure that stores all routines that return this type
	 */
	public Vector<YetiRoutine> creationRoutines = new Vector<YetiRoutine>();

	/**
	 * adds a routine to the type in question and all supertypes
	 * 
	 * @param v
	 *            the creation routine to add to this type.
	 */
	public synchronized void addCreationRoutine(YetiRoutine v) {
		creationRoutines.add(v);
		for (YetiType t : directSuperTypes.values()) {
			t.addCreationRoutine(v);
			YetiLog.printDebugLog("Adding creation routine " + v.getName()
					+ " to " + this.getName(), this);
		}
	}

	/**
	 * Return the creation routines of this type.
	 * 
	 * @return
	 */
	public Vector<YetiRoutine> getCreationRoutines() {
		return creationRoutines;
	}

	/**
	 * Returns a routine of this type at random.
	 * 
	 * @return the routine found.
	 * @throws NoCreationRoutineInType
	 *             Just in case we don't know how to create the type.
	 */
	public YetiRoutine getRandomCreationRoutine()
			throws NoCreationRoutineInType {
		if (creationRoutines.size() == 0)
			throw new NoCreationRoutineInType("no creation routine for: "
					+ this.name);
		double d = Math.random();
		int i = (int) Math.floor(d * (double) (creationRoutines.size()));
		YetiLog.printDebugLog("trying to get routine for: " + this.name, this);
		return creationRoutines.get(i);
	}

	public YetiRoutine getDeterministicCreationRoutine(int index)
			throws NoCreationRoutineInType {
		if (creationRoutines.size() == 0)
			throw new NoCreationRoutineInType("no creation routine for: "
					+ this.name);
		int i = index % creationRoutines.size();
		YetiLog.printDebugLog("trying to get routine for: " + this.name, this);
		return creationRoutines.get(i);
	}

	/**
	 * Structure that stores all instances of this type
	 */
	public Vector<YetiVariable> instances = new Vector<YetiVariable>();

	/**
	 * Structure that stores all direct instances of this type (this type is
	 * their defining type)
	 */
	public Vector<YetiVariable> directInstances = new Vector<YetiVariable>();

	/**
	 * Adds an instance to the type in question and all supertypes
	 * 
	 * @param v
	 *            the instance to add.
	 */
	public synchronized void addInstance(YetiVariable v) {

		// if there is a cap on the number of instances, we will remove one at
		// random to make room
		if (v.getType().equals(this)) {
			// if there is a cap and we got to it
			while ((this.hasSpecificMaximumNumberOfDirectInstances && directInstances
					.size() >= this.specificMaximumNumberOfDirectInstances)
					|| ((!this.hasSpecificMaximumNumberOfDirectInstances) && (YetiType.TYPES_HAVEMAXIMUM_NUMBER_OF_INSTANCES && (directInstances
							.size() >= YetiType.DEFAULT_MAXIMUM_NUMBER_OF_INSTANCES)))) {
				YetiVariable v0 = this.getRandomDirectInstance();
				this.removeInstance(v0);
			}
			directInstances.add(v);
		}
		YetiLog.printDebugLog("Adding " + v.toString() + " to: " + this.name
				+ " number of instances: " + directInstances.size()
				+ " number of types: " + YetiType.allTypes.size(), this);
		instances.add(v);
		// we add the instance to all parents
		for (YetiType t : directSuperTypes.values())
			t.addInstance(v);
	}

	/**
	 * Adds an instance to the type in question and all supertypes
	 * 
	 * @param v
	 *            the instance to add.
	 */
	public synchronized void addInstanceDeterministically(YetiVariable v,
			int index) {

		// if there is a cap on the number of instances, we will remove one at
		// random to make room
		if (v.getType().equals(this)) {
			// if there is a cap and we got to it
			while ((this.hasSpecificMaximumNumberOfDirectInstances && directInstances
					.size() >= this.specificMaximumNumberOfDirectInstances)
					|| ((!this.hasSpecificMaximumNumberOfDirectInstances) && (YetiType.TYPES_HAVEMAXIMUM_NUMBER_OF_INSTANCES && (directInstances
							.size() >= YetiType.DEFAULT_MAXIMUM_NUMBER_OF_INSTANCES)))) {
				YetiVariable v0 = this.getDeterministicDirectInstance(index);
				this.removeInstance(v0);
			}
			directInstances.add(v);
		}
		YetiLog.printDebugLog("Adding " + v.toString() + " to: " + this.name
				+ " number of instances: " + directInstances.size()
				+ " number of types: " + YetiType.allTypes.size(), this);
		instances.add(v);
		// we add the instance to all parents
		for (YetiType t : directSuperTypes.values())
			t.addInstanceDeterministically(v, index);
	}

	/**
	 * Removes an instance to the type in question and all supertypes
	 * 
	 * @param v
	 *            the instance to add.
	 */
	public synchronized void removeInstance(YetiVariable v) {
		YetiLog.printDebugLog("Removing " + v.toString() + " from: "
				+ this.name + " all instances: " + YetiVariable.allId.size(),
				this);
		instances.remove(v);
		// if we are in the type that defined the instance
		if (v.getType().equals(this)) {
			// we remove it from its set of real instances
			directInstances.remove(v);
			YetiVariable.allId.remove(v.identity.value);
			YetiVariable.nVariables--;
		}
		for (YetiType t : directSuperTypes.values())
			t.removeInstance(v);
	}

	/**
	 * Returns all instances of this type
	 * 
	 * @return all instances of this type.
	 */
	public Vector<YetiVariable> getInstances() {
		return instances;
	}

	/**
	 * Returns an instance of this type at random.
	 * 
	 * @return the chosen instance.
	 */
	public YetiVariable getRandomInstance() {
		double d = Math.random();
		int i = (int) Math.floor(d * instances.size());
		return instances.get(i);
	}

	public YetiVariable getDeterministicInstance(int index) {
		int i = index % instances.size();
		return instances.get(i);
	}

	/**
	 * Returns an instance of this type at random.
	 * 
	 * @return the chosen instance.
	 */
	public YetiVariable getRandomDirectInstance() {
		double d = Math.random();
		int i = (int) Math.floor(d * this.directInstances.size());
		return this.directInstances.get(i);
	}

	public YetiVariable getDeterministicDirectInstance(int index) {
		int i = index % this.directInstances.size();
		return this.directInstances.get(i);
	}

	/**
	 * A HashMap that stores direct super types. This might be used by specific
	 * implementations.
	 */
	public HashMap<String, YetiType> directSuperTypes = new HashMap<String, YetiType>();

	/**
	 * A HashMap that stores all instantiated subtypes.
	 * 
	 */
	public HashMap<String, YetiType> allSubtypes = new HashMap<String, YetiType>();

	/**
	 * A HashMap that stores all instantiated types.
	 * 
	 */
	public static HashMap<String, YetiType> allTypes = new HashMap<String, YetiType>();

	/**
	 * Constructor of YetiTypes.
	 * 
	 * @param name
	 *            The name of the YetiType
	 */
	public YetiType(String name) {
		this.name = name;
		allTypes.put(name, this);
	}

	/**
	 * Adds a subtype that was instantiated
	 * 
	 * @param yt
	 *            type to add
	 */
	public synchronized void addSubtype(YetiType yt) {
		allSubtypes.put(yt.getName(), yt);
	}

	/**
	 * Returns the name of the YetiType.
	 * 
	 * @return the name of the type
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Used for pretty-print of the type
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * The vector of interestingValues.
	 */
	public Vector<Object> interestingValues = new Vector<Object>();

	public Vector<Object> interestingValuesDSSR = new Vector<Object>();

	/**
	 * Simple getter for interesting values.
	 * 
	 * @return the vector of interesting values.
	 */
	public Vector<Object> getInterestingValues() {

		return interestingValues;
	}

	/**
	 * Adds an interesting value of this type.
	 * 
	 * @param interestingValue
	 *            the value to add.
	 */
	public void addInterestingValues(Object interestingValue) {

		if (!this.hasInterestingValues())
			this.setHasInterestingValues(true);

		// ***********************************************************************************************************************************
		// ************ There is no need of stopping duplication because the
		// repetition of the value mean that has more chances of finding bug
		// ************ So it should also have more chances of being picked for
		// the next test.

		// int i = interestingValues.indexOf(interestingValue, 0);
		// if(i == -1)
		// {
		this.interestingValues.add(interestingValue);
		YetiLog.printDebugLog("Added interesting value: " + interestingValue,
				this);
		// }
		// ***********************************************************************************************************************************

	}

	public void addDSSRInterestingValues(Object interestingValue) {

		if (!this.hasInterestingValues())
			this.setHasInterestingValues(true);

		// ***********************************************************************************************************************************
		// ************ There is no need of stopping duplication because the
		// repetition of the value mean that has more chances of finding bug
		// ************ So it should also have more chances of being picked for
		// the next test.

		// int i = interestingValues.indexOf(interestingValue, 0);
		// if(i == -1)
		// {
		this.interestingValuesDSSR.add(interestingValue);
		YetiLog.printDebugLog("Added DSSR interesting value: "
				+ interestingValue, this);
		// }
		// ***********************************************************************************************************************************

	}

	/**
	 * Resets interesting values on this type.
	 * 
	 */
	public void resetInterestingValues() {
		this.interestingValues = new Vector<Object>();
		this.setHasInterestingValues(false);
	}

	/**
	 * Returns an interesting value and removes it from the list of interesting
	 * values.
	 * 
	 */
	public Object removeInterestingValue() {
		if (interestingValues.size() == 1)
			this.setHasInterestingValues(false);

		return this.interestingValues.remove(interestingValues.size() - 1);
	}

	/**
	 * Returns an interesting value and does not remove it from the list of
	 * interesting values.
	 * 
	 * @return an object containing the value.
	 */
	public Object getRandomInterestingValue() {
		if (interestingValues.size() == 0)
			return null;
		double d = Math.random();
		int i = (int) Math.floor(d * this.interestingValues.size());
		return this.interestingValues.get(i);
	}

	public Object getDSSRInterestingValue() {
		if (interestingValuesDSSR.size() == 0)
			return null;
		double d = Math.random();
		int i = (int) Math.floor(d * this.interestingValuesDSSR.size());
		return this.interestingValuesDSSR.get(i);
	}

	/**
	 * Returns an interesting value and does not remove it from the list of
	 * interesting values.
	 * 
	 * @return an object containing the value.
	 */
	public Object getDeterministicInterestingValue(int index) {
		if (interestingValues.size() == 0) {
			return null;
		}
		// int i = index % interestingValues.size();
		return interestingValues.get(index);
	}

	/**
	 * Returns an interesting value in a variable and does not remove it from
	 * the list of interesting values.
	 * 
	 */
	public YetiVariable getRandomInterestingVariable() {
		Object value = this.getRandomInterestingValue();
		if (value == null)
			return null;
		YetiLog.printDebugLog("Interesting variable: " + value, this);
		YetiIdentifier id = YetiIdentifier.getFreshIdentifier();
		return new YetiVariable(id, this, value);

	}

	public YetiVariable getDSSRInterestingVariable() {
		Object value = this.getDSSRInterestingValue();
		if (value == null)
			return null;
		YetiLog.printDebugLog("Interesting variable: " + value, this);
		YetiIdentifier id = YetiIdentifier.getFreshIdentifier();
		return new YetiVariable(id, this, value);

	}

	/**
	 * Returns an interesting value in a variable and does not remove it from
	 * the list of interesting values.
	 * 
	 */
	public YetiVariable getDeterministicInterestingVariable(int index) {
		Object value = this.getDeterministicInterestingValue(index);
		if (value == null)
			return null;
		YetiLog.printDebugLog("Interesting variable: " + value, this);
		YetiIdentifier id = YetiIdentifier.getFreshIdentifier();
		return new YetiVariable(id, this, value);

	}

	/**
	 * A simple setter to say that a type has interesting values.
	 * 
	 * @param hasInterestingValues
	 *            true if it has interesting values, false otherwise.
	 */
	public void setHasInterestingValues(boolean hasInterestingValues) {
		this.hasInterestingValues = hasInterestingValues;
	}

	/**
	 * Indicates whether this type has interesting values.
	 */
	private boolean hasInterestingValues = false;

	/**
	 * Defines whether this type has interesting values.
	 * 
	 * @return true if it has interesting values.
	 */
	public boolean hasInterestingValues() {
		return hasInterestingValues;
	}

	/**
	 * Returns true if the present type is a subtype of the argument.
	 * 
	 * @param yt
	 *            the type to compare to.
	 */
	public boolean isSubtype(YetiType yt) {
		if (directSuperTypes.containsValue(yt))
			return true;
		for (YetiType t : directSuperTypes.values())
			if (t.isSubtype(yt))
				return true;

		return false;

	}

	/**
	 * Prints all creation routines listed for all types.
	 */
	public static void printCreationProcedureList() {
		YetiLog.printDebugLog("Constructors per type: ", YetiType.class, true);
		for (YetiType yt : YetiType.allTypes.values()) {
			YetiLog.printDebugLog(" " + yt.name + ":", YetiType.class, true);
			for (YetiRoutine r : yt.creationRoutines) {
				YetiLog.printDebugLog(
						"     " + r.name.value + r.getSignature(),
						YetiType.class, true);
			}
		}
	}

	/**
	 * Resets all the types.
	 */
	public static void reset() {
		YetiType.allTypes = new HashMap<String, YetiType>();
	}

}