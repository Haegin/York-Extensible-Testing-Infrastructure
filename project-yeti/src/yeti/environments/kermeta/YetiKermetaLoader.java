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

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import kyeti.util.KermetaSimplifiedInterpreter;
import kyeti.util.KermetaSimplifiedInterpreterHelper;

import org.eclipse.emf.common.util.EList;
import org.kermeta.model.KermetaModelHelper;

import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.environments.java.YetiJavaMethod;
import yeti.environments.java.YetiJavaModule;
import fr.irisa.triskell.kermeta.language.structure.ClassDefinition;
import fr.irisa.triskell.kermeta.language.structure.Enumeration;
import fr.irisa.triskell.kermeta.language.structure.EnumerationLiteral;
import fr.irisa.triskell.kermeta.language.structure.ObjectTypeVariable;
import fr.irisa.triskell.kermeta.language.structure.Operation;
import fr.irisa.triskell.kermeta.language.structure.Parameter;
import fr.irisa.triskell.kermeta.language.structure.ParameterizedType;
import fr.irisa.triskell.kermeta.language.structure.PrimitiveType;
import fr.irisa.triskell.kermeta.language.structure.Property;
import fr.irisa.triskell.kermeta.language.structure.Type;
import fr.irisa.triskell.kermeta.language.structure.TypeDefinition;
import fr.irisa.triskell.kermeta.language.structure.VoidType;
import fr.irisa.triskell.kermeta.modelhelper.NamedElementHelper;

/**
 * Loader for the YETI Kermeta binding.
 * Does not extend the YetiLoader class /!\ because it does not load Java classes ! 
 * But is directly inspired from the YetiJavaPrefetchingLoader class.
 * 
 * @author Erwan Bousse (erwan.bousse@gmail.com)
 * @date juil. 2011
 *
 */
public class YetiKermetaLoader {

	/**
	 * The classpath of classes to load.
	 */
	protected String classpath;

	
	/**
	 * The general loader.
	 */
	public static YetiKermetaLoader yetiLoader;


	/**
	 * The kermeta interpreter instance
	 */
	private KermetaSimplifiedInterpreter _kermetaInterpreter;
	public KermetaSimplifiedInterpreter getInterpreter() {
		return _kermetaInterpreter;
	}


	/**
	 * A hashtable with all of the loaded Kermeta methods that can't be used because of a missing type.
	 */
	private HashMap<String, ArrayList<Operation>> methodsRequiringTypes;


	/**
	 * Put a method that requires an undefined type on a "waiting list". 
	 * @param typeName The required type.
	 * @param method The method that requires the type in order to be usable.
	 */
	private void addMethodRequiringType(String typeName, Operation method) {
		// First we need the collection in which the methods are stored
		ArrayList<Operation> methodList = null;
		if(!methodsRequiringTypes.containsKey(typeName)){
			methodList = new ArrayList<Operation>();
			methodsRequiringTypes.put(typeName,methodList);
		} else {
			methodList = methodsRequiringTypes.get(typeName);
		}
		// Then we can store the method in it
		methodList.add(method);
	}


	/**
	 * Try again to add methods requiring a specific type to their module.
	 * @param typeName The required type.
	 */
	private void activateMethodsRequiringType (String typeName) {
		// First we check that there are methods that require this type
		if(methodsRequiringTypes.containsKey(typeName)) {
			// If yes, we find them
			ArrayList<Operation> requiringMethods = methodsRequiringTypes.get(typeName);
			// For each method, we try a second time to add it to its module
			for(Operation method : requiringMethods) {
				String className = NamedElementHelper.getQualifiedName(method.getOwningClass());
				YetiModule module = YetiModule.allModules.get(className);
				addMethodToModule(module,method);
			}
			// Then we remove the entry of this required type
			methodsRequiringTypes.remove(typeName);
		}
	}


