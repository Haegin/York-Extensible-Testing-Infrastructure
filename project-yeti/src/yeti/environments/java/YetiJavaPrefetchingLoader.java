package yeti.environments.java;

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

import java.awt.BorderLayout;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JFrame;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import yeti.Yeti;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiName;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.environments.YetiLoader;
import yeti.experimenter.YetiTestArchiveModule;
import yeti.monitoring.YetiCoverageIndicator;
import yeti.monitoring.YetiGUI;
import yeti.monitoring.YetiGraphCoverageOverTime;

/**
 * Class that represents the custom class loader to load classes of the program.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 *
 */
public class YetiJavaPrefetchingLoader extends YetiLoader {

	/**
	 * Constructor that creates a new loader.
	 * 
	 * @param path the classpath to load classes.
	 */
	public YetiJavaPrefetchingLoader(String path) {
		super(path);
	}

	/* (non-Javadoc)
	 * 
	 * Standard class loader method to load a class and resolve it.
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Class loadClass(String name)throws ClassNotFoundException{
		return loadClass(name,true);
	}

	/* (non-Javadoc)
	 * Standard 
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
	public Class loadClass(String name, boolean resolve)	throws ClassNotFoundException{ 

		YetiJavaBytecodeInstrumenter bi = new YetiJavaBytecodeInstrumenter();

		Class clazz = findLoadedClass(name);

		// has the class already been loaded
		if (clazz!=null) return clazz;
		// is it a standard Java Class
		// TODO: check why this does not work properly...


		// Adding support for branch coverage
		boolean instrumented = false;
		if (!(name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun."))&&Yeti.hasBranchCoverage) {
			// we load it from within the standard loader
			try {
				//				if (Yeti.testModule.containsModuleName(name))
				if (exists(Yeti.testModulesName, name))
				{
					byte[] classBytes = bi.loadAndInstrument(name,classpaths);
					clazz = this.defineClass(name, classBytes, 0, classBytes.length);
					instrumented = true;
				}
				else
				{	
					clazz=findSystemClass(name);
				}

			} catch (NotFoundException e) {
				//e.printStackTrace();
				// If this happens we load the class with the standard class loader.
				clazz=findSystemClass(name);
			} catch (CannotCompileException e) {
				e.printStackTrace();
				clazz=findSystemClass(name);
			} catch (BadBytecode e) {
				e.printStackTrace();
				clazz=findSystemClass(name);
			} catch (IOException e) {
				e.printStackTrace();
				clazz=findSystemClass(name);
			} catch (java.lang.ClassFormatError e) {
				System.out.println(name+" Contains a method too long to instrument. Loading uninstrumented instead.");
				clazz=findSystemClass(name);
			} 
		}
		else 
		{
			clazz = super.loadClass(name,resolve);
		}

		YetiLog.printDebugLog("Class loaded in parent class loader: " + clazz.getName(), this);
		resolveClass(clazz);

		addDefinition(clazz);
		return clazz;
	}

	/**
	 * We add the definition of the parameter class to the Yeti structures.
	 * 
	 * @param clazz the class to add.
	 * @return the class that was added.
	 */
	@SuppressWarnings("unchecked")
	public Class addDefinition(Class clazz) {

		// we add the type to the types
		YetiJavaSpecificType type= null;
		if (YetiType.allTypes.containsKey(clazz.getName())){
			type = (YetiJavaSpecificType)YetiType.allTypes.get(clazz.getName());
			if (type.isProperlyInitialized()) {
				return clazz;
			} else {
				type.setProperlyInitialized(true);
			}
		} else {
			type = new YetiJavaSpecificType(clazz.getName());
			YetiType.allTypes.put(type.getName(), type);
			YetiLog.printDebugLog("adding " + type.getName() + " to yeti types ", this);
		}

		// we link this class to the parent class type
		Class parent = clazz.getSuperclass();

		if (parent!=null && YetiType.allTypes.containsKey(parent.getName())){
			YetiLog.printDebugLog("linking " + type.getName() + " to " + parent.getName(), this);
			YetiType.allTypes.get(parent.getName()).allSubtypes.put(clazz.getName(), type);
			YetiType baseType = YetiType.allTypes.get(parent.getName());
			if (!baseType.allSubtypes.containsValue(type))
				baseType.addSubtype(type);
			if (!type.directSuperTypes.containsValue(baseType))
				type.directSuperTypes.put(parent.getName(), baseType);
		}

		// we link this class to the parent interfaces
		Class []interfaces = clazz.getInterfaces();
		for (Class i: interfaces ) {

			if (!YetiType.allTypes.containsKey(i.getName())){
				YetiLog.printDebugLog("Creating super type " +i.getName(), this);
				YetiType.allTypes.put(i.getName(), new YetiJavaSpecificType(i.getName()));
			}
			YetiLog.printDebugLog("linking " + type.getName() + " to " +i.getName(), this);				
			YetiType interfaceType = YetiType.allTypes.get(i.getName());
			interfaceType.addSubtype(type);
			type.directSuperTypes.put(i.getName(), interfaceType);
		}

		// we create the YetiModule out of the class
		YetiModule mod = this.makeModuleFromClass(clazz);
		YetiModule.allModules.put(clazz.getName(), mod);

		// we add the constructors to the type information	
		if (!clazz.getName().equals("yeti.environments.java.YetiJavaSpecificType")) addConstructors(clazz, type, mod);

		// we add methods to the module in which they were defined		
		addMethods(clazz, mod);

		// we add inner classes
		for(Class declaredClazz: clazz.getDeclaredClasses()){
			YetiLog.printDebugLog("Adding inner class: " + declaredClazz.getName(), this);
			addDefinition(declaredClazz);
		}

		return clazz;
	}

