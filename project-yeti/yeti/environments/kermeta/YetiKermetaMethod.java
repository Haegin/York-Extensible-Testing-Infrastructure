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

import java.util.ArrayList;
import java.util.HashMap;

import kyeti.util.KermetaSimplifiedInterpreter;
import yeti.YetiCallException;
import yeti.YetiCard;
import yeti.YetiIdentifier;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiType;
import yeti.YetiVariable;
import fr.irisa.triskell.kermeta.interpreter.KermetaRaisedException;
import fr.irisa.triskell.kermeta.language.structure.ClassDefinition;
import fr.irisa.triskell.kermeta.language.structure.Constraint;
import fr.irisa.triskell.kermeta.language.structure.Operation;
import fr.irisa.triskell.kermeta.language.structure.ParameterizedType;
import fr.irisa.triskell.kermeta.modelhelper.NamedElementHelper;
import fr.irisa.triskell.kermeta.runtime.RuntimeObject;

/**
 * 
 * Class that represents a Kermeta method. 
 * 
 * @author Erwan Bousse (erwan.bousse@gmail.com)
 * @date 28 juil. 2011
 *
 */
public class YetiKermetaMethod extends YetiKermetaRoutine {


	/**
	 * A list of methods not to test.
	 */
	public static HashMap<String,Object> methodsNotToAdd ;


	/**
	 * The Kermeta method.
	 */
	private Operation operation;


	/**
	 * Constructor to define a Kermeta method.
	 * 
	 * @param name the name of the method.
	 * @param openSlots the open slots of the method.
	 * @param returnType the return type of the method.
	 * @param originatingModule the module in which it was defined.
	 * @param m the method implementation.
	 */
	public YetiKermetaMethod(YetiName name, YetiType[] openSlots, YetiType returnType, YetiModule originatingModule, Operation method) {
		super(name, openSlots, returnType, originatingModule);
		this.operation=method;
	}


	/**
	 * A method to initialize the set of methods not to add.
	 */
	public static void initMethodsNotToAdd(){
		methodsNotToAdd = new HashMap<String,Object>();
		methodsNotToAdd.put("hashcode", null); 
		methodsNotToAdd.put("asType", null);
		methodsNotToAdd.put("checkAllInvariants", null);
		methodsNotToAdd.put("checkInvariants", null);
		methodsNotToAdd.put("getViolatedConstraints", null);
	}


	/**
	 * Checks whether the method corresponds to a method not to test.
	 * 
	 * @param methodName the name of the method.
	 * @return <code>true</code> if the method should not be tested. <code>false</code> Otherwise.
	 */
	public static boolean isMethodNotToAdd(String methodName){
		if (methodsNotToAdd==null)
			initMethodsNotToAdd();
		return methodsNotToAdd.containsKey(methodName);
	}


	public String toString() {
		String operationName = NamedElementHelper.getQualifiedName(operation);
		return operationName;
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
		lastCallResult=null;
		String prefix;
		boolean isValue= false;

		// In Kermeta, a method can't be static
		prefix = arg[0].getIdentity().toString();

		// we start generating the log as well as the identifier to use to store the 
		// result if there is one.
		YetiIdentifier id=null;

		String operationName=NamedElementHelper.getQualifiedName(operation);
		String operationClassName=NamedElementHelper.getQualifiedName(operation.getOwningClass());

		if (getReturnType()!= null) {
			// if there is a result to be expected
			YetiLog.printDebugLog("return type is "+getReturnType().getName(), this);
			id=YetiIdentifier.getFreshIdentifier();

			log = prefix + "."+operationName+"(";
		}

		// we adjust the number of arguments according 
		// to the fact that they are static or not.
		// -> in kermeta, no static operations
		int offset=1;
		RuntimeObject target=null;

		// The target object
		target= (RuntimeObject)arg[0].getValue();

		// The parameters
		ArrayList<RuntimeObject> parameters = new ArrayList<RuntimeObject>();

		// The interpreter instance
		KermetaSimplifiedInterpreter interpreter = YetiKermetaLoader.yetiLoader.getInterpreter();

		while(!interpreter.isAvailable()) {} //TODO Busy waiting ! BAD THING TO DO ! But seems to help, and no way found to make a cleaner wait/notify system (we don't know when to wake up).

		for (int i = offset;i<arg.length; i++){
			// if we should replace it by a null value, we do it
			if ((YetiVariable.PROBABILITY_TO_USE_NULL_VALUE>Math.random())){//&&!(((YetiKermetaType)arg[i].getType()).isSimpleType())) {
				parameters.add(interpreter.getVoid());
				arg[i]=new YetiCard(arg[i].getType());
				log=log+"null";
			} else {
				parameters.add((RuntimeObject)arg[i].getValue());
				log=log+arg[i].toString();
			}
			if (i<arg.length-1){
				log=log+",";
			}
		}

		if (target!=null)
			YetiLog.printDebugLog("trying to call "+operationName+" on a "+operationClassName+", target ="+target, this);
		else 
			YetiLog.printDebugLog("trying to call statically "+operationName+" of "+operationClassName, this);				

		Object o=null;

		// we make the real call
		try {
			o = interpreter.callKermetaOperation(target, operation, parameters);
		}
		// And we catch anything thrown during the execution
		catch (Throwable t) {	
			boolean isError = true;
			// If this is a precondition violation
			String originalMessage = t.getMessage();
			if(originalMessage!=null) {
				if(originalMessage.contains("ConstraintViolatedPre")) {
					// We extract the message of the kermeta exception
					RuntimeObject raisedo = ((KermetaRaisedException)t).raised_object;
					RuntimeObject messageRO = raisedo.getProperties().get("message");
					String message = (String) messageRO.getJavaNativeObject();
					// We check that it's not a precondition of this operation that was violated
					for(Constraint constraint : operation.getPre()) {
						String searchedMessage = "pre "+constraint.getName()+" of operation "+operation.getName()+" of class "+operation.getOwningClass().getName()+" violated";
						// If it is, we won't throw the error
						if (message.contains(searchedMessage)) {
							isError = false;
							break;
						}
					}
				}		
			}
			// We throw the error only if it's not a bad variable choice
			if (isError)
				throw new YetiCallException(log,t);
		}

		// if the return type is void, we look it up
		if (returnType==null)
			returnType=YetiType.allTypes.get(getReturnType().getName());
		// if there is a result, we store it and create the variable
		if (id!=null&&o!=null){
			this.lastCallResult=new YetiVariable(id, returnType, o);
		}

		// if this is a value, we print it directly
		if (isValue) {
			log = generateLogForValues(log1, o);
		}
		else
			log=log+");";

		// finally we print the log.
		YetiLog.printYetiLog(log, this);
		return log;
	}



	/**
	 * Takes a log and adds the litteral value in the case this is a value type.
	 * @param log1
	 * @param o
	 * @return
	 */
	public static String generateLogForValues(String log1, Object o) {
		String log;		
		log = log1 + "\'"+o.toString()+"\'"; // TODO make better log
		return log;
	}





}
