package yeti.experimenter;

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
 * Class that represents a class to test with its class path.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Apr 7, 2011
 *
 */
public class YetiTestArchiveModule {
	
	/**
	 * The name of the class to test.
	 */
	private String className;

	/**
	 * The path for the class to test.
	 */
	private String classpath;
	
	/**
	 * Getter for the name of the class.
	 * 
	 * @return the name of the class.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Setter for the name of the class.
	 * 
	 * @param className
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Getter for the classpath.
	 * 
	 * @return the classpath.
	 */
	public String getClasspath() {
		return classpath;
	}

	/**
	 * Setter for the classpath.
	 * 
	 * @param classpath the classpath to set.
	 */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}


	@Override
	public String toString() {
		return "Class "+getClassName()+" from "+getClasspath();
	}

	/**
	 * Simple constructor for the test archive module.
	 * 
	 * @param className The class name.
	 * @param classpath The classpath to it.
	 */
	public YetiTestArchiveModule(String className, String classpath) {
		super();
		this.className = className;
		this.classpath = classpath;
	}

}