	/**
	 * We add the methods of the class to the module.
	 * 
	 * @param clazz the class to add.
	 * @param module the module in which ad it.
	 */
	@SuppressWarnings("unchecked")
	public void addMethods(Class clazz, YetiModule module) {

		// we add all methods
		Method[] methods = clazz.getMethods();
		for (Method method: methods){

			if (method.isSynthetic()) continue;
			if (method.getName().startsWith("__yeti_")) continue;
			boolean usable = true;

			Class []classes=method.getParameterTypes();



			// check if method is static
			boolean isStatic = Modifier.isStatic((method.getModifiers()));
			YetiType []paramTypes;

			// if the method is static we do not introduce a slot for the target.
			int offset = 0;
			if (isStatic) {
				paramTypes=new YetiType[classes.length];
			} else {
				paramTypes=new YetiType[classes.length+1];
				offset = 1;
				if (YetiType.allTypes.containsKey(clazz.getName())){
					paramTypes[0]=YetiType.allTypes.get(clazz.getName());						
				} else {
					usable = false;
				}
			}

			// for all types we box the types.
			for (int i=0; i<classes.length; i++){
				Class c0 = classes[i];
				if (YetiType.allTypes.containsKey(c0.getName())){
					paramTypes[i+offset]=YetiType.allTypes.get(c0.getName());						
				} else {
					usable = false;
				}
			}
			addMethodToModuleIfUsable(module, method, usable, paramTypes);
		}
	}

	/**
	 * Adds a method to the module if it is usable.
	 * 
	 * @param module the module to which add the method.
	 * @param method the method to add.
	 * @param usable True if it should be added.
	 * @param paramTypes the types of the parameters.
	 */
	public void addMethodToModuleIfUsable(YetiModule module, Method method, boolean usable, YetiType[] paramTypes) {
		// if we don't know a type from the method we don't add it
		if (usable && !YetiJavaMethod.isMethodNotToAdd(method.getName())){
			YetiLog.printDebugLog("adding method "+method.getName()+" in module "+module.getModuleName(), this);
			// add it as a creation routine for the return type
			YetiType returnType = YetiType.allTypes.get(method.getReturnType().getName());
			if (returnType==null) {
				returnType = new YetiJavaSpecificType(method.getReturnType().getName());
			}
			YetiRoutine methodRoutine = generateRoutineFromMethod(module , method, paramTypes , returnType);
			// add it as a creation routine for the type
			returnType.addCreationRoutine(methodRoutine);
			// add the method as a routine to test
			module.addRoutineInModule(methodRoutine);
		}
	}

