package yeti.environments;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

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
 * Class that represents a custom class loader. 
 * 
 * @author Vasileios Dimitriadis (vd508@cs.york.ac.uk, vdimitr@hotmail.com)
 * @date 20 Jul 2009
 *
 */
public abstract class YetiLoader extends URLClassLoader {
	
	/**
	 * The classpath of classes to load.
	 */
	protected String []classpaths;


	/**
	 * The general loader.
	 */
	public static YetiLoader yetiLoader;
	
	/**
	 * Constructor that creates a new loader.
	 * 
	 * @param path the classpath to load classes.
	 */
	public YetiLoader(String path) {
		super(new URL[0]);
		this.classpaths = path.split(System.getProperty("path.separator"));
		for(String pathToAdd:classpaths) {
			try {
				File fileToAdd = new File(pathToAdd);
				URL u = null;
				if (fileToAdd.getName().endsWith(".jar")) {
					String jarPath = "jar:" + fileToAdd.toURI().toString() +"!/";
					//URL u = fileToAdd.toURI().toURL();
					u = new URL(jarPath);
					
				} else
					u=fileToAdd.toURI().toURL();
				addURL(u);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		yetiLoader = this;
	}
	
	/**
	 * We load all the classes in the classpath.
	 */
	public abstract void loadAllClassesInPath();
	
	/**
	 * We load all classes in a directory.
	 * 
	 * @param directoryName the name of the directory.
	 * @param prefix the prefix for the class.
	 */
	public abstract void loadAllClassesIn(String directoryName, String prefix);
	
	/**
	 * We add the definition of the parameter class to the Yeti structures.
	 * 
	 * @param clazz the class to add.
	 * @return the class that was added.
	 */
	@SuppressWarnings("rawtypes")
	public abstract Class addDefinition(Class clazz);

	/**
	 * Resets the loader of yeti.
	 */
	public static void reset() {
		yetiLoader = null;
	}
}