	/**
	 * Constructor that creates a new loader.
	 * Load the classes in the path as well.
	 * @param path the classpath to load classes.
	 */
	public YetiKermetaLoader(String path) {
		classpath = path;
		//loadAllClassesInPath();
		yetiLoader = this;
		methodsRequiringTypes = new HashMap<String, ArrayList<Operation>>();
	}


	/**
	 * Load all the kermeta classes that can be found in the path.
	 */
	public void loadAllClassesInPath() {
		// Classes that have been found in the path
		for (ClassDefinition clazz : _kermetaInterpreter.getUserDefinedClasses()) {
			String classname = NamedElementHelper.getQualifiedName(clazz);
			YetiLog.printDebugLog("reading "+classname, this);
			addDefinition(clazz);
		}
		// Enumerations that have been found in the path
		for (Enumeration enu : _kermetaInterpreter.getUserDefinedEnumerations()) {
			String enumname = NamedElementHelper.getQualifiedName(enu);
			YetiLog.printDebugLog("reading "+enumname, this);
			addDefinition(enu);
		}
	}

	/**
	 * Adds to Yeti types the definition of a class (or enumeration) of the kermeta framework.
	 * @param className The name of the framework class to add.
	 */
	public void addDefinitionFromFrameworkClasses(String className) {
		for(ClassDefinition c : _kermetaInterpreter.getFrameworkClasses()) {
			String cName =  NamedElementHelper.getQualifiedName(c);
			if (cName.equals(className))  addDefinition(c);
		}
		for(Enumeration e : _kermetaInterpreter.getFrameworkEnumerations()) {
			String eName =  NamedElementHelper.getQualifiedName(e);
			if (eName.equals(className))  addDefinition(e);
		}
	}


	/**
	 * Add to Yeti types the definition of a Kermeta class
	 * @param clazz The kermeta class.
	 */
	public void addDefinition(ClassDefinition clazz) {
		// We need the full class name
		String className = NamedElementHelper.getQualifiedName(clazz);
		// Either the type already exists, either we create it
		YetiKermetaType type= null;
		if (YetiType.allTypes.containsKey(className)){
			type = (YetiKermetaType)YetiType.allTypes.get(className);
		} else {
			type = new YetiKermetaType(clazz);
			YetiLog.printDebugLog("adding " + type.getName() + " to yeti types ", this);
		}
		// We get he full context : all the classes, abstract or not, that inherits our class from
		Collection<TypeDefinition> fullContext = KermetaModelHelper.ClassDefinition.getFullContext(_kermetaInterpreter.getKermetaUnit(), clazz);
		// For each class of this context
		for (TypeDefinition parentClass : fullContext) {
			if (parentClass instanceof ClassDefinition) {
				String parentClassName = NamedElementHelper.getQualifiedName(parentClass);
				// we only want supertypes of the base class
				if (!parentClassName.equals(className) && KermetaModelHelper.ClassDefinition.isSuperTypeOf(this._kermetaInterpreter.getKermetaUnit(),(ClassDefinition)parentClass ,clazz )) {
					// If the class doesn't already exists as a YetiType, we create it
					if(!YetiType.allTypes.containsKey(parentClassName)) {
						YetiLog.printDebugLog("Creating super type " +parentClassName, this);
						new YetiKermetaType((ClassDefinition)parentClass); // the constructor add the type to all the types
					}
					// In any case, we try to link the types (subtype/directsupertype)
					YetiType parentType = YetiType.allTypes.get(parentClassName);
					if (!parentType.allSubtypes.containsValue(type))
						YetiLog.printDebugLog("linking " + parentClassName + " to " +type.getName(), this);
					parentType.addSubtype(type);
					if (!type.directSuperTypes.containsValue(parentType))
						YetiLog.printDebugLog("linking " + type.getName() + " to " +parentClassName, this);
					type.directSuperTypes.put(parentClassName, parentType);
				}
			}
		}
		// we create the YetiModule out of the class
		YetiModule mod = this.makeModuleFromClass(clazz);
		YetiModule.allModules.put(className, mod);
		// we add the constructors to the type information	
		addConstructors(clazz, type, mod);
		// we add methods to the module in which they were defined		
		addMethods(clazz, mod);
		activateMethodsRequiringType(className);
	}