	/**
	 * Create a Yeti routine for the Java method to test
	 * 
	 * @param module the module to which add the method.
	 * @param method the method to add.
	 * @param paramTypes the types of the parameters.
	 * @param returnType the type returned by the method 
	 * @return a Yeti routine for this method
	 */
	protected YetiRoutine generateRoutineFromMethod(YetiModule module, Method method, YetiType[] paramTypes, YetiType returnType) {
		return new YetiJavaMethod(YetiName.getFreshNameFrom(method.getName()), paramTypes , returnType, module, method);
	}

	/**
	 * Add the constructors of a class.
	 * 
	 * @param clazz the class of the constructor.
	 * @param type the type of the instance created.
	 * @param module the module to which add the class.
	 */
	@SuppressWarnings("unchecked")
	public void addConstructors(Class clazz, YetiType type, YetiModule module) {
		// if the class is abstract, the constructors should not be called
		if (Modifier.isAbstract(clazz.getModifiers()))
			return;

		// we add the constructors
		Constructor[] constructors = clazz.getConstructors();
		for (Constructor con: constructors){
			YetiLog.printDebugLog("Adding constructor "+con.getName()+" of type "+type.getName(),this);
			boolean usable = true;
			Class[] classes= con.getParameterTypes();
			YetiType []paramTypes=new YetiType[classes.length];
			// for all types we box the types.
			for (int i=0; i<classes.length; i++){
				Class c0 = classes[i];
				if (YetiType.allTypes.containsKey(c0.getName())){
					paramTypes[i]=YetiType.allTypes.get(c0.getName());						
				} else {
					YetiType paramType0 = new YetiJavaSpecificType(c0.getName());
					paramTypes[i]=paramType0;	
					// This should not be useful anymore:
					//usable = false;
				}
			}
			addConstructorFromClassToTypeInModuleIfUsable(clazz, type, module, con, usable, paramTypes);
		}
	}

	/**
	 * Add a constructor to a  module and a type if usable.
	 * 
	 * @param clazz the originating class.
	 * @param type the type of the created object.
	 * @param module the module to which we should add it.
	 * @param con the constructor.
	 * @param usable True if it is usable.
	 * @param paramTypes the types of the parameters.
	 */
	@SuppressWarnings("unchecked")
	public void addConstructorFromClassToTypeInModuleIfUsable(Class clazz,	YetiType type, YetiModule module, Constructor con, boolean usable,
			YetiType[] paramTypes) {
		// if we don't know a type from the constructor we don't add it
		if (usable){
			YetiLog.printDebugLog("adding constructor to "+type.getName()+" in module "+module.getModuleName(), this);
			YetiRoutine constructorRoutine = generateRoutineFromConstructor(clazz, paramTypes , type, module, con);
			// add it as a creation routine for the type
			type.addCreationRoutine(constructorRoutine);
			// add the constructor as a routine to test
			module.addRoutineInModule(constructorRoutine);
		}
	}

	/**
	 * Create a Yeti routine for the Java constructor to test
	 * 
	 * @param clazz the originating class.
	 * @param paramTypes the types of the parameters.
	 * @param type the type of the created object.
	 * @param mod the module to which we should add it.
	 * @param m the constructor.
	 * @return the Yeti routine for the constructor of the class c
	 */
	@SuppressWarnings("unchecked")
	protected YetiRoutine generateRoutineFromConstructor(Class clazz, YetiType[] paramTypes, YetiType type, YetiModule mod, Constructor con) {
		return new YetiJavaConstructor(YetiName.getFreshNameFrom(clazz.getName()), paramTypes , type, mod, con);
	}

	/**
	 * Create an empty module from a class (using its class name).
	 * 
	 * @param c the class to make a module from.
	 * @return The module created.
	 */
	@SuppressWarnings("unchecked")
	public YetiModule makeModuleFromClass(Class c){
		YetiModule mod=new YetiJavaModule(c.getName(), c);

		return mod;
	}

