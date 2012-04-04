package yeti.environments.kermeta;

/**

KYETI - Kermeta binding for YETI.

Copyright 2011 (c) IRISA / INRIA / Université de Rennes 1 (Triskell team) / INSA de Rennes
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

import kyeti.util.KermetaSimplifiedInterpreter;
import yeti.YetiCallException;
import yeti.YetiCard;
import yeti.YetiIdentifier;
import yeti.YetiLog;
import yeti.YetiName;
import yeti.YetiType;
import yeti.YetiVariable;
import fr.irisa.triskell.kermeta.language.structure.Enumeration;
import fr.irisa.triskell.kermeta.runtime.RuntimeObject;

/**
 * 
 * Class that represents "constructor" of an instance of a Kermeta enumeration. 
 * 
 * @author Erwan Bousse (erwan.bousse@gmail.com)
 * @date 28 juil. 2011
 *
 */
public class YetiKermetaEnumerationLitteralConstructor extends YetiKermetaRoutine {

	/**
	 * The name of the literal to construct.
	 * Ex. enum{one,two,three} ; "two" is a litteral name. 
	 */
	private String literalName;
	
	/**
	 * The Kermeta enumeration to consider.
	 */
	private Enumeration enumeration;
	
	/**
	 * Constructor of the constructor. 
	 * @param name The YetiName of the routine.
	 * @param returnType The return type, which is the YetiType made from the kermeta enumeration.
	 * @param enumeration The Kermeta enumeration type.
	 * @param literalName The name of the litteral constructed by this constructor.
	 */
	public YetiKermetaEnumerationLitteralConstructor(YetiName name, YetiType returnType, Enumeration enumeration, String literalName) {
		super(name, new YetiType[0], returnType, null);
		this.literalName = literalName;
		this.enumeration=enumeration;
	}


	/**
	 * Makes the effective call (lets return the exceptions and Errors).
	 * 
	 * @param arg the arguments of the call.
	 * @return the logs.
	 * @throws YetiCallException the wrapped exception. 
	 */

	public String makeEffectiveCall(YetiCard[] arg)	throws YetiCallException {
		// We need the interpreter to create the instance
		KermetaSimplifiedInterpreter interpreter = YetiKermetaLoader.yetiLoader.getInterpreter();
		// The log to return
		String log = "";
		// The result, if any
		lastCallResult=null;
		// First we construct the object
		RuntimeObject o = null;	
		try {
			o = interpreter.getEnumerationLitteral(enumeration, literalName);
		} catch (Throwable t) { //useful ? is there any risk ?
			throw new YetiCallException(log,t);
		}
		// Log purpose
		YetiIdentifier id=YetiIdentifier.getFreshIdentifier();
		log = returnType.toString() + " " + id.getValue() + "=new "+returnType.getName()+"(";
		// if it succeeds we create the new variable
		this.lastCallResult=new YetiVariable(id, returnType, o);
		log=log+");";
		// print the log
		YetiLog.printYetiLog(log, this);
		YetiLog.printDebugLog(log, this);
		return log;
	}
	
	public String toString() {
		return this.getName().getValue();
	}



}