	/**
	 * Add to Yeti types the definition of a Kermeta enumeration
	 * @param enu The kermeta enumeration.
	 */
	private void addDefinition(Enumeration enu) {
		String enumName = NamedElementHelper.getQualifiedName(enu);
		// we add the type to the types
		YetiKermetaType type= null;
		if (YetiType.allTypes.containsKey(enumName)){
			type = (YetiKermetaType)YetiType.allTypes.get(enumName);
		} else {
			type = new YetiKermetaType(enumName);
			YetiLog.printDebugLog("adding " + type.getName() + " to yeti types ", this);
		}
		// we add the constructors to the type information	
		addConstructors(enu, type);
		// and we activate methods  that could have been waiting for this enumeration type
		activateMethodsRequiringType(enumName);
	}


	/**
	 * Create an empty module from a class (using its class name).
	 * @param c the class to make a module from.
	 * @return The module created.
	 */
	public YetiModule makeModuleFromClass(ClassDefinition c){
		String className = NamedElementHelper.getQualifiedName(c);
		YetiModule mod=new YetiKermetaModule(className);
		return mod;
	}


	/**
	 * We add the methods of the class to the module.
	 * 
	 * @param clazz the class to add.
	 * @param module the module in which ad it.
	 */
	public void addMethods(ClassDefinition clazz, YetiModule module) {
		// We get all the operations from the ClassDefinition clazz, using the KermetaUnit
		List<Operation> operationList = KermetaModelHelper.ClassDefinition.getAllOperations(_kermetaInterpreter.getKermetaUnit(), clazz);
		// For each operation of the class, we prepare everything
		for(Operation operation : operationList) {
			addMethodToModule(module, operation);
		}
	}


	/**
	 * Adds a Kermeta method to a YetiModule, by creating a YetiKermetaRoutine.
	 * @param module The module.
	 * @param operation The Kermeta method.
	 */
	private void addMethodToModule(YetiModule module, Operation operation) {
		// we don't wan't to use abstract methods
		if (operation.isIsAbstract())
			return;
		// Decide if the found operation is usable or not (depending on the types added to YETI)
		boolean usable = true;
		// We want the type of all the parameters of the operation
		YetiType []paramTypes;
		EList<Parameter> parameters = operation.getOwnedParameter();
		paramTypes=new YetiType[parameters.size()+1]; // we add one for the target object
		// We set the first slot to be of the clazz type (for the target object)
		String className = module.getModuleName();
		if (YetiType.allTypes.containsKey(className)){
			paramTypes[0]=YetiType.allTypes.get(className);						
		}
		// If the type of a slot isn't defined in Yeti, then the method isn't usable, and we store it
		else {
			usable = false;
			addMethodRequiringType(className, operation);
		}
		// For each parameter of the operation
		for (Parameter parameter : parameters) {
			int i = parameters.indexOf(parameter);
			// First we want the class definition of the parameter, either with the type definition, or with the type itself
			TypeDefinition parameterClass = typeToTypeDefinition(parameter.getType());
			// Then we use the classname to find the correct yetitype
			String parameterClassname =  NamedElementHelper.getQualifiedName(parameterClass);
			if (YetiType.allTypes.containsKey(parameterClassname)){
				paramTypes[i+1]=YetiType.allTypes.get(parameterClassname);// i+1 because the first slot is for the target object					
			}
			// if it doesn't exists, the method isn't usable
			else {
				usable = false;
				addMethodRequiringType(parameterClassname, operation);
			}
		}
		// Finally, we call a second method to finish the work (only if usable)
		addMethodToModuleIfUsable(module, operation, usable, paramTypes);
	}


