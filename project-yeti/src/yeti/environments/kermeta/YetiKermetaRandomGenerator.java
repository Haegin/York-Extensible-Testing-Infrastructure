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

import java.util.HashMap;
import java.util.Set;

import kyeti.util.KermetaSimplifiedInterpreter;
import fr.irisa.triskell.kermeta.runtime.RuntimeObject;

/**
 * 
 * Class that contains random generators for Kermeta Types.
 * Is added as a module in the kermeta testing procedures in the YetiKermetaLoader.
 * TODO We only have a random integer generator for now.... maybe other numeric types should have some as well.
 * 
 * @author Erwan Bousse
 * @date 1 juil. 2011
 *
 */
public class YetiKermetaRandomGenerator {

	public static int counter = 0;
	
	/**
	 * To find the kermeta return type of this class methods
	 */
	private static HashMap<String, String> _returnTypes;
	
	private static void initialize() {
		_returnTypes = new HashMap<String, String>();
		_returnTypes.put("randomInteger", "kermeta::standard::Integer");
		//_returnTypes.put("randomBoolean", "kermeta::standard::Boolean");
	}
	
	public static String getReturnTypeName(String methodName) {
		if (_returnTypes==null) {
			initialize();
		} 
		return _returnTypes.get(methodName);
	}
	
	public static  Set<String> getMethodsNames() {
		if (_returnTypes==null) {
			initialize();
		} 
		return _returnTypes.keySet();
	}
	
	
	
	public static RuntimeObject randomInteger() {
		counter++;
		KermetaSimplifiedInterpreter interpreter = YetiKermetaLoader.yetiLoader.getInterpreter();
		double d=Math.random();
		double d2=Math.random()*2-1.0d;
		int value = ((int) Math.floor(2147483647*d*d2));
		return interpreter.constructKermetaInteger(value);
	}
	
	
}
