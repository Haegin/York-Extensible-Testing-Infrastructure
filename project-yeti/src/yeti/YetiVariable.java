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

import yeti.environments.YetiLoader;

/**
 * Class that represents a variable in Yeti. 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 *
 */
/**
 * Class that represents... 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Apr 7, 2011
 *
 */
public class YetiVariable extends YetiCard{

	/**
	 * The probability to use a null value instead of a normal value.
	 */
	public static double PROBABILITY_TO_USE_NULL_VALUE = .10;

	/**
	 * Contains all variables available.
	 */
	public static HashMap<String, YetiVariable> allId = new HashMap<String, YetiVariable>();

	/**
	 * The number of variables created in the system (instances used for testing)
	 */
	public static long nVariables = 0;


	/**
	 * Creates a variable in Yeti. Note that the creation procedure automatically adds
	 * the instance to the types it has.
	 *
	 * @param id the identity for the variable.
	 * @param type the type of the variable.
	 * @param value the value of the variable.
	 */
	public YetiVariable( YetiIdentifier id, YetiType type, Object value) {
		super (id, type, value);
		nVariables++;
		allId.put(id.value, this);
		try {
			YetiLog.printDebugLog("Creating variable "+id.value+" with value "+value, this);
		} catch (Throwable t){
			YetiLog.printDebugLog("Creating variable "+id.value+" with non-serializable value ", this);			
		}
		YetiLog.printDebugLog("type: "+type, this);
		// if the type was not created before we create it on the fly
		if (type==null){
			YetiLog.printDebugLog("value's type: "+value.getClass().getName(), this);
			YetiLoader.yetiLoader.addDefinition(value.getClass());
			this.type=YetiType.allTypes.get(value.getClass().getName());

		}
		YetiLog.printDebugLog("type: "+this.type.name, this);
		// we add the instance to the type
		this.type.addInstance(this);
	}


    /**
	 * Creates a variable in Yeti. Note that the creation procedure automatically adds
	 * the instance to the types it has.
	 *
	 * @param id the identity for the variable.
	 * @param type the type of the variable.
	 * @param value the value of the variable.
	 */
	public YetiVariable( YetiIdentifier id, YetiType type, Object value, int index) {
		super (id, type, value);
		nVariables++;
		allId.put(id.value, this);
		YetiLog.printDebugLog("type: "+type, this);
		// if the type was not created before we create it on the fly
		if (type==null){
			YetiLog.printDebugLog("value's type: "+value.getClass().getName(), this);
			YetiLoader.yetiLoader.addDefinition(value.getClass());
			this.type=YetiType.allTypes.get(value.getClass().getName());

		}
		YetiLog.printDebugLog("type: "+this.type.name, this);
		// we add the instance to the type
		this.type.addInstanceDeterministically(this, index);
	}

	/* (non-Javadoc)
	 * Returns the identity of the variable, used for pretty print.
	 * 
	 * @see yeti.YetiCard#toString()
	 */
	public String toString() {
		return identity.value;
	}

	/**
	 * Getter for the identifier.
	 * 
	 * @return the identifier.
	 */
	public synchronized YetiIdentifier getId() {
		return identity;
	}

	/**
	 * Setter for the identifier.
	 * 
	 * @param id the identifier to set.
	 */
	public synchronized void setId(YetiIdentifier id) {
		this.identity = id;
	}


	/* (non-Javadoc)
	 * Getter for the type.
	 * 
	 * @see yeti.YetiCard#getType()
	 */
	public synchronized YetiType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * Setter for the type.
	 * 
	 * @see yeti.YetiCard#setType(yeti.YetiType)
	 */
	public synchronized void setType(YetiType type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * Getter for the value.
	 * 
	 * @see yeti.YetiCard#getValue()
	 */
	public synchronized Object getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * Setter for the value
	 * 
	 * @see yeti.YetiCard#setValue(java.lang.Object)
	 */
	public synchronized void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Resets all Yeti variables.
	 */
	public static void reset() {
		YetiVariable.allId = new HashMap<String, YetiVariable>();
		YetiVariable.nVariables = 0;

	}

}
