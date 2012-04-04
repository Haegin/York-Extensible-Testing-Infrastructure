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

/**
 * Class that represents a name in Yeti.
 * @author  Manuel Oriol (manuel@cs.york.ac.uk)
 * @date  Jun 22, 2009
 */
public class YetiName {
	/**
	 * The value of the name.
	 */
	protected String value;
	
	/**
	 * Creates a name from a String (appending a number to it)
	 * 
	 * @param s the String to use as a basis.
	 */
	public YetiName(String s){
		value = s;
	}
	
	/**
	 * Used to get unique names. Number appended to the name.
	 */
	public static long index=0;
	
	/**
	 * Get a fresh name.
	 * 
	 * @param name the basis.
	 * @return the fresh name (the basis + the unique number)
	 */
	public static YetiName getFreshNameFrom(String name){
		return new YetiName(name+"_"+index++);
	}

	/**
	 * Getter for the value of this name.
	 * @return  the value of this name.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setter for the value of this name.
	 * @param value  the value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Resets this class.
	 */
	public static void reset() {
		index = 0;
	}

}