	/* (non-Javadoc)
	 * 
	 * Standard javadoc function.
	 * We now use the URLClassLoader and do not need that.
	 * 
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	//	@SuppressWarnings("unchecked")
	//	public Class findClass(String name) throws ClassNotFoundException{
	//		File fc=null;
	//		Class c=null;
	//
	//		// for all paths in class path, we try to load the class 
	//		for (String classpath: classpaths){
	//			fc=new File(classpath+System.getProperty("file.separator")+name.replace('.', System.getProperty("file.separator").charAt(0))+".class");
	//			YetiLog.printDebugLog("trying: "+fc.getAbsolutePath(), this);
	//			// we actually check that the class exists
	//			if (fc.exists()){
	//				YetiLog.printDebugLog("found it", this);
	//				c=readClass(fc,name);
	//				break;
	//			}
	//		}
	//		if (c==null) throw new ClassNotFoundException(name);
	//		return c;
	//	}

	/**
	 *  Utility function to read the class from disk. Should be extended in the future to add reading from a jar file.
	 * 
	 * @param file the file in which the class is.
	 * @param name the name of the class.
	 * @return the class read.
	 */
	@SuppressWarnings("unchecked")
	public Class readClass(File file,String name){
		Class clazz ;
		try {
			BufferedInputStream fr=new BufferedInputStream(new FileInputStream(file));
			long l=file.length();
			byte[] bBuf=new byte[(int)l];
			// we try to read the file as a byte array
			fr.read(bBuf,0,(int)l);
			YetiLog.printDebugLog(name+" read in byte[]", this);
			// we try to define the class
			clazz = defineClass(name, bBuf,0,bBuf.length);
			YetiLog.printDebugLog(name+" defined ", this);
			return clazz ;
		} catch (Throwable e){
			e.printStackTrace();
			YetiLog.printDebugLog(name+" not loaded", this);
			return null;
		}
	}

	/**
	 * We load all the classes in the classpath.
	 */
	public void loadAllClassesInPath(){
		for (String classpath: classpaths){
			//if (!classpath.endsWith(".jar"))
			loadAllClassesIn(classpath, "");
		}
	}

	/**
	 * We load all classes in a directory.
	 * 
	 * @param directoryName the name of the directory.
	 * @param prefix the prefix for the class.
	 */
	public void loadAllClassesIn(String directoryName, String prefix) {
		// we create the directory
		File dir = new File(directoryName);
		YetiLog.printDebugLog("loading from classpath: " + directoryName, this);

		if (dir.exists()) {

			if (dir.isDirectory()) {
				// we iterate through the content
				for (File file: dir.listFiles()) {
					// For each subdirectory we load recursively
					String cname=file.getName();
					if (file.isDirectory()){
						if (prefix.equals("")){
							loadAllClassesIn(directoryName+System.getProperty("file.separator")+cname,cname);
						}else{
							loadAllClassesIn(directoryName+System.getProperty("file.separator")+cname,prefix+"."+cname);
						}
					} else
						// otherwise we load the class
						if (cname.endsWith(".class")){
							String className=cname;
							className=className.substring(0,className.length()-6);
							YetiLog.printDebugLog("reading "+className, this);
							try {
								// we actually try to load the class
								if (prefix.equals(""))
									loadClass(className);
								else
									loadClass(prefix+"."+className);
							} catch (ClassNotFoundException e) {
								// should never happen
								e.printStackTrace();
							}
						}
				}
			} else 
			{
				if (dir.getName().endsWith(".jar")) {
					String classpath = dir.getAbsolutePath();
					YetiLog.printDebugLog("JAR found: "+classpath, this);
					JarFile jf = null;
					try {
						// we first define a JarFile
						jf = new JarFile(dir);
						// we open the JarFile
						Enumeration<JarEntry> jes = jf.entries();
						// for each element, we open it and extract the name and the classpath of the file
						for(JarEntry je=jes.nextElement();jes.hasMoreElements();je=jes.nextElement()) {
							String name = je.getName().replace("/", ".");
							if (name.endsWith(".class")) {
								name = name.substring(0,name.length()-6);
								YetiLog.printDebugLog("Loading class: "+name, this);
								try {
									loadClass(name);
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									YetiLog.printDebugLog(e.getMessage(), this);
								}
							}
						}

					} catch (IOException e) {

						System.err.println("Problem with file: "+dir.getName());
						e.printStackTrace();
					} finally {
						try {
							// we close the file
							jf.close();
						} catch (Exception e) {
							// if this happens, the JarFile is already closed, we ignore it then
						}
					}

				}
			}
		}
	}

	private boolean exists(String[] array, String name)
	{
		for ( int n = 0; n < array.length; n++ )
			if ( array[ n ].equals(name) )
				return true;

		return false;
	}
}