	/**
	 * Adds a method to the module if it is usable.
	 * @param module the module to which add the method.
	 * @param method the method to add.
	 * @param usable True if it should be added.
	 * @param paramTypes the types of the parameters.
	 */
	private void addMethodToModuleIfUsable(YetiModule module, Operation method, boolean usable, YetiType[] paramTypes) {

		// if we don't know a type from the method we don't add it
		if (usable && !YetiKermetaMethod.isMethodNotToAdd(method.getName())){
			YetiLog.printDebugLog("adding method "+method.getName()+" in module "+module.getModuleName(), this);


			// First we want the class definition of the return type of the operation (but it can be void)
			TypeDefinition returnTypeClass = typeToTypeDefinition( method.getType());
			String returnTypeClassName =  NamedElementHelper.getQualifiedName(returnTypeClass);

			// we find the return type (or create the type if needed)
			YetiType returnType = YetiType.allTypes.get(returnTypeClassName);
			if (returnType==null) {
				returnType = new YetiKermetaType(returnTypeClass); 
			}

			// then we create the corresponding yetroutine
			YetiKermetaMethod methodRoutine = generateRoutineFromMethod(module , method, paramTypes , returnType);

			// and add the method as a routine to test
			module.addRoutineInModule(methodRoutine);
			// if the return type is not void
			if (!returnTypeClassName.equals("kermeta::standard::Void")) {
				// add it as a creation routine for the type
				returnType.addCreationRoutine(methodRoutine);
			}

		}

	}



	/**
	 * Create a Yeti routine for the Kermeta method to test
	 * 
	 * @param module the module to which add the method.
	 * @param method the method to add.
	 * @param paramTypes the types of the parameters.
	 * @param returnType the type returned by the method 
	 * @return a Yeti routine for this method
	 */
	protected YetiKermetaMethod generateRoutineFromMethod(YetiModule module, Operation method, YetiType[] paramTypes, YetiType returnType) {
		return new YetiKermetaMethod(YetiName.getFreshNameFrom(method.getName()), paramTypes , returnType, module, method);
	}




	/**
	 * Add the constructors for Yeti of a kermeta class.
	 * @param clazz the class of the constructor.
	 * @param type the type of the instance created.
	 * @param module the module to which add the class.
	 */
	public void addConstructors(ClassDefinition clazz, YetiType type, YetiModule module) {
		// if the class is abstract or only an aspect, the constructors should not be called 
		if (clazz.isIsAbstract() || clazz.isIsAspect())
			return;
		// We don't want to add constructors of some classes
		String className = NamedElementHelper.getQualifiedName(clazz);
		if (className.equals("kermeta::standard::Boolean"))
			return;
		// we add the constructors : one without parameters...
		YetiLog.printDebugLog("trying to add constructor of type "+type.getName()+" without parameters",this);
		YetiType []paramTypesEmpty=new YetiType[0];
		String []paramNamesEmpty=new String[0];
		addConstructorFromClassToTypeInModuleIfUsable(clazz, type, module, true, paramTypesEmpty,paramNamesEmpty);
		//  ...and one that initializes all of them
		YetiLog.printDebugLog("trying to add constructor of type "+type.getName()+" with one parameter for each attribute",this);
		List<Property> allProperties = KermetaModelHelper.ClassDefinition.getAllProperties(_kermetaInterpreter.getKermetaUnit(), clazz);
		// We have to fill collections, because we don't know what size they will have
		ArrayList<YetiType> paramTypes=new ArrayList<YetiType>();
		ArrayList<String> paramNames=new ArrayList<String>();
		for(Property property : allProperties) {
			// If it's an Object property like tag or ownedtags, we don't care
			if (YetiKermetaType.isPropertyToAvoid(property.getName()))
				continue;
			// we store the name of the parameter
			paramNames.add(property.getName());
			// First we want the class definition of the property, either with the type definition, or with the type itself
			TypeDefinition parameterClass = typeToTypeDefinition(property.getType());
			// Then we use the class name to prepare everything related to this parameter
			String parameterClassName =  NamedElementHelper.getQualifiedName(parameterClass);
			// Maybe the type already exists
			if (YetiType.allTypes.containsKey(parameterClassName)){
				paramTypes.add(YetiType.allTypes.get(parameterClassName));						
			}
			// Maybe we have to create a new YetiType
			else {
				YetiType paramType0 = new YetiKermetaType(parameterClass);
				paramTypes.add(paramType0);	
			}
		}
		// But then we need arrays in order to call the next method
		YetiType []paramTypesArray=(YetiType[])paramTypes.toArray(new YetiType[paramTypes.size()]);
		String []paramNamesArray=(String[]) paramNames.toArray(new String[paramNames.size()]);
		// Finally we call another method to finish the job, if the constructor is usable
		addConstructorFromClassToTypeInModuleIfUsable(clazz, type, module, true, paramTypesArray,paramNamesArray );
	}


