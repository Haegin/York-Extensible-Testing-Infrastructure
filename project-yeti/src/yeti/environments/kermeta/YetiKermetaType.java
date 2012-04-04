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

import kyeti.util.KermetaSimplifiedInterpreter;
import yeti.YetiType;
import fr.irisa.triskell.kermeta.language.structure.TypeDefinition;
import fr.irisa.triskell.kermeta.modelhelper.NamedElementHelper;

/**
 * 
 * Class that represents a Kermeta type.
 * Mostly a kermeta class, but can be a Kermeta Enumeration as well.
 * 
 * @author Erwan Bousse (erwan.bousse@gmail.com)
 * @date 28 juil. 2011
 *
 */
public class YetiKermetaType extends YetiType {

	/**
	 * Some kermeta class properties to ignore.
	 */
	private static ArrayList<String> _propertiesToAvoid = null;
	
	/**
	 * Checks if a class property has to be ignored.
	 * @param name The name of the property
	 * @return True if it shoulf be ignored, otherwise false.
	 */
	public static boolean isPropertyToAvoid(String name) {
		if (_propertiesToAvoid ==null) {
			_propertiesToAvoid = new ArrayList<String>();
			// Theses are sime "Object" properties that aren't always defined, so we don't want to consider them in tests.
			_propertiesToAvoid.add("tag");
			_propertiesToAvoid.add("ownedTags");
		}
		return _propertiesToAvoid.contains(name);
	}

	
	/**
	 * Constructor using a simple Type name.
	 * @param name The name of the type (like a Kermeta full class name).
	 */
	public YetiKermetaType(String name) {
		super(name);
	}

	
	/**
	 * Constructor using a Kermeta typedefinition.
	 * @param clazz The Kermeta typedefinition to use.
	 */
	public YetiKermetaType(TypeDefinition clazz) {
		this(NamedElementHelper.getQualifiedName(clazz));
	}


	/**
	 * Initialize important types and their interesting values.
	 */
	public static void initImportantTypes(){
		
		// the unique loader
		YetiKermetaLoader loader =  YetiKermetaLoader.yetiLoader;
		
		// loading some important kermeta classes
		loader.addDefinitionFromFrameworkClasses("kermeta::standard::String");
		loader.addDefinitionFromFrameworkClasses("kermeta::language::structure::Object");
		loader.addDefinitionFromFrameworkClasses("kermeta::standard::Integer");
		loader.addDefinitionFromFrameworkClasses("kermeta::standard::Boolean");

		// we need the kermeta environnement in order to create kermeta values
		KermetaSimplifiedInterpreter interpreter =loader.getInterpreter();

		// boolean interesting values
		YetiType tBoolean = YetiType.allTypes.get("kermeta::standard::Boolean");
		if (tBoolean!=null) {
			tBoolean.addInterestingValues(interpreter.getKermetaBoolean(true));
			tBoolean.addInterestingValues(interpreter.getKermetaBoolean(false));
		}

		// integer interesting values
		YetiType tInt = YetiType.allTypes.get("kermeta::standard::Integer");
		if (tBoolean!=null) {
			for (int j = -10; j<11;j++)
				tInt.addInterestingValues(interpreter.constructKermetaInteger(j));
			tInt.addInterestingValues(interpreter.constructKermetaInteger(Integer.MAX_VALUE));
			tInt.addInterestingValues(interpreter.constructKermetaInteger(Integer.MAX_VALUE-1));
			tInt.addInterestingValues(interpreter.constructKermetaInteger(Integer.MAX_VALUE-2));
			tInt.addInterestingValues(interpreter.constructKermetaInteger(Integer.MIN_VALUE+2));
			tInt.addInterestingValues(interpreter.constructKermetaInteger(Integer.MIN_VALUE+1));
			tInt.addInterestingValues(interpreter.constructKermetaInteger(Integer.MIN_VALUE));
		}
	}
}