	/**
	 * Add the constructors to Yeti for a kermeta enumeration.
	 * @param enu The enumeration on which the constructors are based.
	 * @param type The type we want to add constructors to.
	 */
	private void addConstructors(Enumeration enu, YetiKermetaType type) {
		// We have to add one constructor per litteral
		for(EnumerationLiteral t : enu.getOwnedLiteral()) {
			YetiLog.printDebugLog("adding constructor to enumeration "+type.getName(), this);
			String enumName =  NamedElementHelper.getQualifiedName(enu);
			YetiRoutine constructorRoutine = new YetiKermetaEnumerationLitteralConstructor(new YetiName(enumName), type, enu, t.getName());
			// add it as a creation routine for the type
			type.addCreationRoutine(constructorRoutine);
		}
	}


	/**
	 * Add a constructor to a  module and a type if usable.
	 * @param clazz the originating class.
	 * @param type the type of the created object.
	 * @param module the module to which we should add it.
	 * @param con the constructor.
	 * @param usable True if it is usable.
	 * @param paramTypes the types of the parameters.
	 * @param paramNames the names of the parameters.
	 */
	public void addConstructorFromClassToTypeInModuleIfUsable(ClassDefinition clazz, YetiType type, YetiModule module,  boolean usable,
			YetiType[] paramTypes, String[] paramNames) {
		// if we don't know a type from the constructor we don't add it
		if (usable){
			YetiLog.printDebugLog("adding constructor to "+type.getName()+" in module "+module.getModuleName(), this);
			YetiRoutine constructorRoutine = generateRoutineFromConstructor(clazz, paramTypes , paramNames, type, module);
			// add it as a creation routine for the type
			type.addCreationRoutine(constructorRoutine);
			// add the constructor as a routine to test
			module.addRoutineInModule(constructorRoutine);
		}
	}


	/**
	 * Create a Yeti routine for the Kermeta constructor to test
	 * 
	 * @param clazz the originating class.
	 * @param paramTypes the types of the parameters.
	 * @param type the type of the created object.
	 * @param mod the module to which we should add it.
	 * @return the Yeti routine for the constructor of the class c
	 */
	protected YetiRoutine generateRoutineFromConstructor(ClassDefinition clazz, YetiType[] paramTypes, String[] paramNames, YetiType type, YetiModule mod) {
		String className =  NamedElementHelper.getQualifiedName(clazz);
		return new YetiKermetaConstructor(YetiName.getFreshNameFrom(className), paramTypes, paramNames , type, mod);
	}

	/**
	 * Artificially add a module (without a type) with methods that constructs random instances of kermeta primitive types.
	 * Use the YetiKermetaRandomGenerator class.
	 */
	public void addRandomGenerators() {
		// The class to use (a Java class, but it's okayÃ§)
		Class<YetiKermetaRandomGenerator> clazz = YetiKermetaRandomGenerator.class;
		// we create the YetiModule out of the class
		YetiModule module = new YetiJavaModule(clazz.getName(), clazz);
		YetiModule.allModules.put(clazz.getName(), module);
		// we add all methods that can be found in the keys of the hashtable
		for(String methodName : YetiKermetaRandomGenerator.getMethodsNames()) {
			// We find the correspounding method
			Method method = null;
			for (Method m: clazz.getMethods()){
				if (m.getName().contains(methodName)) {
					method=m;
					break;
				}
			}
			if (method==null) continue;
			// the methods are not supposed to have parameters, so we make as if they don't
			YetiType [] paramTypes=new YetiType[0];
			YetiLog.printDebugLog("adding method "+method.getName()+" in module "+module.getModuleName(), this);
			// add it as a creation routine for the return type (using YetiKermetaRandomGenerator hashmap)
			String returnTypeName = YetiKermetaRandomGenerator.getReturnTypeName(method.getName());
			YetiType returnType = YetiType.allTypes.get(returnTypeName);
			if (returnType==null) {
				returnType = new YetiKermetaType(returnTypeName);
			}
			YetiRoutine methodRoutine = new YetiJavaMethod(YetiName.getFreshNameFrom(method.getName()), paramTypes , returnType, module, method,true);
			returnType.addCreationRoutine(methodRoutine);
			// add the method as a routine to test
			module.addRoutineInModule(methodRoutine);
		}
	}

	/**
	 * Initializes the kermeta interpreter using the classpath provided.
	 */
	public void prepareInterpreterWithPath() {
		// we find the directory
		File dir = new File(classpath);
		YetiLog.printDebugLog("loading from classpath: " + classpath, this);
		try {
			if (dir.exists()) {
				// It can either be a directory (full of .kmt files)
				if (dir.isDirectory()) {
					_kermetaInterpreter = KermetaSimplifiedInterpreterHelper.constructSimplifiedInterpreterFromSourcePath(classpath);
				}
				// Or a file (a main entry point file)
				else {
					_kermetaInterpreter = KermetaSimplifiedInterpreterHelper.constructSimplifiedInterpreterFromMainFile(classpath, dir.getParent());
				}
			}
		} catch (Exception e) {
			YetiLog.printDebugLog("impossible to prepare the kermeta interpreter with the path " + classpath, this);
			e.printStackTrace();
		}

	}
	

	/**
	 * For a kermeta Type, finds the most interesting kermeta TypeDefinition.
	 * @param type The type to consider
	 * @return A classdefinition
	 */
	public TypeDefinition typeToTypeDefinition(Type type) {
		TypeDefinition result = null;

		/*
		 *  Case : ParameterizedType (surely a Class)
		 *  We look for the typeDefinition attribute
		 */
		if (type instanceof ParameterizedType) {
			result = (ClassDefinition) ((ParameterizedType) type).getTypeDefinition();
		}

		/*
		 * Case : ObjectTypeVariable (generics type)
		 * If the type parameter is simple, we return the kermeta::language::structure::Object classdef
		 * If it has a supertype, recursive call on this type  
		 */
		else if (type instanceof ObjectTypeVariable) {
			Type superType = ((ObjectTypeVariable)type).getSupertype();
			// If the class parameter is simple
			if (superType==null) {
				result = _kermetaInterpreter.getClassDefinition("kermeta::language::structure::Object");
			}
			// If it has a supertype, recursive call
			else {
				result = typeToTypeDefinition(superType);
			}

		}

		/*
		 *  Case : VoidType (return types of operations without return type)
		 *  We return the kermeta::standard::Void classdef
		 */
		else if (type instanceof VoidType) {
			result = _kermetaInterpreter.getClassDefinition("kermeta::standard::Void");
		}

		/*
		 *  Case : PrimitiveType (aliases)
		 *  Recursive call on the type
		 */
		else if (type instanceof PrimitiveType) {
			result = typeToTypeDefinition(((PrimitiveType)type).getInstanceType());
		}

		/*
		 *  Case : Enumeration
		 *  no correspounding class ? what to do ?
		 */
		else if (type instanceof Enumeration) {
			return (Enumeration)type;
		}

		/*
		 *  Other cases : lambda expression types or model types
		 *  Not handled in this version yet.
		 */
		else  {

			
		}

		return result;
	}


}
